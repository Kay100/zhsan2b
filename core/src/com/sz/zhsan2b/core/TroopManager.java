package com.sz.zhsan2b.core;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.badlogic.gdx.utils.Array;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.DamageRange;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.StepAction;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.SYN_TYPE;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.entity.StepAction.TileEffect;
import com.sz.zhsan2b.core.entity.Troop.BATTLE_STATE;
import com.sz.zhsan2b.core.entity.Troop.TroopEvent;
@Component
public class TroopManager {
	private static Logger logger = LoggerFactory.getLogger(TroopManager.class);
	private BattleField battleField = GameContext.getBattleField();
	
	public boolean attackObject(Troop origin) {
		if (origin.getCommand().actionKind != ACTION_KIND.ATTACK) {
			return false;
		}
		if(origin.getCommand().object==null){
			return false;
		}
			
		if (!BattleUtils.isObjectInAttackRange(origin.getCommand().object,
				origin)) {
			return false;
		}
		
		attack(origin,origin.getCommand().object);
		return true;
	}

	public void attack(Troop origin,Troop object) {
		//判断是否为攻击目标，确定命令是否将被完成
		if(object.equals(origin.getCommand().object)){
			origin.getCommand().isCompeted=true;
		}
		//构造stepAction	
		StepAction stepAction = new StepAction();
		stepAction.actionTroopId = origin.getId();
		stepAction.actionKind = ACTION_KIND.ATTACK;	
		origin.setFaceDirection(BattleUtils.calculateFaceDirection(origin.getFaceDirection(),origin.getPosition(),object.getPosition()));
		stepAction.faceDirection = origin.getFaceDirection();
		stepAction.isVisible=true;
		stepAction.militaryKindId = origin.getMilitaryKind().getId();
		//stepAction.effects.put(id, TileEffect.BOOST);
		//do attack 修改自身hp和部队状态 判断是否能被反击到。判断是否抵挡反击,抵挡的话,step action 加入 抵挡动画
		if(BattleUtils.isObjectInAttackRange(origin, object)){
			origin.setTempDamage(BattleUtils.calculateDamage(origin,object));
			origin.setHp(origin.getHp()-origin.getTempDamage());
			stepAction.damageMap.put(origin.getId(), origin.getTempDamage());    
			if(origin.getId()<=0){
				handleDestroyedTroop(origin, stepAction);

			}
		}
    
        stepAction.orginPosition.setPosition(origin.getPosition());
		stepAction.objectPosition.setPosition(origin.getPosition());
		
		
		Array<StepAction> stepActionList = battleField.getStepActionList();
		stepActionList.add(stepAction);	
		logger.debug(stepAction.toString());
		origin.setAttackCompleted(true);
		if(origin.isMultiObject()){
			if(origin.getDamageRange()==null){
				origin.setDamageRange(new DamageRange(origin.getBattleProperties().damageRangeType, origin.getPosition(), object.getPosition(), origin.getBattleProperties().range, origin.getBattleProperties().isXie, origin.getBattleProperties().notRange,origin.getBattleProperties().damageRange));
			}else{
				origin.getDamageRange().setObject(object.getPosition());
				origin.getDamageRange().setOrigin(origin.getPosition());
			}
			
			stepAction.ext.put("damageRangeArea", origin.getDamageRange().getDamageRangeList());
			Array<Troop> allTroopsInDamageRange = getAllTroopsInDamageRange(origin);
			if(allTroopsInDamageRange.size!=0){
				for(Troop tr:allTroopsInDamageRange){
					beAttack(origin, tr, stepAction);
				}
			}

			
		}else{
			beAttack(origin, object, stepAction);
		}
        
	}

	private void handleDestroyedTroop(Troop troop_r, StepAction stepAction) {
		troop_r.setBattleState(BATTLE_STATE.IS_DESTROY);
		troop_r.getCommand().isCompeted=true;
		stepAction= getTheLastStepAction(stepAction);
		if(stepAction.actionKind==ACTION_KIND.NONE){
			stepAction.effects.put(troop_r.getId(), TileEffect.DESTROY);
		}else{
			StepAction next = new StepAction();
			next.actionTroopId=troop_r.getId();
			stepAction.next=next;
			next.actionKind=ACTION_KIND.NONE;
			next.effects.put(troop_r.getId(), TileEffect.DESTROY);	
			stepAction=next;
		}
		
		//将所有部队命令中的攻击对象清空
		for(Troop troop:battleField.getTroopList()){
			if(troop.getCommand().object==troop_r){
				troop.getCommand().object=null;
			}
		}
	}

