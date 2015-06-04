package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class CombatNumberLabel extends Widget {
	private int damageValue;
	private boolean isDown;
	
	public CombatNumberLabel(int damageValue,boolean isDown) {
		this.damageValue = damageValue;
		this.isDown = isDown;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		String damageString = String.valueOf(damageValue);
		TextureRegion curNumber;
		int relX=0;
		if(isDown){
			curNumber=Assets.instance.assetNumber.combatNumber[11];
		}else{
			curNumber=Assets.instance.assetNumber.combatNumber[10];
			
		}
		batch.draw(curNumber.getTexture(), getX()+relX, getY(),curNumber.getRegionX(),curNumber.getRegionY(),curNumber.getRegionWidth(),curNumber.getRegionHeight());
		relX+=12f;
		for(int i=0;i<damageString.length();i++){
			curNumber= Assets.instance.assetNumber.combatNumber[Integer.parseInt(String.valueOf(damageString.charAt(i)))];
			batch.draw(curNumber.getTexture(), getX()+relX, getY(),curNumber.getRegionX(),curNumber.getRegionY(),curNumber.getRegionWidth(),curNumber.getRegionHeight());
			relX+=12f;
		}
		
	}
	

}
