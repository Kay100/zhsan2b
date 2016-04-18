package com.sz.zhsan2b.core.entity;

import java.util.ArrayList;
import java.util.List;

import com.sz.zhsan2b.core.util.BattleUtils;

public class DamageRange {
	public enum DamageRangeType {
		NORMAL,LINE,CROSS,ARROW_TOWER, NONE
	}
	private DamageRangeType rangeType;
	private Position origin;
	private Position object;
	private int range = 1;
	private boolean isXie = false;
	private int notRange = 0;
	private int damageRange = 0;
	
	
	
	public DamageRangeType getRangeType() {
		return rangeType;
	}

	public void setRangeType(DamageRangeType rangeType) {
		this.rangeType = rangeType;
	}

	public Position getOrigin() {
		return origin;
	}

	public void setOrigin(Position origin) {
		this.origin = origin;
	}

	public Position getObject() {
		return object;
	}

	public void setObject(Position object) {
		this.object = object;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public boolean isXie() {
		return isXie;
	}

	public void setXie(boolean isXie) {
		this.isXie = isXie;
	}

	public int getNotRange() {
		return notRange;
	}

	public void setNotRange(int notRange) {
		this.notRange = notRange;
	}

	public int getDamageRange() {
		return damageRange;
	}

	public void setDamageRange(int damageRange) {
		this.damageRange = damageRange;
	}

	public DamageRange(DamageRangeType rangeType, Position origin) {
		this(rangeType, origin, null, 1, false, 0, 0);
	}

	public DamageRange(DamageRangeType rangeType, Position origin,
			Position object, int range, boolean isXie, int notRange,
			int damageRange) {
		super();
		this.rangeType = rangeType;
		this.origin = origin;
		this.object = object;
		this.range = range;
		this.isXie = isXie;
		this.notRange = notRange;
		this.damageRange = damageRange;
	}

	/**
	 * 计算攻击伤害的范围,如果没有攻击目标或攻击目标在攻击范围之外，则不产生伤害范围
	 * @param rangeType  伤害范围类型
	 * @param origin 发起攻击的位置
	 * @param object 目标的位置
	 * @param range 攻击者的射程
	 * @param isXie 是否斜向
	 * @param notRange 不能覆盖的射程，例如弓箭的周围
	 * @param damageRange 伤害本身的范围，例如溅射
	 * @return 所有的可能伤害位置
	 */
	public List<Position> getDamageRangeList(){//notRange means cannot hit this area.
		List<Position> positionList = new ArrayList<Position>(1);
		if(!BattleUtils.isObjectInAttackRange(object, origin, range, isXie)){
			return positionList;
		}
		switch (rangeType){
		case NONE:
			positionList.add(object);
			break;	
		case LINE:
			positionList = getLineDamageRangeList(origin,object,damageRange);
			break;
		case NORMAL:
			positionList = getNormalDamageRangeList(origin, range, isXie);
			break;
		case CROSS:
			positionList = getCrossDamageRangeList(object,isXie,damageRange);
			break;	
		case ARROW_TOWER:
			positionList = getArrowTowerDamageRangeList(origin,range,isXie,notRange);
			break;
		default:
			throw new RuntimeException("no this rangeType");
		
		}
		return positionList;
	}

	private List<Position> getArrowTowerDamageRangeList(Position origin2,
			int range2, boolean isXie2, int notRange2) {
		List<Position> outerRangeAreaList = getNormalDamageRangeList(origin2, range2, isXie2);
		List<Position> innerRangeAreaList = getNormalDamageRangeList(origin2, notRange2, isXie2);
			outerRangeAreaList.removeAll(innerRangeAreaList);
		return outerRangeAreaList;
	}

	private List<Position> getLineDamageRangeList(Position self,
			Position object, int damageRange) {
		List<Position> returnList = new ArrayList<Position>(damageRange * 10);
		returnList.add(new Position(object));
		int ox = object.x;
		int oy = object.y;
		int sx = self.x;
		int sy = self.y;
		if (ox > sx && oy == sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x+i, object.y);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox > sx && oy > sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x+i, object.y+i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox == sx && oy > sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x, object.y+i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox < sx && oy > sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x-i, object.y+i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox < sx && oy == sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x-i, object.y);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox < sx && oy < sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x-i, object.y-i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox == sx && oy < sy) {
			for(int i=1;i<=damageRange;i++){
				Position p =new Position(object.x, object.y-i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		} else if (ox > sx && oy < sy) {
			for(int i=1;i<=damageRange;i++){
				Position p = new Position(object.x+i, object.y-i);
				if(!p.outOfBound())
				returnList.add(p);
			}
		}
		return returnList;
	}

	private List<Position> getCrossDamageRangeList(Position object,
			boolean isXie, int damageRange) {
		return BattleUtils.getAttackRangeList(object, damageRange, isXie);
	}

	private List<Position> getNormalDamageRangeList(Position origin,
			int range, boolean isXie) {
		return BattleUtils.getAttackRangeList(origin, range, isXie);
	}
}
