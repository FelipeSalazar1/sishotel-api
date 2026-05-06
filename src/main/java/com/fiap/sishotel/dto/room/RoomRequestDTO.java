package com.fiap.sishotel.dto.room;

import com.fiap.sishotel.model.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RoomRequestDTO(

        @NotNull(message = "Número do quarto é obrigatório")
        @Positive(message = "Número deve ser positivo")
        Integer number,

        @NotNull(message = "Tipo do quarto é obrigatório (STANDARD, DELUXE, SUITE)")
        RoomType type,

        @NotNull(message = "Capacidade é obrigatória")
        @Positive(message = "Capacidade deve ser maior que zero")
        Integer capacity,

        @NotNull(message = "Preço por diária é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        BigDecimal pricePerNight
) {}
