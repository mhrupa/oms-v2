package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.technivaaran.entities.ItemMaster;

@Repository
public interface ItemMasterRepository extends JpaRepository<ItemMaster, Long>{

    List<ItemMaster> findByItemName(String itemName);

}
