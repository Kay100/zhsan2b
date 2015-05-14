package com.sz.zhsan2b.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.sz.zhsan2b.core.Command.ACTION_KIND;

public class StepAction {

	public enum FaceDirection {
		UP, DOWN, LEFT, RIGHT
	}

	public enum TileEffect {
		NONE, RECOVER, RESIST, HUOSHI, LEITING, RAOLUAN, BOOST, CRITICAL, CHAOS, GUANCHUAN, SURROUNDED, STUNT
	}

	public long actionTroopId;
	public ACTION_KIND actionKind;
	public Position objectPosition; // 寻路算法计算出的下一步的目标位置
	public FaceDirection faceDirection;
	public long militaryKindId;
	public Array<Long> affectedTroopList;
	public boolean isVisible;
	public ArrayMap<Long, Integer> damageMap;
	public ArrayMap<Long, TileEffect> effects;

	public StepAction(long actionTroopId) {
		this(actionTroopId, ACTION_KIND.NONE, new Position(0, 0),
				FaceDirection.RIGHT, 0, new Array<Long>(), true,
				new ArrayMap<Long, Integer>(), new ArrayMap<Long, TileEffect>());
	}

	public StepAction(long actionTroopId, ACTION_KIND actionKind,
			Position objectPosition, FaceDirection faceDirection,
			long militaryKindId, Array<Long> affectedTroopList,
			boolean isVisible, ArrayMap<Long, Integer> damageMap,
			ArrayMap<Long, TileEffect> effects) {
		super();
		this.actionTroopId = actionTroopId;
		this.actionKind = actionKind;
		this.objectPosition = objectPosition;
		this.faceDirection = faceDirection;
		this.militaryKindId = militaryKindId;
		this.affectedTroopList = affectedTroopList;
		this.isVisible = isVisible;
		this.damageMap = damageMap;
		this.effects = effects;
	}

}
