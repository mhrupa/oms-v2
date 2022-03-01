package com.technivaaran.repositories;

import com.technivaaran.entities.StockHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockHeaderRepository extends JpaRepository<StockHeader, Long> {

}
