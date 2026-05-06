package com.fiap.sishotel.dto.reservation;

import com.fiap.sishotel.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponseDTO(
        String id,
        String guestId,
        String guestName,
        String roomId,
        Integer roomNumber,
        LocalDate checkinExpected,
        LocalDate checkoutExpected,
        LocalDateTime checkinAt,
        LocalDateTime checkoutAt,
        ReservationStatus status,
        Integer numGuests,
        BigDecimal estimatedAmount,
        BigDecimal finalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
