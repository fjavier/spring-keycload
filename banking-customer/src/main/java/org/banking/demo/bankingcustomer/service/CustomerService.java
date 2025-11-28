package org.banking.demo.bankingcustomer.service;

import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.dto.request.CustomerUpdateDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDto createCustomer(CustomerDto customerDto);

    List<CustomerDto> findAll();

    Optional<CustomerDto> findById(Long id);

    Optional<CustomerDto> updateCustomer(Long id, CustomerUpdateDto customerDto);

    boolean deleteCustomer(Long id);
}
