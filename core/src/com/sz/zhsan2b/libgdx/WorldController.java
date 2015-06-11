package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.sz.zhsan2b.core.Position;

public class WorldController extends InputAdapter {
	private static final String TAG = WorldController.class.getName();
	private DirectedGame game;

	public CameraHelper cameraHelper;
	public Level level;	
	public Stage stage;
	private Vector2 orginPosition = new Vector2();//used for record last screen position
	private boolean isDragByRightMouseKey = false;
	
	public WorldController(DirectedGame game,Stage stage) {
		this.game =game;
		this.stage = stage;
		init();
	}
	
	private void initLevel() {
		level = new Level();
	}

	private void init() {
		cameraHelper = new CameraHelper();

		initLevel();
	}


	public void update(float deltaTime) {
		handleDebugInput(deltaTime);
	
		handleInputGame(deltaTime);
		level.update(deltaTime);
		cameraHelper.update(deltaTime);

	}


	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop)
			return;

			float camMoveSpeed = 400 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT))
				moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT))
				moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP))
				moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN))
				moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
				cameraHelper.setPosition(0, 0);
			// Camera Controls (zoom)
			float camZoomSpeed = 1 * deltaTime;
			float camZoomSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
				camZoomSpeed *= camZoomSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.COMMA))
				cameraHelper.addZoom(camZoomSpeed);
			if (Gdx.input.isKeyPressed(Keys.PERIOD)){
				cameraHelper.addZoom(-camZoomSpeed);
				//Gdx.app.debug(TAG, String.valueOf(camZoomSpeed));
			}

			if (Gdx.input.isKeyPressed(Keys.SLASH)){
				System.out.println(cameraHelper.getZoom());
				cameraHelper.setZoom(1);
			}
				
		
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		
		//记住永远向世界坐标换算位置，拉大放小，影响的只是屏幕的比例尺（即zoom）
//		float halfViewportWidth = Constants.VIEWPORT_WIDTH/2*Constants.UNITSPERPIXEL*cameraHelper.getZoom();
//		float halfViewportHeight = Constants.VIEWPORT_HEIGHT/2*Constants.UNITSPERPIXEL*cameraHelper.getZoom();
//		x = MathUtils.clamp(x, (0+halfViewportWidth), (Constants.WORLD_WIDTH-halfViewportWidth));
//		y = MathUtils.clamp(y, (0+halfViewportHeight), (Constants.WORLD_HEIGHT-halfViewportHeight));
		
		cameraHelper.setPosition(x, y);
	}




	private void handleInputGame(float deltaTime) {
	
	}

    /*
     * do a example, remember to operate stage.
     * @see com.badlogic.gdx.InputAdapter#mouseMoved(int, int)
     */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {  
		return stage.mouseMoved(screenX, screenY);
	}

	@Override
	public boolean keyDown(int keycode) {

		return stage.keyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {

		return stage.keyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {

		return stage.keyTyped(character);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button==Buttons.RIGHT){
			isDragByRightMouseKey=true;
			orginPosition.set(screenX, screenY);
			Gdx.input.setCursorImage(Assets.instance.assetArrow.drag, 8, 8);			
		}

			
		return stage.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button==Buttons.RIGHT){
			isDragByRightMouseKey=false;
			Gdx.input.setCursorImage(Assets.instance.assetArrow.normal, 0, 0);	
		}
		
		return stage.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(isDragByRightMouseKey){
			moveCamera(-(screenX-orginPosition.x)*Constants.UNITSPERPIXEL*cameraHelper.getZoom(), (screenY-orginPosition.y)*Constants.UNITSPERPIXEL*cameraHelper.getZoom());
			orginPosition.set(screenX, screenY);		
		}

		return stage.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean scrolled(int amount) {
		cameraHelper.addZoom(amount/2f);
		//hide trooptile according to the camera's zoom
		Table troopInfoLayer=((Table)((Stack)stage.getRoot().findActor("mainStack")).findActor("troopInfoLayer"));
		if(cameraHelper.getZoom()>2){
			troopInfoLayer.setVisible(false);
		}else{
			troopInfoLayer.setVisible(true);
		}
		return stage.scrolled(amount);
	}



}
