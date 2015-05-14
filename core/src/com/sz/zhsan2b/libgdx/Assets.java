package com.sz.zhsan2b.libgdx;

import org.apache.commons.lang.StringUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {



	public static final String TAG = Assets.class.getName();
	/*
	 * 静态函数的初始化值非常有作用，这个位置可以执行函数，new，或者是调用方法，应善用
	 */
	public static final Assets instance = new Assets();
	public AssetFonts fonts;
	public AssetBack background;
	public AssetMap assetMap;
	public AssetWangge assetWangge;
	public AssetTroop assetTroop;
	private AssetManager assetManager;


	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public void init(AssetManager assetManager) {
		// create game resource objects
		this.assetManager = assetManager;
		// set asset manager error handler
		assetManager.setErrorListener(this);
		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_MAP, TextureAtlas.class);
		assetManager.load(Constants.TEXTURE_ATLUS_WANGGE, TextureAtlas.class);
	
		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG,
				"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);
		// enable texture filtering for pixel smoothing
//		for (Texture t : atlas.getTextures())
//			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		 //create game resource objects
		fonts = new AssetFonts();
		assetMap = new AssetMap();
		assetWangge = new AssetWangge();
		assetTroop = new AssetTroop();
		
	
	}
	public class AssetBack {
		public final AtlasRegion back;

		public AssetBack(TextureAtlas atlas) {
			back = atlas.findRegion("background");
		}
	}
	public class AssetMap {
		public  AtlasRegion[][] map =new AtlasRegion[Constants.MAP_TEXTURE_XCOUNT][Constants.MAP_TEXTURE_YCOUNT];


		public AssetMap() {
			TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_MAP);
			for (int y=0;y<Constants.MAP_TEXTURE_YCOUNT;y++){
				for(int x=0;x<Constants.MAP_TEXTURE_XCOUNT;x++){
					map[x][y]= atlas.findRegion(String.valueOf(y*Constants.MAP_TEXTURE_XCOUNT+x));
				}
			}
		}
	}
	public class AssetWangge {
		public final AtlasRegion wangge;
		public final AtlasRegion xuanze;
		protected AssetWangge() {
			TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLUS_WANGGE);
		      wangge = atlas.findRegion("wangge");
		      xuanze = atlas.findRegion("xuanze");
		}
		

	}	
	
	public class AssetTroop {
		//public final AtlasRegion actionDoneTroopReg;
		public final Animation animFaceUpWalk;
		public final Animation animFaceDownWalk;
		public final Animation animFaceLeftWalk;
		public final Animation animFaceRightWalk;
		//public final Animation animAttack;
		//public final Animation animBeAttacked;		

		public AssetTroop() {
			Texture texture = new Texture(Gdx.files.internal("Troop/0/Move.png")); 
	        TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth()/Constants.TROOP_MODEL_XCOUNT, texture.getHeight()/Constants.TROOP_MODEL_YCOUNT);      
	        TextureRegion[] faceUpRegs = getAnimationFrames(tmp, 7);
	        TextureRegion[] faceDownRegs=getAnimationFrames(tmp, 3);
	        TextureRegion[] faceLeftRegs=getAnimationFrames(tmp, 4);
	        TextureRegion[] faceRigthRegs=getAnimationFrames(tmp, 0);
	        animFaceUpWalk = new Animation(0.1f, faceUpRegs); 
	        animFaceDownWalk = new Animation(0.1f, faceDownRegs); 
	        animFaceLeftWalk = new Animation(0.1f, faceLeftRegs); 
	        animFaceRightWalk = new Animation(0.1f, faceRigthRegs); 
                    

		}

		private TextureRegion[] getAnimationFrames(TextureRegion[][] allSplitedFrames,
				  int rowIndex) {
			TextureRegion[] animationFrames = new TextureRegion[Constants.TROOP_MODEL_XCOUNT];
			int index = 0;
			for (int j = 0; j < Constants.TROOP_MODEL_XCOUNT; j++) {
			    animationFrames[index++] = allSplitedFrames[rowIndex][j];
			}
			return animationFrames;
		}
	}	
	
	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();			
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset.toString() + "'",
				(Exception) throwable);
	}

	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.setScale(0.75f);
			defaultNormal.setScale(1.0f);
			defaultBig.setScale(2.0f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture()
					.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

}
