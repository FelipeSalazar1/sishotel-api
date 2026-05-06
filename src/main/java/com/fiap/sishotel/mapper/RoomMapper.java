package com.fiap.sishotel.mapper;

import com.fiap.sishotel.dto.room.RoomRequestDTO;
import com.fiap.sishotel.dto.room.RoomResponseDTO;
import com.fiap.sishotel.model.Room;

public final class RoomMapper {

    private RoomMapper() {}

    public static RoomResponseDTO toResponseDTO(Room room) {
        if (room == null) return null;
        return new RoomResponseDTO(
                room.getId(),
                room.getNumber(),
                room.getType(),
                room.getCapacity(),
                room.getPricePerNight(),
                room.getStatus()
        );
    }

    public static Room toEntity(RoomRequestDTO dto) {
        if (dto == null) return null;
        return Room.builder()
                .number(dto.number())
                .type(dto.type())
                .capacity(dto.capacity())
                .pricePerNight(dto.pricePerNight())
                .build();
    }
}
