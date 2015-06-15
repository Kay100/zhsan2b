package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;

public class MenuCommand {
	private final String commandName;
	private final boolean isMenu;
	private final Array<MenuCommand> menuList = new Array<MenuCommand>(12);
	private final Executable executable;
	public MenuCommand(String commandName, boolean isMenu, Executable executable) {
		super();
		this.commandName = commandName;
		this.isMenu = isMenu;
		this.executable = executable;
	}
	
	public void addMenuList(MenuCommand...commands){
		menuList.clear();
		menuList.addAll(commands);
		
	}

	public String getCommandName() {
		return commandName;
	}

	public boolean isMenu() {
		return isMenu;
	}

	public Array<MenuCommand> getMenuList() {
		return menuList;
	}

	public Executable getExecutable() {
		return executable;
	}
	

}
