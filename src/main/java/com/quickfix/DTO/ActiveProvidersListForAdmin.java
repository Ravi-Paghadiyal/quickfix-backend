package com.quickfix.DTO;

import com.quickfix.Enums.ProviderActiveStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActiveProvidersListForAdmin {
    private String name;
    private String serviceName;
    private String phone;
    private String email;
    private Long providerId;
    private String address;
    private Long totalBookings;
    private Long totalRevenue;
    private ProviderActiveStatus providerActiveStatus;
}
