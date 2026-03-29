package com.quickfix.Controller;

import com.quickfix.DTO.*;
import com.quickfix.Entitys.Admin;
import com.quickfix.Entitys.Booking;
import com.quickfix.Entitys.ServiceProvider;
import com.quickfix.Service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        String result = adminService.login(loginRequest);

        return result.equalsIgnoreCase("SUCCESS")
                ? ResponseEntity.ok("SUCCESS")
                : ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Invalid email or password");
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<Boolean> updateProfiles(@RequestBody UpdateAdminProfiles updateAdminProfiles){
        Boolean isUpdated = adminService.updateProfile(updateAdminProfiles);
        if(!isUpdated){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
            return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<Admin> getDetails(@PathVariable String email){
        Admin admin = adminService.GetAdmin(email);
        if(admin == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(admin);
    }

    @GetMapping("/get/pending/providers")
    public ResponseEntity<List<ServiceProvider>> getNewProviderPendingRequest(){
        List<ServiceProvider> listOfProviders = adminService.getNewServiceProvidersRequest();
        if(listOfProviders == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(listOfProviders);
    }

    /*Approve New Providers Requests*/
    @PutMapping("/status/{id}")
    public ResponseEntity<Boolean> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Boolean updated = adminService.updateProviderStatus(id, status);

        if (!updated) {
            return ResponseEntity.badRequest().body(false);
        }

        return ResponseEntity.ok(true);
    }

    @GetMapping("/get/active-providerLists")
    ResponseEntity<List<ActiveProvidersListForAdmin>> getAllActiveProviders(){
        List<ActiveProvidersListForAdmin> activeProvidersListForAdmins = adminService.activeProvidersList();

        if(activeProvidersListForAdmins.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(activeProvidersListForAdmins);
    }

    @GetMapping("/get/customersLists")
    ResponseEntity<List<CustomersListsForAdmin>> customersListsForAdmins (){
        List<CustomersListsForAdmin> customersLists = adminService.getCustomersLists();

        if(customersLists.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(customersLists);
    }

    @GetMapping("/dashboard/overview-data")
    ResponseEntity<Map<String, Object>> getDashboardOverviewData(){
        Map<String , Object> myData = adminService.getDashboardOverviewData();
        if(myData.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(myData);
    }

    @GetMapping("/get/bookings")
    public ResponseEntity<List<BookingDTOForAdmin>> getAllBookings() {
        List<BookingDTOForAdmin> bookings = adminService.getAllBookings();

        if(bookings.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bookings);
    }
}
