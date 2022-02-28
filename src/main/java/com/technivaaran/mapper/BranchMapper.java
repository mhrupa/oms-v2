package com.technivaaran.mapper;

import org.springframework.stereotype.Component;

import com.technivaaran.dto.BranchMasterDto;
import com.technivaaran.entities.BranchMaster;
import com.technivaaran.exceptions.EntityConversionExceptioon;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BranchMapper {

	public BranchMaster convertToEntity(BranchMasterDto branchMasterDto) {

		try {
			return BranchMaster.builder().branchAdd(branchMasterDto.getBranchAdd())
					.branchCode(branchMasterDto.getBranchCode()).branchName(branchMasterDto.getBranchName())
					.homeBranch(branchMasterDto.isHomeBranch()).build();
		} catch (Exception exception) {
			log.info("Error occured while converting to Branch entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}
}
