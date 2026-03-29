package com.quickfix.DTO;

import com.quickfix.Enums.PaymentMethod;
import com.quickfix.Enums.PaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaymentHistory {
    private Long paymentId;
    private String serviceName;
    private String name;
    private LocalDateTime paymentDate;
    private String transactionId;
    private double amount;

    private PaymentMethod method;

    private PaymentStatus paymentStatus;
}
