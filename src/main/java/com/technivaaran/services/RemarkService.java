package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.RemarkEntity;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.repositories.RemarkRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RemarkService {
    @Autowired
    private RemarkRepository remarkRepository;

    public ResponseEntity<OmsResponse> createRemark(String remark) {
        RemarkEntity remarkEntity = RemarkEntity.builder()
                .remarkText(remark).build();
        try {
            remarkEntity = remarkRepository.save(remarkEntity);
            return new ResponseEntity<>(OmsResponse.builder().message("Remark created successfully.")
                    .data(remarkEntity).build(), HttpStatus.OK);
        } catch (DataIntegrityViolationException integrityViolationException) {
            throw new OMSException("Remark already exists: " + remark);
        }
    }

    public List<RemarkEntity> findAllRemarks() {
        log.info("find all remark method called in service");
        return remarkRepository.findAll();

    }

    public Optional<RemarkEntity> findRemarkById(long remarkId) {
        log.info("find remark by id method called in service");
        return remarkRepository.findById(remarkId);

    }

    public Optional<RemarkEntity> findRemarkByRemarkText(String remark) {
        log.info("find remark by remarText method called in service");
        return remarkRepository.findByRemarkText(remark);
    }
}
