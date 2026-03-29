package com.quickfix.Entitys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quickfix.Enums.ProviderActiveStatus;
import com.quickfix.Enums.VerifiedStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "service_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true,length = 15)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer experience;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private VerifiedStatus verifiedStatus;

    @Column(name = "provider_active_status",nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderActiveStatus providerActiveStatus = ProviderActiveStatus.ACTIVE;

    @JsonIgnore
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews;

}
