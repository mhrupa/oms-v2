package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.BranchMasterDto;
import com.technivaaran.entities.BranchMaster;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.BranchMapper;
import com.technivaaran.repositories.BranchMasterRepository;

@Service
public class BranchMasterService {

	@Autowired
	BranchMasterRepository branchMasterRepository;

	@Autowired
	BranchMapper branchMapper;

	public BranchMaster findbarnchMasterById(int branchId) {
		Optional<BranchMaster> branchMasterOp = branchMasterRepository.findById(branchId);
		if (branchMasterOp.isPresent()) {
			return branchMasterOp.get();
		} else {
			throw new OMSException("Branch not found for id : " + branchId);
		}
	}

	public List<BranchMaster> findAllbarnchMasters() {
		return branchMasterRepository.findAll();
	}

	public BranchMaster saveBranchmaster(BranchMasterDto branchMasterDto) {
		try {
			BranchMaster branchMaster = branchMapper.convertToEntity(branchMasterDto);
			return branchMasterRepository.save(branchMaster);
		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException(
					"Branch already exists: " + integrityViolationException.getCause().getCause().getMessage());
		}
	}

	public BranchMaster updateBranchMasterById(int branchId, BranchMasterDto branchMasterDto) {
		Optional<BranchMaster> branchMasterOp = branchMasterRepository.findById(branchId);

		if (branchMasterOp.isPresent()) {
			BranchMaster branchMaster = branchMasterOp.get();
			branchMaster.setBranchAdd(branchMasterDto.getBranchAdd());
			branchMaster.setHomeBranch(branchMasterDto.isHomeBranch());

			return branchMasterRepository.save(branchMaster);
		} else {
			throw new OMSException("Branch not found for id : " + branchId);
		}
	}

}
