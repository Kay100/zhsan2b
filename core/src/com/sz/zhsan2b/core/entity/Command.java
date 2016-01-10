package com.sz.zhsan2b.core.entity;


public class Command {
	public enum ACTION_KIND {
		NONE,MOVE,ATTACK,CAST
	}

	public ACTION_KIND actionKind ;
	public long zhanfaId;
	public Troop object;
    public Position objectPosition;
    public boolean isCompeted;
    public Command(){
    	this(ACTION_KIND.NONE,0,null,new Position(0, 0),true);
    }
	public Command(Position objectPosition) {
		this(ACTION_KIND.MOVE,0,null,objectPosition,false);
	}

	public Command(ACTION_KIND actionKind, long zhanfaId, Troop object,
			Position objectPosition ,boolean isCompeted) {
		super();
		this.actionKind = actionKind;
		this.zhanfaId = zhanfaId;
		this.object = object;
		this.objectPosition = objectPosition;
		this.isCompeted = isCompeted;
	}

	@Override
	public String toString() {
		return "Command [actionKind=" + actionKind + ", zhanfaId=" + zhanfaId
				+ ", object=" + object + ", objectPosition=" + objectPosition
				+ ", isCompeted=" + isCompeted + "]";
	}
	
    
}
