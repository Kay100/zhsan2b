package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.sz.zhsan2b.core.entity.BattleField;
import com.sz.zhsan2b.core.entity.BattleProperties;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.MilitaryKind;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.entity.DamageRange.DamageRangeType;
import com.sz.zhsan2b.core.BattleFieldManager;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.PLAYER_TYPE;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;
import com.sz.zhsan2b.libgdx.TroopActor.ACTION_LABEL;

public class BattleFieldOperationStage {
	public class OnBattleProceedClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.clear();
			Zhsan2b.battleScreen.startBattle();
		}

	}	
	public static final String TAG = BattleFieldOperationStage.class.getName();	
	private BattleField battleField =GameContext.getBattleField();
	private Table layerOperation;
	private Label notification;
	

	public Table getLayerOperation() {
		return layerOperation;
	}


	public Label getNotification() {
		return notification;
	}


	public BattleFieldOperationStage() {
		layerOperation = new Table();
		layerOperation.setLayoutEnabled(false);	
		layerOperation.setName("layerOperation");
		notification= new Label("notification", Assets.instance.assetSkin.skinLibgdx);
		notification.setPosition(500, 0);
		notification.setVisible(false);
		// 测试
		BattleProperties bp = new BattleProperties();
		bp.ack = 20;
		bp.def = 10;
		bp.hp = 100;
		bp.isXie = false;
		bp.move = 15;
		bp.range = 1;
		bp.speed = 20;
		bp.damageRangeType=DamageRangeType.ARROW_TOWER;
		bp.damageRange=2;
		bp.backUp();
		BattleProperties bp2= new BattleProperties(bp);
		bp2.range=4;
		bp2.notRange=2;
		bp2.isXie=false;
		BattleProperties bp3= new BattleProperties(bp);
		bp3.isXie=true;
		
		Command com1 = new Command(new Position(3, 4));
		Command com2 = new Command(new Position(4, 6));
		Command com3 = new Command(new Position(4, 6));
		
		com1.actionKind=ACTION_KIND.MOVE;
		com2.actionKind=ACTION_KIND.ATTACK;
		com3.actionKind=ACTION_KIND.MOVE;

		Troop tr1= new Troop(new MilitaryKind(0), bp, new Position(6, 5),
				com1, PLAYER_TYPE.PLAYER.AI);
		
		Troop tr2= new Troop(new MilitaryKind(1), bp2, new Position(1, 9),
				com2, PLAYER_TYPE.PLAYER.PLAYER);
		Troop tr3= new Troop(new MilitaryKind(0), bp3, new Position(6, 4),
				com3, PLAYER_TYPE.PLAYER.AI);		
		tr2.setStepAttack(true);
		tr2.setMultiObject(true);
		com1.object=null;
		com2.object=null;
		com3.object=null;
		battleField.getTroopList().add(tr1);
		battleField.getTroopList().add(tr2);
		battleField.getTroopList().add(tr3);
	}


	public void startOperate() {
		for(TroopActor trA:Zhsan2b.battleScreen.getTroopActorList()){
			trA.setActionLabel(ACTION_LABEL.UNDONE);
		}
		
	}


	public void operate() {
		
	}


	public void displayNotification(String text) {
		notification.setVisible(true);
		notification.setText(text);
		notification.addAction(Actions.delay(3f, Actions.hide()));
		
	}	

}
