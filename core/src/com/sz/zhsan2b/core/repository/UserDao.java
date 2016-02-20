package com.sz.zhsan2b.core.repository;

import org.springframework.data.repository.CrudRepository;

import com.sz.zhsan2b.core.entity.User;

public interface UserDao extends CrudRepository<User, Long> {
	User findByLoginName(String loginName);
}