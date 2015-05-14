package com.sz.zhsan2b.core;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

public class Position implements Comparable<Position> {
	public int x = 0;
	public int y = 0;
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Position(String id) {
		int intId = Integer.parseInt(id);
		this.x = intId%Constants.BATTLE_FIELD_XCOUNT;
		this.y = intId/Constants.BATTLE_FIELD_XCOUNT;
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
	
	public boolean equal(Position p){
		if (this.x==p.x&&this.y==p.y){
			return true;
		}else{
			return false;
		}
	}
	

}
