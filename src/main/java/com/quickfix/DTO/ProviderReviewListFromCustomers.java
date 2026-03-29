package com.quickfix.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderReviewListFromCustomers {
    private String name;
    private LocalDateTime date;
    private Long customerId;
    private Long reviewId;
    private String comment;
}
