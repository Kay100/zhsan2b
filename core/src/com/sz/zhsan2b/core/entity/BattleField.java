package com.sz.zhsan2b.core.entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.VoidSerializer;
import com.esotericsoftware.kryo.serializers.FieldSerializer.Bind;
import com.sz.zhsan2b.core.GameMap;
import com.sz.zhsan2b.core.MapBuilder;

public class BattleField{
	public enum State {
		BATTLE,OPERATE
	}
	public enum SYN_TYPE {
		BattlefieldData,CommandData
	}
	private List<Troop> troopList = new ArrayList<Troop>();
	public State state;
	@Bind(VoidSerializer.class)
	private Map<Long,GameMap> maps  = new HashMap<Long, GameMap>() ;
	private final List<StepAction> stepActionList = new ArrayList<StepAction>(100);

	

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
	
	public Troop getTroopById(long id) {
		Troop returnTroop = null;
		for(Troop tr:troopList){
			if(tr.getId()==id){
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

	public List<Position> getOccupiedPositions() {
		List<Position> positions = new ArrayList<Position>(troopList.size()+10);
		for(Troop tr:troopList){
			positions.add(tr.getPosition());
		}
		
		return positions;
	}




	@Override
	public String toString() {

		return ToStringBuilder.reflectionToString(this);
	}

	public List<Troop> getTroopList() {
		return troopList;
	}

	public List<StepAction> getStepActionList() {
		return stepActionList;
	}

	public GameMap getMapByMilitaryKindId(long militaryKindId) {
		return maps.get(militaryKindId);
	}
	//need to build 
	public void setMaps(Map<Long, GameMap> maps) {
		this.maps = maps;
	}

	public Map<Long, GameMap> getMaps() {
		return maps;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}


	

}
