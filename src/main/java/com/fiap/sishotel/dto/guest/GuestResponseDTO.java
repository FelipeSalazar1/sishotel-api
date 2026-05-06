package com.fiap.sishotel.dto.guest;

import java.time.LocalDateTime;

public record GuestResponseDTO(
        String id,
        String fullName,
        String document,
        String email,
        String phone,
        LocalDateTime createdAt
) {}
