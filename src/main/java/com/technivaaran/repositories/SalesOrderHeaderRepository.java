package com.technivaaran.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.technivaaran.entities.SalesOrderHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader, Long> {

    List<SalesOrderHeader> findByStatus(String string);

    List<SalesOrderHeader> findByIdIn(List<Long> challanNoList);

    Optional<SalesOrderHeader> findByChallanNo(Long challanNo);

    List<SalesOrderHeader> findByChallanNoIn(List<Long> challanNoList);

    List<SalesOrderHeader> findByOrderDateBetween(LocalDate fromDate, LocalDate toDate);

    @Query(value = "SELECT sh.challan_no, sh.order_date, ph.payment_in_date, c.customer_name, im.item_name, sh.order_amount"
            + " FROM OMS.sales_order_header sh, payment_in_details pd, payment_in_header ph, customer c, stock_header shr, item_master im"
            + " WHERE sh.challan_no = pd.challan_no AND pd.payment_in_header_id = ph.id AND c.id = sh.customer_id"
            + " AND sh.stock_header_id = shr.id AND shr.item_master_id = im.id AND sh.payment_type IN ('bank', 'paytm')"
            + " AND sh.remark IS NOT NULL AND sh.remark != 0 AND sh.remark = :account"
            + " AND MONTH(ph.payment_in_date) = :month AND YEAR(payment_in_date) = :year", nativeQuery = true)
    public List<Object[]> getAccountPaymentData(@Param("account") Long account, @Param("month") int month, @Param("year") int year);

    @Modifying
    @Query(value = "DELETE FROM sales_order_header WHERE order_date <= :tillDate", nativeQuery = true)
    public void deleteLessThanEqualToOrderDate(String tillDate);
    
}
