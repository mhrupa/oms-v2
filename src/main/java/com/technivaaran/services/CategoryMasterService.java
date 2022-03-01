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
	private categoryMasterRepository categoryMasterRepository;

	@Autowired
	private CategoryMasterMapper categoryMasterMapper;

	public CategoryMaster findCategoryMasterById(int categoryId) {
		Optional<CategoryMaster> categoryMasterOp = categoryMasterRepository.findById(categoryId);
		if (categoryMasterOp.isPresent()) {
			return categoryMasterOp.get();
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
		Optional<CategoryMaster> categoryMasterOp = categoryMasterRepository.findById(categoryId);

		if (categoryMasterOp.isPresent()) {
			CategoryMaster categoryMaster = categoryMasterOp.get();
			categoryMaster.setCategoryName(categoryMasterDto.getCategoryName());
			return categoryMasterRepository.save(categoryMaster);
		} else {
			throw new OMSException("Category not found for id : " + categoryId);
		}
	}
}
