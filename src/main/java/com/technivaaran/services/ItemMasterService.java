package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.ItemMasterMapper;
import com.technivaaran.repositories.ItemMasterRepository;

@Service
public class ItemMasterService {

	@Autowired
	private ItemMasterRepository itemMasterRepository;

	@Autowired
	private ItemMasterMapper itemMasterMapper;

	public ItemMaster saveItemMaster(ItemMasterDto itemMasterDto) {
		try {
			ItemMaster itemMaster = itemMasterMapper.convertToEntity(itemMasterDto);
			return itemMasterRepository.save(itemMaster);

		} catch (DataIntegrityViolationException integrityViolationException) {
			throw new OMSException("Item already exists with Item name: " + itemMasterDto.getItemName());
		}
	}

	public List<ItemMaster> getAllItems() {

		return itemMasterRepository.findAll();
	}

	public ItemMaster getItemById(long itemMasterId) {
		Optional<ItemMaster> itemMasterOp = itemMasterRepository.findById(itemMasterId);

		if (itemMasterOp.isPresent()) {
			return itemMasterOp.get();
		} else {
			throw new OMSException("Item not found for id : " + itemMasterId);
		}
	}

	public ItemMaster updateItemById(long itemId, ItemMasterDto itemMasterDto) {
		Optional<ItemMaster> itemMasterOp = itemMasterRepository.findById(itemId);

		if (itemMasterOp.isPresent()) {
			// converting DTO to entity first to avoid querying DB for Unit & category
			ItemMaster item = itemMasterMapper.convertToEntity(itemMasterDto);

			ItemMaster itemMaster = itemMasterOp.get();
			itemMaster.setPartNo(itemMasterDto.getPartNo());
			itemMaster.setItemUnit(item.getItemUnit());
			itemMaster.setStatus(item.getStatus());

			return itemMasterRepository.save(itemMaster);
		} else {
			throw new OMSException("Item not found for id : " + itemId);
		}
	}

	public List<ItemMaster> findByItemName(String itemName) {
		return itemMasterRepository.findByItemName(itemName);
	}
}
