package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import com.technivaaran.dto.ItemMasterDto;
import com.technivaaran.dto.OmsResponse;
import com.technivaaran.entities.ItemMaster;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.mapper.ItemMasterMapper;
import com.technivaaran.repositories.ItemMasterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemMasterService {

	@Autowired
	private ItemMasterRepository itemMasterRepository;

	@Autowired
	private ItemMasterMapper itemMasterMapper;

	public ResponseEntity<OmsResponse> saveItemMaster(ItemMasterDto itemMasterDto) {
		try {
			ItemMaster itemMaster;
			if (!ObjectUtils.isEmpty(itemMasterDto.getItemName())) {
				Optional<ItemMaster> itemMasterOp = itemMasterRepository
						.findByItemName(itemMasterDto.getItemName());
				if (itemMasterOp.isEmpty()) {
					itemMaster = ItemMaster.builder()
							.itemName(itemMasterDto.getItemName()).build();
					itemMasterRepository.save(itemMaster);
					log.info("Item creation completed.");
					return new ResponseEntity<>(OmsResponse.builder().message("Item created successfully").build(),
							HttpStatus.CREATED);
				} else {
					throw new OMSException("Item exists with Item name: " + itemMasterDto.getItemName());
				}
			} else {
				throw new OMSException("Item Name can not be empty.");
			}
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
			ItemMaster item = itemMasterMapper.convertToEntity(itemMasterDto);

			ItemMaster itemMaster = itemMasterOp.get();
			itemMaster.setStatus(item.getStatus());

			return itemMasterRepository.save(itemMaster);
		} else {
			throw new OMSException("Item not found for id : " + itemId);
		}
	}

	public Optional<ItemMaster> findByItemName(String itemName) {
		return itemMasterRepository.findByItemName(itemName);
	}
}
