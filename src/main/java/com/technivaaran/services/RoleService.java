package com.technivaaran.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.technivaaran.entities.Role;
import com.technivaaran.exceptions.OMSException;
import com.technivaaran.repositories.RoleRepository;

@Service
public class RoleService {

	@Autowired
	RoleRepository roleRepository;

	public Role findRoleById(int roleId) {
		Optional<Role> roleOp = roleRepository.findById(roleId);
		if (roleOp.isPresent()) {
			return roleOp.get();
		} else {
			throw new OMSException("Role not found for id : " + roleId);
		}
	}

	public List<Role> findAllRoles() {
		return roleRepository.findAll();
	}
}
