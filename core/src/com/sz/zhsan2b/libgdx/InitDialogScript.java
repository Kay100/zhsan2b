package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sz.zhsan2b.appwarp.WarpController;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.User;
import com.sz.zhsan2b.core.entity.User.PLAYER_TYPE;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.script.IScript;

/*
 * iScript for menu logic
 */
public class InitDialogScript implements IScript{
	
	/*
	 * reference to GameStage
	 */
	private Stage stage;
	private SceneLoader initLoader;
	
	private CompositeItem initDialog;
	private CompositeItem messageDialog;
	
	private TextField text;
	private CheckBox liubei,caocao,sunquan;
	
	private Image yesButton,noButton;
	
	private Image systembusy;
	
	private User currentUser = GameContext.getCurrentUser();

	private Skin uiSkin = Assets.instance.assetSkin.skinLibgdx;
	
	public InitDialogScript(Stage stage,SceneLoader initLoader) {
		this.stage = stage;
		this.initLoader = initLoader;
	}

	public void init(CompositeItem initScene) {
		buildMessageDialog(initScene);
		initDialog=initScene.getCompositeById("loginDialog");
		initDialog.setVisible(false);
		//buildInitDialog(initScene);
	}

	private void buildInitDialog(CompositeItem initScene) {		
		text = new TextField("", uiSkin);
		initDialog=initScene.getCompositeById("loginDialog");
		yesButton = initDialog.getImageById("yes");
		noButton= initDialog.getImageById("no");
		
		initDialog.addActor(text);
		text.setPosition(98, 80);
		text.setSize(169, 25);
		text.getStyle().background=null;
		text.setColor(Color.BLACK);
		Image checked = initScene.getImageById("checked");
		Image unchecked = initScene.getImageById("unchecked");
		uiSkin.get(CheckBoxStyle.class).checkboxOff=unchecked.getDrawable();
		uiSkin.get(CheckBoxStyle.class).checkboxOn=checked.getDrawable();
		liubei= new CheckBox("", uiSkin);
		caocao = new CheckBox("", uiSkin);
		sunquan = new CheckBox("", uiSkin);
		liubei.setPosition(99, 35);
		caocao.setPosition(169, 35);
		sunquan.setPosition(229, 35);
		initDialog.addActor(liubei);
		initDialog.addActor(caocao);
		initDialog.addActor(sunquan);
		liubei.setChecked(true);
		liubei.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(liubei.isChecked()){
					currentUser.setSalt("liubei");
					caocao.setChecked(false);	
					sunquan.setChecked(false);
				}
				
				
			}
		});
		caocao.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(caocao.isChecked()){
					currentUser.setSalt("caocao");
					liubei.setChecked(false);	
					sunquan.setChecked(false);
				}
				
				
			}
		});
		sunquan.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(sunquan.isChecked()){
					currentUser.setSalt("sunquan");
					caocao.setChecked(false);	
					liubei.setChecked(false);
				}
				
				
			}
		});
		yesButton.addListener(new ClickListener(){

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				initDialog.setVisible(false);
				displayMessage("等待连接服务器!");
				currentUser.setName(RenderUtils.getRandomHexString(10));
				WarpController.getInstance().startApp(currentUser.getName());
				currentUser.setLoginName(text.getText());
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		noButton.addListener(new ClickListener(){

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				initDialog.setVisible(false);
				displayMessage("单人游戏开始!", 1f);
				currentUser.setPlayerType(PLAYER_TYPE.PLAYER);
				Gdx.input.setInputProcessor(Zhsan2b.battleScreen.getWorldController());
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		InputEvent inputEvent = new InputEvent();
		inputEvent.setType(Type.touchUp);
		yesButton.fire(inputEvent);
	}

	private void buildMessageDialog(CompositeItem initScene) {
		//build messageDialog
		messageDialog= initScene.getCompositeById("messageDialog");
		messageDialog.getImageById("confirm").setVisible(false);
		messageDialog.getImageById("cancel").setVisible(false);
		Label message = new Label("", uiSkin);
		message.setWrap(true);
		message.setWidth(350);
		message.setColor(Color.BLACK);
		message.setName("message");
		message.setPosition(20, 68);
		messageDialog.addActor(message);
		
		systembusy = initScene.getImageById("busy");

		messageDialog.setVisible(false);
		systembusy.setVisible(false);
	}
	public void displayMessage(String mes){
		((Label)messageDialog.findActor("message")).setText(mes);
		messageDialog.getImageById("confirm").setVisible(false);
		messageDialog.getImageById("cancel").setVisible(false);		
		messageDialog.setVisible(true);
	}
	public void displayMessage(String mes,float delay){
		displayMessage(mes);
		messageDialog.addAction(Actions.delay(delay, Actions.hide()));
	}
	public void displayMessage(String mes,final Executable confirmListener,final Executable cancelListener){
		displayMessage(mes);
		messageDialog.getImageById("confirm").setVisible(true);
		messageDialog.getImageById("cancel").setVisible(true);
		messageDialog.getImageById("confirm").addListener(new ClickListener(){

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				messageDialog.setVisible(false);
				confirmListener.execute();
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		messageDialog.getImageById("cancel").addListener(new ClickListener(){

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				messageDialog.setVisible(false);
				cancelListener.execute();
				super.touchUp(event, x, y, pointer, button);
			}
			
		});		
	}	

	/*
	 * This is called every frame
	 */
	public void act(float delta) {

	}

    @Override
    public void dispose() {

    }

	public CompositeItem getInitDialog() {
		return initDialog;
	}

	public void setInitDialog(CompositeItem initDialog) {
		this.initDialog = initDialog;
	}

	public CompositeItem getMessageDialog() {
		return messageDialog;
	}

	public void setMessageDialog(CompositeItem messageDialog) {
		this.messageDialog = messageDialog;
	}


}
