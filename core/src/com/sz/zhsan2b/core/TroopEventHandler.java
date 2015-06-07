package com.sz.zhsan2b.core;

public interface TroopEventHandler {

	void onTroopDestroyed(Troop troop,StepAction stepAction);
	void onAttackAfter(Troop troop,StepAction stepAction);
}
