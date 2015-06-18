package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

import javax.swing.text.ChangedCharSetException;

import org.jfree.ui.Align;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.sz.zhsan2b.core.BattleField.State;
import com.sz.zhsan2b.core.Command;
import com.sz.zhsan2b.core.Command.ACTION_KIND;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.Position;
import com.sz.zhsan2b.core.StepAction;
import com.sz.zhsan2b.core.StepAction.TileEffect;
import com.sz.zhsan2b.core.Troop;
import com.sz.zhsan2b.libgdx.ConfirmationDialog.Confirmable;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;


public class TroopActor extends AnimatedImage {
	
	public enum TroopInputState {
		IDEL,CHOOSE_OBJECT
	}

	public class OnTroopMoveClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.clear();
			layerOperation.add(troopMoveRange);


		}

	}
	public class OnTroopAttackClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.findActor("menuList").remove();
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
					final Position tempP =  new Position(0, 0);
					RenderUtils.toLogicPosition(xuanze.getX(), xuanze.getY(),tempP);
					final Troop object = troop.getBattleField().getTroopByPosition(tempP);
					if(object!=null&&object.getOwner()==troop.getOwner()){
						battleFieldOperationStage.displayNotification("could not select troop of yourself!");
						return;
					}else{
						stage.removeListener(inputListener);
						ConfirmationDialog conD = new ConfirmationDialog(
								new Confirmable() {

									@Override
									public void confirm() {


										troop.getCommand().actionKind=ACTION_KIND.ATTACK;
										troop.getCommand().zhanfaId=0;
										if(object==null){
											troop.getCommand().object=null;
											troop.getCommand().objectPosition=tempP;
										}else{
											troop.getCommand().object=object;
										}
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

	}	

	public enum TROOP_ANIMATION_TYPE {
		WALK,ATTACK,BE_ATTACKED,CAST,BE_CAST
	}

	public static final String TAG = TroopActor.class.getName();
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	private final Vector2 tempCoords = new Vector2();
	//game logic properties
	private Troop troop;
	private Array<TroopActor> affectedTroopList;
	private boolean isDestoryed;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	//for integration
	private Vector2 position = new Vector2();
	private BattleFieldOperationStage battleFieldOperationStage =Zhsan2b.battleScreen.getBattleFieldOperationStage();
	private Table layerOperation = battleFieldOperationStage.getLayerOperation();
	private Stage stage = Zhsan2b.battleScreen.getStage();
	//troop range
	private Table troopAttackRange;
	private Table troopMoveRange;
	
	private TroopInputState troopInputState = TroopInputState.IDEL;
	
	//troop title
	
	private Table troopTitle;
	private int hpVisual; 
	private Image troopState;
	private Label hpVisualLabel;
	private Skin skinTroopTitle= Assets.instance.assetSkin.skinTroopTitle;
	

	public TroopActor(Troop troop) {
	
		this.troop = troop;
		hpVisual=troop.getHp();
		troopState = new Image(Assets.instance.assetTroop.actionUnDone);
		hpVisualLabel=new Label(String.valueOf(hpVisual), skinLibgdx);
		setWidth(Constants.WANGGE_UNIT_WIDTH);
		setHeight(Constants.WANGGE_UNIT_HEIGHT);
		affectedTroopList = null;
		isDestoryed = false;			
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(RenderUtils.getTroopAnimationBy(troop.getMilitaryKind().getId(), com.sz.zhsan2b.core.StepAction.FaceDirection.UP, TROOP_ANIMATION_TYPE.WALK));
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

	public void computeTroopAttackRange() {
		troopAttackRange = new Table();
		troopAttackRange.setLayoutEnabled(false);
		Array<Position> rangeList = troop.getAttackRangeList();
		for(Position p:rangeList){
			Image curImg = new Image(Assets.instance.assetWangge.red);
			curImg.setSize(Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT);
			curImg.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
			curImg.toBack();
			troopAttackRange.add(curImg);
		}
		
	}

	protected void onTroopClicked() {
	

		if(troop.getBattleField().state==State.OPERATE){
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
		hpTable.add(troopState).width(14f);
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
		shiqicao.setAlign(Align.LEFT); 
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

	public void parseStepAction(
			final BattleFieldAnimationStage battleFieldAnimationStage) {
		final StepAction currentStepAction = battleFieldAnimationStage.getCurrentStepAction();
		switch(currentStepAction.actionKind){
		case ATTACK:
		{
			//所有部队的动画都写在这里，不再用观察者模式了，没必要了。内容简单。
			
			setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.ATTACK));
			TileEffectActor effectActor = new TileEffectActor(currentStepAction.effects.get(currentStepAction.actionTroopId));
			effectActor.setPosition(getX(), getY());
			battleFieldAnimationStage.getLayerAnimation().add(effectActor);
			Integer tempInt=currentStepAction.damageMap.get(currentStepAction.actionTroopId);
			CombatNumberLabel damageLabel = new CombatNumberLabel(tempInt, true);
			if(tempInt.equals(0)){
				damageLabel.setVisible(false);
			}
			//add animation for damage hint
			damageLabel.addAction(sequence(Actions.color(Color.RED),moveTo(getX()+50,getY()+50),parallel(Actions.moveBy(0f,50f,Constants.ONE_STEP_TIME,Interpolation.linear))));
			battleFieldAnimationStage.getLayerAnimation().add(damageLabel);				
			

			TroopActor affectedTroopActor = null;
			final Array<TroopActor> affectedTroopActors = new Array<TroopActor>(currentStepAction.affectedTroopList.size);
			for(long i:currentStepAction.affectedTroopList){
				affectedTroopActor= battleFieldAnimationStage.getTroopActorByTroopId(i);
				affectedTroopActor.setAnimation(RenderUtils.getTroopAnimationBy(affectedTroopActor.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.BE_ATTACKED));
				affectedTroopActors.add(affectedTroopActor);
				effectActor=new TileEffectActor(currentStepAction.effects.get(i));
				effectActor.setPosition(affectedTroopActor.getX(), affectedTroopActor.getY());
				battleFieldAnimationStage.getLayerAnimation().add(effectActor);
				damageLabel = new CombatNumberLabel(currentStepAction.damageMap.get(i), true);
				damageLabel.addAction(sequence(Actions.color(Color.RED),moveTo(affectedTroopActor.getX()+50,affectedTroopActor.getY()+50),parallel(Actions.moveBy(0f,50f,Constants.ONE_STEP_TIME,Interpolation.linear))));
				battleFieldAnimationStage.getLayerAnimation().add(damageLabel);
			}
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			RunnableAction runAction = run(new Runnable() {
				public void run() {
					setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.WALK));		
					modifyHpVisual(currentStepAction.damageMap.get(currentStepAction.actionTroopId));
					for(TroopActor trA:affectedTroopActors){
						trA.setAnimation(RenderUtils.getTroopAnimationBy(trA.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.WALK));
					    trA.modifyHpVisual(currentStepAction.damageMap.get(trA.getTroop().getId()));
					}
					Zhsan2b.battleScreen.parseTroopsEffect(currentStepAction.effects);
					//remove layerAnimation
					battleFieldAnimationStage.getLayerAnimation().clear();
					battleFieldAnimationStage.setPlanning(true);
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
	
						battleFieldAnimationStage.setPlanning(true);


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

}
