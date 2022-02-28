package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.CategoryMasterDto;
import com.technivaaran.entities.CategoryMaster;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.CategoryMasterMapper;
import com.technivaaran.repositories.categoryMasterRepository;

@Service
public class CategoryMasterService {

	@Autowired
	categoryMasterRepository categoryMasterRepository;

	@Autowired
	CategoryMasterMapper categoryMasterMapper;

	public CategoryMaster findCategoryMasterById(int categoryId) {
		Optional<CategoryMaster> categorymasterOp = categoryMasterRepository.findById(categoryId);
		if (categorymasterOp.isPresent()) {
			return categorymasterOp.get();
		} else {
			throw new OMSException("Category not found for id : " + categoryId);
		}
	}

	public List<CategoryMaster> findAllCategories() {
		return categoryMasterRepository.findAll();
	}

	public CategoryMaster saveCategoryMaster(CategoryMasterDto categoryMasterDto) {
		try {
			CategoryMaster categoryMaster = categoryMasterMapper.convertToEntity(categoryMasterDto);
			return categoryMasterRepository.save(categoryMaster);
		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException(
					"Category already exists: " + integrityViolationException.getCause().getCause().getMessage());
		}
	}

	public CategoryMaster updateCategoryMasterById(int categoryId, CategoryMasterDto categoryMasterDto) {
		Optional<CategoryMaster> categorymasterOp = categoryMasterRepository.findById(categoryId);

		if (categorymasterOp.isPresent()) {
			CategoryMaster categoryMaster = categorymasterOp.get();
			categoryMaster.setCategoryName(categoryMasterDto.getCategoryName());
			return categoryMasterRepository.save(categoryMaster);
		} else {
			throw new OMSException("Category not found for id : " + categoryId);
		}
	}
}
