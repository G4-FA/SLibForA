package com.g4ap.slibfora;

import java.text.SimpleDateFormat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallhisFragment extends Fragment
{
	MyAddrAdapter m_Adapter;
	MyOnItemClickListener m_ListItemListener;
	MySMSListener m_SMSListener;
	
	public CallhisFragment()
	{
		m_Adapter = new MyAddrAdapter();
		m_Adapter.loadCallhisDataFromDB();
		SLib.getInstance().NeedUpdate_CallhisData = 0;
		
		m_ListItemListener = new MyOnItemClickListener();
		m_SMSListener = new MySMSListener();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_callhis, container, false);
	}

	
    @Override 
    public void onActivityCreated(Bundle savedInstanceState)
    {  
        super.onActivityCreated(savedInstanceState);

		ListView myLV = (ListView)getActivity().findViewById(R.id.fragment_callhis_ListView);
    	if ( SLib.getInstance().NeedUpdate_CallhisData == 1 )
    	{
    		m_Adapter.loadCallhisDataFromDB();
    		SLib.getInstance().NeedUpdate_CallhisData = 0;
    	}
		myLV.setAdapter(m_Adapter);
		myLV.setOnItemClickListener(m_ListItemListener);
    }
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {  
        super.setUserVisibleHint(isVisibleToUser);  
        if (isVisibleToUser)
        {  
        	if ( SLib.getInstance().NeedUpdate_CallhisData == 1 )
        	{
        		m_Adapter.loadCallhisDataFromDB();
        		m_Adapter.notifyDataSetChanged();
        		SLib.getInstance().NeedUpdate_CallhisData = 0;
        	}
        }  
    }
    

	private class MyAddrAdapter extends BaseAdapter
	{
		tSLibRetCallhisData callhisData;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		public void loadCallhisDataFromDB()
		{
			callhisData = SLib.getInstance().getCallhisData();
		}

		@Override
		public int getCount() {
			return callhisData.m_List.size();
		}

		@Override
		public Object getItem(int position) {
			return callhisData.m_List.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if ( convertView == null )
			{
				convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listlayout_callhis, parent, false);
			}
			
			tSLibCallhis callhis = (tSLibCallhis)getItem(position);
			
			ImageView ivState = (ImageView)convertView.findViewById(R.id.ll_callhis_state);
			if ( callhis.m_State == 0 || callhis.m_State == 1 )
			{
				ivState.setImageResource(R.drawable.arrow2);
			}
			else
			{
				ivState.setImageResource(R.drawable.arrow1);
			}
			
			TextView tvTel = (TextView)convertView.findViewById(R.id.ll_callhis_tel);
			tvTel.setText( callhis.m_Name );
			
			TextView tvSec = (TextView)convertView.findViewById(R.id.ll_callhis_sec);
			tvSec.setText( callhis.m_strSec );
			
			TextView tvDate = (TextView)convertView.findViewById(R.id.ll_callhis_date);
            String date = sdf.format(callhis.m_Date);
			tvDate.setText( date );
			
			ImageView ivSMS = (ImageView)convertView.findViewById(R.id.ll_callhis_sms);
			ivSMS.setOnClickListener( m_SMSListener );
			ivSMS.setTag( Integer.valueOf(position) );

			return convertView;
		}

	}
	
	
	
	class MyOnItemClickListener implements OnItemClickListener
	{
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			tSLibCallhis callhis = (tSLibCallhis)CallhisFragment.this.m_Adapter.getItem(arg2);
			Uri callToUri = Uri.parse( "tel:" + callhis.m_Tel );
			Intent intentCall = new Intent( Intent.ACTION_CALL, callToUri );
			CallhisFragment.this.startActivity(intentCall);
		}
	}
	
	
	class MySMSListener implements OnClickListener
	{
		public void onClick(View v)
		{
			Integer pos = (Integer)v.getTag();
			tSLibCallhis callhis = (tSLibCallhis)CallhisFragment.this.m_Adapter.getItem( pos.intValue() );
			Uri smsToUri = Uri.parse( "smsto:" + callhis.m_Tel );
			Intent intentSMS = new Intent( Intent.ACTION_SENDTO, smsToUri );
			CallhisFragment.this.startActivity(intentSMS);
		}
		
	}
	
	
}
