package com.sz.zhsan2b.core.entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.sz.zhsan2b.core.Map;
import com.sz.zhsan2b.core.MapBuilder;
import com.sz.zhsan2b.core.entity.Troop.BATTLE_STATE;

public class BattleField{
	public enum State {
		BATTLE,OPERATE
	}

	private Array<Troop> troopList = new Array<Troop>();
	public State state;
	private ArrayMap<Long,Map> maps  = new ArrayMap<Long, Map>() ;
	private final Array<StepAction> stepActionList = new Array<StepAction>(100);

	

	public void loadMaps() {
		//test,build a map by militaryKindId = 0,1
		maps.put(0l, MapBuilder.buildMap(0l));
		maps.put(1l, MapBuilder.buildMap(1l));
		
	}
	
	public void clearStepActionList(){
		stepActionList.clear();
	}
	
	
	public boolean isPositionOccupied(Position position) {
		boolean occupied = false;
		for(Troop tr:troopList){
			if(tr.getPosition().equals(position)){
				occupied = true;
			}
		}
		
		return occupied;
	}
	public Troop getTroopByPosition(Position p) {
		Troop returnTroop = null;
		for(Troop tr:troopList){
			if(tr.getPosition().equals(p)){
				returnTroop= tr;
			}
		}
		return returnTroop;
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


	public void deleteDestroyedTroops() {
		Troop curTr = null;
		for(int size=troopList.size,i=size-1;i>=0;i--){
			curTr=troopList.get(i);
			if(curTr.getBattleState()==BATTLE_STATE.IS_DESTROY){
				troopList.removeValue(curTr, true);
			}
		}
		
	}
	public void refresh(){
		//refresh troop for new battle
		for(Troop tr:troopList){
			tr.refresh();
		}
	}	
	public Array<Troop> getTroopList() {
		return troopList;
	}

	public Array<StepAction> getStepActionList() {
		return stepActionList;
	}

	public Map getMapByMilitaryKindId(long militaryKindId) {
		return maps.get(militaryKindId);
	}
	//need to build 
	public void setMaps(ArrayMap<Long, Map> maps) {
		this.maps = maps;
	}

	public ArrayMap<Long, Map> getMaps() {
		return maps;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}


	

}
