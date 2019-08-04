package com.xtu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xtu.dao.UserMapper;
import com.xtu.model.User;
import com.xtu.model.UserExample;
import com.xtu.model.UserExample.Criteria;

@Service
public class UserService {
	
	@Autowired
	private UserMapper userMapper;
	
	
	public List<User> getUsers() {
		UserExample example = new UserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUidIsNotNull();
		return userMapper.selectByExample(example);
	}

}
