package com.technivaaran.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.SalesOrderDetails;
import com.technivaaran.entities.SalesOrderHeader;

@Repository
public interface SalesOderDetailRepository extends JpaRepository<SalesOrderDetails, Long> {

    public List<SalesOrderDetails> findBySalesOrderHeader(SalesOrderHeader salesOrderHeader);
}
