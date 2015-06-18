package com.sz.zhsan2b.libgdx;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.BattleField.State;
import com.sz.zhsan2b.core.StepAction;
import com.sz.zhsan2b.core.StepActionHandler;

public class BattleFieldAnimationStage implements StepActionHandler {
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	private final Array<StepAction> stepActionList = new Array<StepAction>(100);
	private StepAction currentStepAction;
	private final BattleScreen battleScreen;
	private Iterator<StepAction> stepActionIter;
	private boolean isPlanning;
	private final Array<TroopActor> troopActorList;
	private Table layerAnimation;
	
	public BattleFieldAnimationStage(BattleScreen battleScreen) {
		this.battleScreen= battleScreen;
		isPlanning = true;
		troopActorList = battleScreen.getTroopActorList();
		layerAnimation = new Table();
		layerAnimation.setLayoutEnabled(false);
		//layerAnimation.setTransform(false);

		
	}
	public void initStepActionIter(){
		stepActionIter = stepActionList.iterator();
	}

	@Override
	public Array<StepAction> getStepActionList() {
		
		return stepActionList;
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

				trA.parseStepAction(this);
			}else{
				transferToOperate();
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
	public void transferToOperate() {
		battleScreen.getWorldController().cameraHelper.setTarget(null);
		battleScreen.getBattleField().deleteDestroyedTroops();
		battleScreen.getBattleField().state=State.OPERATE;
		battleScreen.startOperate();
		
	}
	public void startBattle() {
		stepActionList.clear();
		battleScreen.getBattleField().refresh();
		battleScreen.getBattleField().calculateBattle();
		initStepActionIter();
		
	}
}
