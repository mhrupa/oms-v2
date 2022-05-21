package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.RemarkEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemarkRepository extends JpaRepository<RemarkEntity, Long> {

    Optional<RemarkEntity> findByRemarkText(String remark);
    
}
