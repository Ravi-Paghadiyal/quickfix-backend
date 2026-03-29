package com.quickfix.Repository;

import com.quickfix.Entitys.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepo extends JpaRepository<Admin,Long> {
    Admin findByEmail(String email);
}
