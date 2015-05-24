package com.sz.zhsan2b.libgdx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sz.zhsan2b.core.StepAction.TileEffect;
import com.sz.zhsan2b.core.Troop;
import com.sz.zhsan2b.libgdx.TroopActor.TROOP_ANIMATION_TYPE;

public class TileEffectActor extends AnimatedImage {
	private static Logger logger = LoggerFactory.getLogger(TileEffectActor.class);
	
	public TileEffectActor(TileEffect effect) {
		setWidth(Constants.WANGGE_UNIT_WIDTH);
		setHeight(Constants.WANGGE_UNIT_HEIGHT);
		super.setDrawable(new TextureRegionDrawable());
		setAnimation(RenderUtils.getTileAnimationBy(effect));
	}

}
