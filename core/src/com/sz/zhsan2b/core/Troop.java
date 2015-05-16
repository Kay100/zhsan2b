package com.sz.zhsan2b.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.sz.zhsan2b.core.Command.ACTION_KIND;
import com.sz.zhsan2b.core.StepAction.FaceDirection;
import com.sz.zhsan2b.core.StepAction.TileEffect;

public class Troop implements TroopEventHandler {//增加adapter 实现hook
	public enum TroopEvent {
		ATTACK_BEFORE,ATTACK_AFTER,DESTROY,CAST_BEFORE,CAST_AFTER
	}

	public enum BATTLE_STATE {
		UN_ARRANGE, NORMAL, IS_DESTROY, CHAOS, STAT_IN_FIRE

	}

	private static long seq = 0;

	// properties
	private long id;
	//临时测试，以后会替换成编队，编队里面有兵种
	private MilitaryKind militaryKind;
	//还有很多部队能力待添加到battleProperties中,通过解析影响，
	private BattleProperties battleProperties;
	private BattleProperties currentProperties;
	private boolean isArrowAttack;
	private Position position;
	//troop data
	private int hp;
	private int leftMove;
	private int tempDamage;
	
	private boolean isMultiObject;
	private boolean isStepAttack;
	private long currentZhanfaId;
	private Command command;
	private Array<TroopEventHandler> troopEventHandlers;
	private BATTLE_STATE battleState;//部队所处的状态
	private boolean isAttackCompleted;
	private PLAYER_TYPE owner;
	private FaceDirection faceDirection;
	
	private BattleField battleField;

	public Troop(BattleField battleField) {
		this.battleField = battleField;
		battleField.getTroopList().add(this);
		init();

	}

	private void init() {
		id = seq + 1;
		seq++;
		battleProperties = new BattleProperties();
		currentProperties = new BattleProperties();
		isArrowAttack = false;
		position = new Position(0, 0);
		hp = currentProperties.hp;
		leftMove = currentProperties.move;
		isMultiObject = false;
		isStepAttack = false;
		currentZhanfaId = 0;// 表示没有战法，为普通攻击
		command = new Command(new Position(0, 0));
		troopEventHandlers = new Array<TroopEventHandler>();
		battleState = BATTLE_STATE.UN_ARRANGE;
		isAttackCompleted = false;
		owner = PLAYER_TYPE.AI;
		faceDirection = FaceDirection.RIGHT;

	}
	// attack
	// return true 表示已经顺利完成了对目标的攻击  
	/*
	 * 本方法用于攻击目标部队
	 */
	public boolean attackObject() {
		if (command.actionKind != ACTION_KIND.ATTACK) {
			return false;
		}
		if(command.object==null){
			return false;
		}
			
		if (!BattleUtils.isObjectInAttackRange(command.object,
				this)) {
			return false;
		}
		
		//构造stepAction	
		StepAction stepAction = new StepAction(id);
		//判断是否抵挡反击,抵挡的话,step action 加入 抵挡动画
		
		//do attack 修改自身hp和部队状态
		tempDamage = BattleUtils.calculateDamage(this,command.object);
		hp -= tempDamage;
		stepAction.actionKind = command.actionKind;		
		faceDirection =BattleUtils.calculateFaceDirection(faceDirection,position,command.objectPosition);
		stepAction.faceDirection = faceDirection;
		stepAction.isVisible=true;
		stepAction.militaryKindId = militaryKind.getId();
		stepAction.damageMap.put(id, tempDamage);
        addAttackEffect(stepAction.effects);
		stepAction.objectPosition.setPosition(position);
		
		
		Array<StepAction> stepActionList = getStepActionList();
		stepActionList.add(stepAction);		
		
        fire(TroopEvent.ATTACK_AFTER, stepAction);		
		//判断损毁
		if(hp<=0){
			battleState=BATTLE_STATE.IS_DESTROY;
			stepAction.effects.put(id,TileEffect.DESTROY);
			fire(TroopEvent.DESTROY, stepAction);
		}
		

		command.isCompeted=true;
		return command.isCompeted;
	}

	public  void addTroopEventHandler(TroopEventHandler troopHandler) {

		troopEventHandlers.add(troopHandler);
		
	}
	public  void removeTroopEventHandler(TroopEventHandler troopHandler) {
		troopEventHandlers.removeValue(troopHandler, true);
		
	}	

	public void clearAllTroopEventHandlers() {
		troopEventHandlers.clear();
		
	}

	private Array<StepAction> getStepActionList() {
		return battleField.getStepActionHandler().getStepActionList();
	}

	private void addAttackEffect(ArrayMap<Long, TileEffect> effects) {
		//test
		effects.put(id, TileEffect.BOOST);
		
	}

	@Deprecated
	private ArrayMap<Long, Integer> getObjectDamages(
			Array<Troop> affectedTroopList) {
		ArrayMap<Long, Integer> tmp = new ArrayMap<Long, Integer>();
		for(Troop tr:affectedTroopList){
			tmp.put(tr.id, tr.tempDamage);
		}
		return tmp;
	}

	private Array<Long> getAffectedTroopIdList(Array<TroopEventHandler> affectedTroopList) {
		Array<Long> tmp = new Array<Long>();
		for(TroopEventHandler tr:affectedTroopList)
			tmp.add(((Troop)tr).id);
		return tmp;
	}
	/*
	 * 默认取得敌方部队
	 */
	private Array<Troop> getAllTroopsInAttackRange() {
		Array<Troop> tmpList = new Array<Troop>(10*currentProperties.range);
		for(Troop tr:battleField.getTroopList()){
			if(tr.owner!=owner&&BattleUtils.isObjectInAttackRange(tr,this)){
				tmpList.add(tr);
			}
		}
		
		return tmpList;
	}

