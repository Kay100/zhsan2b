package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.sz.zhsan2b.appwarp.WarpController;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.TroopManager;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.entity.StepAction.TileEffect;
import com.sz.zhsan2b.libgdx.ConfirmationDialog.Confirmable;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;
import com.sz.zhsan2b.libgdx.TroopActor.ACTION_LABEL;


public class TroopActor extends AnimatedImage {
	public enum TroopInputState {
		IDEL,CHOOSE_OBJECT
	}
	public enum ACTION_LABEL {
		UNDONE,DONE,AUTO,AUTODONE
	}
	public enum TROOP_ANIMATION_TYPE {
		WALK,ATTACK,BE_ATTACKED,CAST,BE_CAST
	}

	public static final String TAG = TroopActor.class.getName();
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	BattleScreen battleScreen = Zhsan2b.battleScreen;
	private TroopManager troopManager;
	
	private final Vector2 tempCoords = new Vector2();
	//game logic properties
	private Troop troop;
	private Array<TroopActor> affectedTroopList;
	private boolean isDestoryed;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	//for integration
	private Vector2 position = new Vector2();	
	private Table layerOperation = battleScreen.getLayerOperation();
	private Table layerAnimation = battleScreen.getLayerAnimation();
	private Stage stage = Zhsan2b.battleScreen.getStage();
	//troop range
	private Table troopAttackRange;
	private Table troopMoveRange;
	
	private TroopInputState troopInputState = TroopInputState.IDEL;
	
	//troop title
	
	private Table troopTitle;
	private int hpVisual; 
	private Image actionLabel;
	private Label hpVisualLabel;
	private Skin skinTroopTitle= Assets.instance.assetSkin.skinTroopTitle;	
	public abstract class OnTroopActionClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.findActor("menuList").remove();
			if(isDisplayRange()){
				displayRangeArea();
			}
			
			Vector2 worldP = stage.screenToStageCoordinates(tempCoords.set(Gdx.input.getX(), Gdx.input.getY()));
			final Image xuanze = new Image(Assets.instance.assetWangge.xuanze);
			xuanze.setPosition(((int)(worldP.x/Constants.WANGGE_UNIT_WIDTH))*Constants.WANGGE_UNIT_WIDTH, ((int)(worldP.y/Constants.WANGGE_UNIT_HEIGHT))*Constants.WANGGE_UNIT_HEIGHT);
			layerOperation.add(xuanze);
			Gdx.input.setCursorImage(Assets.instance.assetArrow.select, 0, 0);
			final InputListener inputListener = new InputListener(){

				@Override
				public boolean mouseMoved(InputEvent event, float x, float y) {
					xuanze.setPosition(((int)(x/Constants.WANGGE_UNIT_WIDTH))*Constants.WANGGE_UNIT_WIDTH, ((int)(y/Constants.WANGGE_UNIT_HEIGHT))*Constants.WANGGE_UNIT_HEIGHT);
					return super.mouseMoved(event, x, y);
				}
				
			};
			stage.addListener(inputListener);	
			xuanze.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.input.setCursorImage(Assets.instance.assetArrow.normal,
							0, 0);
					final Position xuanzePosition =  new Position(0, 0);
					RenderUtils.toLogicPosition(xuanze.getX(), xuanze.getY(),xuanzePosition);
					if(isSelectedRightPosition(xuanzePosition)){
						stage.removeListener(inputListener);
						
						ConfirmationDialog conD = new ConfirmationDialog(
								new Confirmable() {

									@Override
									public void confirm() {
										Troop object = troopManager.getBattleField().getTroopByPosition(
												xuanzePosition);

										disposeToAddCommand(object,xuanzePosition);
										synchronizeTroopCommand(troop);
										setActionLabel(ACTION_LABEL.DONE);
										//Gdx.app.debug(TAG, troop.getCommand().toString());
										layerOperation.clear();
									}

									@Override
									public void cancel() {
										layerOperation.clear();
									}
								});						
					layerOperation.add(
							conD.combined);
					conD.combined.setPosition(
							stage.getCamera().position.x - 120,
							stage.getCamera().position.y - 30);						
						
					}
					
