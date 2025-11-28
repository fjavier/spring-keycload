package org.banking.demo.bankingcustomer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerUpdateDto;
import org.banking.demo.bankingcustomer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(
        name = "Customer",
        description = "API para la gestión de clientes (CRUD simulado y validado)")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Crea un nuevo cliente", description = "Delega la creación al CustomerService.")
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos (fallo de validación)")
    @PreAuthorize( "hasRole('admin_client_role')")
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = customerService.createCustomer(customerDto);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }


    @GetMapping
    @Operation(summary = "Obtiene la lista de todos los clientes", description = "Retorna una lista con todos los clientes de la BD.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida")
    @PreAuthorize( "hasRole('admin_client_role') or hasRole('user_client_role')")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        List<CustomerDto> customersDto = customerService.findAll();
        return ResponseEntity.ok(customersDto);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un cliente por ID", description = "Busca y retorna un cliente específico usando su ID.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @PreAuthorize( "hasRole('admin_client_role') or hasRole('user_client_role')")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable Long id) {
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un cliente existente", description = "Modifica los datos de un cliente dado su ID.")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de cliente inválidos (fallo de validación)")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @PreAuthorize( "hasRole('admin_client_role')")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id,
                                                      @RequestBody CustomerUpdateDto updatedCustomerDto) {

        return customerService.updateCustomer(id, updatedCustomerDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un cliente", description = "Borra el registro de un cliente por su ID.")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    @PreAuthorize( "hasRole('admin_client_role')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);

        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
