package org.banking.demo.bankingcustomer.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerUpdateDto;
import org.banking.demo.bankingcustomer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @MockitoBean
    private HttpSecurity httpSecurity;
    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
    }
    // ------------------------------------------------------------
    // POST /api/customers
    // ------------------------------------------------------------
    @Test
    @DisplayName("POST /api/customers → debe crear un cliente (201)")
    void createCustomer_ShouldReturn201() throws Exception {

        CustomerDto request = new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989,10,8),"M","NI");
        Mockito.when(customerService.createCustomer(any(CustomerDto.class)))
                .thenReturn(request);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }


    // ------------------------------------------------------------
    // GET /api/customers
    // ------------------------------------------------------------
    @Test
    @DisplayName("GET /api/customers → debe retornar lista (200)")
    @WithMockUser(roles = {"admin_client_role", "user_client_role"})
    void getAllCustomers_ShouldReturn200() throws Exception {

        Mockito.when(customerService.findAll())
                .thenReturn(Arrays.asList(
                        new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989, 10, 8), "M", "NI"),
                        new CustomerDto(1L, "Juan", "Lopez", LocalDate.of(1989, 10, 8), "M", "NI")
                ));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }


    // ------------------------------------------------------------
    // GET /api/customers/{id}
    // ------------------------------------------------------------
    @Test
    @DisplayName("GET /api/customers/{id} → encontrado (200)")
    @WithMockUser(roles = "user_client_role")
    void getById_ShouldReturn200() throws Exception {
        CustomerDto customer = new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989, 10, 8), "M", "NI");
        Mockito.when(customerService.findById(1L))
                .thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("GET /api/customers/{id} → no encontrado (404)")
    @WithMockUser(roles = "user_client_role")
    void getById_ShouldReturn404() throws Exception {
        Mockito.when(customerService.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isNotFound());
    }


    // ------------------------------------------------------------
    // PUT /api/customers/{id}
    // ------------------------------------------------------------
    @Test
    @DisplayName("PUT /api/customers/{id} → actualizado (200)")
    @WithMockUser(roles = "admin_client_role")
    void updateCustomer_ShouldReturn200() throws Exception {

        CustomerDto updateDto = new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989, 10, 8), "M", "NI");
        CustomerDto updated = new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989, 10, 8), "M", "NI");


        Mockito.when(customerService.updateCustomer(eq(1L), any(CustomerUpdateDto.class)))
                .thenReturn(Optional.of(updated));

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} → no encontrado (404)")
    @WithMockUser(roles = "admin_client_role")
    void updateCustomer_ShouldReturn404() throws Exception {

        CustomerDto updateDto = new CustomerDto(1L, "Juan", "Perez", LocalDate.of(1989, 10, 8), "M", "NI");


        Mockito.when(customerService.updateCustomer(eq(1L), any(CustomerUpdateDto.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }


    // ------------------------------------------------------------
    // DELETE /api/customers/{id}
    // ------------------------------------------------------------
    @Test
    @DisplayName("DELETE /api/customers/{id} → eliminado (204)")
    @WithMockUser(roles = "admin_client_role")
    void deleteCustomer_ShouldReturn204() throws Exception {
        Mockito.when(customerService.deleteCustomer(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} → no encontrado (404)")
    @WithMockUser(roles = "admin_client_role")
    void deleteCustomer_ShouldReturn404() throws Exception {
        Mockito.when(customerService.deleteCustomer(1L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNotFound());
    }

}