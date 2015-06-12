package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ConfirmationDialog {
	
	
	public interface Confirmable {
		void confirm();
		void cancel();

	}

	public Table confirmTable;
	private Skin skinMenu = Assets.instance.assetSkin.skinMenu;

	public ConfirmationDialog() {
		confirmTable = new Table();
		confirmTable.setLayoutEnabled(false);
		Image background = skinMenu.get("background", Image.class);
		Button yes = new Button(skinMenu,"yes");
		Button no = new Button(skinMenu,"no");
		confirmTable.add(background,yes,no);
		background.setSize(240, 60);
		yes.setSize(80, 40);
		no.setSize(80, 40);
		yes.setPosition(20, 10);
		no.setPosition(140, 10);
		
	}
	

}
