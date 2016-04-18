package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;
import com.sz.zhsan2b.core.entity.User.PLAYER_TYPE;
import com.sz.zhsan2b.core.service.TroopManager;


public class TroopActor extends AnimatedImage {

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
	

	//game logic properties
	private Troop troop;
	private List<TroopActor> affectedTroopList;
	private boolean isDestoryed;
	private Skin skinLibgdx = Assets.instance.assetSkin.skinLibgdx;
	//for integration
	private Vector2 position = new Vector2();	
	private Table layerOperation = battleScreen.getLayerOperation();
	private Table layerAnimation = battleScreen.getLayerAnimation();
	private UserCommandHandler userCommandHandler;
	private TroopMenu troopMenu;


	
	//troop title
	
	private Table troopTitle;
	private int hpVisual; 
	private Image actionLabel;
	private Label hpVisualLabel;
	private Skin skinTroopTitle= Assets.instance.assetSkin.skinTroopTitle;	



	

	public TroopActor(Troop troop) {
		troopManager=GameContext.getContext().getBean(TroopManager.class);
		userCommandHandler = Zhsan2b.battleScreen.getUserCommandHandler();
		troopMenu = Zhsan2b.battleScreen.getTroopMenu();
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



	protected void onTroopClicked() {
//		if(GameContext.getCurrentUser().getPlayerType()!=troop.getOwner()){
//			logger.debug(GameContext.getCurrentUser().getPlayerType().toString()+":"+troop.getOwner());
//			return;
//		}

		if(troopManager.getBattleField().state==State.OPERATE){
		   troopMenu.show(this);
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

	public List<TroopActor> getAffectedTroopList() {
		return affectedTroopList;
	}

	public void setAffectedTroopList(List<TroopActor> affectedTroopList) {
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
			final List<TroopActor> affectedTroopActors = new ArrayList<TroopActor>(currentStepAction.affectedTroopList.size);
			for(long i:currentStepAction.affectedTroopList){
				affectedTroopActor= battleScreen.getTroopActorByTroopId(i);
				affectedTroopActor.setAnimation(RenderUtils.getTroopAnimationBy(affectedTroopActor.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.BE_ATTACKED));
				affectedTroopActors.add(affectedTroopActor);
			}
			List<Position> damageRangeList = (List<Position>)currentStepAction.ext.get("damageRangeArea");
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
