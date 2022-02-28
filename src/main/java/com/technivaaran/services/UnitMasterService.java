package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.UnitMasterDto;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.UnitMasterMapper;
import com.technivaaran.repositories.UnitMasterRepository;

@Service
public class UnitMasterService {
	
	@Autowired
	UnitMasterRepository unitMasterRepository;
	
	@Autowired
	UnitMasterMapper unitMasterMapper;

	public UnitMaster findUnitMasterById(int unitId) {
		Optional<UnitMaster> unitMasterOp = unitMasterRepository.findById(unitId);
		if (unitMasterOp.isPresent()) {
			return unitMasterOp.get();
		} else {
			throw new OMSException("Unit not found for id : " + unitId);
		}
	}

	public List<UnitMaster> findAllUnits() {
		return unitMasterRepository.findAll();
	}

	public UnitMaster saveUnitMaster(UnitMasterDto unitMasterDto) {
		try {
			UnitMaster unitMaster = unitMasterMapper.convertToEntity(unitMasterDto);
			return unitMasterRepository.save(unitMaster);
		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException(
					"Unit already exists: " + integrityViolationException.getCause().getCause().getMessage());
		}
	}

	public UnitMaster updateUnitMasterById(int unitId, UnitMasterDto unitMasterDto) {
		Optional<UnitMaster> unitMasterOp = unitMasterRepository.findById(unitId);

		if (unitMasterOp.isPresent()) {
			UnitMaster unitMaster = unitMasterOp.get();
			return unitMasterRepository.save(unitMaster);
		} else {
			throw new OMSException("Unit not found for id : " + unitId);
		}
	}
}
