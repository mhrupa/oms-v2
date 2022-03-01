package com.technivaaran.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.dto.OmsResponse;
import com.technivaaran.dto.UnitMasterDto;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.services.UnitMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UnitMasterController {
	@Autowired
	private UnitMasterService unitMasterService;

	@PostMapping(value = "/units", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveUnitMaster(@RequestBody UnitMasterDto unitMasterDto) {
		log.info("Unit creation started.");
		unitMasterService.saveUnitMaster(unitMasterDto);
		log.info("Unit Creation completed.");
		return new ResponseEntity<>(OmsResponse.builder().message("Unit created successfully").build(),
				HttpStatus.CREATED);
	}

	@GetMapping("/units")
	public List<UnitMaster> getAllUnits() {
		log.info("Get all Units is called.");
		return unitMasterService.findAllUnits();
	}

	@GetMapping("/units/{unitId}")
	public UnitMaster getUnitById(@PathVariable(name = "unitId") int unitId) {
		log.info("Get unit by Id called");
		return unitMasterService.findUnitMasterById(unitId);
	}

	@PutMapping(value = "/units/{unitId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateUnitById(@PathVariable(name = "unitId") int unitId,
			@RequestBody UnitMasterDto unitMasterDto) {
		log.info("Update unit by Id called");
		unitMasterService.updateUnitMasterById(unitId, unitMasterDto);
		return new ResponseEntity<>(OmsResponse.builder().message("Unit updated successfully").build(),
				HttpStatus.OK);
	}
}
