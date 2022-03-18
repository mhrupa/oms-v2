package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.VendorEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.repositories.VendorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public List<VendorEntity> getAllVendors() {
        log.info("Get all vendors called in service.");
        return vendorRepository.findAll();
    }

    public ResponseEntity<OmsResponse> createVendor(String vendorName) {
        VendorEntity vendorEntity = VendorEntity.builder()
                .vendorName(vendorName).build();
        try {
            vendorRepository.save(vendorEntity);
            return new ResponseEntity<>(OmsResponse.builder().message("Vendor created successfully.")
                    .data(vendorEntity).build(), HttpStatus.OK);
        } catch (DataIntegrityViolationException integrityViolationException) {
            throw new OMSException("Vendor already exists with name: " + vendorName);
        }
    }

    public Optional<VendorEntity> findById(long vendorId) {
        return vendorRepository.findById(vendorId);
    }
}
