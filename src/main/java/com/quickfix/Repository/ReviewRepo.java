package com.quickfix.Repository;

import com.quickfix.Entitys.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review,Long> {
    List<Review> findByCustomerCustomerId(Long customerId);
    List<Review> findByProviderProviderId(Long providerId);
}
