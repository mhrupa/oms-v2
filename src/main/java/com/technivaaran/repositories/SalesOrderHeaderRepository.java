package com.technivaaran.repositories;

import java.util.List;

import com.technivaaran.entities.SalesOrderHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader, Long> {

    List<SalesOrderHeader> findByStatus(String string);

    List<SalesOrderHeader> findByIdIn(List<Long> challanNoList);

    List<SalesOrderHeader> findByChallanNoIn(List<Long> challanNoList);

}
