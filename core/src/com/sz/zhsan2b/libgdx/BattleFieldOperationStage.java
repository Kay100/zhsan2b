package com.sz.zhsan2b.libgdx;

import com.sz.zhsan2b.core.BattleField;
import com.sz.zhsan2b.core.BattleProperties;
import com.sz.zhsan2b.core.Command;
import com.sz.zhsan2b.core.MilitaryKind;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.core.Position;
import com.sz.zhsan2b.core.Troop;

public class BattleFieldOperationStage {

	private BattleField battleField;

	public BattleFieldOperationStage(BattleField battleField) {
		this.battleField = battleField;
		// 测试
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = true;
		bp.move = 15;
		bp.range = 1;
		bp.speed = 20;

		Command com = new Command(new Position(0, 4));
		Command com1 = new Command(new Position(0, 4));

		new Troop(new MilitaryKind(0), bp, new Position(8, 5),
				com, PLAYER_TYPE.PLAYER.AI, battleField);
		
		new Troop(new MilitaryKind(0), bp, new Position(7, 5),
				com1, PLAYER_TYPE.PLAYER.PLAYER, battleField);
	}

}
