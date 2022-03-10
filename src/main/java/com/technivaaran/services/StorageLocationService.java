package com.technivaaran.services;

import java.util.List;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.StorageLocationEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.repositories.StorageLocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StorageLocationService {

    @Autowired
    StorageLocationRepository storageLocationRepository;

    public ResponseEntity<OmsResponse> createStorageLocation(String storageLocation) {
        StorageLocationEntity storageLocationEntity = StorageLocationEntity.builder()
                .locationName(storageLocation).build();
        try {
            storageLocationRepository.save(storageLocationEntity);
            return new ResponseEntity<>(OmsResponse.builder().message("Storage location created successfully.").build(),
                    HttpStatus.OK);
        } catch (DataIntegrityViolationException integrityViolationException) {
            throw new OMSException("Location already exists with name: " + storageLocation);
        }
    }

    public List<StorageLocationEntity> findAllStorageLocations() {
        return storageLocationRepository.findAll();
    }
}
