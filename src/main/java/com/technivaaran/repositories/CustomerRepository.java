package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
