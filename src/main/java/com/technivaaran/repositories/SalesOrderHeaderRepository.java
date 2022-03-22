package com.technivaaran.repositories;

import com.technivaaran.entities.SalesOrderHeader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOrderHeaderRepository extends JpaRepository<SalesOrderHeader, Long> {

}
