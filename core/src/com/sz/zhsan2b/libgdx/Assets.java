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
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.sun.javafx.binding.StringFormatter;
import com.sun.scenario.effect.Effect;
import com.sz.zhsan2b.core.StepAction.TileEffect;

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
	public AssetTileEffect assetTileEffect;
	public AssetNumber assetNumber;
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
		assetManager.load(Constants.TEXTURE_ATLUS_NUMBER, TextureAtlas.class);
		assetManager.load(Constants.TEXTURE_ATLUS_TILE_EFFECT, TextureAtlas.class);
		
	
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
		assetNumber = new AssetNumber();
		assetTroop = new AssetTroop();
		assetTileEffect = new AssetTileEffect();
		
	
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
	public class AssetNumber {
		public final TextureRegion[] combatNumber = new TextureRegion[12];

		public AssetNumber() {
			TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLUS_NUMBER);
			TextureRegion srcNumbers = atlas.findRegion("CombatNumber");
			TextureRegion[][] tmp = srcNumbers.split(12,20); 
			for(int i=0;i<12;i++){
				combatNumber[i]=tmp[0][i];
			}
		}
		

	}	
	public class AssetTileEffect{
		public final ArrayMap<TileEffect,Animation> animTileEffect = new ArrayMap<TileEffect, Animation>();
		protected AssetTileEffect(){
			 TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLUS_TILE_EFFECT);
			 TextureRegion texR = atlas.findRegion("huoshi"); 				
		     TextureRegion[] tempRegs = getAnimationFrames(texR.split(texR.getRegionWidth()/8, texR.getRegionHeight()),0,8);
		     animTileEffect.put(TileEffect.HUOSHI, new Animation(1/8f, tempRegs)); 
			 texR = atlas.findRegion("Fire"); 				
		     tempRegs = getAnimationFrames(texR.split(texR.getRegionWidth()/8, texR.getRegionHeight()),0,8);
		     animTileEffect.put(TileEffect.FIRE, new Animation(1/8f, tempRegs)); 		
			 texR = atlas.findRegion("Boost"); 				
		     tempRegs = getAnimationFrames(texR.split(texR.getRegionWidth()/12, texR.getRegionHeight()),0,12);
		     animTileEffect.put(TileEffect.BOOST, new Animation(1/12f, tempRegs)); 	
			 texR = atlas.findRegion("Chaos"); 				
		     tempRegs = getAnimationFrames(texR.split(texR.getRegionWidth()/8, texR.getRegionHeight()),0,8);
		     animTileEffect.put(TileEffect.CHAOS, new Animation(1/8f, tempRegs)); 	
		}
	
	}
	
	public class AssetTroop {
		//public final AtlasRegion actionDoneTroopReg;
		public final ArrayMap<Long,Animation> animFaceUpWalk = new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceDownWalk=new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceLeftWalk=new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceRightWalk=new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceUpAttack = new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceDownAttack=new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceLeftAttack=new ArrayMap<Long, Animation>();
		public final ArrayMap<Long,Animation> animFaceRightAttack=new ArrayMap<Long, Animation>();
		
		public final ArrayMap<Long,Animation> animFaceUpBeAttacked=new ArrayMap<Long, Animation>();		
		public final ArrayMap<Long,Animation> animFaceDownBeAttacked=new ArrayMap<Long, Animation>();		
		public final ArrayMap<Long,Animation> animFaceLeftBeAttacked=new ArrayMap<Long, Animation>();		
		public final ArrayMap<Long,Animation> animFaceRightBeAttacked=new ArrayMap<Long, Animation>();		
	

		public AssetTroop() {
			//暂时测试使用，以后读数据库
			long[] troopIds = {0,1};
			for(long i=0,size= troopIds.length;i<size;i++){
				//move
				Texture texture = new Texture(Gdx.files.internal(StringUtils.replace("Troop/%1/Move.png", "%1", String.valueOf(i)))); 
		        Array<TextureRegion[]> tempRegs = getAnimationRegsArray(texture);
		        Animation tempAnim=new Animation(0.1f, tempRegs.get(0));
		        tempAnim.setPlayMode(PlayMode.LOOP);
		        animFaceUpWalk.put(i,tempAnim);
		        tempAnim=new Animation(0.1f, tempRegs.get(1));
		        tempAnim.setPlayMode(PlayMode.LOOP);
		        animFaceDownWalk.put(i, tempAnim) ; 
		        tempAnim=new Animation(0.1f, tempRegs.get(2));
		        tempAnim.setPlayMode(PlayMode.LOOP);
		        animFaceLeftWalk.put(i, tempAnim); 
		        tempAnim=new Animation(0.1f, tempRegs.get(3));
		        tempAnim.setPlayMode(PlayMode.LOOP);
		        animFaceRightWalk.put(i, tempAnim) ; 	
		        
		        //attack
		        texture = new Texture(Gdx.files.internal(StringUtils.replace("Troop/%1/Attack.png", "%1", String.valueOf(i)))); 
		        tempRegs = getAnimationRegsArray(texture);
		        animFaceUpAttack.put(i, new Animation(0.1f, tempRegs.get(0))); 
		        animFaceDownAttack.put(i, new Animation(0.1f, tempRegs.get(1))) ; 
		        animFaceLeftAttack.put(i, new Animation(0.1f, tempRegs.get(2))); 
		        animFaceRightAttack.put(i, new Animation(0.1f, tempRegs.get(3))) ; 	
		        
		        //beattack
		        texture = new Texture(Gdx.files.internal(StringUtils.replace("Troop/%1/BeAttacked.png", "%1", String.valueOf(i)))); 
		        tempRegs = getAnimationRegsArray(texture);
		        animFaceUpBeAttacked.put(i, new Animation(0.1f, tempRegs.get(0))); 
		        animFaceDownBeAttacked.put(i, new Animation(0.1f, tempRegs.get(1))) ; 
		        animFaceLeftBeAttacked.put(i, new Animation(0.1f, tempRegs.get(2))); 
		        animFaceRightBeAttacked.put(i, new Animation(0.1f, tempRegs.get(3))) ; 			        
		        
			}

                    

		}

		private Array<TextureRegion[]> getAnimationRegsArray(Texture texture) {
			TextureRegion[][] tmp = TextureRegion.split(texture, texture.getWidth()/Constants.TROOP_MODEL_XCOUNT, texture.getHeight()/Constants.TROOP_MODEL_YCOUNT);      
			TextureRegion[] faceUpRegs = getAnimationFrames(tmp, 7,Constants.TROOP_MODEL_XCOUNT);
			TextureRegion[] faceDownRegs=getAnimationFrames(tmp, 3,Constants.TROOP_MODEL_XCOUNT);
			TextureRegion[] faceLeftRegs=getAnimationFrames(tmp, 4,Constants.TROOP_MODEL_XCOUNT);
			TextureRegion[] faceRigthRegs=getAnimationFrames(tmp, 0,Constants.TROOP_MODEL_XCOUNT);
			
			Array<TextureRegion[]> tempRegs = new Array<TextureRegion[]>(4);
			tempRegs.addAll(faceUpRegs,faceDownRegs,faceLeftRegs,faceRigthRegs);
			return tempRegs;
		}


	}	
	private TextureRegion[] getAnimationFrames(TextureRegion[][] allSplitedFrames,
			  int rowIndex , int xcount) {
		TextureRegion[] animationFrames = new TextureRegion[xcount];
		int index = 0;
		for (int j = 0; j < xcount; j++) {
		    animationFrames[index++] = allSplitedFrames[rowIndex][j];
		}
		return animationFrames;
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
