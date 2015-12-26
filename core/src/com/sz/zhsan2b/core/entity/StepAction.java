package com.sz.zhsan2b.core.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;

public class StepAction {

	public enum FaceDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public enum TileEffect {
		NONE, RECOVER, RESIST,DESTROY, HUOSHI, LEITING, RAOLUAN, BOOST, CRITICAL, CHAOS, GUANCHUAN, SURROUNDED, STUNT,FIRE
	}

	public long actionTroopId;
	public ACTION_KIND actionKind;
	public Position objectPosition = new Position(0, 0); // 寻路算法计算出的下一步的目标位置
	public Position orginPosition = new Position(0, 0);
	public FaceDirection faceDirection;
	public long militaryKindId;
	public Array<Long> affectedTroopList = new Array<Long>();//用于部队自身动画
	public boolean isVisible;
	public ArrayMap<Long, Integer> damageMap = new ArrayMap<Long, Integer>();
	public ArrayMap<Long, TileEffect> effects = new ArrayMap<Long, StepAction.TileEffect>();//用于各种效果,里面的效果都发生在同一个step周期内
	public ArrayMap<String,Object> ext = new ArrayMap<String, Object>();
	public StepAction next;

	@Override
	public String toString() {
/*		String effectsString="{";
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
				+ effectsString + "]";*/
		return ToStringBuilder.reflectionToString(this);
	}	
	

}
