package com.quickfix.Controller;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.Booking;
import com.quickfix.Entitys.ServiceProvider;
import com.quickfix.Enums.*;
import com.quickfix.Repository.ServiceProviderRepo;
import com.quickfix.Service.ProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/providers")
public class ProviderController {
    private final ProviderService providerService;
    private final ServiceProviderRepo serviceProviderRepo;

    public ProviderController(ProviderService providerService,ServiceProviderRepo serviceProviderRepo){
        this.providerService = providerService;
        this.serviceProviderRepo = serviceProviderRepo;
    }

    @PostMapping("/register")
    public String registerProvider(@RequestBody ServiceProvider serviceProvider){

        if(serviceProviderRepo.existsByEmail(serviceProvider.getEmail())){
            return "EXISTS";
        }
        System.out.println("ADDRESS: " + serviceProvider.getAddress());
        serviceProvider.setVerifiedStatus(VerifiedStatus.PENDING);
        serviceProvider.setProviderActiveStatus(ProviderActiveStatus.ACTIVE);

        serviceProviderRepo.save(serviceProvider);

        return "SUCCESS";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest){

        String result = providerService.login(loginRequest);

        return switch (result) {
            case "SUCCESS" -> ResponseEntity.ok("SUCCESS");
            case "PENDING" -> ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Your registration is completed. Admin will approve your account within 24 hours.");
            case "REJECTED" -> ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Your account has been rejected. Please contact support.");
            default -> ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password");
        };
    }

    @GetMapping("/servicesDetails")
    public ResponseEntity<List<ServiceProvider>> getAllServiceDetail(){
        List<ServiceProvider> listOfServiceProvider = providerService.getServiceDetails();
        if(listOfServiceProvider == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(listOfServiceProvider);
    }
    
    @GetMapping("/get/providerDetails")
    public ResponseEntity<ServiceProvider> getProviderDetails(@RequestParam String email){

        ServiceProvider provider = providerService.getProviderByEmail(email);

        if(provider == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(provider);
    }

    @GetMapping("/pending-requests/{providerId}")
    public ResponseEntity<List<CustomersRequestForServiceProviders>> getPendingCustomers(
            @PathVariable Long providerId){

        return ResponseEntity.ok(providerService.getPendingCustomers(providerId));
    }


    @PutMapping("updateProfile")
    public ResponseEntity<ServiceProvider> updateProfile(@RequestBody ProviderProfileUpdate providerProfileUpdate){
        ServiceProvider serviceProvider = providerService.updateProfile(providerProfileUpdate);

        if (serviceProvider == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(serviceProvider);
    }

    @PutMapping("/customers-requests/accept-reject")
    public Booking acceptOrReject(@RequestParam Long bookingId, @RequestParam String status){
        return providerService.acceptOrReject(bookingId,status);
    }

    @GetMapping("/accepted-bookings/{providerId}")
    public ResponseEntity<?> acceptedBookings(@PathVariable Long providerId){
        List<AcceptedProviderBookings> acceptedProviderBookings = providerService.getAllAcceptedBooking(providerId);

        if(acceptedProviderBookings == null){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(acceptedProviderBookings);
    }


    @PutMapping("/update-status/{bookingId}")
    public Boolean updateBookingStatus(@PathVariable Long bookingId){
        return providerService.updateStatus(bookingId);
    }


    @PutMapping("/update/cash/payment-status/{bookingId}/{providerId}")
    public ResponseEntity<?> updateCashPaymentStatus(@PathVariable Long bookingId,@PathVariable Long providerId){
        String ans = providerService.updateBookingAndPaymentStatus(bookingId,providerId);

        if(ans.equals("Booking not found")){
            return ResponseEntity.notFound().build();
        }else if(ans.equals("Cash Collected")){
            return ResponseEntity.ok(PaymentStatus.SUCCESS);
        }else if(ans.equals("Request to customer")){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/get/reviews/{providerId}")
    public ResponseEntity<List<ProviderReviewListFromCustomers>> getAllReviews(@PathVariable Long providerId){
        List<ProviderReviewListFromCustomers> list = providerService.providersReviews(providerId);

        if(list == null || list.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(list);
    }
}
