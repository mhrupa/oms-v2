package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.CustomerMapper;
import com.technivaaran.repositories.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerMapper customerMapper;

	public CustomerEntity findCustomerById(long customerId) {
		Optional<CustomerEntity> customerOp = customerRepository.findById(customerId);
		if (customerOp.isPresent()) {
			return customerOp.get();
		} else {
			throw new OMSException("Customer not found for id : " + customerId);
		}
	}

	public List<CustomerEntity> findAllCustomers() {
		return customerRepository.findAll();
	}

	public CustomerEntity saveCustomer(CustomerDto customerDto) {
		try {
			CustomerEntity customer = customerMapper.convertToEntity(customerDto);
			return customerRepository.save(customer);
		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException(
					"Customer already exists: " + integrityViolationException.getCause().getCause().getMessage());
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
