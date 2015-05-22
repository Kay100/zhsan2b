package com.sz.zhsan2b.libgdx;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.BattleField.State;
import com.sz.zhsan2b.core.StepAction;
import com.sz.zhsan2b.core.StepActionHandler;

public class BattleFieldAnimationStage implements StepActionHandler {
	private static Logger logger = LoggerFactory.getLogger(TroopActor.class);
	private final Array<StepAction> stepActionList = new Array<StepAction>(100);
	private StepAction currentStepAction;

	private Iterator<StepAction> stepActionIter;
	private boolean isPlanning;
	private final BattleField battleField;
	private final Array<TroopActor> troopActorList;
	
	public BattleFieldAnimationStage(BattleScreen battleScreen) {
		this.battleField = battleScreen.getBattleField();
		isPlanning = true;
		troopActorList = battleScreen.getTroopActorList();
		
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

	public void parseStepActions() {
		if(isPlanning){
			//parse
			if(stepActionIter.hasNext()){
				currentStepAction = stepActionIter.next();
				getCurrentStepTroopActor(currentStepAction.actionTroopId).parseStepAction(this);
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
		this.battleField.state=State.OPERATE;
		
	}
}
