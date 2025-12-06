package com.technivaaran.controllers;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.AppUserDto;
import com.technivaaran.services.ParametersService;
import com.technivaaran.services.UserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping(AppUrlConstants.BASE_URL)
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private ParametersService parametersService;

	@PostMapping(value = "/validate")
	public String validateUser(@RequestBody AppUserDto appUserDto, Model model) {
		try {
			model.addAttribute("user",
					userService.getUserByEmailAndPassword(appUserDto.getUsername(), appUserDto.getPassword()));
			model.addAttribute("companyDto",
					parametersService.getAllParameters());
			// return "desktop";
			return "v2/dashboard/dashboard";
		} catch (Exception ex) {
			log.error("Error validating user, reason {}", ExceptionUtils.getStackTrace(ex));
			throw ex;
		}
	}
}
