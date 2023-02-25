package com.technivaaran.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.SalesOrderDetails;
import com.technivaaran.entities.SalesOrderHeader;

@Repository
public interface SalesOderDetailRepository extends JpaRepository<SalesOrderDetails, Long> {

    public List<SalesOrderDetails> findBySalesOrderHeader(SalesOrderHeader salesOrderHeader);
    
    
    @Modifying
    @Query(value = "DELETE FROM sales_order_details WHERE sales_order_header_id "
            + "IN ( SELECT id FROM sales_order_header WHERE order_date <= :tillDate)", nativeQuery = true)
    public void deleteLessThanEqualToTransactionDateTime(String tillDate);
}
