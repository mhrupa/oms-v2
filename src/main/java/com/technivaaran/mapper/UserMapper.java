package com.technivaaran.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.technivaaran.AppConstants;
import com.technivaaran.dto.UserDto;
import com.technivaaran.entities.BranchMaster;
import com.technivaaran.entities.Role;
import com.technivaaran.entities.User;
import com.technivaaran.exceptions.EntityConversionExceptioon;
import com.technivaaran.services.BranchMasterService;
import com.technivaaran.services.RoleService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserMapper {

	@Autowired
	BranchMasterService branchMasterService;

	@Autowired
	RoleService roleService;

	public User convertToEntity(UserDto userDto) {
		try {
			BranchMaster branchMaster = branchMasterService.findbarnchMasterById(userDto.getBranchId());
			Role role = roleService.findRoleById(userDto.getRoleId());
			return User.builder().userName(userDto.getUserName()).password(userDto.getPassword())
					.status(StringUtils.hasLength(userDto.getStatus()) ? userDto.getStatus()
							: AppConstants.STATUS_DISABLED)
					.email(userDto.getEmail()).userRole(role).userBranch(branchMaster).build();
		} catch (Exception exception) {
			log.info("Error occured while converting to User entity");
			throw new EntityConversionExceptioon(exception.getMessage(), exception);
		}
	}
}
