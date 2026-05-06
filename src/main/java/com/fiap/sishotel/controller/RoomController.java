package com.fiap.sishotel.controller;

import com.fiap.sishotel.config.OpenApiConfig;
import com.fiap.sishotel.dto.room.RoomRequestDTO;
import com.fiap.sishotel.dto.room.RoomResponseDTO;
import com.fiap.sishotel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Quartos", description = "CRUD de quartos e consulta de disponibilidade")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os quartos")
    public List<RoomResponseDTO> getAll() {
        return roomService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar quarto por ID")
    public RoomResponseDTO getById(@PathVariable String id) {
        return roomService.getById(id);
    }

    @GetMapping("/available")
    @Operation(summary = "Listar quartos disponíveis em um período")
    public List<RoomResponseDTO> getAvailable(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkin,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkout) {
        return roomService.getAvailable(checkin, checkout);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Cadastrar novo quarto")
    public RoomResponseDTO create(@Valid @RequestBody RoomRequestDTO dto) {
        return roomService.create(dto);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Atualizar dados do quarto")
    public RoomResponseDTO update(@PathVariable String id, @Valid @RequestBody RoomRequestDTO dto) {
        return roomService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Desativar quarto (exclusão lógica — não pode ter reservas ativas)")
    public RoomResponseDTO deactivate(@PathVariable String id) {
        return roomService.deactivate(id);
    }
}
