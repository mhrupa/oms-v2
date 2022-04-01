package com.technivaaran.repositories;

import java.util.List;
import java.util.Optional;

import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.entities.VendorEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHeaderRepository extends JpaRepository<StockHeader, Long> {

    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndBuyPrice(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, float buyPrice);

    List<StockHeader> findByClosingQtyGreaterThan(float i);

}
