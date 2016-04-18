package com.sz.zhsan2b.core.entity;

import com.sz.zhsan2b.core.Constants;
import com.sz.zhsan2b.core.util.BattleUtils;


public class Position implements Comparable<Position> {
	public int x = 0;
	public int y = 0;
	
	public Position() {
	}
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Position(Position p) {
		this.x = p.x;
		this.y = p.y;
	}	
	public Position(String id) {
		int intId = Integer.parseInt(id);
		this.x = intId%Constants.BATTLE_FIELD_XCOUNT;
		this.y = intId/Constants.BATTLE_FIELD_YCOUNT;
	}
	@Override
	public int compareTo(Position p) {
		int a =getId() ;
		int b =p.getId();

		return a-b;
      
	}
	public int getId() {
		return y*Constants.BATTLE_FIELD_XCOUNT+x;
	}
	@Override
	public String toString() {
		return "Position [" + x + "," + y + "]";
	}
	public void setPosition(Position p){
		this.x = p.x;
		this.y = p.y;
	}
	
	public void set(int x,int y){
		this.x=x;
		this.y=y;
	}
	@Override
	public boolean equals(Object o){
		Position p = (Position)o;
		if (this.x==p.x&&this.y==p.y){
			return true;
		}else{
			return false;
		}
	}
	public boolean outOfBound(){
		return BattleUtils.isOutOfBound(x, 0, Constants.BATTLE_FIELD_XCOUNT - 1)
		|| BattleUtils.isOutOfBound(y, 0,
				Constants.BATTLE_FIELD_YCOUNT - 1);
	}
	public Position validate(){
		if(outOfBound()){
			return null;
		}else{
			return this;
		}
	}
	

}
