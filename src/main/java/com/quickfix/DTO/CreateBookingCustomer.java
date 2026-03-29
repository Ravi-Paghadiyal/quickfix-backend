package com.quickfix.DTO;

import com.quickfix.Entitys.Payment;
import com.quickfix.Enums.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreateBookingCustomer {
    private String email;
    private Long provider_id;
    private PaymentMethod method;
}
