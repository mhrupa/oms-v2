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

import com.technivaaran.dto.CategoryMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.CategoryMaster;
import com.technivaaran.services.CategoryMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CategoryMasterController {

	@Autowired
	CategoryMasterService categoryMasterService;

	@PostMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveCategoryMaster(@RequestBody CategoryMasterDto categoryMasterDto) {
		log.info("Category creation started.");
		categoryMasterService.saveCategoryMaster(categoryMasterDto);
		log.info("Category Creation completed.");
		return new ResponseEntity<>(OmsResponse.builder().message("Category created successfully").build(),
				HttpStatus.CREATED);
	}

	@GetMapping("/categories")
	public List<CategoryMaster> getAllCategories() {
		log.info("Get all categories is called.");
		return categoryMasterService.findAllCategories();
	}

	@GetMapping("/categories/{categoryId}")
	public CategoryMaster getCategoryById(@PathVariable(name = "categoryId") int categoryId) {
		log.info("Get category by Id called");
		return categoryMasterService.findCategoryMasterById(categoryId);
	}

	@PutMapping(value = "/categories/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateCategoryById(@PathVariable(name = "categoryId") int categoryId,
			@RequestBody CategoryMasterDto categoryMasterDto) {
		log.info("Update category by Id called");
		categoryMasterService.updateCategoryMasterById(categoryId, categoryMasterDto);
		return new ResponseEntity<>(OmsResponse.builder().message("category updated successfully").build(),
				HttpStatus.OK);
	}
}
