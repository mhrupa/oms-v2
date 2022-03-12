package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartRepository extends JpaRepository<PartEntity, Long> {

    Optional<PartEntity> findByPartNoAndItemMaster(String partNo, ItemMaster itemMaster);

    Optional<PartEntity> findByPartNo(String partNo);
    
}
