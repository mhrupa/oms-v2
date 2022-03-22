package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.CustomerMapper;
import com.technivaaran.repositories.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerMapper customerMapper;

	public Optional<CustomerEntity> findCustomerById(long customerId) {
		return customerRepository.findById(customerId);
	}

	public List<CustomerEntity> findAllCustomers() {
		return customerRepository.findAll();
	}

	public ResponseEntity<OmsResponse> saveCustomer(CustomerDto customerDto) {
		CustomerEntity customer = null;
		Optional<CustomerEntity> customerOp = customerRepository.findByCustomerName(customerDto.getCustomerName());
		if (customerOp.isEmpty()) {
			customer = customerMapper.convertToEntity(customerDto);
			customer = customerRepository.save(customer);
			log.info("Customer Creation completed.");
			return new ResponseEntity<>(OmsResponse.builder().message("Customer created successfully")
					.data(customer).build(),
					HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(OmsResponse.builder().message("Customer already exists.")
					.build(), HttpStatus.BAD_REQUEST);
		}
	}

	public CustomerEntity updateCustomerById(long customerId, CustomerDto customerDto) {
		Optional<CustomerEntity> customerOp = customerRepository.findById(customerId);

		if (customerOp.isPresent()) {
			CustomerEntity customer = customerOp.get();

			CustomerEntity customerFromDto = customerMapper.convertToEntity(customerDto);

			customer.setCustomerName(customerDto.getCustomerName());
			customer.setEmail(customerDto.getEmail());
			customer.setContact(customerFromDto.getContact());
			customer.setLocation(customerDto.getLocation());
			customer.setUser(customerFromDto.getUser());

			return customerRepository.save(customer);
		} else {
			throw new OMSException("Customer not found for id : " + customerId);
		}
	}
}
