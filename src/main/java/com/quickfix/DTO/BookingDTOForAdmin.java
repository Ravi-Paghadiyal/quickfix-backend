package com.quickfix.DTO;

import com.quickfix.Enums.BookingStatus;
import com.quickfix.Enums.PaymentMethod;
import com.quickfix.Enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTOForAdmin {
    private Long id;
    private Long providerId;
    private String providerName;
    private String serviceName;
    private String providerPhone;
    private String providerAddress;

    private LocalDateTime bookingDate;

    private Double amount;

    private String bookingStatus;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    // Customers Details
    private String customerName;
    private String customerAddress;
    private String customerPhone;
}
