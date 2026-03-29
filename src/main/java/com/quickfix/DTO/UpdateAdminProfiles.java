package com.quickfix.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateAdminProfiles {
    private String name;
    private String email;
    private String phone;
    private String address;
}
