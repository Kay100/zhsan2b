package com.sz.zhsan2b.core.entity;

public class GameStateData {
	//constants
	public static int TYPE_COMMAND = 0;
	public static int TYPE_BATTLEFIELD =1;
	
	//field
	private String userNameForWarp;
	private int type;
	private Command command;
	private BattleField remoteBattleField;
	
	
	public String getUserNameForWarp() {
		return userNameForWarp;
	}
	public void setUserNameForWarp(String userNameForWarp) {
		this.userNameForWarp = userNameForWarp;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Command getCommand() {
		return command;
	}
	public void setCommand(Command command) {
		this.command = command;
	}
	public BattleField getRemoteBattleField() {
		return remoteBattleField;
	}
	public void setRemoteBattleField(BattleField remoteBattleField) {
		this.remoteBattleField = remoteBattleField;
	}

	

}
