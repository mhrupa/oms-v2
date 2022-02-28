package com.technivaaran.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.technivaaran.AppConstants;
import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.entities.CategoryMaster;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.exceptions.EntityConversionException;
import com.technivaaran.services.CategoryMasterService;
import com.technivaaran.services.UnitMasterService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemMasterMapper {

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	CategoryMasterService categoryMasterService;

	public ItemMaster convertToEntity(ItemMasterDto itemMasterDto) {
		try {
			UnitMaster unitMaster = unitMasterService.findUnitMasterById(itemMasterDto.getItemUnitId());
			CategoryMaster categoryMaster = categoryMasterService
					.findCategoryMasterById(itemMasterDto.getItemCategoryId());

			return ItemMaster.builder().itemUnit(unitMaster).itemName(itemMasterDto.getItemName())
					.partNo(itemMasterDto.getPartNo()).itemCategory(categoryMaster)
					.status(StringUtils.hasLength(itemMasterDto.getStatus()) ? itemMasterDto.getStatus()
							: AppConstants.STATUS_ACTIVE)

					.build();
		} catch (Exception exception) {
			log.info("Error occurred while converting to Item entity");
			throw new EntityConversionException(exception.getMessage(), exception);
		}
	}
}