	private StepAction getTheLastStepAction(StepAction stepAction) {
		if(stepAction.next==null){
			return stepAction;
		}else{
			return getTheLastStepAction(stepAction.next);
		}
	}

	/*
	 * 默认取得敌方部队
	 */
	private Array<Troop> getAllTroopsInAttackRange(Troop troop) {
		return getAllTroopsInRange(BattleUtils.getAttackRangeList(troop.getPosition(), troop.getBattleProperties().range, troop.getBattleProperties().isXie),troop);
	}
	private Array<Troop> getAllTroopsInDamageRange(Troop troop) {
		if(troop.getDamageRange()==null){
			throw new RuntimeException("damageRange was not initialized before");
		}
		return getAllTroopsInRange(troop.getDamageRange().getDamageRangeList(),troop);
	}
	private Array<Troop> getAllTroopsInRange(Array<Position> rangeList ,Troop troop) {
		Array<Troop> tmpList = new Array<Troop>(10*troop.getBattleProperties().range);
		for(Troop tr:battleField.getTroopList()){
			if(tr.getBattleState()==BATTLE_STATE.IS_DESTROY){
				continue;
			}
			if(tr.getOwner()!=troop.getOwner()&&rangeList.contains(tr.getPosition(), false)){
				tmpList.add(tr);
			}
		}
		
		return tmpList;
	}

	private Troop getFirstTroopInAttackRange(Troop troop) {
		Array<Troop> tr =getAllTroopsInAttackRange(troop);
		if(tr.size==0){
			return null;
		}
		return tr.first();
	}	
	//不能触发通知，即不能再反击。与attack逻辑有区别
	public void beAttack(Troop damageFrom,Troop damageTo, StepAction stepAction) {
		//判断是否抵抗 ，待实现，addStepAction  新的step，new StepAction 攻击方的用参数里的 
		
		//be attack 修改自身hp和部队状态
		damageTo.setTempDamage(BattleUtils.calculateDamage(damageTo, damageFrom));
		damageTo.setHp(damageTo.getHp()-damageTo.getTempDamage()) ;
		stepAction.effects.put(damageTo.getId(), TileEffect.HUOSHI);
		//以后可以考虑下朝想的实现，也得做troopid的map。
		stepAction.damageMap.put(damageTo.getId(), damageTo.getTempDamage());		
		if(damageTo.getId()<=0){
			handleDestroyedTroop(damageTo, stepAction);
		}	
		stepAction.affectedTroopList.add(damageTo.getId());


		
	}
	

	//return true mean already move to the positon where troop can attack(at the end of one step move)
	public boolean moveToAttackPositionByOneStep(boolean isAttackFirst,Troop tr) {
		//判断是否到达目的地和无法移动的情况。
		//return null means no path calculated , return size 0 means already reached.
		Map currentMap = battleField.getMapByMilitaryKindId(tr.getMilitaryKind().getId());
		Array<Position> positionPath = currentMap.calculatePositionPath(tr.getPosition(),tr.getCommand().objectPosition,battleField.getOccupiedPositions());
		if(positionPath==null||positionPath.size==0){
			tr.setLeftMove(tr.getLeftMove()-tr.getMilitaryKind().getDefaultMoveWeight());
			if(tr.getLeftMove()<=0){
				tr.getCommand().isCompeted=true;
				return !tr.isAttackCompleted();
			}else{
				return false;
			}
		}
		//没有到达目的地，可以移动
		int nextWeight = currentMap.calculateNextNodeWeight(positionPath.get(1));
		if(tr.getLeftMove()<nextWeight){
			tr.getCommand().isCompeted=true;
			return !tr.isAttackCompleted();
		}else{//移动一格
			tr.setLeftMove(tr.getLeftMove()-nextWeight);
			//这个地方要判断一下移动的位置是否被目标站住了（其他的点不要判断的原因是选路算法代劳了）
			Position nextPosition = positionPath.get(1);
			if(battleField.isPositionOccupied(nextPosition)){
				nextPosition = battleField.getNotOccupiedNeighborPosition(nextPosition,tr.getPosition());
				if(nextPosition==null){
					return false;
				}
			}
			//判断到这里，才正真可以移动。
			//add stepAction
			StepAction stepAction = new StepAction();
			stepAction.actionTroopId=tr.getId();
			stepAction.actionKind=ACTION_KIND.MOVE;
			stepAction.faceDirection= BattleUtils.calculateFaceDirection(tr.getFaceDirection(), tr.getPosition(), tr.getCommand().objectPosition);
			stepAction.isVisible =true;
			stepAction.orginPosition.setPosition(tr.getPosition());
			stepAction.militaryKindId=tr.getMilitaryKind().getId();
			battleField.getStepActionList().add(stepAction);
			//移动
			tr.getPosition().setPosition(nextPosition);	
			stepAction.objectPosition.setPosition(tr.getPosition());
			logger.debug(stepAction.toString());
			if(tr.isStepAttack()){
				return true;
			}
			if(tr.getCommand().object==null){
				if(isAttackFirst){
					return !tr.isAttackCompleted();
				}else{
					return false;
				}
				
			}else{
				return false;
			}
			
		}
		
	}




