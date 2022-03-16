package com.technivaaran.repositories;

import com.technivaaran.entities.VendorEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<VendorEntity, Long>{
    
}
