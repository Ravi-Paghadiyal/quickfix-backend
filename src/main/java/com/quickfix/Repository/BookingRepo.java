package com.quickfix.Repository;

import com.quickfix.Entitys.Booking;
import com.quickfix.Enums.BookingStatus;
import com.quickfix.Enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepo extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT b FROM Booking b
    LEFT JOIN FETCH b.customer
    LEFT JOIN FETCH b.payment
    WHERE b.status = 'PENDING'
    AND b.provider.providerId = :providerId
    """)
    List<Booking> findPendingBookingsByProvider(@Param("providerId") Long providerId);

    List<Booking> findByCustomerCustomerId(Long customerId);

    List<Booking> findByProviderProviderIdAndStatusInAndPaymentMethodIn(
            Long providerId,
            List<BookingStatus> statuses,
            List<PaymentMethod> methods
    );

    Booking findByBookingIdAndProviderProviderId(Long bookingId, Long providerId);

    List<Booking> findByProviderProviderId(Long providerId);

}