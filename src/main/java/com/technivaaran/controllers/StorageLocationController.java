package com.technivaaran.controllers;

import java.util.List;
import java.util.Map;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.services.StorageLocationService;

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
public class StorageLocationController {

    @Autowired
    StorageLocationService storageLocationService;

    @PostMapping("/storageLocations")
    public ResponseEntity<OmsResponse> createStorageLocation(@RequestBody Map<String, String> requestData) {
        log.info("create storage location called with data {}", requestData);
        return storageLocationService.createStorageLocation(requestData.get("storageLocation"));
    }

    @GetMapping("/storageLocations")
    public List<StorageLocationEntity> getAllStorageLocations() {
        log.info("get all storage location called");
        return storageLocationService.findAllStorageLocations();
    }
}
