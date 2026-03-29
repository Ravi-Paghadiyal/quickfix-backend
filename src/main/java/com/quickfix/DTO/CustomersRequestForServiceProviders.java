package com.quickfix.DTO;

import com.quickfix.Enums.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumeratedValue;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomersRequestForServiceProviders {

    private Long bookingId;
    private String name;
    private String address;
    private LocalDateTime date;
    private String status;
    private PaymentMethod method;

}
