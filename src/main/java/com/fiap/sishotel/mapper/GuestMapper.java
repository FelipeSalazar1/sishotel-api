package com.fiap.sishotel.mapper;

import com.fiap.sishotel.dto.guest.GuestRequestDTO;
import com.fiap.sishotel.dto.guest.GuestResponseDTO;
import com.fiap.sishotel.model.Guest;

public final class GuestMapper {

    private GuestMapper() {}

    public static GuestResponseDTO toResponseDTO(Guest guest) {
        if (guest == null) return null;
        return new GuestResponseDTO(
                guest.getId(),
                guest.getFullName(),
                guest.getDocument(),
                guest.getEmail(),
                guest.getPhone(),
                guest.getCreatedAt()
        );
    }

    public static Guest toEntity(GuestRequestDTO dto) {
        if (dto == null) return null;
        return Guest.builder()
                .fullName(dto.fullName())
                .document(dto.document())
                .email(dto.email())
                .phone(dto.phone())
                .build();
    }
}
