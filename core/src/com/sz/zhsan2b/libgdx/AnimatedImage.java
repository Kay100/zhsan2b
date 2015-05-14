package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedImage extends Image {
	protected Animation animation = null;
	private float stateTime = 0;
	
	

	protected AnimatedImage() {
		super();
	}

	public AnimatedImage(Animation animation) {
		super(animation.getKeyFrame(0));
		this.animation = animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
		stateTime = 0;
		if(animation!=null){
			((TextureRegionDrawable) getDrawable()).setRegion(animation
					.getKeyFrame(0));	
		}
		this.setScale(0.9f);

	}

	@Override
	public void act(float delta) {

		((TextureRegionDrawable) getDrawable()).setRegion(animation.getKeyFrame(stateTime += delta, true));
		super.act(delta);
	}
}
