package com.quickfix.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerMakeReview {
    private Long customerId;
    private Long providerId;
    private String comment;
    private Long bookingId;
}
