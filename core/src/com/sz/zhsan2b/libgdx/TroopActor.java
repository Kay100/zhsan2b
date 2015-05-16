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
	
	public static final String TAG = TroopActor.class.getName();
	//game logic properties
	private Troop troop;
	private Array<TroopActor> affectedTroopList;
	private boolean isDestoryed;

	private Animation animFaceUpWalk;
//	private Animation animAttack;
//	private Animation animUnderAttack;
	
	
	

	public TroopActor(Troop troop) {
		this.troop = troop;
		init();


	}

	public void init() {
		affectedTroopList = null;
		isDestoryed = false;		
		animFaceUpWalk = Assets.instance.assetTroop.animFaceUpWalk;		
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(animFaceUpWalk);
		
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
		StepAction currentStepAction = battleFieldAnimationStage.getCurrentStepAction();
		switch(currentStepAction.actionKind){
		case ATTACK:
			//所有部队的动画都写在这里，不再用观察者模式了，没必要了。内容简单。
			
			break;
		case CAST:
			break;
		case MOVE:
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
			
			break;
		case NONE:
			break;
		default:
			break;
		
		}
		
		
		
	}

}
