package com.quickfix.Repository;

import com.quickfix.Entitys.ServiceProvider;
import com.quickfix.Enums.ProviderActiveStatus;
import com.quickfix.Enums.VerifiedStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceProviderRepo extends JpaRepository<ServiceProvider,Long> {
    ServiceProvider findByEmail(String email);
    boolean existsByEmail(String email);
    List<ServiceProvider> findByVerifiedStatus(VerifiedStatus verifiedStatus);
    List<ServiceProvider> findByVerifiedStatusAndProviderActiveStatus(VerifiedStatus verifiedStatus, ProviderActiveStatus providerActiveStatus);
}