	private void notify(TroopEvent event,StepAction stepAction) {
		if(troopEventHandlers.size==0)
			return;
		for(TroopEventHandler tr:troopEventHandlers){
			switch(event){
			case ATTACK_AFTER:
				tr.onAttackAfter(this, stepAction);
				break;
			case ATTACK_BEFORE:
				break;
			case CAST_AFTER:
				break;
			case CAST_BEFORE:
				break;
			case DESTROY:
				tr.onTroopDestroyed(this, stepAction);
				break;
			default:
				break;
			
			}
			
			
		}
		
	}

	public void beAttack(Troop damageFrom, StepAction stepAction) {
		//判断是否抵抗 ，待实现，addStepAction  新的step，new StepAction 攻击方的用参数里的 
		
		//be attack 修改自身hp和部队状态
		tempDamage =BattleUtils.calculateDamage(this, damageFrom);
		hp-= tempDamage;
		if(hp<=0){
			battleState=BATTLE_STATE.IS_DESTROY;
			stepAction.effects.put(id, TileEffect.DESTROY);
			fire(TroopEvent.DESTROY, stepAction);
		}
			
		
		stepAction.damageMap.put(id, tempDamage);
		addBeAttackEffect(stepAction.effects);
		
	}

	private void addBeAttackEffect(ArrayMap<Long, TileEffect> effects) {

		//test
		effects.put(id, TileEffect.HUOSHI);

		
	}
	//move
	//return true
	public boolean moveOneStep(){
		return true;
	}
	
	//set troop Complete
	public void setCommandComplete(){
		command.isCompeted=true;
	}

	public BattleProperties getCurrentProperties() {
		return currentProperties;
	}

	public PLAYER_TYPE getOwner() {
		return owner;
	}

	public Position getPosition() {
		return position;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public boolean isStepAttack() {
		return isStepAttack;
	}
	//return true mean already move to the positon where troop can attack(at the end of one step move)
	public boolean moveToAttackPositionByOneStep() {
		//判断是否到达目的地
		//return null means no path calculated , return size 0 means already reached.
		Map currentMap = battleField.getMapByMilitaryKindId(militaryKind.getId());
		Array<Position> positionPath = currentMap.calculatePositionPath(position,command.objectPosition,battleField.getOccupiedPositions());
		if(positionPath==null){
			leftMove-=militaryKind.getDefaultMoveWeight();
			if(leftMove<=0){
				command.isCompeted=true;
				return true;
			}else{
				return false;
			}
		}
		if(positionPath.size==0){ //已经抵达目标
			command.isCompeted=true;
			return true;
		}
		int nextWeight = currentMap.calculateNextEdgeWeight(positionPath.get(0),positionPath.get(1));
		if(leftMove<nextWeight){
			command.isCompeted=true;
			return true;
		}else{//移动一格
			leftMove-=nextWeight;
			//这个地方要判断一下移动的位置是否被目标站住了（其他的点不要判断的原因是选路算法代劳了）
			Position nextPosition = positionPath.get(1);
			if(battleField.isPositionOccupied(nextPosition)){
				nextPosition = battleField.getNotOccupiedNeighborPosition(nextPosition,position);
				if(nextPosition==null){
					return false;
				}
			}
			
			
			position.setPosition(nextPosition);
			//add stepAction
			StepAction stepAction = new StepAction(this.id);
			stepAction.actionKind=ACTION_KIND.MOVE;
			stepAction.faceDirection= BattleUtils.calculateFaceDirection(faceDirection, position, command.objectPosition);
			stepAction.isVisible =true;
			stepAction.objectPosition.setPosition(position);
			getStepActionList().add(stepAction);
			return false;
		}
		
	}


//已on开头的事件都是获得对方的事件，自己的事件不需要通过接口获得，直接操作对象即可。
	@Override
	public void onTroopDestroyed(Troop troop, StepAction stepAction) {
		// 接收消息,将部队命令中的攻击对象清空
		if(command.object.equals(troop)){
			command.object=null;
		}
		
	}

	@Override
	public void onAttackAfter(Troop troop, StepAction stepAction) {
		troop.beAttack(troop, stepAction);
		
	}
	
	public void fire(TroopEvent event,StepAction stepAction){
		switch(event){
		case ATTACK_AFTER:
			//增加攻击对象
			clearAllTroopEventHandlers();
			if(isMultiObject){
				Array<Troop> allTroopsInAttackRange = getAllTroopsInAttackRange();
				if(allTroopsInAttackRange.size!=0){
					for(Troop tr:allTroopsInAttackRange){
						addTroopEventHandler(tr);
					}
				}

				
			}else{
				addTroopEventHandler(command.object);
			}
			stepAction.affectedTroopList = getAffectedTroopIdList(troopEventHandlers);
			//通知被攻击的部队
			notify(event,stepAction);			
			break;
		case ATTACK_BEFORE:
			break;
		case CAST_AFTER:
			break;
		case CAST_BEFORE:
			break;
		case DESTROY:
			
			clearAllTroopEventHandlers();
			addTroopEventHandler(battleField);
			notify(event, stepAction);
			break;
		default:
			break;
		
		}
		
	}
	


}
