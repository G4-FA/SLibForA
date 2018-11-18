package com.g4ap.slibfora;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class SLibForA_SYS_PhoneCallReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

		// in call
		if ( !intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL) && tm.getCallState() == TelephonyManager.CALL_STATE_RINGING )
		{
			String phoneNumber = intent.getStringExtra("incoming_number");
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 2);
			bundle.putString("tel", phoneNumber);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			context.startService(is);
			return;
		}

		// out call
		if ( intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL) )
		{
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 3);
			bundle.putString("tel", phoneNumber);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			context.startService(is);
			return;
		}

		// close call
		if ( tm.getCallState() == TelephonyManager.CALL_STATE_IDLE )
		{
			Intent is = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
			Bundle bundle = new Bundle();
			bundle.putInt("op", 4);
			is.putExtras(bundle);
			is.setPackage("com.g4ap.slibfora");
			context.startService(is);
			return;
		}

	}


}
