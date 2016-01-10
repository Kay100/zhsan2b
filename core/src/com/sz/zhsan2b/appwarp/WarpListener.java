package com.sz.zhsan2b.appwarp;

public interface WarpListener {
	
	public void onWaitingStarted(String message);
	
	public void onError(String message);
	
	public void onGameStarted(String message);
	
	public void onGameFinished(int code, boolean isRemote);
	
	public void onGameUpdateReceived(String message);
	
	
	public void onPlayerEntered(boolean isPlayer);

	public void onCanCalculateBattle();

	public void onGameStateDataReceived(byte[] data);
}
