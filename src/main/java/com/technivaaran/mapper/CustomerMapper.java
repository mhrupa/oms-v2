package com.technivaaran.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.entities.User;
import com.technivaaran.exceptions.EntityConversionException;
import com.technivaaran.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerMapper {

    @Autowired
    UserService userService;

    public CustomerEntity convertToEntity(CustomerDto customerDto) {
        try {
            User user = userService.getUserById(customerDto.getUserId());
            return CustomerEntity.builder().customerName(customerDto.getCustomerName())
                    .email(customerDto.getEmail()).contact(customerDto.getContact()).location(customerDto.getLocation())
                    .user(user).build();
        } catch (Exception exception) {
            log.info("Error occurred while converting to Customer entity");
            throw new EntityConversionException(exception.getMessage(), exception);
        }
    }
}
