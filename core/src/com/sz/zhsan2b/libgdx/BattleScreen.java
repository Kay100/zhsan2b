package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.Iterator;

import org.json.JSONObject;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.sz.zhsan2b.appwarp.WarpController;
import com.sz.zhsan2b.appwarp.WarpListener;
import com.sz.zhsan2b.core.BattleFieldManager;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.TroopManager;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.BattleField.SYN_TYPE;
import com.sz.zhsan2b.core.entity.BattleProperties;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.GameStateData;
import com.sz.zhsan2b.core.entity.MilitaryKind;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.entity.DamageRange.DamageRangeType;
import com.sz.zhsan2b.core.entity.StepAction.TileEffect;
import com.sz.zhsan2b.core.entity.User;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;
import com.sz.zhsan2b.libgdx.TroopActor.ACTION_LABEL;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.actor.CompositeItem;
import com.uwsoft.editor.renderer.actor.TextBoxItem;
import com.uwsoft.editor.renderer.resources.ResourceManager;

public class BattleScreen extends AbstractGameScreen implements WarpListener{	
	public class OnBattleProceedClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.clear();
			WarpController.getInstance().updateResult(WarpController.ONE_PLAYER_DONE, "already done!");
			initDialog.displayMessage("等待另一名玩家行动");
		}

	}	
	private static final String TAG = BattleScreen.class.getName();	
	
	private User currentUser = GameContext.getCurrentUser();
	//overlap2d
	private ResourceManager resourceManager;
	private SceneLoader initLoader;	
	
	private InitDialogScript initDialog;
	
	//world old api
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;
	
	//scene2d
	private Stage stage;
	private Stage uiStage;
	
	//resource
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	private Skin skinMenu= Assets.instance.assetSkin.skinMenu;
	
	//wait for componenting
	private Label mousePositionLabel;
	private Label notification;	
	private Array<TroopActor> troopActorList=new Array<TroopActor>(20);
	
	//layer
	private Table layerOperation;	
	private Table layerAnimation;
	
	//game core(logic)
	
	private final BattleField battleField = GameContext.getBattleField();
	private BattleFieldManager battleFieldManager;	
	private TroopManager troopManager;
	
	private boolean isBattleStart=true;
	private boolean isOperateStart=true;
	private StepAction currentStepAction;
	private Iterator<StepAction> stepActionIter;
	private boolean isPlanning;

	
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 20f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;



	public BattleScreen(DirectedGame game) {
		super(game);
		initOperation();
		initAnimation();
	}
	private void initOperation() {
		layerOperation = new Table();
		layerOperation.setLayoutEnabled(false);	
		layerOperation.setName("layerOperation");
		notification= new Label("notification", Assets.instance.assetSkin.skinLibgdx);
		notification.setPosition(500, 0);
		notification.setVisible(false);
		// 测试
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = false;
		bp.move = 15;
		bp.range = 1;
		bp.speed = 20;
		bp.damageRangeType=DamageRangeType.ARROW_TOWER;
		bp.damageRange=2;
		bp.backUp();
		BattleProperties bp2= new BattleProperties(bp);
		bp2.range=4;
		bp2.notRange=2;
		bp2.isXie=false;
		BattleProperties bp3= new BattleProperties(bp);
		bp3.isXie=true;
		Command com1 = new Command();
		Command com2 = new Command();
		Command com3 = new Command();		
//		Command com1 = new Command(new Position(3, 4));
//		Command com2 = new Command(new Position(4, 6));
//		Command com3 = new Command(new Position(4, 6));
//		
//		com1.actionKind=ACTION_KIND.MOVE;
//		com2.actionKind=ACTION_KIND.ATTACK;
//		com3.actionKind=ACTION_KIND.MOVE;
//		com1.object=null;
//		com2.object=null;
//		com3.object=null;

		Troop tr1= new Troop(new MilitaryKind(0), bp, new Position(6, 5),
				com1, PLAYER_TYPE.PLAYER.AI);
		
		Troop tr2= new Troop(new MilitaryKind(1), bp2, new Position(1, 9),
				com2, PLAYER_TYPE.PLAYER.PLAYER);
		Troop tr3= new Troop(new MilitaryKind(0), bp3, new Position(6, 4),
				com3, PLAYER_TYPE.PLAYER.AI);		
		tr2.setStepAttack(true);
		tr2.setMultiObject(true);

		battleField.getTroopList().add(tr1);
		battleField.getTroopList().add(tr2);
		battleField.getTroopList().add(tr3);
		
	}
	private void initAnimation() {
		isPlanning = true;
		layerAnimation = new Table();
		layerAnimation.setLayoutEnabled(false);
		//layerAnimation.setTransform(false);
		battleFieldManager=GameContext.getContext().getBean(BattleFieldManager.class);
		troopManager=GameContext.getContext().getBean(TroopManager.class);
	}	


	//循环主逻辑操作期调用
	public void operate() {
		
	}


	public void displayNotification(String text) {
		initDialog.displayMessage(text, 1f);
		
	}		
	
	public void initStepActionIter(){
		stepActionIter = battleField.getStepActionList().iterator();
	}
	public void parseStepActions() {
		if(isPlanning){
			//parse
			if(stepActionIter.hasNext()){
				currentStepAction = stepActionIter.next();
				//add camera follow
				CameraHelper ch =worldController.cameraHelper;
				TroopActor trA = getCurrentStepTroopActor(currentStepAction.actionTroopId);			
				AbstractGameObject object = new AbstractGameObject() {					
					@Override
					public void render(SpriteBatch batch) {					
					}
				};
				object.position=trA.getPosition();
				if(!ch.objectInCamera(object)){
					ch.setTarget(object);
				}

				trA.parseStepAction();
			}else{
				startOperate();
			}
			
			
			//进入到进行态
			isPlanning=false;
		}
		
	}
	private TroopActor getCurrentStepTroopActor(long actionTroopId) {
		return getTroopActorByTroopId(actionTroopId);
	}
	public  TroopActor getTroopActorByTroopId(long troopId) {
		for(TroopActor trActor: troopActorList){
			if(trActor.getTroop().getId()==troopId){
				return trActor;
			}
		}
		return null;
	}	

	public boolean isDisplayFinished() {
		
		return !stepActionIter.hasNext();
	}
	public void displayTileEffects(ArrayMap<Long,TileEffect> effects){
		for(Entry<Long,TileEffect> en:effects){
			TileEffectActor effectActor = new TileEffectActor(effects.get(en.key));
			TroopActor trA = getTroopActorByTroopId(en.key);
			effectActor.setPosition(trA.getX(), trA.getY());
			layerAnimation.add(effectActor);				
		}
	}
	public void displayCombatNumbers(ArrayMap<Long,Integer> damages){
		for(Entry<Long,Integer>en:damages){
			int tempInt =damages.get(en.key);
			CombatNumberLabel damageLabel = new CombatNumberLabel(tempInt, true);
			if(tempInt==0){
				damageLabel.setVisible(false);
			}			
			TroopActor trA = getTroopActorByTroopId(en.key);
			damageLabel.addAction(sequence(Actions.color(Color.RED),moveTo(trA.getX()+50,trA.getY()+50),parallel(Actions.moveBy(0f,50f,Constants.ONE_STEP_TIME,Interpolation.linear))));
			layerAnimation.add(damageLabel);				
		}
	}
	public void modifyTroopsHpVisual(ArrayMap<Long,Integer> damages){
		for(Entry<Long,Integer>en:damages){
			TroopActor trA = getTroopActorByTroopId(en.key);
		    trA.modifyHpVisual(damages.get(trA.getTroop().getId()));	
		}
	
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

	@Override
	public InputProcessor getInputProcessor() {
			return uiStage;
		
	}

	private void buildStage() {
		// build all layers or components
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
		stack.add(layerAnimation);
		stack.add(layerOperation);
		stack.setName("mainStack");
		
		
		
		//uiStage
		
		mousePositionLabel= new Label("mousePosition", Assets.instance.assetSkin.skinLibgdx);
		mousePositionLabel.setPosition(500, 0);
		mousePositionLabel.setVisible(false);
		uiStage.addActor(mousePositionLabel);
		uiStage.addActor(notification);
		//scene load
		// Initializing iScript MenuSceneScript that will be holding all menu logic, and passing this stage for later use
		initDialog = new InitDialogScript(uiStage,initLoader);
		initLoader.sceneActor.addScript(initDialog);
		uiStage.addActor(initLoader.sceneActor);		
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
				buildStage();
			}
		}	
		
		// Do not update game world when paused.
		if (!paused) {
			//game logic plan do here.
			switch(battleField.state){
			case BATTLE:
				if(isBattleStart){
					//battleFieldManager.startBattle();
					for(TroopActor trA:troopActorList){
						trA.hideActionLabel();
					}
					
					initStepActionIter();
					isBattleStart = false;
					Gdx.app.debug(TAG, "animation is displayed!");
				}else{
					parseStepActions();
				}
				synchronizeTroopLayer();
				break;
			case OPERATE:
				if(isOperateStart){
					for(TroopActor trA:troopActorList){
						trA.setActionLabel(ACTION_LABEL.UNDONE);
					}
					isOperateStart=false;
				}else{
					operate();
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
		initDialog.displayMessage("本回合开始!", 1f);
		battleField.state=State.BATTLE;
		isBattleStart=true;
		isPlanning=true;
	}
	public void startOperate() {
		worldController.cameraHelper.setTarget(null);
		battleFieldManager.deleteDestroyedTroops();
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
        //new 一个新的sceneloader去load另外的scene
		initLoader = new SceneLoader(resourceManager);
		
		// loading MenuScene.dt from assets folder
		initLoader.loadScene("MainScene");
		
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

		buildStage();
		
	

	}

	@Override
	public void hide() {
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
		stage.dispose();
		uiStage.dispose();
		skinLibgdx.dispose();	
		skinMenu.dispose();
		initLoader.sceneActor.dispose();
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
				TroopActor destroyedTroop = getTroopActorByTroopId(teE.key);
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
	@Override
	public void onWaitingStarted(String message) {
		initDialog.displayMessage("等待另一名玩家进入游戏!");
		
	}

	@Override
	public void onError(String message) {
		Executable confirm = new Executable() {
			
			@Override
			public void execute() {
				initDialog.displayMessage("等待连接服务器!");
				WarpController.getInstance().startApp(currentUser.getName());
				
			}
		};
		Executable cancel = new Executable() {
			
			@Override
			public void execute() {
				initDialog.displayMessage("单人游戏开始!",1f);
				currentUser.setPlayerType(PLAYER_TYPE.PLAYER);
				Gdx.input.setInputProcessor(worldController);
				
				
			}
		};
		initDialog.displayMessage("连接失败!国外的服务就是难用",confirm,cancel);
		
	}

	@Override
	public void onGameStarted(String message) {
		initDialog.getMessageDialog().setVisible(false);
		initDialog.displayMessage("对战游戏开始!", 1f);
		Gdx.input.setInputProcessor(worldController);
	}

	@Override
	public void onGameFinished(int code, boolean isRemote) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGameUpdateReceived(String message) {
		//目前只用来传command了
		SYN_TYPE type = battleFieldManager.updateBattleFieldRelatedData(message);
	}
	
	@Override
	public void onPlayerEntered(boolean isPlayer) {
		if(isPlayer){
			currentUser.setPlayerType(PLAYER_TYPE.PLAYER);
		}else{
			currentUser.setPlayerType(PLAYER_TYPE.AI);
		}
		
	}
	
	@Override
	public void onCanCalculateBattle() {
		battleFieldManager.startBattle();
		byte[] data = battleFieldManager.buildGameStateData();
		WarpController.getInstance().sendGameSynchronizeBytes(data);
		startBattle();
	}
	
	
	
	@Override
	public void onGameStateDataReceived(byte[] data) {
		GameStateData stateData = battleFieldManager.rebuildGameStateData(data);
		if(stateData.getUserNameForWarp()!=currentUser.getName()){
			if(stateData.getType()==GameStateData.TYPE_BATTLEFIELD){
				battleFieldManager.updateBattleField(stateData.getRemoteBattleField());
			}
		}
	}
	public Table getLayerOperation() {
		return layerOperation;
	}
	public Table getLayerAnimation() {
		return layerAnimation;
	}
	public StepAction getCurrentStepAction() {
		return currentStepAction;
	}
	public boolean isPlanning() {
		return isPlanning;
	}
	public void setPlanning(boolean isPlanning) {
		this.isPlanning = isPlanning;
	}
	public void setCurrentStepAction(StepAction currentStepAction) {
		this.currentStepAction = currentStepAction;
	}

}
