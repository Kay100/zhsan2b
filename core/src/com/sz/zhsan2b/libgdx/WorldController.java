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
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.libgdx.BattleScreen.OnBattleProceedClicked;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;

public class WorldController extends InputAdapter {
	private static final String TAG = WorldController.class.getName();
	private DirectedGame game;

	public CameraHelper cameraHelper;
	public Level level;	
	public Stage stage;
	private Vector2 orginPosition = new Vector2();//used for record last screen position
	private boolean isDragPrepared = false;
	private boolean isDragging = false;
	private final BattleScreen battleScreen = Zhsan2b.battleScreen;
	private final Vector2 tempCoords = new Vector2();
	private final Position tempP = new Position(0,0);
	
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
		//handleDebugInput(deltaTime);
	
		handleGameInput(deltaTime);
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




	private void handleGameInput(float deltaTime) {
	
	}

    /*
     * do a example, remember to operate stage.
     * @see com.badlogic.gdx.InputAdapter#mouseMoved(int, int)
     */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		tempCoords.set(stage.screenToStageCoordinates(tempCoords.set(screenX, screenY)));
		RenderUtils.toLogicPosition(tempCoords.x, tempCoords.y, tempP);
		battleScreen.getMousePositionLabel().setText("("+tempP.x+","+tempP.y+")");
		battleScreen.getMousePositionLabel().setVisible(true);
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
			isDragPrepared=true;
			orginPosition.set(screenX, screenY);	
			
			
		
		}
		
			
		return stage.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Buttons.RIGHT) {
			isDragPrepared = false;
			Gdx.input.setCursorImage(Assets.instance.assetArrow.normal, 0, 0);
			
			if(!isDragging){
				Table layerOperation = ((Table) stage.getRoot().findActor(
						"layerOperation"));
				if (layerOperation.hasChildren()){
					layerOperation.clear();
				}else{
					OnBattleProceedClicked onBattleProceedClicked = battleScreen.new OnBattleProceedClicked();
					
					new ContextMenu(layerOperation, false, new MenuCommand("proceed", false, onBattleProceedClicked)).setPosition(cameraHelper.getPosition().x, cameraHelper.getPosition().y);
					
				}				
			}
			isDragging=false;


			
		}
		

		return stage.touchUp(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(isDragPrepared){
			isDragging=true;
//			Table layerOperation = ((Table) stage.getRoot().findActor(
//					"layerOperation"));
//			layerOperation.clear();
			Gdx.input.setCursorImage(Assets.instance.assetArrow.drag, 8, 8);				
			moveCamera(-(screenX-orginPosition.x)*Constants.UNITSPERPIXEL*cameraHelper.getZoom(), (screenY-orginPosition.y)*Constants.UNITSPERPIXEL*cameraHelper.getZoom());
			orginPosition.set(screenX, screenY);		
		}

		return stage.touchDragged(screenX, screenY, pointer);
	}

	@Override
	public boolean scrolled(int amount) {
		cameraHelper.addZoom(amount/2f);
		//hide trooptile according to the camera's zoom
		Table troopInfoLayer=stage.getRoot().findActor("troopInfoLayer");
		if(cameraHelper.getZoom()>2){
			troopInfoLayer.setVisible(false);
		}else{
			troopInfoLayer.setVisible(true);
		}
		return stage.scrolled(amount);
	}



}
