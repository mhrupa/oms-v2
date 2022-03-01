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

import com.technivaaran.dto.BranchMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.BranchMaster;
import com.technivaaran.services.BranchMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BranchMasterController {

	@Autowired
	private BranchMasterService branchMasterService;

	@GetMapping("/branches")
	public List<BranchMaster> getAllBranches() {
		log.info("Get all branches is called.");
		return branchMasterService.findAllBranchMasters();
	}

	@GetMapping("/branches/{branchId}")
	public BranchMaster getBranchById(@PathVariable(value = "branchId", required = true) int branchId) {
		log.info("Get branch by id is called.");
		return branchMasterService.findBranchMasterById(branchId);
	}

	@PostMapping(value = "/branches", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveUser(@RequestBody BranchMasterDto branchMasterDto) {
		log.info("Branch Creation started.");
		branchMasterService.saveBranchMaster(branchMasterDto);
		log.info("Branch Creation completed.");
		return new ResponseEntity<>(OmsResponse.builder().message("Branch created successfully").build(),
				HttpStatus.CREATED);
	}

	@PutMapping(value = "/branches/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateUserById(@PathVariable(name = "branchId") int branchId,
			@RequestBody BranchMasterDto branchMasterDto) {
		log.info("Update Branch by Id called");
		branchMasterService.updateBranchMasterById(branchId, branchMasterDto);

		return new ResponseEntity<>(OmsResponse.builder().message("Branch updated successfully").build(),
				HttpStatus.OK);
	}
}
