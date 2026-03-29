package com.quickfix.Entitys;
import com.quickfix.Repository.AdminRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    @Autowired
    private AdminRepo adminRepo;

    @PostConstruct
    public void initAdmin() {

        if(adminRepo.count() == 0){

            Admin admin = new Admin();

            admin.setName("Ravi Paghadiyal");
            admin.setEmail("ravi@gmail.com");
            admin.setPassword("Ravi@123");
            admin.setPhone("9737874452");
            admin.setAddress("Patel Parmananad Chawl,Rakhial Road,Ahmedabad-380023");

            adminRepo.save(admin);
        }
    }
}