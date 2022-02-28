package com.technivaaran.mapper;

import org.springframework.stereotype.Component;

import com.technivaaran.dto.CategoryMasterDto;
import com.technivaaran.entities.CategoryMaster;
import com.technivaaran.exceptions.EntityConversionExceptioon;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CategoryMasterMapper {

	public CategoryMaster convertToEntity(CategoryMasterDto categoryMasterDto) {
		try {
			return CategoryMaster.builder().categoryCode(categoryMasterDto.getCategoryCode())
					.categoryName(categoryMasterDto.getCategoryName()).build();
		} catch (Exception exception) {
			log.info("Error occured while converting to category entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}
}
