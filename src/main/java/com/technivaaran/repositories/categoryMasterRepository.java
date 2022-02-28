package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.CategoryMaster;

@Repository
public interface categoryMasterRepository extends JpaRepository<CategoryMaster, Integer>{

}
