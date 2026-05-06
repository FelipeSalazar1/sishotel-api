package com.fiap.sishotel.controller;

import com.fiap.sishotel.config.OpenApiConfig;
import com.fiap.sishotel.dto.guest.GuestRequestDTO;
import com.fiap.sishotel.dto.guest.GuestResponseDTO;
import com.fiap.sishotel.service.GuestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/guests")
@Tag(name = "Hóspedes", description = "CRUD de hóspedes do hotel")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os hóspedes")
    public List<GuestResponseDTO> getAll() {
        return guestService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar hóspede por ID")
    public GuestResponseDTO getById(@PathVariable String id) {
        return guestService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Cadastrar novo hóspede")
    public GuestResponseDTO create(@Valid @RequestBody GuestRequestDTO dto) {
        return guestService.create(dto);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Atualizar dados do hóspede")
    public GuestResponseDTO update(@PathVariable String id, @Valid @RequestBody GuestRequestDTO dto) {
        return guestService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @Operation(summary = "Remover hóspede")
    public void delete(@PathVariable String id) {
        guestService.delete(id);
    }
}
