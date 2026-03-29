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
public class AcceptedProviderBookings {
    private Long bookingId;
    private String name;
    private LocalDateTime bookingDate;
    private PaymentMethod paymentMethod;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private double amount;
}
