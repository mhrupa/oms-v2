package com.technivaaran.repositories;

import com.technivaaran.entities.SalesOrderDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOderDetailRepository extends JpaRepository<SalesOrderDetails, Long> {

}
