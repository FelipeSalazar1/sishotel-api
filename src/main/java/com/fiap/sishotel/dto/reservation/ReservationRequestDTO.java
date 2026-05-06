package com.fiap.sishotel.dto.reservation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationRequestDTO(

        @NotBlank(message = "ID do hóspede é obrigatório")
        String guestId,

        @NotBlank(message = "ID do quarto é obrigatório")
        String roomId,

        @NotNull(message = "Data de check-in prevista é obrigatória")
        LocalDate checkinExpected,

        @NotNull(message = "Data de check-out prevista é obrigatória")
        LocalDate checkoutExpected,

        @NotNull(message = "Número de hóspedes é obrigatório")
        @Min(value = 1, message = "Deve haver pelo menos 1 hóspede")
        Integer numGuests
) {}
