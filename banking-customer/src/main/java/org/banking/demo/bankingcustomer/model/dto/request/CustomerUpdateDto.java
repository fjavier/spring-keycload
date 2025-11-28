package org.banking.demo.bankingcustomer.model.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CustomerUpdateDto(
        String name,
        String lastname,
        LocalDate datebirth,
        String gender,
        String country
) {

}
