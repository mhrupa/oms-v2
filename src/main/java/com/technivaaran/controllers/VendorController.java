package com.technivaaran.controllers;

import java.util.List;
import java.util.Map;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.VendorEntity;
import com.technivaaran.services.VendorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping(AppUrlConstants.BASE_URL)
@RestController
public class VendorController {
    
    @Autowired
    private VendorService vendorService;

    @PostMapping("/vendors")
    public ResponseEntity<OmsResponse> createVendor(@RequestBody Map<String, String> requestData) {
        log.info("create storage location called with data {}", requestData);
        return vendorService.createVendor(requestData.get("vendorName"));
    }

    @GetMapping("/vendors")
    public List<VendorEntity> getAllVendors() {
        log.info("get all storage location called");
        return vendorService.getAllVendors();
    }
}
