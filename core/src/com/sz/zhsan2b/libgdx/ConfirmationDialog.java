package com.sz.zhsan2b.libgdx;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ConfirmationDialog {

	
	public interface Confirmable {
		void confirm();
		void cancel();

	}
	private Confirmable confirmable;
	public Table combined;
	private Skin skinMenu = Assets.instance.assetSkin.skinMenu;

	public ConfirmationDialog(Confirmable confirmable) {
		this.confirmable = confirmable;
		combined = new Table();
		combined.setLayoutEnabled(false);
		Image background = skinMenu.get("background", Image.class);
		Button yes = new Button(skinMenu,"yes");
		yes.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onYesClicked(); 
				
			}
			
		});
		Button no = new Button(skinMenu,"no");
		no.addListener(new ChangeListener(){

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onNoClicked(); 
				
			}
			
		});
		combined.add(background,yes,no);
		background.setSize(240, 60);
		yes.setSize(80, 40);
		no.setSize(80, 40);
		yes.setPosition(20, 10);
		no.setPosition(140, 10);
		combined.setName("confirmationDialog");
		
	}

	private void onNoClicked() {
		confirmable.cancel();
		dispose();
	}

	private void onYesClicked() {
		confirmable.confirm();
		dispose();
	}
	
	public void dispose(){
		combined.remove();
	}
	

}
