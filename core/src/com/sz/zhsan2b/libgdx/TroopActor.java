package com.sz.zhsan2b.libgdx;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.core.StepAction;
import com.sz.zhsan2b.core.Troop;


public class TroopActor extends AnimatedImage {
	
	public enum TROOP_ANIMATION_TYPE {
		WALK,ATTACK,BE_ATTACKED,CAST,BE_CAST
	}

	public static final String TAG = TroopActor.class.getName();
	//game logic properties
	private Troop troop;
	private Array<TroopActor> affectedTroopList;
	private boolean isDestoryed;

	
	
	

	public TroopActor(Troop troop) {
		this.troop = troop;
		init();


	}

	public void init() {
		affectedTroopList = null;
		isDestoryed = false;			
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(RenderUtils.getTroopAnimationBy(troop.getMilitaryKind().getId(), com.sz.zhsan2b.core.StepAction.FaceDirection.UP, TROOP_ANIMATION_TYPE.WALK));
		
	}

	public Troop getTroop() {
		return troop;
	}

	public void setTroop(Troop troop) {
		this.troop = troop;
	}

	public Array<TroopActor> getAffectedTroopList() {
		return affectedTroopList;
	}

	public void setAffectedTroopList(Array<TroopActor> affectedTroopList) {
		this.affectedTroopList = affectedTroopList;
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
			//still has effects need to dispose.

			setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.ATTACK));
			TroopActor affectedTroopActor = null;
			final Array<TroopActor> affectedTroopActors = new Array<TroopActor>(currentStepAction.affectedTroopList.size);
			for(long i:currentStepAction.affectedTroopList){
				affectedTroopActor= battleFieldAnimationStage.getTroopActorByTroopId(i);
				affectedTroopActor.setAnimation(RenderUtils.getTroopAnimationBy(affectedTroopActor.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.BE_ATTACKED));
				affectedTroopActors.add(affectedTroopActor);
			}
			float x = RenderUtils.translate(currentStepAction.orginPosition.x);
			float y = RenderUtils.translate(currentStepAction.orginPosition.y);
			RunnableAction runAction = run(new Runnable() {
				public void run() {
					setAnimation(RenderUtils.getTroopAnimationBy(currentStepAction.militaryKindId, currentStepAction.faceDirection, TROOP_ANIMATION_TYPE.WALK));
					for(TroopActor trA:affectedTroopActors){
						trA.setAnimation(RenderUtils.getTroopAnimationBy(trA.getTroop().getMilitaryKind().getId(), RenderUtils.getOppositeFaceDirection(currentStepAction.faceDirection), TROOP_ANIMATION_TYPE.WALK));
					}
					battleFieldAnimationStage.setPlanning(true);
					battleFieldAnimationStage.nextStep();
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
					battleFieldAnimationStage.nextStep();
				}
			});
			
			//分析stepAction 处理朝向
			addAction(sequence(moveTo(x,y),moveTo(toX, toY, Constants.ONE_STEP_TIME, Interpolation.linear),runAction));
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


}
