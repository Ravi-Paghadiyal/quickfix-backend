package com.quickfix.Controller;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.Booking;
import com.quickfix.Entitys.Customer;
import com.quickfix.Entitys.ServiceProvider;
import com.quickfix.Enums.PaymentStatus;
import com.quickfix.Service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController( CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer){

        String result = customerService.register(customer);

        if(result.equals("EXISTS")){
            return ResponseEntity
                    .badRequest()
                    .body("User already exists");
        }

        return ResponseEntity.ok("Customer registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        String result = customerService.loginCustomer(loginRequest);

        return result.equalsIgnoreCase("SUCCESS")
                ? ResponseEntity.ok("SUCCESS")
                : ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid email or password");
    }

    @GetMapping("/by/{email}")
    public ResponseEntity<Customer> getCustomerDetails(@PathVariable() String email){
        Customer customer = customerService.getCustomer(email);
        if(customer == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<Boolean> updateCustomerProfile(@RequestBody UpdateCustomerProfiles updateCustomerProfiles){
        Boolean isUpdateCustomer = customerService.updateProfile(updateCustomerProfiles);
        if(!isUpdateCustomer){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/get/ApprovedProviders")
    public ResponseEntity<List<ServiceProvider>> getProvidersDetails(){
        List<ServiceProvider> listOfProvider = customerService.getApprovedProvider();
        if(listOfProvider == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(listOfProvider);
    }

    /*Customer Booking API */
    @PostMapping("create/booking")
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingCustomer createBookingCustomer){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.createBooking(createBookingCustomer));
    }

   /* *//*Customers Bookings Details*//*
    @GetMapping("/bookings/{custId}")
    public List<Booking> getCustomerBookings(@PathVariable Long custId){
        return customerService.getCustomerBookings(custId);
    }*/


    @GetMapping("/all-bookings/{customerId}")
    public ResponseEntity<List<CustomersBooking>> getAllCustomersBookings(@PathVariable Long customerId){
        List<CustomersBooking> customersBookings = customerService.getAllCustomerBookings(customerId);

        if(customersBookings == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(customersBookings);
    }

    @PostMapping("/payment/pay-to/provider")
    public ResponseEntity<PaymentStatus> customerPayToProvider(@RequestBody PaymentRequestDTO paymentRequestDTO){
        PaymentStatus paymentStatus = customerService.customersPayment(paymentRequestDTO);

        if(paymentStatus.equals(PaymentStatus.PENDING)){
            return ResponseEntity.accepted().body(PaymentStatus.PENDING);
        }


        return ResponseEntity.ok(paymentStatus);
    }

    @GetMapping("/payment/history/{bookingId}")
    public ResponseEntity<?> customerPaymentHistory(@PathVariable Long bookingId){

        CustomerPaymentHistory dto = customerService.customerPaymentHistory(bookingId);

        if(dto == null){
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/make/review")
    public ResponseEntity<Boolean> makeReview(@RequestBody CustomerMakeReview customerReview){
        Boolean ans = customerService.customerMakeReview(customerReview);

        if(!ans){
            return ResponseEntity.badRequest().body(false);
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/get/reviews/{customerId}")
    public ResponseEntity<List<CustomerAllReviewList>> getAllCustomerLists(@PathVariable Long customerId){
        List<CustomerAllReviewList> customerAllReviewLists = customerService.getCustomersReview(customerId);

        if(customerAllReviewLists == null){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customerAllReviewLists);
    }

}
