package org.banking.demo.bankingcustomer.model.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CustomerDto(

        Long id,

        @NotEmpty(message = "El nombre no puede ser nulo o vacío")
        String name,

        @NotEmpty(message = "El apellido no puede ser nulo o vacío")
        String lastname,

        @NotNull(message = "La fecha de nacimiento no puede ser nula")
        @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
        LocalDate datebirth,

        @Pattern(regexp = "[MF]", message = "El sexo debe ser 'M' (Masculino) o 'F' (Femenino)")
        @NotEmpty(message = "El sexo no puede ser nulo o vacío")
        String gender, // No se requiere validación específica, se mantiene como String

        @NotBlank(message = "El país no puede ser nulo o vacío")
        String country
) {
}