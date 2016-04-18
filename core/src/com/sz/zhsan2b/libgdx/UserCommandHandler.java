package com.sz.zhsan2b.libgdx;

import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sz.zhsan2b.appwarp.WarpController;
import com.sz.zhsan2b.core.GameContext;
import com.sz.zhsan2b.core.entity.Command;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.entity.Troop;
import com.sz.zhsan2b.core.entity.Command.ACTION_KIND;
import com.sz.zhsan2b.core.service.TroopManager;
import com.sz.zhsan2b.libgdx.ConfirmationDialog.Confirmable;
import com.sz.zhsan2b.libgdx.ContextMenu.Executable;
import com.sz.zhsan2b.libgdx.TroopActor.ACTION_LABEL;
/**
 * 这个组件是处理操作期的展示逻辑的主要部件
 * @author Administrator
 *
 */
public class UserCommandHandler {
	private final Vector2 tempCoords = new Vector2();
	private Table layerOperation = Zhsan2b.battleScreen.getLayerOperation();
	private Table layerAnimation = Zhsan2b.battleScreen.getLayerAnimation();
	private Stage stage = Zhsan2b.battleScreen.getStage();
	private TroopManager troopManager;
	private TroopActor target;
	//troop range
	private Table troopAttackRange;
	private Table troopMoveRange;
	
	public UserCommandHandler() {
		troopManager=GameContext.getContext().getBean(TroopManager.class);
	}
	
	
	public abstract class OnTroopActionClicked implements Executable {

		@Override
		public void execute() {
			layerOperation.findActor("menuList").remove();
			if(isDisplayRange()){
				displayRangeArea();
			}
			
			Vector2 worldP = stage.screenToStageCoordinates(tempCoords.set(Gdx.input.getX(), Gdx.input.getY()));
			final Image xuanze = new Image(Assets.instance.assetWangge.xuanze);
			xuanze.setPosition(((int)(worldP.x/Constants.WANGGE_UNIT_WIDTH))*Constants.WANGGE_UNIT_WIDTH, ((int)(worldP.y/Constants.WANGGE_UNIT_HEIGHT))*Constants.WANGGE_UNIT_HEIGHT);
			layerOperation.add(xuanze);
			Gdx.input.setCursorImage(Assets.instance.assetArrow.select, 0, 0);
			final InputListener inputListener = new InputListener(){

				@Override
				public boolean mouseMoved(InputEvent event, float x, float y) {
					xuanze.setPosition(((int)(x/Constants.WANGGE_UNIT_WIDTH))*Constants.WANGGE_UNIT_WIDTH, ((int)(y/Constants.WANGGE_UNIT_HEIGHT))*Constants.WANGGE_UNIT_HEIGHT);
					return super.mouseMoved(event, x, y);
				}
				
			};
			stage.addListener(inputListener);	
			xuanze.addListener(new ClickListener(){

				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.input.setCursorImage(Assets.instance.assetArrow.normal,
							0, 0);
					final Position xuanzePosition =  new Position(0, 0);
					RenderUtils.toLogicPosition(xuanze.getX(), xuanze.getY(),xuanzePosition);
					if(isSelectedRightPosition(xuanzePosition)){
						stage.removeListener(inputListener);
						
						ConfirmationDialog conD = new ConfirmationDialog(
								new Confirmable() {

									@Override
									public void confirm() {
										Troop object = troopManager.getBattleField().getTroopByPosition(
												xuanzePosition);

										disposeToAddCommand(object,xuanzePosition);
										synchronizeTroopCommand(target.getTroop());
									    target.setActionLabel(ACTION_LABEL.DONE);
										//Gdx.app.debug(TAG, troop.getCommand().toString());
										layerOperation.clear();
									}

									@Override
									public void cancel() {
										layerOperation.clear();
									}
								});						
					layerOperation.add(
							conD.combined);
					conD.combined.setPosition(
							stage.getCamera().position.x - 120,
							stage.getCamera().position.y - 30);						
						
					}
					
					super.clicked(event, x, y);
				}

				


				
			});
			
			

		}

