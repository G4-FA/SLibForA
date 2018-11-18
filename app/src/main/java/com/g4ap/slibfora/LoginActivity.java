package com.g4ap.slibfora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends Activity
{
	int bInit = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Button testBtn = (Button) findViewById(R.id.act_login_login);
		testBtn.setOnClickListener(myAdapterBtnListener);
		
		Button btnInitDB = (Button) findViewById(R.id.act_login_initdb);
		btnInitDB.setOnClickListener(myAdapterBtnListener);
	}

	private View.OnClickListener myAdapterBtnListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View v) {

			switch (v.getId())
			{
			case R.id.act_login_login:
				if (bInit != 1096)
				{
					SLib.getInstance().loadSlibDataFromDB();
					bInit = 1096;
				}
				
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				break;

			//case R.id.act_login_initdb:
			//	copyResDBFile();
			//	break;
			}

		}

	};
	
	
/*
	private void copyResDBFile()
	{
		
		try
		{
			InputStream resDB = getResources().openRawResource(R.raw.slib);
			FileOutputStream dataDB = new FileOutputStream( getApplicationContext().getFilesDir() + "/slib.ax" );

			int byteread = 0;
			byte[] buffer = new byte[1024];

			while ( (byteread = resDB.read(buffer)) != -1 )
			{
				dataDB.write(buffer, 0, byteread);
			}

			dataDB.flush();
			dataDB.close();
			resDB.close();
			
		} catch (Exception e)
		{

		}

	}
*/

}
