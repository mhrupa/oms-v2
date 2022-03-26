package com.technivaaran.controllers;

import java.util.List;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.services.PartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(AppUrlConstants.BASE_URL)
public class PartController {
    
    @Autowired
    private PartService partService;

    @PostMapping("/parts")
    public ResponseEntity<OmsResponse> savePart(@RequestBody ItemMasterDto itemMasterDto) {
        log.info("creating part started");

        return partService.createPartNo(itemMasterDto);
    }

    @GetMapping("/parts")
    public List<PartEntity> getAllParts() {
        return partService.getAllParts();
    }
}
