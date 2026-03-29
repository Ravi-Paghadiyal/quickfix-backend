package com.quickfix.Service;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.*;
import com.quickfix.Enums.*;
import com.quickfix.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProviderService {
    private final ServiceProviderRepo serviceProviderRepo;
    private final BookingRepo bookingRepo;
    private final ReviewRepo reviewRepo;


    public ProviderService(ServiceProviderRepo serviceProviderRepo,BookingRepo bookingRepo,ReviewRepo reviewRepo){
        this.serviceProviderRepo = serviceProviderRepo;
        this.bookingRepo = bookingRepo;
        this.reviewRepo = reviewRepo;
    }

    public String registerProvider(ServiceProvider provider){

        if(serviceProviderRepo.existsByEmail(provider.getEmail())){
            return "EXISTS";
        }

        provider.setVerifiedStatus(VerifiedStatus.PENDING);
        provider.setProviderActiveStatus(ProviderActiveStatus.ACTIVE);

        serviceProviderRepo.save(provider);

        return "SUCCESS";
    }

    public String login(LoginRequest loginRequest){

        ServiceProvider provider =
                serviceProviderRepo.findByEmail(loginRequest.getEmail());

        if (provider == null) {
            return "INVALID";
        }

        if (!provider.getPassword().equals(loginRequest.getPassword())) {
            return "INVALID";
        }

        if (provider.getVerifiedStatus().equals(VerifiedStatus.PENDING)) {
            return "PENDING";
        }

        if (provider.getVerifiedStatus().equals(VerifiedStatus.REJECTED)) {
            return "REJECTED";
        }

        return "SUCCESS";
    }

    /*Get Available Service List */
    public List<ServiceProvider> getServiceDetails(){
        return serviceProviderRepo.findAll();
    }

    // get Approved Service providers
    public ServiceProvider getProviderByEmail(String email){
        return serviceProviderRepo.findByEmail(email);
    }

    /*Return all Pending Customers Requests*/
    public List<CustomersRequestForServiceProviders> getPendingCustomers(Long providerId){

        List<Booking> bookings = bookingRepo.findPendingBookingsByProvider(providerId);

        List<CustomersRequestForServiceProviders> response = new ArrayList<>();

        for(Booking booking : bookings){

            CustomersRequestForServiceProviders dto = new CustomersRequestForServiceProviders();

            dto.setBookingId(booking.getBookingId());
            dto.setDate(booking.getBookingDate());

            if(booking.getStatus() != null){
                dto.setStatus(booking.getStatus().toString());
            }

            // CUSTOMER SAFE CHECK
            Customer customer = booking.getCustomer();

            if(customer != null){
                dto.setName(customer.getName());
                dto.setAddress(customer.getAddress());
            }else{
                dto.setName("Unknown Customer");
                dto.setAddress("Address Not Available");
            }

            // PAYMENT SAFE CHECK
            Payment payment = booking.getPayment();

            if(payment != null && payment.getMethod() != null){
                dto.setMethod(payment.getMethod());
            }else{
                dto.setMethod(null);
            }

            response.add(dto);
        }

        return response;
    }

    public ServiceProvider updateProfile(ProviderProfileUpdate providerProfileUpdate){
        ServiceProvider serviceProvider = serviceProviderRepo.findByEmail(providerProfileUpdate.getEmail());

        if(serviceProvider == null){
            return null;
        }

        serviceProvider.setName(providerProfileUpdate.getName());
        serviceProvider.setExperience(providerProfileUpdate.getExperience());
        serviceProvider.setAmount(providerProfileUpdate.getAmount());
        serviceProvider.setAddress(providerProfileUpdate.getAddress());
        serviceProvider.setPhone(providerProfileUpdate.getPhone());
        serviceProvider.setDescription(providerProfileUpdate.getDescription());

        if(providerProfileUpdate.getProviderActiveStatus() != null){
            serviceProvider.setProviderActiveStatus(providerProfileUpdate.getProviderActiveStatus());
        }

        return serviceProviderRepo.save(serviceProvider);
    }

    /*Accept Customers Requests | Reject*/
    public Booking acceptOrReject(Long bookingId,String status){

        Booking booking = bookingRepo.findById(bookingId).orElse(null);

        if (booking != null){

            if (status.equalsIgnoreCase("ACCEPT")){
                booking.setStatus(BookingStatus.ACCEPTED);
            }

            else if (status.equalsIgnoreCase("REJECT")){
                booking.setStatus(BookingStatus.CANCELLED);
            }

            bookingRepo.save(booking);
        }

        return booking;
    }

    public List<AcceptedProviderBookings> getAllAcceptedBooking(Long providerId){

        List<BookingStatus> statuses = List.of(
                BookingStatus.ACCEPTED,
                BookingStatus.COMPLETED
        );

        List<PaymentMethod> paymentMethods = List.of(
                PaymentMethod.CASH,
                PaymentMethod.UPI,
                PaymentMethod.NET_BANKING
        );

        List<Booking> bookings =
                bookingRepo.findByProviderProviderIdAndStatusInAndPaymentMethodIn(providerId, statuses,paymentMethods);

        List<AcceptedProviderBookings> acceptedProviderBookings = new ArrayList<>();

        for(Booking booking : bookings){
            AcceptedProviderBookings dto = new AcceptedProviderBookings();

            dto.setName(booking.getCustomer().getName());
            dto.setBookingDate(booking.getBookingDate());
            dto.setPaymentMethod(booking.getPayment().getMethod());
            dto.setBookingStatus(booking.getStatus());
            dto.setPaymentStatus(booking.getPayment().getPaymentStatus());
            dto.setBookingId(booking.getBookingId());
            dto.setAmount(booking.getPayment().getAmount());


            acceptedProviderBookings.add(dto);
        }

        return acceptedProviderBookings;
    }

    public String updateBookingAndPaymentStatus(Long bookingId, Long providerId){

        Booking booking = bookingRepo.findByBookingIdAndProviderProviderId(bookingId, providerId);

        if(booking == null){
            return "Booking not found";
        }

        PaymentMethod paymentMethod = booking.getPayment().getMethod();
        BookingStatus bookingStatus = booking.getStatus();
        PaymentStatus paymentStatus = booking.getPayment().getPaymentStatus();

        if(paymentMethod == PaymentMethod.CASH
                && bookingStatus == BookingStatus.COMPLETED
                && paymentStatus == PaymentStatus.PENDING){

            booking.getPayment().setPaymentStatus(PaymentStatus.SUCCESS);
            bookingRepo.save(booking);

            return "Cash Collected";
        }

        else if(bookingStatus == BookingStatus.COMPLETED &&
                (paymentMethod == PaymentMethod.UPI ||
                        paymentMethod == PaymentMethod.NET_BANKING)){

            return "Request to customer";
        }

        return "Invalid request";
    }

    /*Update Booking Status*/
    public Boolean updateStatus(Long bookingId){
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(()-> new RuntimeException("Booking Not Found"));

        if(booking == null){
            return false;
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepo.save(booking);

        return true;

    }

    public List<ProviderReviewListFromCustomers> providersReviews(Long providerId){
        List<Review> allReview = reviewRepo.findByProviderProviderId(providerId);

        List<ProviderReviewListFromCustomers> providerReviewListFromCustomers = new ArrayList<>();

        for(Review review : allReview){
            ProviderReviewListFromCustomers dto = new ProviderReviewListFromCustomers();

            dto.setReviewId(review.getReviewId());
            dto.setName(review.getCustomer().getName());
            dto.setDate(review.getDate());
            dto.setComment(review.getComment());
            if(review.getCustomer() != null){
                dto.setName(review.getCustomer().getName());
            }

            providerReviewListFromCustomers.add(dto);
        }

        return providerReviewListFromCustomers;
    }
}
