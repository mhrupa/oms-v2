package com.technivaaran.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.technivaaran.AppUrlConstants;
import com.technivaaran.dto.AppUserDto;
import com.technivaaran.services.UserService;

@Controller
@RequestMapping(AppUrlConstants.BASE_URL)
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping(value = "/validate")
	public String validateUser(@RequestBody AppUserDto appUserDto, Model model) {
		try {
			model.addAttribute("user",
					userService.getUserByEmailAndPassword(appUserDto.getUsername(), appUserDto.getPassword()));
			return "desktop";
		} catch (Exception ex) {
			throw ex;
		}
	}
}
