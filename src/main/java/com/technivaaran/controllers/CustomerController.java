package com.technivaaran.controllers;

import java.util.List;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.CustomerDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.services.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveCustomer(@RequestBody CustomerDto customerDto) {
		log.info("Customer Creation started.");
		customerService.saveCustomer(customerDto);
		log.info("Customer Creation completed.");
		return new ResponseEntity<>(OmsResponse.builder().message("Customer created successfully").build(),
				HttpStatus.CREATED);
	}

	@GetMapping("/customers")
	public List<CustomerEntity> getAllUsers() {
		log.info("Get all Customer is called.");
		return customerService.findAllCustomers();
	}

	@GetMapping("/customers/{customerId}")
	public CustomerEntity getCustomerById(@PathVariable(name = "customerId") long customerId) {
		log.info("Get Customer by Id called");
		return customerService.findCustomerById(customerId);
	}

	@PutMapping(value = "/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateCustomerById(@PathVariable(name = "customerId") long customerId,
			@RequestBody CustomerDto customerDto) {
		log.info("Update Customer by Id called");
		customerService.updateCustomerById(customerId, customerDto);

		return new ResponseEntity<>(OmsResponse.builder().message("Customer updated successfully").build(),
				HttpStatus.OK);
	}
}
