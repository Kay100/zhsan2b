package com.sz.zhsan2b.core.entity;

import com.badlogic.gdx.utils.ArrayMap;
import com.sz.zhsan2b.core.entity.DamageRange.DamageRangeType;

public class BattleProperties {
	public int hp = 1000;
	public int ack = 200;
	public int def = 100;
	public int range = 1;
	public int notRange = 0;
	public boolean isXie = false;
	public int speed = 10;
	public int move = 30; //一步默认消耗5点机动
	public int damageRange = 0;
	public DamageRangeType damageRangeType = DamageRangeType.NONE;
	public BattleProperties initProperties;
	public ArrayMap<String,Object> ext = new ArrayMap<String,Object>();
	public BattleProperties() {
		super();
	}
	public BattleProperties(BattleProperties bp) {
		this.hp=bp.hp;
		this.ack=bp.ack;
		this.def=bp.def;
		this.range=bp.range;
		this.isXie=bp.isXie;
		this.speed=bp.speed;
		this.move=bp.move;
		this.notRange=bp.notRange;
		this.damageRange=bp.damageRange;
		this.damageRangeType=bp.damageRangeType;
		this.ext=bp.ext;
		initProperties = new BattleProperties();
		initProperties.hp=bp.initProperties.hp;
		initProperties.ack=bp.initProperties.ack;
		initProperties.def=bp.initProperties.def;
		initProperties.range=bp.initProperties.range;
		initProperties.isXie=bp.initProperties.isXie;
		initProperties.speed=bp.initProperties.speed;
		initProperties.move=bp.initProperties.move;
		initProperties.notRange=bp.initProperties.notRange;
		initProperties.damageRange=bp.initProperties.damageRange;
		initProperties.damageRangeType=bp.initProperties.damageRangeType;
		initProperties.ext=bp.initProperties.ext;
	}
	public void backUp(){
		initProperties = new BattleProperties();
		initProperties.hp=this.hp;
		initProperties.ack=this.ack;
		initProperties.def=this.def;
		initProperties.range=this.range;
		initProperties.isXie=this.isXie;
		initProperties.speed=this.speed;
		initProperties.move=this.move;
		initProperties.notRange=this.notRange;
		initProperties.damageRange=this.damageRange;	
		initProperties.damageRangeType= this.damageRangeType;
		initProperties.ext.clear();
		initProperties.ext.putAll(this.ext);
	}
	public void recover(){
		if(initProperties==null)
			throw new RuntimeException("battleproperties must backup first!");
		this.hp=initProperties.hp;
		this.ack=initProperties.ack;
		this.def=initProperties.def;
		this.range=initProperties.range;
		this.isXie=initProperties.isXie;
		this.speed=initProperties.speed;
		this.move=initProperties.move;
		this.notRange=initProperties.notRange;
		this.damageRange=initProperties.damageRange;
		this.damageRangeType=initProperties.damageRangeType;
		this.ext.clear();
		this.ext.putAll(initProperties.ext);
	}
	

}
