package com.sz.zhsan2b.core.entity;


import javax.persistence.Entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.entity.StepAction.FaceDirection;


public class Troop{//增加adapter 实现hook
	public enum TroopEvent {
		ATTACK_BEFORE,ATTACK_AFTER,DESTROY,CAST_BEFORE,CAST_AFTER
	}

	public enum BATTLE_STATE {
		UN_ARRANGE, NORMAL, IS_DESTROY, CHAOS, STAT_IN_FIRE

	}
	private static Logger logger = LoggerFactory.getLogger(Troop.class);
	private static long seq = 0;

	// properties
	private long id;
	//临时测试，以后会替换成编队，编队里面有兵种
	private MilitaryKind militaryKind;
	//还有很多部队能力待添加到battleProperties中,通过解析影响，
	private BattleProperties battleProperties;
	private boolean isArrowAttack;
	private Position position;
	//troop data
	private int hp;
	private int leftMove;
	private int tempDamage;
	
	private boolean isMultiObject;//if true ,must transfer value to damageRange
	private boolean isStepAttack;
	private long currentZhanfaId;
	private DamageRange damageRange;
	private Command command;
	private BATTLE_STATE battleState;//部队所处的状态
	private boolean isAttackCompleted;
	private PLAYER_TYPE owner;
	private FaceDirection faceDirection;

	public Troop(){


		this(new MilitaryKind(0), new BattleProperties(), new Position(0, 0), new Command(new Position(0, 0)), PLAYER_TYPE.AI);

	}

	public Troop(MilitaryKind militaryKind, BattleProperties battleProperties,
			Position position, Command command, PLAYER_TYPE owner) {
		id = seq + 1;
		seq++;
		this.militaryKind = militaryKind;
		this.battleProperties = battleProperties;
		this.position = position;
		this.command = command;
		this.owner = owner;
		isArrowAttack = false;
		hp = battleProperties.hp;
		leftMove = battleProperties.move;
		isMultiObject = false;
		isStepAttack = false;
		currentZhanfaId = 0;// 表示没有战法，为普通攻击
		battleState = BATTLE_STATE.UN_ARRANGE;
		isAttackCompleted = false;
		setFaceDirection(FaceDirection.RIGHT);
	}
	//每回合开始，战场会调用troop的refresh方法，来重置部队的状态。
	public void refresh() {
		if(command!=null){
			command.isCompeted=false;
		}
		this.leftMove=battleProperties.move;
		isAttackCompleted=false;
	}	

	//set troop Complete
	public void setCommandComplete(){
		command.isCompeted=true;
	}

	public PLAYER_TYPE getOwner() {
		return owner;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isArrowAttack() {
		return isArrowAttack;
	}

	public void setArrowAttack(boolean isArrowAttack) {
		this.isArrowAttack = isArrowAttack;
	}

	public boolean isMultiObject() {
		return isMultiObject;
	}

	public void setMultiObject(boolean isMultiObject) {
		this.isMultiObject = isMultiObject;
	}

	public MilitaryKind getMilitaryKind() {
		return militaryKind;
	}

	public void setMilitaryKind(MilitaryKind militaryKind) {
		this.militaryKind = militaryKind;
	}

	public boolean isStepAttack() {
		return isStepAttack;
	}
	public void setStepAttack(boolean isStepAttack) {
		this.isStepAttack = isStepAttack;
	}

	public BATTLE_STATE getBattleState() {
		return battleState;
	}

	public void setBattleState(BATTLE_STATE battleState) {
		this.battleState = battleState;
	}

	public long getId() {
		return id;
	}

	public int getHp() {
		return hp;
	}

	public DamageRange getDamageRange() {
		return damageRange;
	}

	public void setDamageRange(DamageRange damageRange) {
		this.damageRange = damageRange;
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

	public void setHp(int hp) {
		this.hp = hp;
	}

	public boolean isAttackCompleted() {
		return isAttackCompleted;
	}

	public void setAttackCompleted(boolean isAttackCompleted) {
		this.isAttackCompleted = isAttackCompleted;
	}

	public BattleProperties getBattleProperties() {
		return battleProperties;
	}

	public void setBattleProperties(BattleProperties battleProperties) {
		this.battleProperties = battleProperties;
	}

	public int getLeftMove() {
		return leftMove;
	}

	public void setLeftMove(int leftMove) {
		this.leftMove = leftMove;
	}

	public long getCurrentZhanfaId() {
		return currentZhanfaId;
	}

	public void setCurrentZhanfaId(long currentZhanfaId) {
		this.currentZhanfaId = currentZhanfaId;
	}

	public void setOwner(PLAYER_TYPE owner) {
		this.owner = owner;
	}




}
