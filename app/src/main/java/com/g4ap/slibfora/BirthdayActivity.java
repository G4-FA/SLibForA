package com.g4ap.slibfora;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class BirthdayActivity extends Activity {

	MyBirthdayAdapter m_Adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_birthday);

		m_Adapter = new MyBirthdayAdapter();
		m_Adapter.loadBirthdayDataFromDB();

		ListView myLV = (ListView)findViewById(R.id.act_birthday_ListView);
		myLV.setAdapter(m_Adapter);
	}



	private class MyBirthdayAdapter extends BaseAdapter
	{
		ArrayList<t_SLibBirthday> m_BirthdayData;

		public void loadBirthdayDataFromDB()
		{
			m_BirthdayData = SLib.getInstance().getBirthdayData();
		}

		@Override
		public int getCount() {
			return m_BirthdayData.size();
		}

		@Override
		public Object getItem(int position) {
			return m_BirthdayData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			view = LayoutInflater.from(BirthdayActivity.this.getApplicationContext()).inflate(R.layout.listlayout_birth, null);

			t_SLibBirthday birth = (t_SLibBirthday)getItem(position);

			TextView tvName = (TextView)view.findViewById(R.id.ll_birth_name);
			tvName.setText( birth.m_Name );

			TextView tvDayleft = (TextView)view.findViewById(R.id.ll_birth_dayleft);

			String strDayleft = String.format("AGE:%d->%d  %d Days", birth.m_Age, birth.m_Age+1, birth.m_DayLeft );
			tvDayleft.setText( strDayleft );

			TextView tvDetailDate = (TextView)view.findViewById(R.id.ll_birth_detialdate);
			String strSL = birth.m_IsLunar==0?"S":"L";
			String strDetailDate = String.format("(Source) %s %04d.%02d.%02d      (Current) S %04d.%02d.%02d  L %04d.%02d.%02d",
					strSL, birth.m_Year, birth.m_Month, birth.m_Day,
					birth.m_NextSolar.solarYear, birth.m_NextSolar.solarMonth, birth.m_NextSolar.solarDay,
					birth.m_NextLunar.lunarYear, birth.m_NextLunar.lunarMonth, birth.m_NextLunar.lunarDay );

			tvDetailDate.setText( strDetailDate );

			return view;
		}

	}




}
