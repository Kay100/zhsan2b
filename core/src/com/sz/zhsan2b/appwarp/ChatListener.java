package com.sz.zhsan2b.appwarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.listener.ChatRequestListener;



public class ChatListener implements ChatRequestListener{
	
	WarpController callBack;
	
	public ChatListener(WarpController callBack) {
		this.callBack = callBack;
	}

	public void onSendChatDone(byte result) {
		callBack.onSendChatDone(result);
	}

	@Override
	public void onSendPrivateChatDone (byte arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
