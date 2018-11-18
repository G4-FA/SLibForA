package com.g4ap.slibfora;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MemoActivity extends Activity
{

	MyAddrAdapter m_Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memo);

		m_Adapter = new MyAddrAdapter();
		m_Adapter.loadMemoDataFromDB();
		SLib.getInstance().NeedUpdate_MemoData = 0;

		ListView myLV = (ListView)findViewById(R.id.act_memo_ListView);
		myLV.setAdapter(m_Adapter);
		myLV.setOnItemClickListener( new MyOnItemClickListener() );

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}



	private class MyAddrAdapter extends BaseAdapter
	{
		ArrayList<t_SLibMemo> m_MemoData;

		public void loadMemoDataFromDB()
		{
			m_MemoData = SLib.getInstance().getMemoData();
		}

		@Override
		public int getCount() {
			return m_MemoData.size();
		}

		@Override
		public Object getItem(int position) {
			return m_MemoData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			view = LayoutInflater.from(MemoActivity.this.getApplicationContext()).inflate(R.layout.listlayout_memo, null);

			t_SLibMemo memo = (t_SLibMemo)getItem(position);

			TextView tvText = (TextView)view.findViewById(R.id.ll_memo_text);
			tvText.setText( memo.m_Text );
			TextView tvDate = (TextView)view.findViewById(R.id.ll_memo_date);
			tvDate.setText( memo.m_strDate );

			return view;
		}

	}



	class MyOnItemClickListener implements OnItemClickListener
	{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			t_SLibMemo memo = (t_SLibMemo)MemoActivity.this.m_Adapter.getItem(arg2);

    		Intent intent = new Intent(MemoActivity.this, MemoDetailActivity.class);
    		intent.putExtra( "Text", memo.m_Text );
    		startActivity(intent);
		}
	}



}
