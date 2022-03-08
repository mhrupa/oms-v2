package com.technivaaran.repositories;

import java.util.Optional;

import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.StockHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHeaderRepository extends JpaRepository<StockHeader, Long> {

    Optional<StockHeader> findByLocationAndItemMaster(String boxNo, ItemMaster item);

}
