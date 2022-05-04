package com.g4ap.slibfora;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SLibForA_SYS_PhoneCallReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

		Bundle exData = intent.getExtras();
		String intentAction = intent.getAction();
		int callState = tm.getCallState();

		String stateStr = exData.getString(TelephonyManager.EXTRA_STATE);
		String number = exData.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if ( stateStr == null ) {
			Log.i("aa","11111111111111111111111111111111 stateStr err");
			return;
		}
		else {
			Log.i("aa", stateStr);
		}


		if ( number == null ){
			Log.i("aa","11111111111111111111111111111111 number err");
			return; //会有一次多余的空号广播 可以忽略
		}
		else
		{
			Log.i("aa",number);
		}

		Log.i("aa","11111111111111111111111111111111ok");

		SLib.m_SLibDBPath = context.getExternalFilesDir(null) + "/SLib.ax";

		// in call
		if ( !intentAction.equals(Intent.ACTION_NEW_OUTGOING_CALL) && callState == TelephonyManager.CALL_STATE_RINGING )
		{
			String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 2);
			bundle.putString("tel", phoneNumber);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			context.startService(is);

			Log.i("aa","11111111111111111111111111111111 CALL_STATE_RINGING");
			return;
		}

		// out call
		/*
		if ( intentAction.equals(Intent.ACTION_NEW_OUTGOING_CALL) )
		{
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 3);
			bundle.putString("tel", phoneNumber);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			//context.startService(is);

			Log.i("aa","11111111111111111111111111111111 ACTION_NEW_OUTGOING_CALL");
			return;
		}
		*/

		// close call
		if ( callState == TelephonyManager.CALL_STATE_IDLE )
		{
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 4);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			context.startService(is);

			Log.i("aa","11111111111111111111111111111111 CALL_STATE_IDLE");
			return;
		}

		Log.i("aa","11111111111111111111111111111111 no process");
	}


}
