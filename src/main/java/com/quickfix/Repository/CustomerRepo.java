package com.quickfix.Repository;

import com.quickfix.Entitys.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo extends JpaRepository<Customer,Long> {
    Customer findByEmail(String email);
    boolean existsByEmail(String email);

    /*Customer findByPhone(String phone);*/
}
