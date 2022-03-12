package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.ConfigDetailsEntity;
import com.technivaaran.entities.PartEntity;
import com.technivaaran.repositories.ConfigDetailsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfigDetailsService {

    @Autowired
    ConfigDetailsRepository configDetailsRepository;

    @Autowired
    PartService partService;

    public ResponseEntity<OmsResponse> createConfigDetails(ItemMasterDto itemMasterDto) {
        log.info("Create config details service method called {}", itemMasterDto.getConfigDetails());
        if (!ObjectUtils.isEmpty(itemMasterDto.getConfigDetails())) {
            Optional<ConfigDetailsEntity> configDetailsOp = configDetailsRepository
                    .findByConfiguration(itemMasterDto.getConfigDetails());
            if (configDetailsOp.isEmpty()) {
                Optional<PartEntity> partEntityOp = partService.getPartByPartName(itemMasterDto.getPartNo());
                if (!partEntityOp.isEmpty()) {
                    ConfigDetailsEntity configDetailsEntity = ConfigDetailsEntity.builder()
                            .configuration(itemMasterDto.getConfigDetails())
                            .partEntity(partEntityOp.get())
                            .build();
                    configDetailsRepository.save(configDetailsEntity);
                    return new ResponseEntity<>(
                            OmsResponse.builder().message("Configuration created successfully.").build(),
                            HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>(OmsResponse.builder().message("Invalid Part No received.").build(),
                            HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(OmsResponse.builder().message("Configuration already exists").build(),
                        HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(
                    OmsResponse.builder().message("Invalid Configuration details received.").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public List<ConfigDetailsEntity> getAllConfigs() {
        log.info("Get all config details called");
        return configDetailsRepository.findAll();
    }
}
