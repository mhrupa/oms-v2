package com.technivaaran.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.entities.Customer;
import com.technivaaran.entities.User;
import com.technivaaran.exceptions.EntityConversionExceptioon;
import com.technivaaran.services.UserService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerMapper {

	@Autowired
	UserService userService;

	public Customer convertToEntity(CustomerDto customerDto) {
		try {
			User user = userService.getUserById(customerDto.getUserId());

			return Customer.builder().firstName(customerDto.getFirstName()).lastName(customerDto.getLastName())
					.email(customerDto.getEmail()).contact(customerDto.getContact()).contact1(customerDto.getContact1())
					.add1(customerDto.getAdd1()).add2(customerDto.getAdd2()).city(customerDto.getCity())
					.state(customerDto.getState()).pincode(customerDto.getPincode()).user(user).build();
		} catch (Exception exception) {
			log.info("Error occured while converting to Customer entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}
}
