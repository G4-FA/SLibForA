package com.g4ap.slibfora;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class SyncFragment extends Fragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_sync, container, false);
	}

	
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
    	
		Button btnSyncCallhis = (Button)getActivity().findViewById(R.id.fragment_sync_SyncCallhis);
		Button btnSyncSMS = (Button)getActivity().findViewById(R.id.fragment_sync_SyncSMS);
		btnSyncCallhis.setOnClickListener( new myAdapternListener() );
		btnSyncSMS.setOnClickListener( new myAdapternListener() );

		Button btnsql = (Button)getActivity().findViewById(R.id.button2);
		btnsql.setOnClickListener( new myAdapternListener() );
		
    	refreshState();
    	
    	//Switch swService = (Switch)getActivity().findViewById(R.id.fragment_sync_PhoneCallService);
    	//swService.setOnCheckedChangeListener( new MyOnCheckedChangeListener() );
    }
    
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {  
        super.setUserVisibleHint(isVisibleToUser);  
        if (isVisibleToUser)
        {  
        	refreshState();
        }  
    }



	private void refreshState()
	{
		TextView tvSycConf = (TextView)getActivity().findViewById(R.id.fragment_sync_SystemConfig);
		TextView tvCallhisSYS = (TextView)getActivity().findViewById(R.id.fragment_sync_MaxCallhis_SYS);
		TextView tvCallhisSLib = (TextView)getActivity().findViewById(R.id.fragment_sync_MaxCallhis_SLib);
		TextView tvSMSSYS = (TextView)getActivity().findViewById(R.id.fragment_sync_MaxSMS_SYS);
		TextView tvSMSSLib = (TextView)getActivity().findViewById(R.id.fragment_sync_MaxSMS_SLib);

		
		retSyncStateA state = SLib.getInstance().getSyncStateA( getActivity() );
		tvSycConf.setText( "sync_type: " + state.sync_type + "  sync_device: " + state.sync_device );
		tvCallhisSYS.setText( "MAX_CALLHIS_SYS: " + state.m_CallhisSys );
		tvCallhisSLib.setText( "MAX_CALLHIS_SLib: " + state.m_CallhisSLib );
		tvSMSSYS.setText( "MAX_SMS_SYS: " + state.m_SMSSys );
		tvSMSSLib.setText( "MAX_SMS_SLib: " + state.m_SMSSLib );
		
		/*
		Switch swService = (Switch)getActivity().findViewById(R.id.fragment_sync_PhoneCallService);
        ActivityManager activityManager = (ActivityManager)getActivity().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
        for ( ActivityManager.RunningServiceInfo cur : serviceList)
        {
        	if ( cur.service.getClassName().equals("SLibForA_SYS_PhoneCallService") )
        	{
        		swService.setChecked(true);
        		return;
        	}
        }
        swService.setChecked(false);
        */
	}


	class  myAdapternListener implements OnClickListener
	{
		public void onClick(View v)
		{
		      switch(v.getId())
		      {
		       case R.id.button2:
		    	   EditText etsql = (EditText)getActivity().findViewById(R.id.editText2);
			   		SQLiteDatabase db = SQLiteDatabase.openDatabase("data/data/com.g4ap.slibfora/files/slib.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
					db.execSQL( etsql.getText().toString() );
					db.close();
					break;

		       case R.id.fragment_sync_SyncCallhis:
		    	   SLib.getInstance().syncAndroidCallhisDB( SyncFragment.this.getActivity() );
		    	   refreshState();
		    	   break;
		        
		       case R.id.fragment_sync_SyncSMS:
		    	   SLib.getInstance().syncAndroidSMSDB( SyncFragment.this.getActivity() );
		    	   refreshState();
		    	   break;
			        
		       default:break;
		      }
		}
	}
	
	
	/*
	class MyOnCheckedChangeListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		{
			if (isChecked)
			{
				Intent intent = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
		        Bundle bundle = new Bundle();
		        bundle.putInt("op", 1);
		        intent.putExtras(bundle);
		        getActivity().startService(intent);
			}
			else
			{
				Intent intent = new Intent("com.g4ap.slibfora.SLibForA_SYS_PhoneCallService");
		        Bundle bundle = new Bundle();
		        bundle.putInt("op", 4);
		        intent.putExtras(bundle);
		        getActivity().startService(intent);
			}
		}
	}
	*/
	
	
}
