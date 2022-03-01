package com.technivaaran.mapper;

import com.technivaaran.AppConstants;
import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.exceptions.EntityConversionException;
import com.technivaaran.services.UnitMasterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemMasterMapper {

	@Autowired
	UnitMasterService unitMasterService;

	public ItemMaster convertToEntity(ItemMasterDto itemMasterDto) {
		try {
			UnitMaster unitMaster = unitMasterService.findUnitMasterById(itemMasterDto.getItemUnitId());
			return ItemMaster.builder().itemUnit(unitMaster).itemName(itemMasterDto.getItemName())
					.partNo(itemMasterDto.getPartNo())
					.status(StringUtils.hasLength(itemMasterDto.getStatus()) ? itemMasterDto.getStatus()
							: AppConstants.STATUS_ACTIVE)

					.build();
		} catch (Exception exception) {
			log.info("Error occurred while converting to Item entity");
			throw new EntityConversionException(exception.getMessage(), exception);
		}
	}
}
