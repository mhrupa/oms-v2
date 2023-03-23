package com.technivaaran.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.entities.StockHeader;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.entities.VendorEntity;

@Repository
public interface StockHeaderRepository extends JpaRepository<StockHeader, Long> {

    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndBuyPrice(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, float buyPrice);
    
    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendor(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor);

    @Query(value = "SELECT sh FROM StockHeader sh JOIN FETCH sh.storageLocation sl JOIN FETCH sh.partEntity pe"
    + " JOIN FETCH sh.configDetailsEntity cd JOIN FETCH sh.vendor sv JOIN FETCH sh.itemMaster WHERE sh.rowDelStatus = 0")        
    List<StockHeader> findByClosingQtyGreaterThan(float i);

    @Query(value="SELECT SUM(buy_price * closing_qty) priceSum, SUM(closing_qty) rowCount FROM stock_header"
            + " WHERE closing_qty > 0 AND config_detail_id = :configDetailsId"
            + " AND item_master_id = :itemMasterId AND part_id = :partId", nativeQuery = true)
    Map<String, Object> getSumAndRowCountForBuyPrice(Long configDetailsId, Long itemMasterId, Long partId);
    
    @Modifying
    @Query(value="UPDATE stock_header SET buy_price = :buyPrice, sell_price = :sellPrice WHERE config_detail_id = :configDetailsId"
            + " AND item_master_id = :itemMasterId AND part_id = :partId", nativeQuery = true)
    void updateAverageBuyAndSellPrice(float buyPrice, float sellPrice, Long configDetailsId, Long itemMasterId, Long partId);
    
}