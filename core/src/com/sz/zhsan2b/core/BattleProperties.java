package com.sz.zhsan2b.core;

public class BattleProperties {
	public int hp = 1000;
	public int ack = 200;
	public int def = 100;
	public int range = 1;
	public boolean isXie = false;
	public int speed = 10;
	public int move = 30; //一步默认消耗5点机动
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
	}

}
