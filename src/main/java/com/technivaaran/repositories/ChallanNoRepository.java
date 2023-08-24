package com.technivaaran.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technivaaran.entities.ChallanNoEntity;

@Repository
public interface ChallanNoRepository extends JpaRepository<ChallanNoEntity, Long> {

    @Query("SELECT c FROM ChallanNoEntity c")
    ChallanNoEntity getChallanNo();
}
