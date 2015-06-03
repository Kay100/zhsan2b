package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.Troop;
import com.sz.zhsan2b.core.StepAction.TileEffect;

public class BattleScreen extends AbstractGameScreen {
	private static final String TAG = BattleScreen.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	private Stage stage;
	private Skin skinLibgdx;
	private Skin skinCanyonBunny;
	private Image imgBackground;
	
	//game core(logic)
	private final BattleField battleField = new BattleField();


	private boolean isBattleStart=true;
	
	
	private BattleFieldAnimationStage battleFieldAnimationStage;
	
	private BattleFieldOperationStage battleFieldOperationStage;

	
	//troop actor list
	
	private Array<TroopActor> troopActorList=new Array<TroopActor>(20);;
	
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 20f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;	

	public BattleScreen(DirectedGame game) {
		super(game);
		battleFieldOperationStage = new BattleFieldOperationStage(battleField);
		battleFieldAnimationStage = new BattleFieldAnimationStage(this);
		battleField.setStepActionHandler(battleFieldAnimationStage);
	}
	public BattleField getBattleField() {
		return battleField;
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
	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

	private void rebuildStage() {
		skinCanyonBunny = new Skin(
				Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),
				new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
		// build all layers

		Table layerTroops = buildTroopsLayer();
		// assemble stage for battle screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.WORLD_WIDTH,
				Constants.WORLD_HEIGHT);
		stack.add(layerTroops);
		stack.add(battleFieldAnimationStage.getLayerAnimation());	
		TileEffectActor effectActor = new TileEffectActor(TileEffect.BOOST);
		effectActor.setPosition(100, 100);
		battleFieldAnimationStage.getLayerAnimation().add(effectActor);

		
	}
	private Table buildTroopsLayer() {
		Table layer = new Table();
		layer.setLayoutEnabled(false);
		createTroopActors();
		for(TroopActor trA:troopActorList){
			layer.add(trA);
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

	private Table buildBackgroundLayer() {
		Table layer = new Table();
		// + Background
		//imgBackground = new Image(Assets.instance.background.back.getTexture());
		imgBackground = new Image(skinCanyonBunny, "background");
		//System.out.println(Assets.instance.background.back.getRegionWidth());
		layer.add(imgBackground);
		return layer;
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
		//game logic plan do here.
		switch(battleField.state){
		case BATTLE:
			if(isBattleStart){
				battleField.calculateBattle();
				battleFieldAnimationStage.initStepActionIter();
				isBattleStart = false;
				
			}else{
				battleFieldAnimationStage.parseStepActions();
			}
			
			break;
		case OPERATE:
			
			break;
		default:
			break;
		
		}
		
		
		
		
		
		
		// Do not update game world when paused.
		if (!paused) {
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
			stage.act(deltaTime);
		}
		//clear the screen
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// Render game world to screen
		worldRenderer.render();
        stage.draw();		
	}



	@Override
	public void resize(int width, int height) {
		worldRenderer.resize(width, height);
	}

	@Override
	public void show() {
		GamePreferences.instance.load();
		//Viewport vp = new ExtendViewport(Constants.WORLD_HEIGHT,Constants.WORLD_WIDTH);
		//ScreenViewport 操作的是世界坐标，像素和米的比例关系
		ScreenViewport vp = new ScreenViewport();
		stage = new Stage(vp);	
		//vp.setUnitsPerPixel(Constants.UNITSPERPIXEL);	
		worldController = new WorldController(game,stage);
		worldRenderer = new WorldRenderer(worldController);
		//stage.getViewport().setCamera(worldRenderer.getCamera());	
		
		
		
		Gdx.input.setCatchBackKey(true);



		
		//set debug
		if (debugEnabled)
			stage.setDebugAll(true);
		rebuildStage();		
	}

	@Override
	public void hide() {
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
		stage.dispose();
		skinLibgdx.dispose();		
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
}
