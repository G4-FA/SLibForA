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

public class AddrFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_addr, container, false);
	}

	
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {  
        super.onActivityCreated(savedInstanceState);

		MyAddrAdapter myAdapter = new MyAddrAdapter();
		myAdapter.loadAddrDataFromDB();
		MyOnItemClickListener myListener = new MyOnItemClickListener( myAdapter );
		
		ListView myLV = (ListView)getActivity().findViewById(R.id.fragment_addr_ListView);
		myLV.setAdapter(myAdapter);
		myLV.setOnItemClickListener(myListener);

    }
	
	
	private class MyAddrAdapter extends BaseAdapter
	{
		tRetAddrData addrData;
		
		public void loadAddrDataFromDB()
		{
			addrData = SLib.getInstance().getAddrData();
		}

		@Override
		public int getCount() {
			return addrData.m_AddrList.size();
		}

		@Override
		public Object getItem(int position) {
			return addrData.m_AddrList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position)
		{
			tAddr addr = (tAddr)getItem(position);
			if ( addrData.m_ClassList.contains(addr.m_name) )
			{
				return false;
			}

			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			tAddr addr = (tAddr)getItem(position);
			
			if ( addrData.m_ClassList.contains(addr.m_name) )
			{
				view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listlayout_addr, null);
			}
			else
			{
				view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listlayout_addr, null);
			}
			
			TextView tvName = (TextView)view.findViewById(R.id.ll_addr_name);
			tvName.setText( addr.m_name );
			
			TextView tvTel = (TextView)view.findViewById(R.id.ll_addr_tel);
			tvTel.setText( addr.m_tel );

			return view;
		}

	}


	class MyOnItemClickListener implements OnItemClickListener
	{
		private MyAddrAdapter curAdapter;
		
		public MyOnItemClickListener( MyAddrAdapter adapter )
		{
			curAdapter = adapter;
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			tAddr curAddr = (tAddr)curAdapter.getItem( arg2 );
            	
    		Intent intent = new Intent(getActivity(), AddrDetailActivity.class);
    		intent.putExtra( "name", curAddr.m_name );
    		intent.putExtra( "tel", curAddr.m_tel );
    		startActivity(intent);
		}
	}
	
}
