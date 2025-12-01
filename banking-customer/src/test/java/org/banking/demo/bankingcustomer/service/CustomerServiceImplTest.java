package org.banking.demo.bankingcustomer.service;

import org.banking.demo.bankingcustomer.mapper.CustomerMapper;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerUpdateDto;
import org.banking.demo.bankingcustomer.model.entity.CustomerEntity;
import org.banking.demo.bankingcustomer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    private CustomerEntity customerEntity;
    private CustomerDto customerDto;
    private final Long CUSTOMER_ID = 1L;

    @BeforeEach
    void setUp() {
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        customerEntity = new CustomerEntity(CUSTOMER_ID, "Juan", "Perez", dateOfBirth, "M", "NI");
        customerDto = new CustomerDto(CUSTOMER_ID, "Juan", "Perez", dateOfBirth, "M", "NI");
    }

    // ------------------------------------------------------------
    // Test: createCustomer
    // ------------------------------------------------------------
    @Test
    @DisplayName("createCustomer -> Debe guardar una nueva entidad y retornar el DTO mapeado")
    void createCustomer_ShouldSaveAndReturnDto() {
        when(customerMapper.toEntity(any(CustomerDto.class))).thenReturn(customerEntity);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        when(customerMapper.toDto(any(CustomerEntity.class))).thenReturn(customerDto);

        CustomerDto result = customerService.createCustomer(customerDto);
        assertNotNull(result);
        assertEquals(customerDto.id(), result.id());

        verify(customerMapper, times(1)).toEntity(customerDto);
        verify(customerRepository, times(1)).save(customerEntity);
    }

    // ------------------------------------------------------------
    // Test: findAll
    // ------------------------------------------------------------
    @Test
    @DisplayName("findAll -> Debe retornar una lista de DTOs cuando hay entidades")
    void findAll_ShouldReturnListOfDtos() {
        List<CustomerEntity> entityList = Arrays.asList(customerEntity, new CustomerEntity());

        when(customerRepository.findAll()).thenReturn(entityList);
        when(customerMapper.toDto(any(CustomerEntity.class))).thenReturn(customerDto);

        List<CustomerDto> result = customerService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(entityList.size(), result.size());

        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, times(entityList.size())).toDto(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("findAll -> Debe retornar una lista vacía cuando no hay entidades")
    void findAll_ShouldReturnEmptyList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<CustomerDto> result = customerService.findAll();
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
        verify(customerMapper, never()).toDto(any(CustomerEntity.class));
    }

    @Test
    @DisplayName("findById -> Debe retornar Optional con DTO si la entidad es encontrada")
    void findById_ShouldReturnOptionalWithDto() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDto(customerEntity)).thenReturn(customerDto);

        Optional<CustomerDto> result = customerService.findById(CUSTOMER_ID);
        assertTrue(result.isPresent());
        assertEquals(customerDto.id(), result.get().id());
    }

    @Test
    @DisplayName("findById -> Debe retornar Optional vacío si la entidad no es encontrada")
    void findById_ShouldReturnEmptyOptional() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        Optional<CustomerDto> result = customerService.findById(CUSTOMER_ID);
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
        verify(customerMapper, never()).toDto(any());
    }


    @Test
    @DisplayName("updateCustomer -> Debe actualizar solo los campos presentes (no nulos) y preservar los demás")
    void updateCustomer_ShouldUpdatePresentFields() {

        CustomerUpdateDto updateDto = new CustomerUpdateDto(
                "Carlos",
                null,
                LocalDate.of(2000, 5, 5),
                "F",
                "PE"
        );

        // Entidad original encontrada en la base de datos
        CustomerEntity originalEntity = new CustomerEntity(CUSTOMER_ID, "Juan", "Perez",
                LocalDate.of(1990, 1, 1), "M", "NI");

        // Entidad esperada DESPUÉS de aplicar las actualizaciones del servicio:
        CustomerEntity expectedUpdatedEntity = new CustomerEntity(
                CUSTOMER_ID,
                "Carlos",
                "Perez",
                LocalDate.of(2000, 5, 5), // Cambiado
                "F",
                "PE"
        );

        // DTO de salida esperado
        CustomerDto updatedDto = new CustomerDto(
                CUSTOMER_ID,
                "Carlos",
                "Perez",
                LocalDate.of(2000, 5, 5),
                "F",
                "PE"
        );

        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(originalEntity));
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(expectedUpdatedEntity);
        when(customerMapper.toDto(expectedUpdatedEntity)).thenReturn(updatedDto);

        Optional<CustomerDto> result = customerService.updateCustomer(CUSTOMER_ID, updateDto);

        assertTrue(result.isPresent());
        CustomerDto finalDto = result.get();
        assertEquals("Carlos", finalDto.name(), "El nombre debe ser actualizado.");
        assertEquals("Perez", finalDto.lastname(), "El apellido debe ser el original (no se envió cambio).");
        assertEquals("F", finalDto.gender(), "El género debe ser actualizado.");
        assertEquals("PE", finalDto.country(), "El país debe ser actualizado.");
        assertEquals(LocalDate.of(2000, 5, 5), finalDto.datebirth(), "La fecha de nacimiento debe ser actualizada.");

        verify(customerRepository, times(1)).save(argThat(entity ->
                entity.getName().equals("Carlos") &&
                        entity.getLastname().equals("Perez") && // Campo preservado (era null en el DTO)
                        entity.getGender().equals("F")
        ));
    }

    @Test
    @DisplayName("updateCustomer -> Debe retornar Optional vacío si no se encuentra la entidad")
    void updateCustomer_ShouldReturnEmptyOptionalIfNotFound() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());
        Optional<CustomerDto> result = customerService.updateCustomer(CUSTOMER_ID, new CustomerUpdateDto(null, null, null, null, null));
        assertTrue(result.isEmpty());
        verify(customerRepository, never()).save(any());
        verify(customerMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("deleteCustomer -> Debe retornar true y llamar a deleteById si la entidad existe")
    void deleteCustomer_ShouldReturnTrueAndCallDelete() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(CUSTOMER_ID);
        boolean result = customerService.deleteCustomer(CUSTOMER_ID);
        assertTrue(result);
        verify(customerRepository, times(1)).existsById(CUSTOMER_ID);
        verify(customerRepository, times(1)).deleteById(CUSTOMER_ID);
    }

    @Test
    @DisplayName("deleteCustomer -> Debe retornar false si la entidad no existe")
    void deleteCustomer_ShouldReturnFalseIfNotFound() {
        when(customerRepository.existsById(CUSTOMER_ID)).thenReturn(false);

        boolean result = customerService.deleteCustomer(CUSTOMER_ID);

        assertFalse(result);
        verify(customerRepository, times(1)).existsById(CUSTOMER_ID);
        verify(customerRepository, never()).deleteById(any());
    }
}