package com.technivaaran.controllers;

import java.util.List;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.services.ConfigDetailsService;

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
public class ConfigDetailsController {
    
    @Autowired
    private ConfigDetailsService configDetailsService;

    @PostMapping("/configs")
    public ResponseEntity<OmsResponse> saveItemMaster(@RequestBody ItemMasterDto itemMasterDto) {
        log.info("creating part started");

        return configDetailsService.createConfigDetails(itemMasterDto);
    }

    @GetMapping("/configs")
    public List<ConfigDetailsEntity> getAllParts() {
        return configDetailsService.getAllConfigs();
    }
}
