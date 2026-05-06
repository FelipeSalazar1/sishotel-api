package com.fiap.sishotel.controller;

import com.fiap.sishotel.config.OpenApiConfig;
import com.fiap.sishotel.dto.reservation.ReservationRequestDTO;
import com.fiap.sishotel.dto.reservation.ReservationResponseDTO;
import com.fiap.sishotel.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@Tag(name = "Reservas", description = "Ciclo de vida das reservas: criar → check-in → check-out / cancelar")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @Operation(summary = "Listar todas as reservas")
    public List<ReservationResponseDTO> getAll() {
        return reservationService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ReservationResponseDTO getById(@PathVariable String id) {
        return reservationService.getById(id);
    }

    @GetMapping("/guest/{guestId}")
    @Operation(summary = "Listar reservas de um hóspede")
    public List<ReservationResponseDTO> getByGuest(@PathVariable String guestId) {
        return reservationService.getByGuest(guestId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Criar nova reserva")
    public ReservationResponseDTO create(@Valid @RequestBody ReservationRequestDTO dto) {
        return reservationService.create(dto);
    }

    @PatchMapping("/{id}/checkin")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Realizar check-in (CREATED → CHECKED_IN)")
    public ReservationResponseDTO checkIn(@PathVariable String id) {
        return reservationService.checkIn(id);
    }

    @PatchMapping("/{id}/checkout")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Realizar check-out (CHECKED_IN → CHECKED_OUT). Calcula valor final.")
    public ReservationResponseDTO checkOut(@PathVariable String id) {
        return reservationService.checkOut(id);
    }

    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Cancelar reserva (apenas status CREATED)")
    public ReservationResponseDTO cancel(@PathVariable String id) {
        return reservationService.cancel(id);
    }
}
