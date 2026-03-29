package com.quickfix.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomersListsForAdmin {
    private String name;
    private Long customerId;
    private String phone;
    private String email;
    private String address;
    private Long totalBookings;
    private Long totalSpent;
}
