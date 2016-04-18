package com.sz.zhsan2b.core.entity;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.utils.ArrayMap;

public class TroopStepVO {
	public enum FaceDirection {
		UP, DOWN, LEFT, RIGHT
	}	
	public enum ANIMATION_TYPE{
		WALK,ATTACK,BE_ATTACK
	}
	public enum TileEffect {
		NONE, RECOVER, RESIST,DESTROY, HUOSHI, LEITING, RAOLUAN, BOOST, CRITICAL, CHAOS, GUANCHUAN, SURROUNDED, STUNT,FIRE
	}	
	private long troopId;
	private ANIMATION_TYPE animationType;
	private Position objectPosition = new Position(0, 0); // 寻路算法计算出的下一步的目标位置
	private Position orginPosition = new Position(0, 0);	
	private FaceDirection faceDirection;
	private int tempDamage;
	private List<TileEffect> effectList= new ArrayList<TroopStepVO.TileEffect>();
	public ArrayMap<String,Object> ext = new ArrayMap<String, Object>();
	public long getTroopId() {
		return troopId;
	}
	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}
	public ANIMATION_TYPE getAnimationType() {
		return animationType;
	}
	public void setAnimationType(ANIMATION_TYPE animationType) {
		this.animationType = animationType;
	}
	public Position getObjectPosition() {
		return objectPosition;
	}
	public void setObjectPosition(Position objectPosition) {
		this.objectPosition = objectPosition;
	}
	public Position getOrginPosition() {
		return orginPosition;
	}
	public void setOrginPosition(Position orginPosition) {
		this.orginPosition = orginPosition;
	}
	public FaceDirection getFaceDirection() {
		return faceDirection;
	}
	public void setFaceDirection(FaceDirection faceDirection) {
		this.faceDirection = faceDirection;
	}
	public int getTempDamage() {
		return tempDamage;
	}
	public void setTempDamage(int tempDamage) {
		this.tempDamage = tempDamage;
	}
	public List<TileEffect> getEffectList() {
		return effectList;
	}
	public void setEffectList(List<TileEffect> effectList) {
		this.effectList = effectList;
	}
	public ArrayMap<String, Object> getExt() {
		return ext;
	}
	public void setExt(ArrayMap<String, Object> ext) {
		this.ext = ext;
	}	

}
