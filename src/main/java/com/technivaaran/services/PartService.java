package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.repositories.PartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PartService {

    @Autowired
    PartRepository partRepository;

    @Autowired
    ItemMasterService itemMasterService;

    public ResponseEntity<OmsResponse> createPartNo(ItemMasterDto itemMasterDto) {
        log.info("Creating part no in service for {}", itemMasterDto.getPartNo());

        Optional<ItemMaster> itemMasterOp = itemMasterService.findByItemName(itemMasterDto.getItemName());

        if (itemMasterOp.isPresent()) {
            Optional<PartEntity> partOp = partRepository.findByPartNoAndItemMaster(itemMasterDto.getPartNo(),
                    itemMasterOp.get());
            if (partOp.isEmpty()) {
                PartEntity part = PartEntity.builder()
                        .partNo(itemMasterDto.getPartNo()).itemMaster(itemMasterOp.get()).build();
                partRepository.save(part);
                return new ResponseEntity<>(OmsResponse.builder().message("Part created successfully.").build(),
                        HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(OmsResponse.builder().message("Part No already exists.").build(),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(OmsResponse.builder().message("Model not present in DB.").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public List<PartEntity> getAllParts() {
        log.info("Get all parts called");
        return partRepository.findAll();
    }
}
