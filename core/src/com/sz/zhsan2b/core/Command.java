package com.sz.zhsan2b.core;

public class Command {
	public enum ACTION_KIND {
		NONE,MOVE,ATTACK,CAST
	}

	public ACTION_KIND actionKind ;
	public long zhanfaId;
	public Troop object;
    public Position objectPosition;
    boolean isCompeted;
    //position must be needed. 默认构造函数被重载，不能使用
	public Command(Position objectPosition) {
		this(ACTION_KIND.MOVE,0,null,objectPosition);
	}

	public Command(ACTION_KIND actionKind, long zhanfaId, Troop object,
			Position objectPosition) {
		super();
		this.actionKind = actionKind;
		this.zhanfaId = zhanfaId;
		this.object = object;
		this.objectPosition = objectPosition;
		isCompeted = false;
	}

	@Override
	public String toString() {
		return "Command [actionKind=" + actionKind + ", zhanfaId=" + zhanfaId
				+ ", object=" + object + ", objectPosition=" + objectPosition
				+ ", isCompeted=" + isCompeted + "]";
	}
	
    
}
