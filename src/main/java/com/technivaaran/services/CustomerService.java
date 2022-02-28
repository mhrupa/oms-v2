package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.entities.Customer;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.CustomerMapper;
import com.technivaaran.repositories.CustomerRepository;

@Service
public class CustomerService {
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	CustomerMapper customerMapper;

	public Customer findCustpmerById(long customerId) {
		Optional<Customer> customerOp = customerRepository.findById(customerId);
		if (customerOp.isPresent()) {
			return customerOp.get();
		} else {
			throw new OMSException("Customer not found for id : " + customerId);
		}
	}

	public List<Customer> findAllCustomers() {
		return customerRepository.findAll();
	}

	public Customer saveCustomer(CustomerDto customerDto) {
		try {
			Customer customer = customerMapper.convertToEntity(customerDto);
			return customerRepository.save(customer);
		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException(
					"Customer already exists: " + integrityViolationException.getCause().getCause().getMessage());
		}
	}

	public Customer updateCustomerById(long customerId, CustomerDto customerDto) {
		Optional<Customer> customerOp = customerRepository.findById(customerId);

		if (customerOp.isPresent()) {
			Customer customer = customerOp.get();
			
			Customer customerFromDto = customerMapper.convertToEntity(customerDto);
			
			customer.setFirstName(customerDto.getFirstName());
			customer.setLastName(customerDto.getLastName());
			customer.setContact(customerFromDto.getContact());
			customer.setContact1(customerFromDto.getContact1());
			customer.setAdd1(customerFromDto.getAdd1());
			customer.setAdd2(customerFromDto.getAdd2());
			customer.setCity(customerFromDto.getCity());
			customer.setState(customerFromDto.getState());
			customer.setPincode(customerFromDto.getPincode());
			customer.setUser(customerFromDto.getUser());

			return customerRepository.save(customer);
		} else {
			throw new OMSException("Customer not found for id : " + customerId);
		}
	}
}
