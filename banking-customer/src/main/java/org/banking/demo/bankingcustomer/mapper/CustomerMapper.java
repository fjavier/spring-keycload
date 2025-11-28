package org.banking.demo.bankingcustomer.mapper;



import org.banking.demo.bankingcustomer.model.dto.request.CustomerDto;
import org.banking.demo.bankingcustomer.model.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    @Mapping(target = "id", ignore = true)
    CustomerEntity toEntity(CustomerDto customerDto);

    CustomerDto toDto(CustomerEntity customerEntity);

}
