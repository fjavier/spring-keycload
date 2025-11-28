package org.banking.demo.bankingcustomer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.banking.demo.bankingcustomer.mapper.CustomerMapper;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerUpdateDto;
import org.banking.demo.bankingcustomer.model.entity.CustomerEntity;
import org.banking.demo.bankingcustomer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService
{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        CustomerEntity entityToSave = customerMapper.toEntity(customerDto);
        CustomerEntity savedEntity = customerRepository.save(entityToSave);
        return customerMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public List<CustomerDto> findAll() {
        // Obtener todas las entidades y mapearlas a DTOs
        return customerRepository.findAll().stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Optional<CustomerDto> findById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toDto);
    }

    @Override
    @Transactional
    public Optional<CustomerDto> updateCustomer(Long id, CustomerUpdateDto customerDto) {
        return customerRepository.findById(id).map(existingEntity -> {

            String newName = customerDto.name();
            if (newName != null && !newName.isBlank()) {
                existingEntity.setName(newName);
            }

            String newLastname = customerDto.lastname();
            if (newLastname != null && !newLastname.isBlank()) {
                existingEntity.setLastname(newLastname);
            }

            String newGender = customerDto.gender();
            if (newGender != null && !newGender.isBlank()) {
                existingEntity.setGender(newGender);
            }

            LocalDate newDatebirth = customerDto.datebirth();
            if (newDatebirth != null) {
                existingEntity.setDatebirth(newDatebirth);
            }

            String newCountry = customerDto.country();
            if (newCountry != null && !newCountry.isBlank()) {
                existingEntity.setCountry(newCountry);
            }

            CustomerEntity updatedEntity = customerRepository.save(existingEntity);

            return customerMapper.toDto(updatedEntity);
        });
    }


    @Override
    @Transactional
    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}