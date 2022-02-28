package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.UnitMaster;

@Repository
public interface UnitMasterRepository extends JpaRepository<UnitMaster, Integer> {

}
