package com.sz.zhsan2b.core.entity;

import com.sz.zhsan2b.core.entity.Troop.BATTLE_STATE;
import com.sz.zhsan2b.core.entity.TroopStepVO.FaceDirection;

//POJO for VO
public class TroopVo {
	private long troopId;
	private long militaryKindId;
	private int hp ;
	private int ack;
	private int def;
	private int speed ;
	private int move ;
	
	//troop data
	private int leftHp;
	private int leftMove;

	private BATTLE_STATE battleState;//部队所处的状态
	private FaceDirection faceDirection;	

	public long getTroopId() {
		return troopId;
	}

	public void setTroopId(long troopId) {
		this.troopId = troopId;
	}

	public long getMilitaryKindId() {
		return militaryKindId;
	}

	public void setMilitaryKindId(long militaryKindId) {
		this.militaryKindId = militaryKindId;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getMove() {
		return move;
	}

	public void setMove(int move) {
		this.move = move;
	}

	public int getLeftHp() {
		return leftHp;
	}

	public void setLeftHp(int leftHp) {
		this.leftHp = leftHp;
	}

	public int getLeftMove() {
		return leftMove;
	}

	public void setLeftMove(int leftMove) {
		this.leftMove = leftMove;
	}

	public BATTLE_STATE getBattleState() {
		return battleState;
	}

	public void setBattleState(BATTLE_STATE battleState) {
		this.battleState = battleState;
	}

	public FaceDirection getFaceDirection() {
		return faceDirection;
	}

	public void setFaceDirection(FaceDirection faceDirection) {
		this.faceDirection = faceDirection;
	}
	
	

}
