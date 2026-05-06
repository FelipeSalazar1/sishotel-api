package com.fiap.sishotel.dto.guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuestRequestDTO(

        @NotBlank(message = "Nome completo é obrigatório")
        @Size(max = 120)
        String fullName,

        @NotBlank(message = "Documento (CPF/passaporte) é obrigatório")
        @Size(max = 30)
        String document,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 120)
        String email,

        @Size(max = 30)
        String phone
) {}
