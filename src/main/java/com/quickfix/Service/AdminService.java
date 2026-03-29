package com.quickfix.Service;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.*;
import com.quickfix.Enums.BookingStatus;
import com.quickfix.Enums.PaymentStatus;
import com.quickfix.Enums.ProviderActiveStatus;
import com.quickfix.Enums.VerifiedStatus;
import com.quickfix.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class AdminService {
    private final AdminRepo adminRepo;
    private final ServiceProviderRepo providerRepo;
    private final PaymentRepo paymentRepo;
    private final BookingRepo bookingRepo;
    private final CustomerRepo customerRepo;

    public AdminService(AdminRepo adminRepo,ServiceProviderRepo providerRepo,PaymentRepo paymentRepo,BookingRepo bookingRepo,CustomerRepo customerRepo){
        this.adminRepo = adminRepo;
        this.providerRepo = providerRepo;
        this.paymentRepo = paymentRepo;
        this.bookingRepo = bookingRepo;
        this.customerRepo = customerRepo;
    }

    public String login(LoginRequest loginRequest) {
        Admin admin = adminRepo.findByEmail(loginRequest.getEmail());

        if(admin == null){
            return "INVALID";
        }

        if(admin.getPassword().equals(loginRequest.getPassword())){
            return "SUCCESS";
        }else{
            return "INVALID";
        }
    }

    public Boolean updateProfile(UpdateAdminProfiles updateAdminProfiles){
        Admin admin = adminRepo.findByEmail(updateAdminProfiles.getEmail());
        if(admin != null){
            admin.setName(updateAdminProfiles.getName());
            admin.setPhone(updateAdminProfiles.getPhone());
            admin.setAddress(updateAdminProfiles.getAddress());

            adminRepo.save(admin);
            return true;
        }
        return false;
    }

    public Admin GetAdmin(String email){
        return adminRepo.findByEmail(email);
    }

    // Return New Pending Service Provides Request
    public List<ServiceProvider> getNewServiceProvidersRequest(){
        return providerRepo.findByVerifiedStatus(VerifiedStatus.PENDING);
    }

    public Boolean updateProviderStatus(Long id, String status) {

        Optional<ServiceProvider> optionalProvider = providerRepo.findById(id);

        if (optionalProvider.isEmpty()) {
            return false;
        }

        ServiceProvider provider = optionalProvider.get();

        try {
            VerifiedStatus verifiedStatus = VerifiedStatus.valueOf(status.toUpperCase());

            // 🔥 If Admin Rejects → Delete Provider
            if (verifiedStatus == VerifiedStatus.REJECTED) {
                providerRepo.delete(provider);
                return true;
            }

            // Otherwise just update status
            provider.setVerifiedStatus(verifiedStatus);
            providerRepo.save(provider);

            return true;

        } catch (IllegalArgumentException e) {
            return false; // invalid status string
        }
    }

   public List<ActiveProvidersListForAdmin> activeProvidersList(){
        List<ServiceProvider> providerServices = providerRepo.findAll();
        List<ActiveProvidersListForAdmin> activeProvidersLists = new ArrayList<>();

        for(ServiceProvider provider : providerServices){

            ActiveProvidersListForAdmin dto = new ActiveProvidersListForAdmin();

            dto.setProviderId(provider.getProviderId());
            dto.setServiceName(provider.getServiceName());
            dto.setEmail(provider.getEmail());
            dto.setAddress(provider.getAddress());
            dto.setPhone(provider.getPhone());
            dto.setName(provider.getName());

            // Count Total Bookings
            List<Booking> bookings = bookingRepo.findByProviderProviderId(provider.getProviderId());

            long totalBookings = bookings.stream()
                    .filter(b -> b.getStatus().equals(BookingStatus.COMPLETED))
                    .count();

            dto.setTotalBookings(totalBookings);

            // Count Total Revenue

            List<Payment> payments = paymentRepo.findByBookingProviderProviderId(provider.getProviderId());
            Long totalRevenue = Math.round(
                    payments.stream()
                            .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                            .mapToDouble(Payment::getAmount)
                            .sum()
            );

            dto.setTotalRevenue(totalRevenue);

            if(provider.getProviderActiveStatus() == ProviderActiveStatus.ACTIVE) {
                dto.setProviderActiveStatus(ProviderActiveStatus.ACTIVE);
                activeProvidersLists.add(dto);
            }
        }
        return activeProvidersLists;
    }


    public List<CustomersListsForAdmin> getCustomersLists(){
        List<Customer> customers = customerRepo.findAll();
        List<CustomersListsForAdmin> customersListsForAdmins = new ArrayList<>();


        for(Customer customer : customers){
            CustomersListsForAdmin dto = new CustomersListsForAdmin();
            List<Booking> bookings = bookingRepo.findByCustomerCustomerId(customer.getCustomerId());

            dto.setCustomerId(customer.getCustomerId());
            dto.setAddress(customer.getAddress());
            dto.setPhone(customer.getPhone());
            dto.setName(customer.getName());
            dto.setEmail(customer.getEmail());

            // Count Total Bookings
            Long totalBookings = bookings.stream()
                    .filter(b -> b.getStatus().equals(BookingStatus.COMPLETED) || b.getStatus().equals(BookingStatus.ACCEPTED))
                    .count();

            dto.setTotalBookings(totalBookings);

            // Customers Total Spend Amount
            Long totalSpend = Math.round(bookings.stream()
                    .filter(b -> b.getStatus().equals(BookingStatus.ACCEPTED) || b.getStatus().equals(BookingStatus.COMPLETED))
                    .mapToDouble(b ->{
                        Payment payment = paymentRepo.findByBookingBookingId(b.getBookingId());
                        return payment != null ? payment.getAmount() : 0;
                    })

                    .sum()
            );

            dto.setTotalSpent(totalSpend);

            customersListsForAdmins.add(dto);

        }

        return customersListsForAdmins;
    }

    public Map<String, Object> getDashboardOverviewData(){
        List<Booking> bookings = bookingRepo.findAll();
        List<Payment> payments = paymentRepo.findAll();
        List<ServiceProvider> providers  = providerRepo.findAll();
        List<Customer> customers = customerRepo.findAll();

        // Total Revenue
        Double totalRevenue = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        Long activeBookings = bookings.stream()
                .filter(b -> BookingStatus.ACCEPTED.equals(b.getStatus()))
                .count();

         Long totalCustomers = (long) customers.size();
         Long totalProviders = (long) providers.size();

        Map<String, Long> serviceCount = bookings.stream()
                .filter(b -> b.getProvider() != null && b.getProvider().getServiceName() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getProvider().getServiceName(),
                        Collectors.counting()
                ));

        String mostBookedService = serviceCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
        LocalDate today = LocalDate.now();

        Double todayRevenue = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> p.getPaymentDate().toLocalDate().equals(today))
                .mapToDouble(Payment::getAmount)
                .sum();

        YearMonth currentMonth = YearMonth.now();

        Double monthRevenue = payments.stream()
                .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                .filter(p -> p.getPaymentDate() != null)
                .filter(p -> YearMonth.from(p.getPaymentDate().toLocalDate()).equals(currentMonth))
                .mapToDouble(Payment::getAmount)
                .sum();

        // DAILY REVENUE (last 7 days)
        List<Map<String, Object>> dailyRevenue = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            double total = payments.stream()
                    .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                    .filter(p -> p.getPaymentDate() != null)
                    .filter(p -> p.getPaymentDate().toLocalDate().equals(date))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", date.getDayOfWeek().toString().substring(0,3));
            dayData.put("revenue", total);

            dailyRevenue.add(dayData);
        }
        List<Map<String, Object>> monthlyRevenue = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            int month = i;

            double total = payments.stream()
                    .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                    .filter(p -> p.getPaymentDate() != null)
                    .filter(p -> p.getPaymentDate().getMonthValue() == month)
                    .mapToDouble(Payment::getAmount)
                    .sum();

            Map<String, Object> data = new HashMap<>();
            data.put("month", Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            data.put("revenue", total);

            monthlyRevenue.add(data);
        }

        List<Map<String, Object>> yearlyRevenue = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();

        for (int year = currentYear - 3; year <= currentYear; year++) {

            int finalYear = year;

            double total = payments.stream()
                    .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                    .filter(p -> p.getPaymentDate() != null)
                    .filter(p -> p.getPaymentDate().getYear() == finalYear)
                    .mapToDouble(Payment::getAmount)
                    .sum();

            Map<String, Object> data = new HashMap<>();
            data.put("year", String.valueOf(year));
            data.put("revenue", total);

            yearlyRevenue.add(data);
        }

        Map<String,Object> allBookingsStatus = new HashMap<>();

        Long pendingBookings = bookings.stream()
                .filter(b->BookingStatus.PENDING.equals(b.getStatus()))
                .count();

        Long acceptedBooking = bookings.stream()
                .filter(b -> BookingStatus.ACCEPTED.equals(b.getStatus()))
                .count();

        Long completedBooking = bookings.stream()
                .filter(b -> BookingStatus.COMPLETED.equals(b.getStatus()))
                .count();

        Long cancelledBookings = bookings.stream()
                .filter(b -> BookingStatus.CANCELLED.equals(b.getStatus()))
                .count();

        Map<String, List<Booking>> serviceGrouped = bookings.stream()
                .filter(b -> b.getProvider() != null && b.getProvider().getServiceName() != null)
                .collect(Collectors.groupingBy(
                        b -> b.getProvider().getServiceName().trim().toLowerCase()
                ));
        List<Map<String, Object>> serviceStats = new ArrayList<>();

        for (Map.Entry<String, List<Booking>> entry : serviceGrouped.entrySet()) {

            String service = entry.getKey();
            List<Booking> serviceBookings = entry.getValue();

            long totalBookings = serviceBookings.size();

            double totalRevenueService = payments.stream()
                    .filter(p -> PaymentStatus.SUCCESS.equals(p.getPaymentStatus()))
                    .filter(p -> p.getBooking().getProvider() != null &&
                            service.equalsIgnoreCase(
                                    p.getBooking().getProvider().getServiceName().trim().toLowerCase()
                            ))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            long activeProvidersService = providers.stream()
                    .filter(p -> p.getServiceName() != null &&
                            service.equalsIgnoreCase(p.getServiceName().trim().toLowerCase()))
                    .count();

            long avgCharge = totalBookings > 0 ? (long)(totalRevenueService / totalBookings) : 0;

            // Display name fix
            String displayName = service.substring(0,1).toUpperCase() + service.substring(1);



            Map<String, Object> map = new HashMap<>();
            map.put("service", displayName);
            map.put("totalBookings", totalBookings);
            map.put("totalRevenue", (long) totalRevenueService);
            map.put("activeProviders", activeProvidersService);
            map.put("avgServiceCharge", avgCharge);

            serviceStats.add(map);
        }

        serviceStats.sort((a, b) ->
                Long.compare((Long)b.get("totalBookings"), (Long)a.get("totalBookings"))
        );

        // AVG BOOKINGS PER DAY
        double avgBookingsPerDay = 0;

        if (!bookings.isEmpty()) {

            LocalDate firstDate = bookings.stream()
                    .filter(b -> b.getBookingDate() != null)
                    .map(b -> b.getBookingDate().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            LocalDate lastDate = bookings.stream()
                    .filter(b -> b.getBookingDate() != null)
                    .map(b -> b.getBookingDate().toLocalDate())
                    .max(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            long days = java.time.temporal.ChronoUnit.DAYS.between(firstDate, lastDate) + 1;

            if (days > 0) {
                avgBookingsPerDay = (double) bookings.size() / days;
            }
        }

        // 🔹 Response
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", totalRevenue);
        response.put("activeBookings", activeBookings);
        response.put("totalCustomers", totalCustomers);
        response.put("totalProviders", totalProviders);
        response.put("mostBookedService", mostBookedService);
        response.put("todayRevenue", todayRevenue);
        response.put("monthRevenue", monthRevenue);
        response.put("dailyRevenue", dailyRevenue);
        response.put("monthlyRevenue", monthlyRevenue);
        response.put("yearlyRevenue", yearlyRevenue);
        response.put("pendingBookings",pendingBookings);
        response.put("acceptedBooking",acceptedBooking);
        response.put("completedBooking",completedBooking);
        response.put("cancelledBookings",cancelledBookings);
        response.put("serviceStats", serviceStats);
        response.put("avgBookingsPerDay", avgBookingsPerDay);

        return response;
    }

    public List<BookingDTOForAdmin> getAllBookings(){
        List<Booking> bookings = bookingRepo.findAll();
        List<BookingDTOForAdmin> bookingDTOForAdmins = new ArrayList<>();

        for (Booking booking : bookings) {


            if (booking.getProvider() == null || booking.getCustomer() == null) {
                continue;
            }

            BookingDTOForAdmin dto = new BookingDTOForAdmin();

            dto.setId(booking.getBookingId());


            dto.setProviderId(booking.getProvider().getProviderId());
            dto.setProviderName(booking.getProvider().getName());
            dto.setServiceName(booking.getProvider().getServiceName());
            dto.setProviderAddress(booking.getProvider().getAddress());
            dto.setProviderPhone(booking.getProvider().getPhone());


            dto.setCustomerName(booking.getCustomer().getName());
            dto.setCustomerAddress(booking.getCustomer().getAddress());
            dto.setCustomerPhone(booking.getCustomer().getPhone());


            dto.setAmount(
                    booking.getPayment() != null
                            ? booking.getPayment().getAmount()
                            : booking.getProvider().getAmount()
            );


            dto.setBookingStatus(booking.getStatus().name());

            dto.setPaymentMethod(
                    booking.getPayment() != null &&
                            booking.getPayment().getMethod() != null
                            ? booking.getPayment().getMethod()
                            : null
            );

            dto.setPaymentStatus(
                    booking.getPayment() != null &&
                            booking.getPayment().getPaymentStatus() != null
                    ? booking.getPayment().getPaymentStatus()
                            : null
            );

            dto.setBookingDate(booking.getBookingDate());

            bookingDTOForAdmins.add(dto);
        }
        return bookingDTOForAdmins;
    }
}
