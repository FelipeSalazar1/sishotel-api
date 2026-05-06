package com.fiap.sishotel.mapper;

import com.fiap.sishotel.dto.reservation.ReservationResponseDTO;
import com.fiap.sishotel.model.Reservation;

public final class ReservationMapper {

    private ReservationMapper() {}

    public static ReservationResponseDTO toResponseDTO(Reservation r) {
        if (r == null) return null;
        return new ReservationResponseDTO(
                r.getId(),
                r.getGuest().getId(),
                r.getGuest().getFullName(),
                r.getRoom().getId(),
                r.getRoom().getNumber(),
                r.getCheckinExpected(),
                r.getCheckoutExpected(),
                r.getCheckinAt(),
                r.getCheckoutAt(),
                r.getStatus(),
                r.getNumGuests(),
                r.getEstimatedAmount(),
                r.getFinalAmount(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }
}
