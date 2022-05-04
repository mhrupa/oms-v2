package com.technivaaran.repositories;

import com.technivaaran.entities.ChallanNoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallanNoRepository extends JpaRepository<ChallanNoEntity, Long> {

    @Query("SELECT c FROM ChallanNoEntity c")
    ChallanNoEntity getChallanNo();
}
