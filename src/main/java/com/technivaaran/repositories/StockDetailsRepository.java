package com.technivaaran.repositories;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import com.technivaaran.entities.StockDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDetailsRepository extends JpaRepository<StockDetails, Long> {

        @Query(value = "SELECT SUM(in_qty) in_qty, SUM(out_qty) FROM stock_details s"
                        + " WHERE stock_header_id = :headerId AND transaction_date = :transactionDate", nativeQuery = true)
        Map<String, String> getByStockHeaderAndTransactionDate(@Param(value = "headerId") Long headerId,
                        @Param(value = "transactionDate") LocalDate transactionDate);

        @Query(value = "SELECT * FROM stock_details WHERE stock_header_id = :stockHeaderId"
                        + " AND id = (SELECT MAX(id) FROM stock_details WHERE stock_header_id = :stockHeaderId"
                        + " AND type = 'In')", nativeQuery = true)
        Optional<StockDetails> findLatestStockDetailsByStockHeader(@Param(value = "stockHeaderId") long stockHeaderId);

}
