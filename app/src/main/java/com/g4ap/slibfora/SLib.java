package com.g4ap.slibfora;


import java.util.ArrayList;

import android.app.Activity;




public class SLib
{

	public SLib()
	{
		m_SLibSync = new SLib_Sync();
		m_SLibDBLoader = new SLib_DBLoader();
		
		NeedUpdate_CallhisData = 0;
		NeedUpdate_SMSData = 0;
		NeedUpdate_MemoData = 0;
	}
	
	public void loadSlibDataFromDB()
	{
		m_AddrData = m_SLibDBLoader.loadAddrFromDB();
		m_CallhisData = m_SLibDBLoader.loadCallhisFromDB( m_AddrData );
		m_SMSData = m_SLibDBLoader.loadSMSFromDB( m_AddrData );
		m_MemoData = m_SLibDBLoader.loadMemoFromDB();
		m_BirthdayData = m_SLibDBLoader.loadBirthdayFromDB();
		NeedUpdate_CallhisData = 1;
		NeedUpdate_SMSData = 1;
		NeedUpdate_MemoData = 1;
	}


	public tRetAddrData getAddrData() { return m_AddrData; }
	public tSLibRetCallhisData getCallhisData() { return m_CallhisData; }
	public t_SlibRetSMS getSMSData() { return m_SMSData; }
	public ArrayList<t_SLibMemo> getMemoData() { return m_MemoData; }
	public ArrayList<t_SLibBirthday> getBirthdayData() { return m_BirthdayData; }
	
	
	public void syncAndroidCallhisDB( Activity act )
	{
		m_SLibSync.syncAndroidCallhisDB(act);
		m_CallhisData = m_SLibDBLoader.loadCallhisFromDB( m_AddrData );
		NeedUpdate_CallhisData = 1;
	}
	
	public void syncAndroidSMSDB( Activity act )
	{
		m_SLibSync.syncAndroidSMSDB(act);
		m_SMSData = m_SLibDBLoader.loadSMSFromDB( m_AddrData );
		NeedUpdate_SMSData = 1;
	}
	
	public retSyncStateA getSyncStateA( Activity act )
	{
		return m_SLibSync.getSyncStateA(act);
	}

	
	public String quaryAddrNameByTel( String tel )
	{
		return m_SLibSync.quaryAddrNameByTel(tel);
	}
	
	
	private t_SlibAddrSMSs m_curAddrSMSs;
	public void setCurAddrSMSs( t_SlibAddrSMSs cur ) { m_curAddrSMSs = cur; }
	public t_SlibAddrSMSs getCurAddrSMSs() { return m_curAddrSMSs; }
	
	
	
	private static SLib _instance = null; 
	public static synchronized SLib getInstance()
	{
		if (_instance == null)
			_instance = new SLib();
		return _instance;
	}

	private tRetAddrData				m_AddrData;
	private tSLibRetCallhisData		m_CallhisData;
	private t_SlibRetSMS				m_SMSData;
	private ArrayList<t_SLibMemo>		m_MemoData;
	private ArrayList<t_SLibBirthday>	m_BirthdayData;
	
	
	
	public int NeedUpdate_CallhisData;
	public int NeedUpdate_SMSData;
	public int NeedUpdate_MemoData;
	
	
	private SLib_Sync m_SLibSync;
	private SLib_DBLoader m_SLibDBLoader;
	
	
	//String m_SLibDBPath = "data/data/com.g4ap.slibfora/files/slib.sqlite";
	public static String m_SLibDBPath = "/sdcard/Download/SLib.ax";

}


