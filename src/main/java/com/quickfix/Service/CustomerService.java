package com.quickfix.Service;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.*;
import com.quickfix.Enums.*;
import com.quickfix.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final ServiceProviderRepo serviceProviderRepo;
    private final BookingRepo bookingRepo;
    private final PaymentRepo paymentRepo;
    private final ReviewRepo reviewRepo;


    public CustomerService(CustomerRepo customerRepo, ServiceProviderRepo serviceProviderRepo, BookingRepo bookingRepo, PaymentRepo paymentRepo, ReviewRepo reviewRepo) {
        this.customerRepo = customerRepo;
        this.serviceProviderRepo = serviceProviderRepo;
        this.bookingRepo = bookingRepo;
        this.paymentRepo = paymentRepo;
        this.reviewRepo = reviewRepo;
    }

    public String register(Customer customer) {

        if (customerRepo.existsByEmail(customer.getEmail())) {
            return "EXISTS";
        }

        customerRepo.save(customer);
        return "SUCCESS";
    }

    /*Checking Login Customers Details By Email*/
    public String loginCustomer(LoginRequest loginRequest) {
        Customer customer = customerRepo.findByEmail(loginRequest.getEmail());

        if (customer == null) {
            return "INVALID";
        }

        if (!customer.getPassword().equals(loginRequest.getPassword())) {
            return "INVALID";
        }
        return "SUCCESS";
    }

    /*Get Customer Details [Only One Customer]*/
    public Customer getCustomer(String email) {
        Customer customer = customerRepo.findByEmail(email);
        return customer;
    }

    public Boolean updateProfile(UpdateCustomerProfiles updateCustomerProfiles) {
        Customer customer = customerRepo.findByEmail(updateCustomerProfiles.getEmail());
        if (customer != null) {
            customer.setAddress(updateCustomerProfiles.getAddress());
            customer.setPhone(updateCustomerProfiles.getPhone());
            customer.setName(updateCustomerProfiles.getName());
            // Save Updated Customer Details in Customer Table
            customerRepo.save(customer);
            return true;
        }
        return false;
    }

    /*Return All Approved Service Providers Details On Customers Dashboard*/
    public List<ServiceProvider> getApprovedProvider() {
        return serviceProviderRepo.findByVerifiedStatusAndProviderActiveStatus(VerifiedStatus.APPROVED, ProviderActiveStatus.ACTIVE);
    }

    /*Customer Create Booking */
    @Transactional
    public Booking createBooking(CreateBookingCustomer createBookingCustomer) {

        Customer customer = customerRepo.findByEmail(createBookingCustomer.getEmail());

        ServiceProvider serviceProvider = serviceProviderRepo
                .findById(createBookingCustomer.getProvider_id())
                .orElseThrow(() -> new RuntimeException("Service Provider Not Found"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setProvider(serviceProvider);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepo.save(booking);

        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setMethod(createBookingCustomer.getMethod());
        payment.setAmount(serviceProvider.getAmount());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        paymentRepo.save(payment);

        return savedBooking;
    }

    /*Customers Bookings */
    public List<CustomersBooking> getAllCustomerBookings(Long customerId) {

        List<Booking> bookings = bookingRepo.findByCustomerCustomerId(customerId);
        List<CustomersBooking> customersBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            CustomersBooking dto = new CustomersBooking();

            dto.setName(booking.getProvider().getName());
            dto.setBookingDate(booking.getBookingDate());
            dto.setAmount(booking.getProvider().getAmount());
            dto.setServiceName(booking.getProvider().getServiceName());
            dto.setBookingStatus(booking.getStatus());
            dto.setProviderPhone(booking.getProvider().getPhone());
            dto.setBookingId(booking.getBookingId());
            dto.setProviderId(booking.getProvider().getProviderId());
            if (booking.getPayment() != null) {
                dto.setPaymentMethod(booking.getPayment().getMethod());
                dto.setPaymentStatus(booking.getPayment().getPaymentStatus());
            }

            customersBookings.add(dto);

        }

        return customersBookings;

    }


    public PaymentStatus customersPayment(PaymentRequestDTO paymentRequestDTO) {

        Booking booking = bookingRepo.findById(paymentRequestDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));


        if (booking.getPayment() == null) {
            Payment payment = new Payment();
            payment.setBooking(booking);
            booking.setPayment(payment);
        }

        booking.getPayment().setPaymentStatus(paymentRequestDTO.getPaymentStatus());
        booking.getPayment().setTransactionId(paymentRequestDTO.getTransactionId());
        booking.getPayment().setPaymentDate(LocalDateTime.now());

        bookingRepo.save(booking);

        return paymentRequestDTO.getPaymentStatus();
    }

    public Boolean customerMakeReview(CustomerMakeReview customerReview) {

        if (customerReview == null) {
            return false;
        }

        Review review = new Review();

        review.setComment(customerReview.getComment());

        Customer customer = customerRepo.findById(customerReview.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));

        ServiceProvider provider = serviceProviderRepo.findById(customerReview.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider Not Found"));

        Booking booking = bookingRepo.findById(customerReview.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));

        review.setCustomer(customer);
        review.setProvider(provider);
        review.setBooking(booking);

        review.setDate(LocalDateTime.now());

        reviewRepo.save(review);

        return true;
    }

    public CustomerPaymentHistory customerPaymentHistory(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking Not Found"));

        Payment payment = booking.getPayment();

        if (payment == null || payment.getPaymentStatus() != PaymentStatus.SUCCESS) {
            return null;
        }

        CustomerPaymentHistory dto = new CustomerPaymentHistory();

        dto.setPaymentId(payment.getPaymentId());
        dto.setMethod(payment.getMethod());
        dto.setServiceName(booking.getProvider().getServiceName());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setName(booking.getProvider().getName());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setAmount(payment.getAmount());

        return dto;
    }


    public List<CustomerAllReviewList> getCustomersReview(Long customerId) {
        List<Review> reviews = reviewRepo.findByCustomerCustomerId(customerId);

        List<CustomerAllReviewList> customerAllReviewLists = new ArrayList<>();

        for (Review review : reviews) {
            CustomerAllReviewList dto = new CustomerAllReviewList();

            dto.setServiceName(review.getProvider().getServiceName());
            dto.setName(review.getProvider().getName());
            dto.setDate(review.getDate());
            dto.setComment(review.getComment());
            dto.setProviderId(review.getProvider().getProviderId());
            if(review.getBooking() != null){
                dto.setBookingId(review.getBooking().getBookingId());
            }

            customerAllReviewLists.add(dto);

        }

        return customerAllReviewLists;

    }
}
