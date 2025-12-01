package com.technivaaran.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.CustomerEntity;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>{

    Optional<CustomerEntity> findByCustomerName(String customerName);
    Optional<CustomerEntity> findByCustomerNameAndLocation(String customerName, String location);
    
    @Query(value = "SELECT c FROM CustomerEntity c WHERE c.isDeleted = false ORDER BY c.customerName")
    List<CustomerEntity> findAllNonDeletedCustomers();

}
