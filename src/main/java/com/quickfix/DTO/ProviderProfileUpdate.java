package com.quickfix.DTO;

import com.quickfix.Enums.ProviderActiveStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ProviderProfileUpdate {
    private String name;
    private Integer experience;
    private double amount;
    private String address;
    private String phone;
    private String description;
    private String email;

    private ProviderActiveStatus providerActiveStatus;
}
