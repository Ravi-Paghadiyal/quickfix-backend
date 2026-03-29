package com.quickfix.Repository;

import com.quickfix.Entitys.Payment;
import com.quickfix.Enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepo extends JpaRepository<Payment,Long> {
    List<Payment> findByBookingProviderProviderId(Long providerId);
    Payment findByBookingBookingId(Long bookingId);
}
