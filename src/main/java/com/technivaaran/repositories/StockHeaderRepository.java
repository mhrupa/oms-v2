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
    
    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndDetails(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, String details);
    
    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndDetailsAndRowDelStatus(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, String details, boolean rowDelStatus);
    
    Optional<StockHeader> findByStorageLocationAndItemMasterAndPartEntityAndConfigDetailsEntityAndVendorAndDetailsAndRowDelStatusAndRemark(
            StorageLocationEntity storageLocationEntity, ItemMaster itemMaster, PartEntity partEntity,
            ConfigDetailsEntity configDetailsEntity, VendorEntity vendor, String details, boolean rowDelStatus,
            String remark);

        @Query(value = "SELECT sh FROM StockHeader sh JOIN FETCH sh.storageLocation sl JOIN FETCH sh.partEntity pe"
        + " JOIN FETCH sh.configDetailsEntity cd JOIN FETCH sh.vendor sv JOIN FETCH sh.itemMaster WHERE sh.rowDelStatus = false AND sh.closingQty > :closingQty")        
        List<StockHeader> findByClosingQtyGreaterThan(float closingQty);
    
    
    @Query(value = "SELECT sh.id id, sl.location_name box, sl.id boxId, im.item_name model, im.id modelId,"
            + " pd.part_no part, pd.id partId, cd.configuration configuration, cd.id configurationId,"
            + " sh.details details, sh.id detailsId, sh.closing_qty qty, v.vendor_name vendor, v.id vendorId,"
            + " sh.buy_price buyPrice, sh.sell_price sellPrice, sh.stock_detail_id stockDetailsId,"
            + " sh.remark remarkText, sh.remark_id remarkId, 'Normal' stockTransactionType"
            + " FROM stock_header sh, storage_location sl, item_master im, part_details pd, config_details cd, vendors v"
            + " WHERE sh.location_id = sl.id AND sh.item_master_id = im.id AND sh.part_id = pd.id"
            + " AND sh.config_detail_id = cd.id AND sh.vendor_id = v.id AND sh.row_del_status = 0", nativeQuery = true)  
    List<String[]> getInventoryData();

    @Query(value = "SELECT SUM(buy_price * closing_qty) priceSum, SUM(closing_qty) rowCount FROM stock_header"
            + " WHERE closing_qty > 0 AND config_detail_id = :configDetailsId AND item_master_id = :itemMasterId"
            + " AND part_id = :partId AND details = :details AND remark = :remark", nativeQuery = true)
    Map<String, Object> getSumAndRowCountForBuyPrice(Long configDetailsId, Long itemMasterId, Long partId,
            String details, String remark);
    
    @Query(value = "SELECT SUM(buy_price * closing_qty)/SUM(closing_qty) avgPrice FROM stock_header"
            + " WHERE closing_qty > 0 AND config_detail_id = :configDetailsId AND item_master_id = :itemMasterId"
            + " AND part_id = :partId AND details = :details AND remark = :remark", nativeQuery = true)
    double getAveragePriceForConfig(Long configDetailsId, Long itemMasterId, Long partId, String details,
            String remark);
    
    @Modifying
    @Query(value = "UPDATE stock_header SET buy_price = :buyPrice, sell_price = :sellPrice"
            + " WHERE config_detail_id = :configDetailsId AND item_master_id = :itemMasterId"
            + " AND part_id = :partId AND details = :details AND remark = :remark", nativeQuery = true)
    void updateAverageBuyAndSellPrice(float buyPrice, float sellPrice, Long configDetailsId, Long itemMasterId,
            Long partId, String details, String remark);
    
}