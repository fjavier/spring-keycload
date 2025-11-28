package org.banking.demo.bankingcustomer.repository;

import org.banking.demo.bankingcustomer.model.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
