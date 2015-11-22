package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.sz.zhsan2b.core.BattleFieldManager;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.StepAction.TileEffect;

public class BattleFieldAnimationStage {
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	
	private StepAction currentStepAction;
	private final BattleScreen battleScreen;
	private Iterator<StepAction> stepActionIter;
	private boolean isPlanning;
	private final Array<TroopActor> troopActorList;
	private Table layerAnimation;
	
	private BattleFieldManager battleFieldManager;
	
	public BattleFieldAnimationStage(BattleScreen battleScreen) {
		this.battleScreen= battleScreen;
		isPlanning = true;
		troopActorList = battleScreen.getTroopActorList();
		layerAnimation = new Table();
		layerAnimation.setLayoutEnabled(false);
		//layerAnimation.setTransform(false);
		battleFieldManager=GameContext.getContext().getBean(BattleFieldManager.class);
		
	}
	public void initStepActionIter(){
		stepActionIter = battleScreen.getBattleField().getStepActionList().iterator();
	}

	public StepAction getCurrentStepAction() {
		return currentStepAction;
	}

	public void setCurrentStepAction(StepAction currentStepAction) {
		this.currentStepAction = currentStepAction;
	}

	public boolean isPlanning() {
		return isPlanning;
	}

	public void setPlanning(boolean isPlanning) {
		this.isPlanning = isPlanning;
	}

	public Stage getStage() {
		return battleScreen.getStage();
	}
	public Table getLayerAnimation() {
		return layerAnimation;
	}
	public void setLayerAnimation(Table layerAnimation) {
		this.layerAnimation = layerAnimation;
	}
	public void parseStepActions() {
		if(isPlanning){
			//parse
			if(stepActionIter.hasNext()){
				currentStepAction = stepActionIter.next();
				//add camera follow
				CameraHelper ch =battleScreen.getWorldController().cameraHelper;
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
				battleScreen.startOperate();
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
	public void startBattle() {
		battleScreen.getBattleField().clearStepActionList();
		battleScreen.getBattleField().refresh();
		battleFieldManager.calculateBattle();
		for(TroopActor trA:battleScreen.getTroopActorList()){
			trA.hideActionLabel();
		}
		
		initStepActionIter();
		
	}
	public void displayTileEffects(ArrayMap<Long,TileEffect> effects){
		for(Entry<Long,TileEffect> en:effects){
			TileEffectActor effectActor = new TileEffectActor(effects.get(en.key));
			TroopActor trA = getTroopActorByTroopId(en.key);
			effectActor.setPosition(trA.getX(), trA.getY());
			getLayerAnimation().add(effectActor);				
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
			getLayerAnimation().add(damageLabel);				
		}
	}
	public void modifyTroopsHpVisual(ArrayMap<Long,Integer> damages){
		for(Entry<Long,Integer>en:damages){
			TroopActor trA = getTroopActorByTroopId(en.key);
		    trA.modifyHpVisual(damages.get(trA.getTroop().getId()));	
		}
	
	}
}
