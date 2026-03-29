package com.quickfix.DTO;

import com.quickfix.Enums.BookingStatus;
import com.quickfix.Enums.PaymentMethod;
import com.quickfix.Enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class CustomersBooking {
    private Long providerId;
    private Long bookingId;
    private String serviceName;
    private String name;
    private double amount;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String providerPhone;


}