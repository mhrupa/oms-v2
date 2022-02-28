package com.technivaaran.mapper;

import org.springframework.stereotype.Component;

import com.technivaaran.dto.UnitMasterDto;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.exceptions.EntityConversionExceptioon;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UnitMasterMapper {

	public UnitMaster convertToEntity(UnitMasterDto unitMasterDto) {
		try {
			return UnitMaster.builder().unitName(unitMasterDto.getUnitName()).unitCode(unitMasterDto.getUnitCode())
					.build();
		} catch (Exception exception) {
			log.info("Error occured while converting to Unit entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}

}
