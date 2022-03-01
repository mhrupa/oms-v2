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

import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.services.ItemMasterService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ItemMasterController {

	@Autowired
	private ItemMasterService itemMasterService;

	@PostMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> saveItemMaster(@RequestBody ItemMasterDto itemMasterDto) {
		log.info("Item creation started.");
		itemMasterService.saveItemMaster(itemMasterDto);
		log.info("Item Creation completed.");
		return new ResponseEntity<>(OmsResponse.builder().message("Item created successfully").build(),
				HttpStatus.CREATED);
	}

	@GetMapping("/items")
	public List<ItemMaster> getAllItems() {
		log.info("Get all items is called.");
		return itemMasterService.getAllItems();
	}

	@GetMapping("/items/{itemId}")
	public ItemMaster getItemById(@PathVariable(name = "itemId") int itemId) {
		log.info("Get Item by Id called");
		return itemMasterService.getItemById(itemId);
	}

	@PutMapping(value = "/items/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OmsResponse> updateItemById(@PathVariable(name = "itemId") int itemId,
			@RequestBody ItemMasterDto itemMasterDto) {
		log.info("Update Item by Id called");
		itemMasterService.updateItemById(itemId, itemMasterDto);
		return new ResponseEntity<>(OmsResponse.builder().message("Item updated successfully").build(),
				HttpStatus.OK);
	}
}
