package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


public class TroopActor extends AnimatedImage {
	
	public static final String TAG = TroopActor.class.getName();
	//private final float JUMP_TIME_MAX = 0.3f;

	private Animation animFaceUpWalk;
//	private Animation animAttack;
//	private Animation animUnderAttack;

	public TroopActor() {
		init();
	}

	public void init() {
		animFaceUpWalk = Assets.instance.assetTroop.animFaceUpWalk;		
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(animFaceUpWalk);
		
	}



}
