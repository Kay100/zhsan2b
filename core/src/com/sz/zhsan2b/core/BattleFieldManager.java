package com.sz.zhsan2b.core;


import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sz.zhsan2b.core.entity.BattleField;

import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.BattleField.State;

@Component
public class BattleFieldManager {
	private BattleField battleField = GameContext.getBattleField();
	
	private TroopManager troopManager;
	@PersistenceContext
	private EntityManager em;
	
	public BattleFieldManager() {
		init();
	}

	public void init() {

		//test,暂时是battle状态
		battleField.state=State.BATTLE;
		battleField.loadMaps();	
		
	}
	

	//进行一轮战斗
	public void calculateBattle(){
		Metamodel model = em.getEntityManagerFactory().getMetamodel();

		for (EntityType entityType : model.getEntities()) {
			String entityName = entityType.getName();
			em.createQuery("select o from " + entityName + " o").getResultList();
			System.out.println("ok: " + entityName);

		}			
		while(!isAllTroopsActioned()){
			//重置命令的目标位置为目标部队的位置
			for(Troop tr:battleField.getTroopList()){
				Troop object = tr.getCommand().object;
				if(object!=null){
					tr.getCommand().objectPosition.setPosition(object.getPosition());
				}
			}
			//主循环
			
			for(int i= 0,size=battleField.getTroopList().size;i<size;i++){
				Troop curTr = battleField.getTroopList().get(i);
				if(curTr.getCommand().isCompeted==true)
					continue;
				switch(curTr.getCommand().actionKind){
				case ATTACK:
					if(!troopManager.attackObject(curTr)){
						moveOneStepToAttack(curTr,true);
					}
					break;
				case CAST:
					//暂时不实现
					
					break;
				case MOVE:
					moveOneStepToAttack(curTr,false);
					break;
				case NONE:
					curTr.setCommandComplete();
					break;
				}
			}
			
		}
	}

	private void moveOneStepToAttack(Troop curTr,boolean isAttackFirst) {
		if(troopManager.moveToAttackPositionByOneStep(isAttackFirst,curTr)){
				troopManager.oneRandomAttack(curTr);
		}
	}
	

	private boolean isAllTroopsActioned() {
		boolean allActioned = true;
		for(Troop tr:battleField.getTroopList()){
			if(!tr.getCommand().isCompeted){
				allActioned = false;
			}
		}
		
		
		return allActioned;
	}

	public TroopManager getTroopManager() {
		return troopManager;
	}
	@Autowired
	public void setTroopManager(TroopManager troopManager) {
		this.troopManager = troopManager;
	}
}
