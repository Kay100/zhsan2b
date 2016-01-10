package com.sz.zhsan2b.core;

import org.springframework.context.ApplicationContext;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.User;
/**
 * Environment class that holds references of instances.
 * @author Administrator
 *
 */
public abstract class GameContext {
	private static final BattleField battleField = new BattleField();
//	spring context store here 
	private static ApplicationContext context;

	private static User currentUser;

	public static BattleField getBattleField() {
		return battleField;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static void setContext(ApplicationContext context) {
		GameContext.context = context;
	}

	public static User getCurrentUser() {
		if(currentUser==null){
			currentUser=new User();
			currentUser.setPlayerType(PLAYER_TYPE.AI);
			return currentUser;
		}else{
			return currentUser;
		}		
	}
}
