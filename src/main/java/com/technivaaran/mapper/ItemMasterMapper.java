package com.technivaaran.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.technivaaran.AppConstants;
import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.entities.CategoryMaster;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.entities.UnitMaster;
import com.technivaaran.exceptions.EntityConversionExceptioon;
import com.technivaaran.services.CategoryMasterServce;
import com.technivaaran.services.UnitMasterService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ItemMasterMapper {

	@Autowired
	UnitMasterService unitMasterService;

	@Autowired
	CategoryMasterServce categoryMasterServce;

	public ItemMaster convertToEntity(ItemMasterDto itemMasterDto) {
		try {
			UnitMaster unitMaster = unitMasterService.findUnitMasterById(itemMasterDto.getItemUnitId());
			CategoryMaster categoryMaster = categoryMasterServce
					.findCategoryMasterById(itemMasterDto.getItemCategoryId());

			return ItemMaster.builder().itemUnit(unitMaster).itemName(itemMasterDto.getItemName())
					.partNo(itemMasterDto.getPartNo()).itemCategory(categoryMaster)
					.status(StringUtils.hasLength(itemMasterDto.getStatus()) ? itemMasterDto.getStatus()
							: AppConstants.STATUS_ACTIVE)

					.build();
		} catch (Exception exception) {
			log.info("Error occured while converting to Item entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}
}
