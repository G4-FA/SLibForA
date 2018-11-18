package com.g4ap.slibfora;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity
{
	
   private AddrFragment		m_AddrFragment		= new AddrFragment();  
   private CallhisFragment	m_CallhisFragment	= new CallhisFragment();  
   private SMSFragment		m_SMSFragment		= new SMSFragment();  
   private SyncFragment		m_SyncFragment		= new SyncFragment();

   ViewPager m_ViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        final ActionBar actionBar = getActionBar();  
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
  
        MyViewPagerAdapter mViewPagerAdapter = new MyViewPagerAdapter( getSupportFragmentManager() );  
        m_ViewPager = (ViewPager)findViewById(R.id.act_main_viewpager);  
        m_ViewPager.setOffscreenPageLimit( 3 );
        m_ViewPager.setAdapter( mViewPagerAdapter );  
        m_ViewPager.setOnPageChangeListener( new MySimpleOnPageChangeListener() );  

        MyTabListener myTabLis = new MyTabListener();
        for ( int i = 0; i < mViewPagerAdapter.getCount(); i++ )
        {
        	Tab curTab = actionBar.newTab();
        	curTab.setText( mViewPagerAdapter.getPageTitle(i) );
            curTab.setTabListener( myTabLis );
            actionBar.addTab( curTab );  
        }
 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
		else if ( id == R.id.action_birthday )
		{
			Intent intent = new Intent(this, BirthdayActivity.class);
			startActivity(intent);
		}
        else if ( id == R.id.action_memo )
        {
    		Intent intent = new Intent(this, MemoActivity.class);
    		startActivity(intent);
        }
        
        
        return super.onOptionsItemSelected(item);
    }
    
    

	private class MyTabListener implements TabListener
	{
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) { }
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) { }
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft)
		{
			m_ViewPager.setCurrentItem(tab.getPosition());
		}
	}

	
	
	class MyViewPagerAdapter extends FragmentPagerAdapter
	{

		public MyViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position) {
			case 0:
				return MainActivity.this.m_AddrFragment;
			case 1:
				return MainActivity.this.m_CallhisFragment;
			case 2:
				return MainActivity.this.m_SMSFragment;
			case 3:
				return MainActivity.this.m_SyncFragment;
			}
			throw new IllegalStateException("No fragment at position "+ position);
		}

		@Override
		public int getCount()
		{
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			String tabLabel = null;
			switch (position) {
			case 0:
				tabLabel = "联系人";
				break;
			case 1:
				tabLabel = "通话记录";
				break;
			case 2:
				tabLabel = "信息";
				break;
			case 3:
				tabLabel = "SYNC";
				break;
			}
			return tabLabel;
		}
	}

	
    
	class MySimpleOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener
	{  
        @Override  
        public void onPageSelected(int position)
        {  
            final ActionBar actionBar = getActionBar();  
            actionBar.setSelectedNavigationItem(position);  
        }  
          
        @Override  
        public void onPageScrollStateChanged(int state)
        {  
            switch(state)
            {  
                case ViewPager.SCROLL_STATE_IDLE:  
                    break;  
                case ViewPager.SCROLL_STATE_DRAGGING:  
                    break;  
                case ViewPager.SCROLL_STATE_SETTLING:  
                    break;  
                default:  
                    break;  
            }  
        }  
    }


	
}

