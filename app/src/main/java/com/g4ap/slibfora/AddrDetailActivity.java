package com.g4ap.slibfora;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AddrDetailActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addr_detail);

		TextView aName = (TextView)findViewById(R.id.act_addr_detail_name);
		TextView aTel = (TextView)findViewById(R.id.act_addr_detail_tel);
		String strName = (String) getIntent().getExtras().get("name");
		String strTel = (String) getIntent().getExtras().get("tel");
		aName.setText(strName);
		aTel.setText(strTel);
		
		Button aCall = (Button)findViewById(R.id.act_addr_detail_call);
		Button aSMS = (Button)findViewById(R.id.act_addr_detail_sms);
		aCall.setOnClickListener( new myListener() );
		aSMS.setOnClickListener( new myListener() );

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.addr_detail, menu);
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
	
	
	
	class  myListener implements OnClickListener
	{
		public void onClick(View v)
		{
			TextView tvTel = (TextView) findViewById(R.id.act_addr_detail_tel);

			switch (v.getId())
			{
			case R.id.act_addr_detail_call:
				Uri callToUri = Uri.parse( "tel:" + tvTel.getText() );
				Intent intentCall = new Intent( Intent.ACTION_CALL, callToUri );
				AddrDetailActivity.this.startActivity(intentCall);
				break;

			case R.id.act_addr_detail_sms:
				Uri smsToUri = Uri.parse( "smsto:" + tvTel.getText() );
				Intent intentSMS = new Intent( Intent.ACTION_SENDTO, smsToUri );
				AddrDetailActivity.this.startActivity(intentSMS);
				break;

			default:break;
			}
		}
		
	}
	
	
	
	
}

