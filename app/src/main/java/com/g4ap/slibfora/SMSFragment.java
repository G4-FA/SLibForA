package com.g4ap.slibfora;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SMSFragment extends Fragment
{
	
	MyAddrAdapter	m_Adapter;
	
	public SMSFragment()
	{
		m_Adapter = new MyAddrAdapter();
		m_Adapter.loadSMSDataFromDB();
		SLib.getInstance().NeedUpdate_SMSData = 0;
	}

		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_sms, container, false);
	}

	
    @Override  
    public void onActivityCreated(Bundle savedInstanceState)
    {
    	super.onActivityCreated(savedInstanceState);
		
		ListView myLV = (ListView)getActivity().findViewById(R.id.fragment_SMS_ListView);
    	if ( SLib.getInstance().NeedUpdate_SMSData == 1 )
    	{
    		m_Adapter.loadSMSDataFromDB();
    		SLib.getInstance().NeedUpdate_SMSData = 0;
    	}
		myLV.setAdapter(m_Adapter);

		MyOnItemClickListener myListener = new MyOnItemClickListener();
		myLV.setOnItemClickListener(myListener);

    }
	
    
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {  
        super.setUserVisibleHint(isVisibleToUser);  
        if (isVisibleToUser)
        {  
        	if ( SLib.getInstance().NeedUpdate_SMSData == 1 )
        	{
        		m_Adapter.loadSMSDataFromDB();
        		m_Adapter.notifyDataSetChanged();
        		SLib.getInstance().NeedUpdate_SMSData = 0;
        	}
        }  
    }
	

	private class MyAddrAdapter extends BaseAdapter
	{
		t_SlibRetSMS smsData;
		
		public void loadSMSDataFromDB()
		{
			smsData = SLib.getInstance().getSMSData();
		}

		@Override
		public int getCount() {
			return smsData.m_AddrSMSsList.size();
		}

		@Override
		public Object getItem(int position) {
			return smsData.m_AddrSMSsList.get(position);
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
				convertView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listlayout_sms, parent, false);
			}

			t_SlibAddrSMSs curAddrSMSs = (t_SlibAddrSMSs)getItem(position);
			
			TextView tvName = (TextView)convertView.findViewById(R.id.ll_sms_name);
			tvName.setText( String.valueOf(curAddrSMSs.m_Name) );
			
			TextView tvLastDate = (TextView)convertView.findViewById(R.id.ll_sms_lastdate);
			tvLastDate.setText( curAddrSMSs.m_strLastDate );
			
			TextView tvLastText = (TextView)convertView.findViewById(R.id.ll_sms_lasttext);
			tvLastText.setText( String.valueOf(curAddrSMSs.m_LastText) );

			return convertView;
		}

	}
	
	
	class MyOnItemClickListener implements OnItemClickListener
	{
		//private MyAddrAdapter curAdapter;
		
		//public MyOnItemClickListener( MyAddrAdapter adapter )
		//{
		//	curAdapter = adapter;
		//}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			t_SlibAddrSMSs cur = (t_SlibAddrSMSs)SMSFragment.this.m_Adapter.getItem( arg2 );
			SLib.getInstance().setCurAddrSMSs(cur);
			
    		Intent intent = new Intent(SMSFragment.this.getActivity(), SMSDetailActivity.class);
    		SMSFragment.this.getActivity().startActivity(intent);
		}
	}
	
	
}
