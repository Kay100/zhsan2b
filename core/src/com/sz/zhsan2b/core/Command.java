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
    
}
