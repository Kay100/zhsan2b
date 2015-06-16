package com.sz.zhsan2b.core;

import javax.xml.stream.events.Comment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static Logger logger = LoggerFactory.getLogger(Troop.class);
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


		this(new MilitaryKind(0), new BattleProperties(), new Position(0, 0), new Command(new Position(0, 0)), PLAYER_TYPE.AI, battleField);

	}

	public Troop(MilitaryKind militaryKind, BattleProperties battleProperties,
			Position position, Command command, PLAYER_TYPE owner,
			BattleField battleField) {
		id = seq + 1;
		seq++;
		this.militaryKind = militaryKind;
		this.battleProperties = battleProperties;
		this.position = position;
		this.command = command;
		this.owner = owner;
		this.battleField = battleField;
		battleField.getTroopList().add(this);
		currentProperties=new BattleProperties(battleProperties);
		isArrowAttack = false;
		hp = currentProperties.hp;
		leftMove = currentProperties.move;
		isMultiObject = false;
		isStepAttack = false;
		currentZhanfaId = 0;// 表示没有战法，为普通攻击
		troopEventHandlers = new Array<TroopEventHandler>();
		battleState = BATTLE_STATE.UN_ARRANGE;
		isAttackCompleted = false;
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
		
		attack(command.object);
		return true;
	}

	public void attack(Troop troop) {
		//判断是否为攻击目标，确定命令是否将被完成
		if(troop.equals(command.object)){
	        command.isCompeted=true;
		}
		//构造stepAction	
		StepAction stepAction = new StepAction(id);
		//判断是否抵挡反击,抵挡的话,step action 加入 抵挡动画
		
		//do attack 修改自身hp和部队状态 判断是否能被反击到。
		if(BattleUtils.isObjectInAttackRange(this, troop)){
			tempDamage = BattleUtils.calculateDamage(this,troop);
			hp -= tempDamage;
		}else{
			tempDamage=0;
		}

		stepAction.actionKind = ACTION_KIND.ATTACK;	
		faceDirection =BattleUtils.calculateFaceDirection(faceDirection,position,troop.position);
		stepAction.faceDirection = faceDirection;
		stepAction.isVisible=true;
		stepAction.militaryKindId = militaryKind.getId();		
        addAttackEffect(stepAction.effects);
		stepAction.damageMap.put(id, tempDamage);        
        stepAction.orginPosition.setPosition(position);
		stepAction.objectPosition.setPosition(position);
		
		
		Array<StepAction> stepActionList = getStepActionList();
		stepActionList.add(stepAction);		
		
        fire(TroopEvent.ATTACK_AFTER, stepAction,troop);
		

		//判断损毁
		if(hp<=0){
			battleState=BATTLE_STATE.IS_DESTROY;
			stepAction.effects.put(id,TileEffect.DESTROY);
			fire(TroopEvent.DESTROY, stepAction);
		}
	}

	public  void addTroopEventHandler(TroopEventHandler... eventHandlers) {
		for(int i= 0;i<eventHandlers.length;i++){
			troopEventHandlers.add(eventHandlers[i]);
		}
		
		
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
	private Troop getFirstTroopInAttackRange() {
		
		Troop returnTr = null;
		for(Troop tr:battleField.getTroopList()){
			if(tr.owner!=owner&&BattleUtils.isObjectInAttackRange(tr,this)){
				returnTr = tr;
				break;
			}
		}
		
		return returnTr;
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
	//不能触发通知，即不能再反击。与attack逻辑有区别
	public void beAttack(Troop damageFrom, StepAction stepAction) {
		//判断是否抵抗 ，待实现，addStepAction  新的step，new StepAction 攻击方的用参数里的 
		
		//be attack 修改自身hp和部队状态
		tempDamage =BattleUtils.calculateDamage(this, damageFrom);
		hp-= tempDamage;
		
		//以后可以考虑下朝想的实现，也得做troopid的map。
		stepAction.damageMap.put(id, tempDamage);
		if(hp<=0){
			battleState=BATTLE_STATE.IS_DESTROY;
			stepAction.effects.put(id, TileEffect.DESTROY);
			fire(TroopEvent.DESTROY, stepAction);
		}else{
			addBeAttackEffect(stepAction.effects);
		}



		
	}

	private void addBeAttackEffect(ArrayMap<Long, TileEffect> effects) {

		//test
		effects.put(id, TileEffect.HUOSHI);

		
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

	public BattleField getBattleField() {
		return battleField;
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
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
			logger.debug(String.valueOf(leftMove));
			//这个地方要判断一下移动的位置是否被目标站住了（其他的点不要判断的原因是选路算法代劳了）
			Position nextPosition = positionPath.get(1);
			if(battleField.isPositionOccupied(nextPosition)){
				nextPosition = battleField.getNotOccupiedNeighborPosition(nextPosition,position);
				if(nextPosition==null){
					return false;
				}
			}

			//add stepAction
			StepAction stepAction = new StepAction(this.id);
			stepAction.actionKind=ACTION_KIND.MOVE;
			stepAction.faceDirection= BattleUtils.calculateFaceDirection(faceDirection, position, command.objectPosition);
			stepAction.isVisible =true;
			stepAction.orginPosition.setPosition(position);
			stepAction.militaryKindId=militaryKind.getId();
			getStepActionList().add(stepAction);
			position.setPosition(nextPosition);
			stepAction.objectPosition.setPosition(position);
			logger.debug(stepAction.toString());
			return false;
		}
		
	}


//已on开头的事件都是获得对方的事件，自己的事件不需要通过接口获得，直接操作对象即可。
	
	//接收战场上有部队损毁的消息。
	@Override
	public void onTroopDestroyed(Troop troop, StepAction stepAction) {
		// 接收消息,将部队命令中的攻击对象清空
		if(command.object.equals(troop)){
			command.object=null;
		}
		
	}

	@Override
	public void onAttackAfter(Troop troop, StepAction stepAction) {
		beAttack(troop, stepAction);//这个错误还真难找啊。
		
	}
	
	public void fire(TroopEvent event,StepAction stepAction,TroopEventHandler...eventHandlers ){
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
				addTroopEventHandler(eventHandlers);
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
// if object in range,attack object.
	public void oneRandomAttack() {
		Troop tr = null;
		if(command.object!=null&&BattleUtils.isObjectInAttackRange(command.object, this)){
			tr=command.object;
		}else{
			tr=getFirstTroopInAttackRange();		
		}
		if(tr==null){
			return;
		}
		attack(tr);
	}

	public Array<Position> getAttackRangeList(){
		return BattleUtils.getAttackRangeList(position, battleProperties.range, battleProperties.isXie);
	}

	public void refresh() {
		command.isCompeted=false;
		this.leftMove=currentProperties.move;
		
	}


}
