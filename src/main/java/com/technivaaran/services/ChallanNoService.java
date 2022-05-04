package com.technivaaran.services;

import com.technivaaran.entities.ChallanNoEntity;
import com.technivaaran.repositories.ChallanNoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChallanNoService {
    
    @Autowired
    private ChallanNoRepository challanNoRepository;


    public synchronized long getMaxChallanNo() {
        ChallanNoEntity challanNoEntity = challanNoRepository.getChallanNo();

        challanNoEntity.setChallanNo(challanNoEntity.getChallanNo() + 1);
        challanNoEntity = challanNoRepository.save(challanNoEntity);
        return challanNoEntity.getChallanNo();
    }
}
