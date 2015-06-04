package com.sz.zhsan2b.libgdx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.sz.zhsan2b.core.StepAction.FaceDirection;
import com.sz.zhsan2b.core.StepAction.TileEffect;
import com.sz.zhsan2b.libgdx.TroopActor.TROOP_ANIMATION_TYPE;

public class RenderUtils {
	public static float translate(int value){
		return value*Constants.WANGGE_UNIT_WIDTH;
	}

	public static Animation getTroopAnimationBy(long militaryKindId,
			FaceDirection faceDirection, TROOP_ANIMATION_TYPE type) {
		Animation returnAnim = null;
		switch(type){
		case ATTACK:
			switch(faceDirection){
			case DOWN:
				returnAnim=Assets.instance.assetTroop.animFaceDownAttack.get(militaryKindId);
				break;
			case LEFT:
				returnAnim=Assets.instance.assetTroop.animFaceLeftAttack.get(militaryKindId);
				break;
			case RIGHT:
				returnAnim=Assets.instance.assetTroop.animFaceRightAttack.get(militaryKindId);
				break;
			case UP:
				returnAnim=Assets.instance.assetTroop.animFaceUpAttack.get(militaryKindId);
				break;
			
			}
			break;
		case BE_ATTACKED:
			switch(faceDirection){
			case DOWN:
				returnAnim=Assets.instance.assetTroop.animFaceDownBeAttacked.get(militaryKindId);
				break;
			case LEFT:
				returnAnim=Assets.instance.assetTroop.animFaceLeftBeAttacked.get(militaryKindId);
				break;
			case RIGHT:
				returnAnim=Assets.instance.assetTroop.animFaceRightBeAttacked.get(militaryKindId);
				break;
			case UP:
				returnAnim=Assets.instance.assetTroop.animFaceUpBeAttacked.get(militaryKindId);
				break;
			
			}			
			break;
		case BE_CAST:
			break;
		case CAST:
			break;
		case WALK:
			switch(faceDirection){
			case DOWN:
				returnAnim=Assets.instance.assetTroop.animFaceDownWalk.get(militaryKindId);
				break;
			case LEFT:
				returnAnim=Assets.instance.assetTroop.animFaceLeftWalk.get(militaryKindId);
				break;
			case RIGHT:
				returnAnim=Assets.instance.assetTroop.animFaceRightWalk.get(militaryKindId);
				break;
			case UP:
				returnAnim=Assets.instance.assetTroop.animFaceUpWalk.get(militaryKindId);
				break;
			
			}			
			break;
		
		}
		return returnAnim;
	}
	public static Animation getTileAnimationBy(TileEffect effect) {
		return Assets.instance.assetTileEffect.animTileEffect.get(effect);
	}
	public static FaceDirection getOppositeFaceDirection(
			FaceDirection faceDirection) {
		FaceDirection returnFD = faceDirection.UP;
		switch(faceDirection){
		case DOWN:
			returnFD=FaceDirection.UP;
			break;
		case LEFT:
			returnFD=FaceDirection.RIGHT;
			break;
		case RIGHT:
			returnFD=FaceDirection.LEFT;
			break;
		case UP:
			returnFD=FaceDirection.DOWN;
			break;

		
		}
		return returnFD;
	}

}
