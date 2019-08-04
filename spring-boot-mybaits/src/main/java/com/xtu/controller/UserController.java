package com.xtu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xtu.annotation.CryptField;
import com.xtu.enumeration.CryptTypeEnum;
import com.xtu.model.User;
import com.xtu.service.UserService;

@RestController
public class UserController {
	
	@Autowired
	private UserService userService;

	@GetMapping("/getUsers")
	private List<User> getUsers(){
		return userService.getUsers();
	}
	
}
