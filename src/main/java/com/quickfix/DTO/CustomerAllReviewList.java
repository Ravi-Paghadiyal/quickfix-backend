package com.quickfix.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAllReviewList {
    private String name;
    private String serviceName;
    private Long providerId;
    private String comment;
    private LocalDateTime date;
    private Long bookingId;

}
