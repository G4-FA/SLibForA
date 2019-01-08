package com.g4ap.slibfora;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class SMSDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smsdetail);
		
		TextView tvText = (TextView)findViewById(R.id.act_SMSDetail_TextView);
		tvText.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		initSMSText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.smsdetail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		else if (id == R.id.action_call)
		{
			t_SlibAddrSMSs cur = SLib.getInstance().getCurAddrSMSs();
			Uri callToUri = Uri.parse( "tel:" + cur.m_Tel );
			Intent intentCall = new Intent( Intent.ACTION_CALL, callToUri );
			SMSDetailActivity.this.startActivity(intentCall);
			return true;
		}
		else if (id == R.id.action_sms)
		{
			t_SlibAddrSMSs cur2 = SLib.getInstance().getCurAddrSMSs();
			Uri smsToUri = Uri.parse( "smsto:" + cur2.m_Tel );
			Intent intentSMS = new Intent( Intent.ACTION_SENDTO, smsToUri );
			SMSDetailActivity.this.startActivity(intentSMS);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initSMSText()
	{
		TextView tvText = (TextView)findViewById(R.id.act_SMSDetail_TextView);
		
		t_SlibAddrSMSs curAddrSMSs = SLib.getInstance().getCurAddrSMSs();
		
		StringBuffer strText = new StringBuffer();
		strText.append("DEST\r\n\r\n");

		for ( t_SlibSMS cur : curAddrSMSs.m_SMSList )
		{
			if ( cur.m_Type == 1 )
			{
				strText.append("<<<<====\r\n");
			}
			else if ( cur.m_Type == 2 )
			{
				strText.append("====>>>>\r\n");
			}
			else
			{
				strText.append("========\r\n");
			}
		
			strText.append(cur.m_strDate);
			strText.append("\r\n");
			
			strText.append(cur.m_Text);
			strText.append("\r\n\r\n");
		}
		
		tvText.setText(strText);
		
	}
	
	
}
