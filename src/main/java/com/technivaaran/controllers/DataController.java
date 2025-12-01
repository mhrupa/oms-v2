package com.technivaaran.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.services.DataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(AppUrlConstants.BASE_URL)
public class DataController {

  @Autowired
  private DataService dataService;

  @PostMapping(value = "/cleanUpData/{tillDate}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OmsResponse> cleanUpData(@PathVariable(name = "tillDate") String tillDate) {

    log.info("Data clean up started till Date {}", tillDate);

    return dataService.cleanUpData(tillDate);
  }

  @PostMapping("/backUpData")
  public ResponseEntity<OmsResponse> backUpData() {
    log.info("Data back up started");
    return new ResponseEntity<>(OmsResponse.builder().message("Data backed up successfully").build(),
            HttpStatus.CREATED);
  }
}
