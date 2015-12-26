package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;
import com.sz.zhsan2b.core.entity.StepAction.TileEffect;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.resources.ResourceManager;

public class BattleScreen extends AbstractGameScreen {	
	
	//overlap2d
	private ResourceManager resourceManager;
	
	private static final String TAG = BattleScreen.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	private Stage stage;
	private Stage uiStage;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	private Skin skinCanyonBunny= Assets.instance.assetSkin.skinCanyonBunny;
	private Label mousePositionLabel;
	
	//game core(logic)
	private final BattleField battleField = GameContext.getBattleField();


	private boolean isBattleStart=true;
	private boolean isOperateStart=true;
	
	
	private BattleFieldAnimationStage battleFieldAnimationStage;
	
	private BattleFieldOperationStage battleFieldOperationStage;

	
	//troop actor list
	
	private Array<TroopActor> troopActorList=new Array<TroopActor>(20);;
	
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 20f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;

	private SceneLoader menuLoader;	

	public BattleScreen(DirectedGame game) {
		super(game);
		battleFieldOperationStage = new BattleFieldOperationStage();
		battleFieldAnimationStage = new BattleFieldAnimationStage(this);
	}
	public BattleField getBattleField() {
		return battleField;
	}	

	public WorldController getWorldController() {
		return worldController;
	}
	public Array<TroopActor> getTroopActorList() {
		return troopActorList;
	}
	public Stage getStage() {
		return stage;
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public Stage getUiStage() {
		return uiStage;
	}
	public void setUiStage(Stage uiStage) {
		this.uiStage = uiStage;
	}
	public Label getMousePositionLabel() {
		return mousePositionLabel;
	}
	public boolean isBattleStart() {
		return isBattleStart;
	}
	public void setBattleStart(boolean isBattleStart) {
		this.isBattleStart = isBattleStart;
	}
	public boolean isOperateStart() {
		return isOperateStart;
	}
	public void setOperateStart(boolean isOperateStart) {
		this.isOperateStart = isOperateStart;
	}
	public BattleFieldOperationStage getBattleFieldOperationStage() {
		return battleFieldOperationStage;
	}
	public BattleFieldAnimationStage getBattleFieldAnimationStage() {
		return battleFieldAnimationStage;
	}
	@Override
	public InputProcessor getInputProcessor() {
		return worldController;
	}

	private void rebuildStage() {
		// build all layers
		Table troopsLayer = buildTroopsLayer();

		Table troopInfoLayer = buildTroopInfoLayer();

		Table effectsLayer = buildEffectsLayer();

		
		// assemble stage for battle screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.WORLD_WIDTH,
				Constants.WORLD_HEIGHT);
		stack.add(troopsLayer);
		stack.add(troopInfoLayer);
		stack.add(effectsLayer);
		stack.add(battleFieldAnimationStage.getLayerAnimation());
		stack.add(battleFieldOperationStage.getLayerOperation());
		stack.setName("mainStack");
		
		//uiStage
		
		mousePositionLabel= new Label("mousePosition", Assets.instance.assetSkin.skinLibgdx);
		mousePositionLabel.setPosition(500, 0);
		mousePositionLabel.setVisible(false);
		uiStage.addActor(mousePositionLabel);
		uiStage.addActor(battleFieldOperationStage.getNotification());
		//scene load
		// Initializing iScript MenuSceneScript that will be holding all menu logic, and passing this stage for later use
		MenuScreenScript menuScript = new MenuScreenScript(stage,menuLoader);
		
		// adding this script to the root scene of menu which is hold in menuLoader.sceneActor
		menuLoader.sceneActor.addScript(menuScript);
		//battleFieldOperationStage.getLayerOperation().add(menuLoader.sceneActor);	
		
		synchronizeTroopLayer();
		
	}

	private Table buildEffectsLayer() {
		Table layer = new Table();
		layer.setLayoutEnabled(false);
		layer.setName("effectsLayer");//permenent layer
		return layer;
	}
	private Table buildTroopsLayer() {
		Table layer = new Table();
		layer.setLayoutEnabled(false);
		layer.setName("troopLayer");		
		createTroopActors();
		for(TroopActor trA:troopActorList){
			layer.add(trA);
		}

		return layer;
	}
	private Table buildTroopInfoLayer() {
		Table layer = new Table();
		layer.setLayoutEnabled(false);
		layer.setName("troopInfoLayer");
		for(TroopActor trA:troopActorList){
			layer.add(trA.getTroopTitle());
		}

		return layer;
	}	