		abstract protected boolean isDisplayRange() ;
		abstract protected void displayRangeArea();
		abstract protected boolean isSelectedRightPosition(final Position xuanzePosition);
		abstract protected void disposeToAddCommand(final Troop object,final Position xuanzePosition);

	}	

	public class OnTroopMoveClicked extends OnTroopActionClicked {

		@Override
		protected boolean isDisplayRange() {
			return true;
		}

		@Override
		protected void displayRangeArea() {
			computerTroopMoveRange();
			//Gdx.app.debug(TAG, String.valueOf(troopMoveRange.getChildren().size));
			layerOperation.add(troopMoveRange);
			
		}

		@Override
		protected void disposeToAddCommand(final Troop object,final Position xuanzePosition) {

			Command command = target.getTroop().getCommand();
			command.actionKind = ACTION_KIND.MOVE;
			target.getTroop().getCommand().zhanfaId = 0;
			if (object == null) {
				command.object = null;
				command.objectPosition = xuanzePosition;
			} else {
				command.object = object;
			}
		}



		@Override
		protected boolean isSelectedRightPosition(Position xuanzePosition) {
			return true;
		}

	}
	public class OnTroopAttackClicked extends OnTroopActionClicked {
		@Override
		protected boolean isSelectedRightPosition(final Position xuanzePosition) {
			final Troop object = troopManager.getBattleField().getTroopByPosition(xuanzePosition);	
			if(object!=null&&object.getOwner()==target.getTroop().getOwner()){
				Zhsan2b.battleScreen.displayNotification("不能选择己方部队!");
				return false;
			}else{
				return true;
			}	
		}		
		@Override
		public void disposeToAddCommand(final Troop object,final Position xuanzePosition) {
			Command command =target.getTroop().getCommand();
			command.actionKind = ACTION_KIND.ATTACK;
			command.zhanfaId = 0;
			if (object == null) {
				command.object = null;
				command.objectPosition = xuanzePosition;
			} else {
				command.object = object;
			}

		}

		@Override
		protected boolean isDisplayRange() {
			return false;
		}

		@Override
		protected void displayRangeArea() {
			computeTroopAttackRange();
			layerOperation.add(troopAttackRange);			
		}


	}
	
	private void synchronizeTroopCommand(Troop troop) {
		String data = troopManager.buildTroopCommandJSON(troop);
		WarpController.getInstance().sendGameUpdate(data);
	}
	public Table computerTroopMoveRange() {
		assertTargetNotNull();
		troopMoveRange = new Table();
		troopMoveRange.setLayoutEnabled(false);
		List<Position> rangeList = troopManager.getMoveRangeList(target.getTroop(),target.getTroop().getPosition(),target.getTroop().getBattleProperties().move,null);
		for(Position p:rangeList){
			Image curImg = new Image(Assets.instance.assetWangge.blue);
			curImg.setSize(Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT);
			curImg.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
			curImg.toBack();
			troopMoveRange.add(curImg);
		}
		return troopMoveRange;
	}

	public Table computeTroopAttackRange() {
		assertTargetNotNull();
		troopAttackRange = new Table();
		troopAttackRange.setLayoutEnabled(false);
		List<Position> rangeList = troopManager.getAttackRangeList(target.getTroop());
		for(Position p:rangeList){
			Image curImg = new Image(Assets.instance.assetWangge.red);
			curImg.setSize(Constants.WANGGE_UNIT_WIDTH, Constants.WANGGE_UNIT_HEIGHT);
			curImg.setPosition(RenderUtils.translate(p.x), RenderUtils.translate(p.y));
			curImg.toBack();
			troopAttackRange.add(curImg);
		}
		return troopAttackRange;
	}
	private void assertTargetNotNull(){
		if(target==null){
			throw new RuntimeException("Target troop must be set before invoke this method!");
		}
	}
	
	public Executable attack(){
		return new OnTroopAttackClicked();
	}
	public Executable move(){
		return new OnTroopMoveClicked();
	}	
	public TroopActor getTarget() {
		return target;
	}
	public void setTarget(TroopActor target) {
		this.target = target;
	}	
}
