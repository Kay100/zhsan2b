package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class TroopMenu extends ContextMenu {
	private UserCommandHandler userCommandHandler;

	public TroopMenu() {
		setVisible(false);
		userCommandHandler=Zhsan2b.battleScreen.getUserCommandHandler();
		layer=Zhsan2b.battleScreen.getLayerOperation();

		MenuCommand attackCommand =new MenuCommand("attack", false, userCommandHandler.attack());
		//attackCommand.addMenuList(new MenuCommand("plan", false, null),new MenuCommand("occupy", false, null));
		super.loadMenuData(layer, true, new MenuCommand("move", false, userCommandHandler.move()),attackCommand,new MenuCommand("done", false, null));
		
		
		
	}
	public void show(TroopActor target){
		userCommandHandler.setTarget(target);
		layer.add(userCommandHandler.computeTroopAttackRange());

		//menu.disableButtonByName("move");
		//((Button)menu.combined.findActor("move")).getStyle().pressedOffsetX=20f;
		setPosition(target.getX()+100,target.getY());
		show();
	}
	

}
