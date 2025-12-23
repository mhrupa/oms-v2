package com.technivaaran.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.CustomerDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.CustomerEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.CustomerMapper;
import com.technivaaran.repositories.CustomerRepository;
import com.technivaaran.utils.JsonUtils;
import com.technivaaran.ws.WsEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private WsEventPublisher wsEventPublisher;

    public Optional<CustomerEntity> findCustomerById(long customerId) {
        return customerRepository.findById(customerId);
    }

    public Optional<CustomerEntity> findCustomerByNameAndLocation(String customerName, String customerLocation) {
        return customerRepository.findByCustomerNameAndLocation(customerName, customerLocation);
    }

    public List<CustomerEntity> findAllCustomers() {
        // return customerRepository.findAll();
        return customerRepository.findAllNonDeletedCustomers();
    }

    public ResponseEntity<OmsResponse> saveCustomer(CustomerDto customerDto) {
        CustomerEntity customer = null;
        Optional<CustomerEntity> customerOp = customerRepository
                .findByCustomerNameAndLocation(customerDto.getCustomerName(), customerDto.getLocation());
        if (customerOp.isEmpty()) {
            customer = customerMapper.convertToEntity(customerDto);
            customer = customerRepository.save(customer);
            log.debug("Customer Creation completed.");

            wsEventPublisher.customerChanged();

            return new ResponseEntity<>(OmsResponse.builder().message("Customer created successfully")
                    .data(customer).build(), HttpStatus.CREATED);
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

    public ResponseEntity<OmsResponse> deleteCustomerById(long customerId) {
        Optional<CustomerEntity> customerOp = customerRepository.findById(customerId);

        if (customerOp.isPresent()) {
            CustomerEntity customer = customerOp.get();

            customer.setDeleted(true);

            customerRepository.save(customer);

            return new ResponseEntity<>(
                    OmsResponse.builder().message("Customer " + customer.getCustomerName() + " deleted successfully")
                            .build(),
                    HttpStatus.OK);

        } else {
            throw new OMSException("Customer not found for id : " + customerId);
        }
    }
}
