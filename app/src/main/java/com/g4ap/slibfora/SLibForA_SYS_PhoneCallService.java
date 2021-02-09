package com.g4ap.slibfora;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.WindowManager;
import android.widget.TextView;

public class SLibForA_SYS_PhoneCallService extends Service
{
	TextView m_TextView = null;
			
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate()
	{

	}

	@Override
	public void onDestroy()
	{

	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		if ( intent == null ) return;
		if ( intent.getExtras() == null ) return;
		
		Bundle bundle = intent.getExtras();
		int op = bundle.getInt("op");

		switch (op)
		{
		case 1:
			//NormalStart();
			break;
		case 2:
			InCall(intent);
			break;
		case 3:
			OutCall(intent);
			break;
		case 4:
			CloseCall();
			break;
		}

	}

	public void InCall( Intent intent )
	{
		WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Bundle bundle = intent.getExtras();
		String tel = bundle.getString("tel");

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.RGBA_8888;

		m_TextView = new TextView(this.getApplicationContext());
		m_TextView.setText( SLib.getInstance().quaryAddrNameByTel(tel) + "   " + tel );
		wm.addView(m_TextView, params);
	}
	
	public void OutCall( Intent intent )
	{
		WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Bundle bundle = intent.getExtras();
		String tel = bundle.getString("tel");

		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.RGBA_8888;

		m_TextView = new TextView(this.getApplicationContext());
		m_TextView.setText( SLib.getInstance().quaryAddrNameByTel(tel) + "   " + tel );
		wm.addView(m_TextView, params);
	}


	public void CloseCall()
	{
		WindowManager wm = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        if( m_TextView != null )
        {
            wm.removeView(m_TextView);
        }
	}



}
