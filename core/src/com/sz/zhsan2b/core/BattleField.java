package com.sz.zhsan2b.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

public class BattleField implements TroopEventHandler{
	public enum State {
		BATTLE,OPERATE
	}

	private Array<Troop> troopList;
	public State state;
	private StepActionHandler stepActionHandler;
	private ArrayMap<Long,Map> maps;
	
	

	public BattleField() {
		init();
	}

	public void init() {
		troopList = new Array<Troop>();
		
		loadMaps();
		
	}
	
	private void loadMaps() {
		maps = new ArrayMap<Long, Map>();
		//test,build a map by militaryKindId = 1
		maps.put(1l, MapBuilder.buildMap(1l));
		
	}

	//进行一轮战斗
	public void calculateBattle(){
		while(isAllTroopsActioned()){
			//重置命令的目标位置为目标部队的位置
			for(Troop tr:troopList){
				Troop object = tr.getCommand().object;
				if(object!=null){
					tr.getCommand().objectPosition.setPosition(object.getPosition());
				}
			}
			//主循环
			for(Troop curTr:troopList){
				if(curTr.getCommand().isCompeted==true)
					continue;
				switch(curTr.getCommand().actionKind){
				case ATTACK:
					if(!curTr.attackObject()){
						if(curTr.moveToAttackPositionByOneStep()||curTr.isStepAttack()){
							curTr.attack();
						}
					}
					break;
				case CAST:
					//暂时不实现
					
					break;
				case MOVE:
					curTr.moveOneStep();
					break;
				case NONE:
					curTr.setCommandComplete();
					break;
				}
				

				
			}
			
		}
	}
	

	private boolean isAllTroopsActioned() {
		boolean allActioned = true;
		for(Troop tr:troopList){
			if(!tr.getCommand().isCompeted){
				allActioned = false;
			}
		}
		
		
		return allActioned;
	}

	public Array<Troop> getTroopList() {
		return troopList;
	}

	public StepActionHandler getStepActionHandler() {
		return stepActionHandler;
	}

	public void setStepActionHandler(StepActionHandler stepActionHandler) {
		this.stepActionHandler = stepActionHandler;
	}

	public Map getMapByMilitaryKindId(long militaryKindId) {
		return maps.get(militaryKindId);
	}
	//need to build 
	public void setMaps(ArrayMap<Long, Map> maps) {
		this.maps = maps;
	}

	public boolean isPositionOccupied(Position position) {
		boolean occupied = false;
		for(Troop tr:troopList){
			if(tr.getPosition().equal(position)){
				occupied = true;
			}
		}
		
		return occupied;
	}

	public Position getNotOccupiedNeighborPosition(Position to,
			Position from) {
		Position tmpPo = new Position(to.x,from.y);
		Position tmpPo2 = new Position(from.x, to.y);
		Position returnPo = null;
		if(!isPositionOccupied(tmpPo)){
			returnPo = tmpPo;
		}else if (!isPositionOccupied(tmpPo2)){
			returnPo = tmpPo2;
		}
		
		return returnPo;
	}

	public Array<Position> getOccupiedPositions() {
		Array<Position> positions = new Array<Position>(troopList.size+10);
		for(Troop tr:troopList){
			positions.add(tr.getPosition());
		}
		
		return positions;
	}

	@Override
	public void onTroopDestroyed(Troop troop, StepAction stepAction) {
		for(Troop tr:troopList){
			if(tr.getOwner()!=troop.getOwner()){
				tr.onTroopDestroyed(troop, stepAction);
			}
		}
		
	}

	@Override
	public void onAttackAfter(Troop troop, StepAction stepAction) {
		// TODO Auto-generated method stub
		
	}

	

}
