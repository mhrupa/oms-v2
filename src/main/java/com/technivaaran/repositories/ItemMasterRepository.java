package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.ItemMaster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemMasterRepository extends JpaRepository<ItemMaster, Long> {

    Optional<ItemMaster> findByItemName(String itemName);

}
