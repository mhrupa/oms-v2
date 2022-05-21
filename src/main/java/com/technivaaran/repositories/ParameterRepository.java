package com.technivaaran.repositories;

import com.technivaaran.entities.Parameters;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends JpaRepository<Parameters, Integer>{
    
}
