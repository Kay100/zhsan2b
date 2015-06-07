package com.sz.zhsan2b.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.sz.zhsan2b.core.Command.ACTION_KIND;

public class StepAction {

	public enum FaceDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public enum TileEffect {
		NONE, RECOVER, RESIST,DESTROY, HUOSHI, LEITING, RAOLUAN, BOOST, CRITICAL, CHAOS, GUANCHUAN, SURROUNDED, STUNT,FIRE
	}

	public long actionTroopId;
	public ACTION_KIND actionKind;
	public Position objectPosition; // 寻路算法计算出的下一步的目标位置
	public Position orginPosition;
	public FaceDirection faceDirection;
	public long militaryKindId;
	public Array<Long> affectedTroopList;//用于部队自身动画
	public boolean isVisible;
	public ArrayMap<Long, Integer> damageMap;
	public ArrayMap<Long, TileEffect> effects;//用于各种效果,里面的效果都发生在同一个step周期内

	public StepAction(long actionTroopId) {
		this(actionTroopId, ACTION_KIND.NONE, new Position(0, 0), new Position(0, 0),
				FaceDirection.RIGHT, 0, new Array<Long>(), true,
				new ArrayMap<Long, Integer>(), new ArrayMap<Long, TileEffect>());
	}

	public StepAction(long actionTroopId, ACTION_KIND actionKind,
			Position objectPosition,Position orginPosition, FaceDirection faceDirection,
			long militaryKindId, Array<Long> affectedTroopList,
			boolean isVisible, ArrayMap<Long, Integer> damageMap,
			ArrayMap<Long, TileEffect> effects) {
		super();
		this.actionTroopId = actionTroopId;
		this.actionKind = actionKind;
		this.objectPosition = objectPosition;
		this.orginPosition=orginPosition;
		this.faceDirection = faceDirection;
		this.militaryKindId = militaryKindId;
		this.affectedTroopList = affectedTroopList;
		this.isVisible = isVisible;
		this.damageMap = damageMap;
		this.effects = effects;
	}
	
	public void setStepAction(long actionTroopId, ACTION_KIND actionKind,
			Position objectPosition, FaceDirection faceDirection,
			long militaryKindId, Array<Long> affectedTroopList,
			boolean isVisible, ArrayMap<Long, Integer> damageMap,
			ArrayMap<Long, TileEffect> effects) {
		this.actionTroopId = actionTroopId;
		this.actionKind = actionKind;
		this.objectPosition = objectPosition;
		this.faceDirection = faceDirection;
		this.militaryKindId = militaryKindId;
		this.affectedTroopList = affectedTroopList;
		this.isVisible = isVisible;
		this.damageMap = damageMap;
		this.effects = effects;
	}

	@Override
	public String toString() {
		String effectsString="{";
		for(Entry<Long,TileEffect> entry:effects){
			effectsString+="["+entry.key+","+entry.value+"]";
		}
		effectsString+="}";
		
		return "StepAction [actionTroopId=" + actionTroopId + ", actionKind="
				+ actionKind + ", objectPosition=" + objectPosition
				+ ", orginPosition=" + orginPosition + ", faceDirection="
				+ faceDirection + ", militaryKindId=" + militaryKindId
				+ ", affectedTroopList=" + affectedTroopList + ", isVisible="
				+ isVisible + ", damageMap=" + damageMap + ", effects="
				+ effectsString + "]";
	}	
	

}
