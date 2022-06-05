package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.technivaaran.entities.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>{

    Optional<CustomerEntity> findByCustomerName(String customerName);
    Optional<CustomerEntity> findByCustomerNameAndLocation(String customerName, String location);

}