	private void createTroopActors() {
		TroopActor trActor;
		troopActorList.clear();
		for(Troop tr:battleField.getTroopList()){
			trActor = new TroopActor(tr);
			trActor.setPosition(RenderUtils.translate(tr.getPosition().x), RenderUtils.translate(tr.getPosition().y));
			troopActorList.add(trActor);
			
		}
		
	}
	@Override
	public void render(float deltaTime) {
		if (debugEnabled) {
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0) {
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}	
		
		// Do not update game world when paused.
		if (!paused) {
			//game logic plan do here.
			switch(battleField.state){
			case BATTLE:
				if(isBattleStart){
					battleFieldAnimationStage.startBattle();
					isBattleStart = false;
					Gdx.app.debug(TAG, "calucate is correct!");
				}else{
					battleFieldAnimationStage.parseStepActions();
				}
				synchronizeTroopLayer();
				break;
			case OPERATE:
				if(isOperateStart){
					battleFieldOperationStage.startOperate();
					isOperateStart=false;
				}else{
					battleFieldOperationStage.operate();
				}
				
				break;
			default:
				break;
			
			}

			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
			stage.act(deltaTime);
			uiStage.act(deltaTime);
		}
		//clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();
        stage.draw();	
        uiStage.draw();
	}
	public void startBattle(){
		battleField.state=State.BATTLE;
		isBattleStart=true;
		battleFieldAnimationStage.setPlanning(true);
	}
	public void startOperate() {
		worldController.cameraHelper.setTarget(null);
		battleField.deleteDestroyedTroops();
		battleField.state=State.OPERATE;		
		isOperateStart=true;
		
	}	


	private void synchronizeTroopLayer() {
		for(TroopActor trA:troopActorList){
			trA.getTroopTitle().setX(trA.getX()+30f);
			trA.getTroopTitle().setY(trA.getY()+60f);
		}
		
	}
	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
		uiStage.getViewport().update(width, height);
		// 这个camera相对于窗口是不动的，不动的carema可以类同于世界坐标系。
		uiStage.getCamera().viewportHeight = Constants.VIEWPORT_HEIGHT;
		uiStage.getCamera().viewportWidth = (Constants.VIEWPORT_HEIGHT / (float) height)
				* (float) width;
		uiStage.getCamera().position.set(uiStage.getCamera().viewportWidth / 2,
				uiStage.getCamera().viewportHeight / 2, 0);
		uiStage.getCamera().update();
	}

	@Override
	public void show() {
		// Initializing asset manager
        resourceManager = new ResourceManager();
		
		// loading assets into memory
        resourceManager.initAllResources();
		menuLoader = new SceneLoader(resourceManager);
		
		// loading MenuScene.dt from assets folder
		menuLoader.loadScene("MenuScene");
		
		
		
		GamePreferences.instance.load();
		//Viewport vp = new ExtendViewport(Constants.WORLD_HEIGHT,Constants.WORLD_WIDTH);
		//ScreenViewport 操作的是世界坐标，像素和米的比例关系
		ScreenViewport vp = new ScreenViewport();
		stage = new Stage(vp);	
		uiStage = new Stage(new ScreenViewport(), stage.getBatch());
		//vp.setUnitsPerPixel(Constants.UNITSPERPIXEL);	
		worldController = new WorldController(game,stage);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);



		
		//set debug
		if (debugEnabled){
			stage.setDebugAll(true);
			uiStage.setDebugAll(true);
		}

		rebuildStage();
		
	

	}

	@Override
	public void hide() {
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
		stage.dispose();
		uiStage.dispose();
		skinLibgdx.dispose();	
		skinCanyonBunny.dispose();
	}

	@Override
	public void pause() {
		paused = true;
	}

	@Override
	public void resume() {
		super.resume();
		// Only called on Android!
		paused = false;
	}
	public void deleteTroopActor(TroopActor destroyedTroop) {
		destroyedTroop.getTroopTitle().remove();
		getTroopActorList().removeValue(destroyedTroop, true);
		destroyedTroop.remove();
		
	}

	public void parseTroopsEffect(ArrayMap<Long,TileEffect> effects) {
		//解析效果，根据效果分类处理troopActor
		for(Entry<Long,TileEffect> teE:effects){
			switch(teE.value){
			case CRITICAL:
				break;
			case DESTROY:
				TroopActor destroyedTroop = battleFieldAnimationStage.getTroopActorByTroopId(teE.key);
				deleteTroopActor(destroyedTroop);
				break;
			case FIRE:
				break;
			case HUOSHI:
				break;
			case RECOVER:
				break;
			case RESIST:
				break;
			default:
				break;
			
			}
		}
	}	

}
