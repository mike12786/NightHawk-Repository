package com.zapporoo.nighthawk.quickblox.push;

import android.content.Intent;

public class PushHelper {
	public Intent intent;
	public String title;
	public String message;
	public Integer notification_id;
	public String user_chat_id;
	public String dialog_id;
	public int flag_notification;
	public String type;
	
	public static final int TYPE_CHAT = 0;
	public static final int TYPE_OTHER = 1;
    public String sender_name;
	public int notification_type;

	public int getUserUserChatId() {
		try{
			return Integer.parseInt(user_chat_id);
		}
		catch (Exception e){
			return 0;
		}
	}
}
