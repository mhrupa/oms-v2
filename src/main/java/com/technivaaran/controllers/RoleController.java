package com.technivaaran.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.technivaaran.entities.Role;
import com.technivaaran.services.RoleService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@GetMapping("/roles")
	public List<Role> getAllUsers() {
		log.info("Get all Roles is called.");
		return roleService.findAllRoles();
	}

	@GetMapping("/roles/{roleId}")
	public Role getUserById(@PathVariable(name = "roleId") int roleId) {
		log.info("Get Role by Id called");
		return roleService.findRoleById(roleId);
	}

}