					super.clicked(event, x, y);
				}

				


				
			});
			
			

		}

		abstract protected boolean isDisplayRange() ;
		abstract protected void displayRangeArea();
		abstract protected boolean isSelectedRightPosition(final Position xuanzePosition);
		abstract protected void disposeToAddCommand(final Troop object,final Position xuanzePosition);

	}	

	public class OnTroopMoveClicked extends OnTroopActionClicked {

		@Override
		protected boolean isDisplayRange() {
			return true;
		}

		@Override
		protected void displayRangeArea() {
			computerTroopMoveRange();
			//Gdx.app.debug(TAG, String.valueOf(troopMoveRange.getChildren().size));
			layerOperation.add(troopMoveRange);
			
		}

		@Override
		protected void disposeToAddCommand(final Troop object,final Position xuanzePosition) {

			troop.getCommand().actionKind = ACTION_KIND.MOVE;
			troop.getCommand().zhanfaId = 0;
			if (object == null) {
				troop.getCommand().object = null;
				troop.getCommand().objectPosition = xuanzePosition;
			} else {
				troop.getCommand().object = object;
			}
		}



		@Override
		protected boolean isSelectedRightPosition(Position xuanzePosition) {
			return true;
		}

	}
	public class OnTroopAttackClicked extends OnTroopActionClicked {
		@Override
		protected boolean isSelectedRightPosition(final Position xuanzePosition) {
			final Troop object = troopManager.getBattleField().getTroopByPosition(xuanzePosition);	
			if(object!=null&&object.getOwner()==troop.getOwner()){
				Zhsan2b.battleScreen.displayNotification("不能选择己方部队!");
				return false;
			}else{
				return true;
			}	
		}		
		@Override
		public void disposeToAddCommand(final Troop object,final Position xuanzePosition) {
			troop.getCommand().actionKind = ACTION_KIND.ATTACK;
			troop.getCommand().zhanfaId = 0;
			if (object == null) {
				troop.getCommand().object = null;
				troop.getCommand().objectPosition = xuanzePosition;
			} else {
				troop.getCommand().object = object;
			}

		}

		@Override
		protected boolean isDisplayRange() {
			return false;
		}

		@Override
		protected void displayRangeArea() {
			computeTroopAttackRange();
			layerOperation.add(troopAttackRange);			
		}


	}	

	private void synchronizeTroopCommand(Troop troop) {
		String data = troopManager.buildTroopCommandJSON(troop);
		WarpController.getInstance().sendGameUpdate(data);
	}
	

	public TroopActor(Troop troop) {
		troopManager=GameContext.getContext().getBean(TroopManager.class);
		this.troop = troop;
		hpVisual=troop.getHp();
		actionLabel = new Image(Assets.instance.assetTroop.actionUnDone);
		hpVisualLabel=new Label(String.valueOf(hpVisual), skinLibgdx);
		setWidth(Constants.WANGGE_UNIT_WIDTH);
		setHeight(Constants.WANGGE_UNIT_HEIGHT);
		affectedTroopList = null;
		isDestoryed = false;			
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(RenderUtils.getTroopAnimationBy(troop.getMilitaryKind().getId(), com.sz.zhsan2b.core.entity.StepAction.FaceDirection.UP, TROOP_ANIMATION_TYPE.WALK));
		addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
	
				super.clicked(event, x, y);
				onTroopClicked();	
			}
			
		});
		//build troopTitle
		troopTitle=buildTroopTitle();
	}

	public void computerTroopMoveRange() {
		troopMoveRange = new Table();
		troopMoveRange.setLayoutEnabled(false);
		Array<Position> rangeList = troopManager.getMoveRangeList(troop,troop.getPosition(),troop.getBattleProperties().move,null);
		for(Position p:rangeList){
			Image curImg = new Image(Assets.instance.assetWangge.blue);
			curImg.setSize(Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT);
			curImg.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
			curImg.toBack();
			troopMoveRange.add(curImg);
		}
		
	}

	public void computeTroopAttackRange() {
		troopAttackRange = new Table();
		troopAttackRange.setLayoutEnabled(false);
		Array<Position> rangeList = troopManager.getAttackRangeList(troop);
		for(Position p:rangeList){
			Image curImg = new Image(Assets.instance.assetWangge.red);
			curImg.setSize(Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT);
			curImg.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
			curImg.toBack();
			troopAttackRange.add(curImg);
		}
		
	}

	protected void onTroopClicked() {
		if(GameContext.getCurrentUser().getPlayerType()!=troop.getOwner()){
			logger.debug(GameContext.getCurrentUser().getPlayerType().toString()+":"+troop.getOwner());
			return;
		}

		if(troopManager.getBattleField().state==State.OPERATE){
			layerOperation.clear();

			computeTroopAttackRange();
			layerOperation.add(troopAttackRange);				
			
			
			MenuCommand attackCommand =new MenuCommand("attack", false, new OnTroopAttackClicked());
			attackCommand.addMenuList(new MenuCommand("plan", false, null),new MenuCommand("occupy", false, null));
			ContextMenu  menu = new ContextMenu(layerOperation,true,new MenuCommand("move", false, new OnTroopMoveClicked()),attackCommand,new MenuCommand("done", false, null));
			//menu.disableButtonByName("move");
			//((Button)menu.combined.findActor("move")).getStyle().pressedOffsetX=20f;
			menu.setPosition(getX()+100,getY());	
		}
	
		
	}

	private Table buildTroopTitle() {
		Table returnTable = new Table();	
		returnTable.setLayoutEnabled(false);
//		returnTable.setTransform(true);
//		returnTable.setScaleX(0.8f);
//		returnTable.setScaleY(0.6f);
		Image back =new Image(Assets.instance.assetTroop.background);
		back.setSize(120, 70);
		back.setPosition(30, 0);
		returnTable.add(back);
		Table infoTable = new Table();
		returnTable.add(infoTable);
		infoTable.setSize(150f, 70f);
		infoTable.setPosition(0, 0);
		Image faction = new Image(Assets.instance.assetTroop.faction);
		if(getTroop().getOwner()==PLAYER_TYPE.PLAYER){
			faction.setColor(Color.BLUE);
		}else{
			faction.setColor(Color.RED);
		}
		infoTable.add(faction).width(30f).height(40f).top();
		Image portrait = new Image(Assets.instance.assetPerson.smallPersonPortraits.firstValue());
		infoTable.add(portrait).width(50f).left();
		Table hpTable = new Table();
		hpTable.add(hpVisualLabel).width(50).left();
		hpTable.add(actionLabel).width(14f);
		hpTable.row();
		Label name = new Label("zhangfei",skinLibgdx);
		hpTable.add(name).colspan(2).center();
		infoTable.add(hpTable).width(70);
		infoTable.row();
		infoTable.add();
		Table shiqiTable = new Table();
		infoTable.add(shiqiTable).colspan(2).height(8f).width(120f);
		Image shiqicao = new Image(Assets.instance.assetTroop.shiqicao);
		Image shiqitiao = new Image(Assets.instance.assetTroop.shiqitiao);
		shiqiTable.setLayoutEnabled(false);
		shiqiTable.add(shiqicao);
		shiqiTable.add(shiqitiao);
		shiqicao.setAlign(Align.left); 
		shiqicao.setWidth(120f);
		shiqitiao.setPosition(2f,2f);
		shiqitiao.setWidth(50f);

		returnTable.setX(getX()+30f);
		returnTable.setY(getY()+60f);
		return returnTable;
	}

	public Troop getTroop() {
		return troop;
	}

	public void setTroop(Troop troop) {
		this.troop = troop;
	}

	public Vector2 getPosition() {
		return position;
	}

	public Array<TroopActor> getAffectedTroopList() {
		return affectedTroopList;
	}

	public void setAffectedTroopList(Array<TroopActor> affectedTroopList) {
		this.affectedTroopList = affectedTroopList;
	}

	public Table getTroopTitle() {
		return troopTitle;
	}

	public boolean isDestoryed() {
		return isDestoryed;
	}

	public void setDestoryed(boolean isDestoryed) {
		this.isDestoryed = isDestoryed;
	}

	public void parseStepAction() {
		final StepAction currentStepAction = battleScreen.getCurrentStepAction();
		switch(currentStepAction.actionKind){
		case ATTACK:
		{
			//所有部队的动画都写在这里，不再用观察者模式了，没必要。内容很简单。
			
			setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.ATTACK));
			battleScreen.displayTileEffects(currentStepAction.effects);
			battleScreen.displayCombatNumbers(currentStepAction.damageMap);
			

			TroopActor affectedTroopActor = null;
			final Array<TroopActor> affectedTroopActors = new Array<TroopActor>(currentStepAction.affectedTroopList.size);
			for(long i:currentStepAction.affectedTroopList){
				affectedTroopActor= battleScreen.getTroopActorByTroopId(i);
				affectedTroopActor.setAnimation(RenderUtils.getTroopAnimationBy(affectedTroopActor.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.BE_ATTACKED));
				affectedTroopActors.add(affectedTroopActor);
			}
			Array<Position> damageRangeList = (Array<Position>)currentStepAction.ext.get("damageRangeArea");
			if(damageRangeList!=null){
				for(Position p:damageRangeList){
					Pixmap redBlock = RenderUtils.getRedBlockPixmap();
					Image damagePosition = new Image(new Texture(redBlock));
					damagePosition.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
					layerAnimation.add(damagePosition);
				}	
			}
			
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			RunnableAction runAction = run(new Runnable() {
				public void run() {
					setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.WALK));		
					for(TroopActor trA:affectedTroopActors){
						trA.setAnimation(RenderUtils.getTroopAnimationBy(trA.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.WALK));
					}
					battleScreen.modifyTroopsHpVisual(currentStepAction.damageMap);
					Zhsan2b.battleScreen.parseTroopsEffect(currentStepAction.effects);
					//remove layerAnimation
					battleScreen.getLayerAnimation().clear();
					if(currentStepAction.next==null){
						battleScreen.setPlanning(true);
					}else{
						battleScreen.setCurrentStepAction(currentStepAction.next);
						TroopActor trA = battleScreen.getTroopActorByTroopId(currentStepAction.next.actionTroopId);
						trA.parseStepAction();
					}
					

				}
			});			
			addAction(sequence(moveTo(x, y),delay(Constants.ONE_STEP_TIME,runAction)));
		}	
			break;
		case CAST:
			break;
		case MOVE:
		{
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			float toX = RenderUtils.translate(currentStepAction.objectPosition.x);
			float toY = RenderUtils.translate(currentStepAction.objectPosition.y);
			
			RunnableAction runAction = run(new Runnable() {
				public void run() {
	
					battleScreen.setPlanning(true);


				}
			});
			
			//分析stepAction 处理朝向
			addAction(sequence(moveTo(x,y),moveTo(toX, toY, Constants.ONE_STEP_TIME, Interpolation.linear),runAction));
//			logger.debug(currentStepAction.orginPosition.toString());
//			logger.debug(currentStepAction.objectPosition.toString());
			Animation temp = RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId,currentStepAction.faceDirection,TROOP_ANIMATION_TYPE.WALK);
			setAnimation(temp);
		}
			break;
		case NONE:
		{
			battleScreen.displayTileEffects(currentStepAction.effects);
			
			battleScreen.displayCombatNumbers(currentStepAction.damageMap);
			


			RunnableAction runAction = run(new Runnable() {
				public void run() {
					battleScreen.parseTroopsEffect(currentStepAction.effects);	
					battleScreen.getLayerAnimation().clear();
					if(currentStepAction.next==null){
						battleScreen.setPlanning(true);
					}else{
						battleScreen.setCurrentStepAction(currentStepAction.next);
						TroopActor trA = battleScreen.getTroopActorByTroopId(currentStepAction.next.actionTroopId);
						trA.parseStepAction();
					}



				}
			});			
			addAction(Actions.delay(Constants.ONE_STEP_TIME, runAction));
		}
			break;
		default:
			break;
		
		}
		
		
		
	}

	protected void modifyHpVisual(int damage) {
		hpVisual-=damage;
		if(hpVisual<=0){
			hpVisual=0;
		}
		hpVisualLabel.setText(String.valueOf(hpVisual));	
		
	}

	@Override
	public void act(float delta) {
		position.set(getX(), getY());
		super.act(delta);
	}

	public void hideActionLabel() {
		actionLabel.setVisible(false);
		
	}

	public void setActionLabel(ACTION_LABEL actionL) {
		actionLabel.setVisible(true);
		switch(actionL){
		case AUTO:
			((TextureRegionDrawable) actionLabel.getDrawable()).setRegion(Assets.instance.assetTroop.actionAuto);
			break;
		case AUTODONE:
			((TextureRegionDrawable) actionLabel.getDrawable()).setRegion(Assets.instance.assetTroop.actionAutoDone);
			break;
		case DONE:
			((TextureRegionDrawable) actionLabel.getDrawable()).setRegion(Assets.instance.assetTroop.actionDone);
			setTouchable(Touchable.disabled);
			break;
		case UNDONE:
			((TextureRegionDrawable) actionLabel.getDrawable()).setRegion(Assets.instance.assetTroop.actionUnDone);
			setTouchable(Touchable.enabled);
			break;
		default:
			break;
		
		}
		
	}

}
