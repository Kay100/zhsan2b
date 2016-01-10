package com.sz.zhsan2b.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.listener.UpdateRequestListener;

public class UpdateListener implements UpdateRequestListener {
	WarpController callBack;
	
	public UpdateListener(WarpController callBack) {
		this.callBack = callBack;
	}	
	@Override
	public void onSendUpdateDone(byte arg0) {
		callBack.onSendUpdateDone(arg0);

	}

}
