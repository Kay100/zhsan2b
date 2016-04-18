package com.sz.zhsan2b.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;


@Entity
@Table(name = "ss_user")
public class User extends IdEntity {
	public enum PLAYER_TYPE {
		PLAYER,AI
	}	
	private String loginName;
	//use for appwarp's localuser
	private String name;
	private String plainPassword;
	private String password;
	private String salt;
	private String roles;
	private PLAYER_TYPE playerType;
	private Date registerDate;

	public User() {
	}

	public User(Long id) {
		this.id = id;
	}

	@NotBlank
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// 不持久化到数据库，也不显示在Restful接口的属性.
	@Transient
	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Transient
	public List<String> getRoleList() {
		// 角色列表在数据库中实际以逗号分隔字符串存储，因此返回不能修改的List.
		//return ImmutableList.copyOf(StringUtils.split(roles, ","));
		return null;
	}

	// 设定JSON序列化时的日期格式
	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	@Transient
	public PLAYER_TYPE getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PLAYER_TYPE playerType) {
		this.playerType = playerType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}