	// if object in range,attack object. 随机攻击是会分辨敌我的
	public void oneRandomAttack(Troop tr_r) {
		Troop tr = null;
		if(tr_r.getCommand().object!=null&&tr_r.getCommand().object.getOwner()!=tr_r.getOwner()&&BattleUtils.isObjectInAttackRange(tr_r.getCommand().object, tr_r)){
			tr=tr_r.getCommand().object;
		}else{
			tr=getFirstTroopInAttackRange(tr_r);		
		}
		if(tr==null){
			return;
		}
		attack(tr_r,tr);
	}

	public Array<Position> getAttackRangeList(Troop tr){
		return BattleUtils.getAttackRangeList(tr.getPosition(), tr.getBattleProperties().range, tr.getBattleProperties().isXie);
	}
	public Array<Position> getMoveRangeList(Troop tr,Position orgin,int moveLong,Array<Position> unMovablePositions) {
		Map map = battleField.getMapByMilitaryKindId(tr.getMilitaryKind().getId());
		return map.calculateMoveRangeList(orgin, moveLong, unMovablePositions);
	}
	
	public void refresh(){
		//refresh troop for new battle
		for(Troop tr:battleField.getTroopList()){
			tr.refresh();
		}		
	}
	public void deleteDestroyedTroops() {
		Troop curTr = null;
		for(int size=battleField.getTroopList().size,i=size-1;i>=0;i--){
			curTr=battleField.getTroopList().get(i);
			if(curTr.getBattleState()==BATTLE_STATE.IS_DESTROY){
				battleField.getTroopList().removeValue(curTr, true);
			}
		}
		
	}	

	public BattleField getBattleField() {
		return battleField;
	}

	public void setBattleField(BattleField battleField) {
		this.battleField = battleField;
	}

	public String buildTroopCommandJSON(Troop troop) {
		JSONObject jo = new JSONObject();
		jo.put("troopId", troop.getId());
		jo.put("actionKind", troop.getCommand().actionKind);
		jo.put("objectTroopId", troop.getCommand().object==null?JSONObject.NULL:troop.getCommand().object.getId());
		if(troop.getPosition()!=null){
			JSONObject positionJSON = new JSONObject();
			positionJSON.put("x", troop.getPosition().x);
			positionJSON.put("y", troop.getPosition().y);
			jo.put("position", positionJSON);
		}
		jo.put("zhanfaId", troop.getCommand().zhanfaId);
		jo.put("isCompeted", troop.getCommand().isCompeted);
		String returnString = SYN_TYPE.CommandData.toString()+Constants.SYN_DATA_SEPARATOR+jo.toString();
		logger.debug(returnString);
		return returnString;
	}

	public void updateLocalTroopCommandByJSON(String message) {
		JSONObject jo = new JSONObject(message);
		long id = jo.getLong("troopId");
		Troop troop = battleField.getTroopById(id);
		troop.getCommand().actionKind= ACTION_KIND.valueOf(jo.getString("actionKind"));
		if(jo.get("objectTroopId")!=JSONObject.NULL){
			long objectTroopId = ((Integer)jo.get("objectTroopId")).longValue();
			Troop object = battleField.getTroopById(objectTroopId);
			troop.getCommand().object=object;
		}
		JSONObject position = (JSONObject)jo.get("position");
		if(position!=null){
			Position p = new Position(position.getInt("x"), position.getInt("y"));
			troop.getCommand().objectPosition=p;
		}
		troop.getCommand().zhanfaId = jo.getLong("zhanfaId");
		troop.getCommand().isCompeted= jo.getBoolean("isCompeted");
		logger.debug("update troop command,troopId:"+troop.getId());
	}	
}
