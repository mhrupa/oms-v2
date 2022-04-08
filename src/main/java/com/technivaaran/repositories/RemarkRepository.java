package com.technivaaran.repositories;

import com.technivaaran.entities.RemarkEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemarkRepository extends JpaRepository<RemarkEntity, Long> {
    
}
