package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.BranchMaster;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMaster, Integer>{

}
