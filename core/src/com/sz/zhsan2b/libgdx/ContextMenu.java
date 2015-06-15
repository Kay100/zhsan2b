package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class ContextMenu {
	private static final String TAG = ContextMenu.class.getName();
	public interface Executable {
		void execute();

	}
	private Array<MenuCommand> menuList = new Array<MenuCommand>(12);
	private Skin skinMenu = Assets.instance.assetSkin.skinMenu;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	private Table layerOperation;
	
	public Table combined;

	public ContextMenu(final Table layerOperation,final boolean isLeftClickMenu,MenuCommand... menu) {
		menuList.addAll(menu);
		this.layerOperation=layerOperation;
		combined = new Table();
		layerOperation.add(combined);
		combined.setName("menuList");
		combined.setSize(170, menuList.size*40);
		for(final MenuCommand menuCom:menuList){
			Button btn = new Button(skinMenu,isLeftClickMenu?"menuLeftClick":"menuRightClick" );
			combined.add(btn).expandX().width(150).height(40).padBottom(0).left();
			
			Label menuLabel = new Label(menuCom.getCommandName(), new LabelStyle(skinLibgdx.get(LabelStyle.class)));
			menuLabel.getStyle().fontColor= Color.GRAY;
			btn.add(menuLabel);
			btn.setName(menuCom.getCommandName());
			btn.addListener(new ClickListener(Buttons.LEFT){

				@Override
				public void enter(InputEvent event, float x, float y,
						int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					Button btn =(Button)event.getListenerActor();
					if(!btn.isDisabled()){
						((Label)btn.getChildren().get(0)).getStyle().fontColor=Color.WHITE;
					}
					
				}

				@Override
				public void exit(InputEvent event, float x, float y,
						int pointer, Actor toActor) {
					super.exit(event, x, y, pointer, toActor);
					((Label)((Button)event.getListenerActor()).getChildren().get(0)).getStyle().fontColor=Color.GRAY;				
				}

				@Override
				public void clicked(InputEvent event, float x, float y) {
					super.clicked(event, x, y);
					if(menuCom.isMenu()){
						Button btn = (Button)event.getListenerActor();
						((Table)btn.getParent()).getCell(btn).right();
						((Table)btn.getParent()).invalidate();
						MenuCommand[] array = menuCom.getMenuList().toArray(MenuCommand.class);
						ContextMenu conM = new ContextMenu(layerOperation, isLeftClickMenu, array);
						Vector2 position = btn.getParent().localToStageCoordinates(new Vector2(btn.getX(),btn.getY()));
						
						conM.setPosition(position.x+170+10,position.y-(menuCom.getMenuList().size-1)*40);
						
						//Gdx.app.debug(TAG, String.valueOf(menuCom.getMenuList().size));
						btn.setTouchable(Touchable.disabled);
						
					}else{
						menuCom.getExecutable().execute();
					}					
				}
				
			});
			
			combined.row();
		}
		if(menuList.size==0){
			throw new RuntimeException("There is no menu content");
		}
	}
	public void disableButtonByName(String s){
		Button button = (Button)combined.findActor(s);
		button.setDisabled(true);
		button.setTouchable(Touchable.disabled);
		
	}
	public void setPosition(float f, float g) {
		combined.setPosition(f, g);
		
	}
	
	

}
