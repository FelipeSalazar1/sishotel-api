package com.fiap.sishotel.dto.room;

import com.fiap.sishotel.model.RoomStatus;
import com.fiap.sishotel.model.RoomType;

import java.math.BigDecimal;

public record RoomResponseDTO(
        String id,
        Integer number,
        RoomType type,
        Integer capacity,
        BigDecimal pricePerNight,
        RoomStatus status
) {}
