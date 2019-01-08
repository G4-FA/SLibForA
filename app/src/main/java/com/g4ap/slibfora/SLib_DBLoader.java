package com.g4ap.slibfora;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

public class SLib_DBLoader
{
	
	public tRetAddrData loadAddrFromDB()
	{
		tRetAddrData ret = new tRetAddrData();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(SLib.getInstance().m_SLibDBPath, null, SQLiteDatabase.OPEN_READONLY);
		

		// read the class list
		ret.m_ClassList = new ArrayList<String>();
		Cursor c = db.rawQuery( "SELECT DISTINCT class FROM t_addr WHERE isvalid = ?", new String[] { "1" } );
		while ( c.moveToNext() )
		{
			ret.m_ClassList.add( c.getString(c.getColumnIndex("class")) );
		}
		c.close();
		
		
		// read all addr (saved sort by class) include tag-addr
		ret.m_AddrList = new ArrayList<tAddr>();
		for ( int i=0; i<ret.m_ClassList.size(); i++ )
		{
			tAddr addrtag = new tAddr();
			addrtag.m_name = ret.m_ClassList.get(i);
			addrtag.m_tel = "0";
			ret.m_AddrList.add( addrtag );

			c = db.rawQuery( "SELECT name, tel FROM t_addr WHERE class = ? AND isvalid = ?", new String[] { ret.m_ClassList.get(i), "1" } );
			while ( c.moveToNext() )
			{
				tAddr addr = new tAddr();
				addr.m_name = ret.m_ClassList.get(i) + c.getString( c.getColumnIndex("name") );
				addr.m_tel = c.getString( c.getColumnIndex("tel") );
				ret.m_AddrList.add( addr );
			}
			c.close();
		}

		
		db.close();

		return ret;
	}
	
	
	public tSLibRetCallhisData loadCallhisFromDB( tRetAddrData addrData )
	{
		tSLibRetCallhisData ret = new tSLibRetCallhisData();
		ret.m_List = new ArrayList<tSLibCallhis>();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(SLib.getInstance().m_SLibDBPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor c = db.rawQuery( "SELECT * FROM t_callhis WHERE isvalid = ? ORDER BY rowid DESC", new String[] { "1" } );
		
		if ( c.getCount() <= 0 )
		{
			c.close();
			db.close();
			return ret;
		}
		
		int idx_ios_addres = c.getColumnIndex("ios_addres");
		int idx_ios_date = c.getColumnIndex("ios_date");
		int idx_ios_duration = c.getColumnIndex("ios_duration");
		int idx_ios_flags = c.getColumnIndex("ios_flags");
		int idx_a_number = c.getColumnIndex("a_number");
		int idx_a_date = c.getColumnIndex("a_date");
		int idx_a_duration = c.getColumnIndex("a_duration");
		int idx_a_type = c.getColumnIndex("a_type");
		int idx_sync_type = c.getColumnIndex("sync_type");
		
		while ( c.moveToNext() )
		{
			tSLibCallhis curCallHis = new tSLibCallhis();

			// ios db data
			if ( c.getInt(idx_sync_type) == 1 )
			{
				curCallHis.m_Tel = c.getString(idx_ios_addres);
				curCallHis.m_Name = getAddrNameByTel( curCallHis.m_Tel, addrData );
				curCallHis.m_Date = new Date( c.getLong(idx_ios_date) * 1000l );
				curCallHis.m_Sec = c.getInt(idx_ios_duration);
				
				int nFlags = c.getInt(idx_ios_flags);
				if ( nFlags == 0 )
				{
					if ( curCallHis.m_Sec == 0 )
						curCallHis.m_State = 1;	// in-err
					else
						curCallHis.m_State = 0;	// in-ok
				}
				else
				{
					if ( curCallHis.m_Sec == 0 )
						curCallHis.m_State = 3;	// out-err
					else
						curCallHis.m_State = 2;	// out-ok
				}
			}
			// android db data
			else if ( c.getInt(idx_sync_type) == 2 )
			{
				curCallHis.m_Tel = c.getString(idx_a_number);
				curCallHis.m_Name = getAddrNameByTel( curCallHis.m_Tel, addrData );
				curCallHis.m_Date = new Date( c.getLong(idx_a_date) );
				curCallHis.m_Sec = c.getInt(idx_a_duration);
				
				int nType = c.getInt(idx_a_type);
				if ( nType == 3 )
				{
					curCallHis.m_State = 1;	// in-err
				}
				else if ( nType == 1 )
				{
					curCallHis.m_State = 0;	// in-ok
				}
				else if ( nType == 2 )
				{
					if ( curCallHis.m_Sec == 0 )
						curCallHis.m_State = 3;	// out-err
					else
						curCallHis.m_State = 2;	// out-ok
				}
				else
				{
					curCallHis.m_State = 3;	// !err!
				}
				
			}
			else
			{
				continue;
			}

			if ( curCallHis.m_Sec <= 0 )
			{
				curCallHis.m_strSec = "ERR!";
			}
			else if ( curCallHis.m_Sec < 60 )
			{
				curCallHis.m_strSec = curCallHis.m_Sec + "sec";
			}
			else
			{
				curCallHis.m_strSec = curCallHis.m_Sec/60  + "min";
			}
			ret.m_List.add( curCallHis );

		}

		
		c.close();
		db.close();

		return ret;

	}
	
	
	// 39sec -> 18sec -> 13sec -> 6sec
	public t_SlibRetSMS loadSMSFromDB( tRetAddrData addrData )
	{
		t_SlibRetSMS ret = new t_SlibRetSMS();
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(SLib.getInstance().m_SLibDBPath, null, SQLiteDatabase.OPEN_READONLY);
		Time tDate = new Time();

		// load ios sms-addr-list
		ArrayList<t_SlibAddrSMSs> iosAddrSMSsList = new ArrayList<t_SlibAddrSMSs>();
		Cursor c = db.rawQuery( "SELECT tel, ios_date, ios_is_from_me, ios_text FROM t_sms WHERE sync_type = 1 AND isvalid = ? ORDER BY tel", new String[] { "1" } );
		if ( c.getCount() > 0 )
		{
			int idx1 = c.getColumnIndex("tel");
			int idx2 = c.getColumnIndex("ios_date");
			int idx3 = c.getColumnIndex("ios_is_from_me");
			int idx4 = c.getColumnIndex("ios_text");
			String strLastTel = "a nil tel";
			t_SlibAddrSMSs cur = null;

			while ( c.moveToNext() )
			{
				// if a new tel
				if ( !strLastTel.equals(c.getString(idx1)) )
				{
					// insert last addr
					if ( cur != null )
					{
						iosAddrSMSsList.add(cur);
					}
					
					// create new addr
					cur = new t_SlibAddrSMSs();
					cur.m_SMSList = new ArrayList<t_SlibSMS>();
					strLastTel = c.getString(idx1);
					cur.m_Tel = strLastTel;
				}

				// insert cur sms
				t_SlibSMS curSMS = new t_SlibSMS();
				curSMS.m_Date = c.getLong(idx2) * 1000l + 978307200000l; //2001 to 1970
				tDate.set( curSMS.m_Date );
				StringBuffer strDate = new StringBuffer();
				strDate.append( tDate.year );
				strDate.append( "-" );
				strDate.append( tDate.month+1 );
				strDate.append( "-" );
				strDate.append( tDate.monthDay );
				strDate.append( " " );
				strDate.append( tDate.hour );
				strDate.append( ":" );
				strDate.append( tDate.minute );
				strDate.append( ":" );
				strDate.append( tDate.second );
				curSMS.m_strDate = strDate.toString();
				curSMS.m_Type = c.getInt(idx3);
				curSMS.m_Text = c.getString(idx4);

				cur.m_SMSList.add(curSMS);
				
			}
			
			// insert last addr
			if ( cur != null )
			{
				iosAddrSMSsList.add(cur);
			}

		}
		c.close();


		// load android sms-addr-list
		ArrayList<t_SlibAddrSMSs> aAddrSMSsList = new ArrayList<t_SlibAddrSMSs>();
		c = db.rawQuery( "SELECT a_address, (CASE WHEN a_date_sent <= 1 THEN a_date ELSE a_date_sent END) AS finaldate, a_type, a_body FROM t_sms WHERE sync_type = 2 AND isvalid = ? ORDER BY a_address", new String[] { "1" } );
		if ( c.getCount() > 0 )
		{
			int idx1 = c.getColumnIndex("a_address");
			int idx2 = c.getColumnIndex("finaldate");
			int idx3 = c.getColumnIndex("a_type");
			int idx4 = c.getColumnIndex("a_body");
			
			String strLastTel = "a nil tel";
			t_SlibAddrSMSs cur = null;

			while ( c.moveToNext() )
			{
				// if a new tel
				if ( !strLastTel.equals(c.getString(idx1)) )
				{
					// insert last addr
					if ( cur != null )
					{
						aAddrSMSsList.add(cur);
					}
					
					// create new addr
					cur = new t_SlibAddrSMSs();
					cur.m_SMSList = new ArrayList<t_SlibSMS>();
					strLastTel = c.getString(idx1);
					cur.m_Tel = strLastTel;
				}
				
				// insert cur sms
				t_SlibSMS curSMS = new t_SlibSMS();
				curSMS.m_Date = c.getLong(idx2);
				tDate.set( curSMS.m_Date );
				StringBuffer strDate = new StringBuffer();
				strDate.append( tDate.year );
				strDate.append( "-" );
				strDate.append( tDate.month+1 );
				strDate.append( "-" );
				strDate.append( tDate.monthDay );
				strDate.append( " " );
				strDate.append( tDate.hour );
				strDate.append( ":" );
				strDate.append( tDate.minute );
				strDate.append( ":" );
				strDate.append( tDate.second );
				curSMS.m_strDate = strDate.toString();
				int nType = c.getInt(idx3);
				if ( nType == 1 ) curSMS.m_Type = 1; else curSMS.m_Type = 2;
				curSMS.m_Text = c.getString(idx4);

				cur.m_SMSList.add(curSMS);
			}
			
			// insert last addr
			if ( cur != null )
			{
				aAddrSMSsList.add(cur);
			}

		}
		c.close();

		db.close();

		// transform tel to name
		for ( t_SlibAddrSMSs curIOS : iosAddrSMSsList)
		{
			curIOS.m_Name = getAddrNameByTel( curIOS.m_Tel, addrData );
		}
		for ( t_SlibAddrSMSs curAndroid : aAddrSMSsList)
		{
			curAndroid.m_Name = getAddrNameByTel( curAndroid.m_Tel, addrData );
		}
		
	
		// merge ios & android to ret list
		ArrayList<t_SlibAddrSMSs> retAddrSMSsList = new ArrayList<t_SlibAddrSMSs>();
		// ios
		for ( t_SlibAddrSMSs curIOS : iosAddrSMSsList)
		{
			int nFind = 0;
			for ( t_SlibAddrSMSs curRet : retAddrSMSsList )
			{
				// if the same name merge the sms
				if ( curIOS.m_Name.equals(curRet.m_Name) )
				{
					nFind = 1;
					curRet.m_SMSList.addAll(curIOS.m_SMSList);
					break;
				}
			}
			if ( nFind == 0 )
			{
				retAddrSMSsList.add(curIOS);
			}
		}
		// android
		for ( t_SlibAddrSMSs curAnd : aAddrSMSsList)
		{
			int nFind = 0;
			for ( t_SlibAddrSMSs curRet : retAddrSMSsList )
			{
				// if the same name merge the sms
				if ( curAnd.m_Name.equals(curRet.m_Name) )
				{
					nFind = 1;
					curRet.m_SMSList.addAll(curAnd.m_SMSList);
					break;
				}
			}
			if ( nFind == 0 )
			{
				retAddrSMSsList.add(curAnd);
			}
		}
		
		
		// sort sms for each addr-sms
		SlibSMSComparator cmpSMS = new SlibSMSComparator();
		for ( t_SlibAddrSMSs cur : retAddrSMSsList )
		{
			Collections.sort(cur.m_SMSList, cmpSMS);
		}
		
		// fill the last sms info for each addr-sms
		for ( t_SlibAddrSMSs cur : retAddrSMSsList )
		{
			cur.m_LastDate = cur.m_SMSList.get(0).m_Date;
			cur.m_strLastDate = cur.m_SMSList.get(0).m_strDate;
			cur.m_LastText = cur.m_SMSList.get(0).m_Text;
		}
		
		// sort addr-sms by date
		SlibAddrSMSsComparator cmpAddr = new SlibAddrSMSsComparator();
		Collections.sort(retAddrSMSsList, cmpAddr);
		
		ret.m_AddrSMSsList = retAddrSMSsList;
		return ret;

	}
	

	
	public ArrayList<t_SLibMemo> loadMemoFromDB()
	{
		ArrayList<t_SLibMemo> ret = new ArrayList<t_SLibMemo>();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(SLib.getInstance().m_SLibDBPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor c = db.rawQuery( "SELECT rowid, text, datemodified FROM t_memo WHERE isvalid = ? ORDER BY datemodified DESC", new String[] { "1" } );
		
		if ( c.getCount() <= 0 )
		{
			c.close();
			db.close();
			return ret;
		}
		
		int idx_rowid = c.getColumnIndex("rowid");
		int idx_text = c.getColumnIndex("text");
		int idx_datemodified = c.getColumnIndex("datemodified");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		while ( c.moveToNext() )
		{
			t_SLibMemo curMemo = new t_SLibMemo();
			curMemo.m_rowid = c.getLong(idx_rowid);
			curMemo.m_Text = c.getString(idx_text);
			curMemo.m_Date = c.getLong(idx_datemodified);
			curMemo.m_strDate = sdf.format( new Date(curMemo.m_Date) );
			ret.add( curMemo );
		}

		c.close();
		db.close();

		return ret;

	}
	
	
	
	public ArrayList<t_SLibBirthday> loadBirthdayFromDB()
	{
		ArrayList<t_SLibBirthday> ret = new ArrayList<t_SLibBirthday>();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(SLib.getInstance().m_SLibDBPath, null, SQLiteDatabase.OPEN_READONLY);
		Cursor c = db.rawQuery( "SELECT name, islunar, year, month, day, createtime FROM t_birthday WHERE isvalid = ?", new String[] { "1" } );
		
		if ( c.getCount() <= 0 )
		{
			c.close();
			db.close();
			return ret;
		}
		
		int idx_name = c.getColumnIndex("name");
		int idx_islunar = c.getColumnIndex("islunar");
		int idx_year = c.getColumnIndex("year");
		int idx_month = c.getColumnIndex("month");
		int idx_day = c.getColumnIndex("day");
		int idx_createtime = c.getColumnIndex("createtime");
		
		while ( c.moveToNext() )
		{
			t_SLibBirthday curBirth = new t_SLibBirthday();
			curBirth.m_Name = c.getString(idx_name);
			curBirth.m_IsLunar = (int)c.getLong(idx_islunar);
			curBirth.m_Year = (int)c.getLong(idx_year);
			curBirth.m_Month = (int)c.getLong(idx_month);
			curBirth.m_Day = (int)c.getLong(idx_day);
			curBirth.m_CreateTime = c.getLong(idx_createtime);
			curBirth.m_BirSolar = new Solar();
			curBirth.m_BirLunar = new Lunar();
			curBirth.m_NextSolar = new Solar();
			curBirth.m_NextLunar = new Lunar();
			ret.add( curBirth );
		}


		Calendar nowCal = Calendar.getInstance();
		Solar nowSolar = new Solar();
		Lunar nowLunar = new Lunar();
		nowSolar.solarYear = nowCal.get(Calendar.YEAR);
		nowSolar.solarMonth = nowCal.get(Calendar.MONTH) + 1;
		nowSolar.solarDay = nowCal.get(Calendar.DAY_OF_MONTH);
		nowLunar = LunarSolarConverter.SolarToLunar(nowSolar);

		for ( t_SLibBirthday cur : ret )
		{

			if ( cur.m_IsLunar == 0 )
			{
				// init birthday solar
				cur.m_BirSolar.solarYear = cur.m_Year;
				cur.m_BirSolar.solarMonth = cur.m_Month;
				cur.m_BirSolar.solarDay = cur.m_Day;

				// init birthday lunar
				if ( cur.m_Year != 0 )
				{
					cur.m_BirLunar = LunarSolarConverter.SolarToLunar(cur.m_BirSolar);
				}
				else
				{
					cur.m_BirLunar.lunarYear = 0;
					cur.m_BirLunar.lunarMonth = 0;
					cur.m_BirLunar.lunarDay = 0;
				}

				// init next solar
				cur.m_NextSolar.solarYear = nowSolar.solarYear;
				cur.m_NextSolar.solarMonth = cur.m_Month;
				cur.m_NextSolar.solarDay = cur.m_Day;
				if (
						(cur.m_BirSolar.solarMonth < nowSolar.solarMonth) ||
						( (cur.m_BirSolar.solarMonth == nowSolar.solarMonth) && cur.m_BirSolar.solarDay < nowSolar.solarDay )
					)
				{
					cur.m_NextSolar.solarYear = nowSolar.solarYear + 1;
				}

				// init next lunar
				cur.m_NextLunar = LunarSolarConverter.SolarToLunar(cur.m_NextSolar);
			}
			else
			{
				// init birthday lunar
				cur.m_BirLunar.lunarYear = cur.m_Year;
				cur.m_BirLunar.lunarMonth = cur.m_Month;
				cur.m_BirLunar.lunarDay = cur.m_Day;

				// init birthday solar
				if ( cur.m_Year != 0 )
				{
					cur.m_BirSolar = LunarSolarConverter.LunarToSolar(cur.m_BirLunar);
				}
				else
				{
					cur.m_BirSolar.solarYear = 0;
					cur.m_BirSolar.solarMonth = 0;
					cur.m_BirSolar.solarDay = 0;
				}

				// init next lunar
				cur.m_NextLunar.lunarYear = nowLunar.lunarYear;
				cur.m_NextLunar.lunarMonth = cur.m_Month;
				cur.m_NextLunar.lunarDay = cur.m_Day;
				if (
						(cur.m_BirLunar.lunarMonth < nowLunar.lunarMonth) ||
						( (cur.m_BirLunar.lunarMonth == nowLunar.lunarMonth) && cur.m_BirLunar.lunarDay < nowLunar.lunarDay )
					)
				{
					cur.m_NextLunar.lunarYear = nowLunar.lunarYear + 1;
				}

				// init next solar
				cur.m_NextSolar = LunarSolarConverter.LunarToSolar(cur.m_NextLunar);
			}

		}

		// calc dayleft / age
		for ( t_SLibBirthday cur : ret )
		{
			Calendar calCur = Calendar.getInstance();
			Calendar calBirth = Calendar.getInstance();
			calBirth.set( cur.m_NextSolar.solarYear, cur.m_NextSolar.solarMonth-1, cur.m_NextSolar.solarDay );

			long diffMillis = calBirth.getTimeInMillis()-calCur.getTimeInMillis();
			cur.m_DayLeft = new Long(diffMillis/(1000*60*60*24)).intValue();

			if ( cur.m_Year == 0 ) {
				cur.m_Age = 0;
			}
			else
			{
				cur.m_Age = cur.m_NextSolar.solarYear - cur.m_BirSolar.solarYear;
			}
		}

		// sort by dayleft
		SlibBirthdayComparator cmpBirthday = new SlibBirthdayComparator();
		Collections.sort(ret, cmpBirthday);

		c.close();
		db.close();

		return ret;

	}
	
	
	
	// transform tel to addr
	private String getAddrNameByTel( String tel, tRetAddrData addrData )
	{
		if ( tel.length() <= 3 ) { return tel; }

		String strFix = tel;
		strFix = strFix.replace("+86","");
		strFix = strFix.replace(" ","");
		strFix = strFix.replace("-","");

		// find addr
		for ( tAddr cur : addrData.m_AddrList )
		{
			if ( strFix.equals(cur.m_tel) )
			{
				return cur.m_name;
			}
		}
		
		return tel;
	}
	
	
	
	
}


class SlibAddrSMSsComparator implements Comparator<t_SlibAddrSMSs>
{
    @Override
    public int compare(t_SlibAddrSMSs a, t_SlibAddrSMSs b)
    {
    	if ( a.m_LastDate > b.m_LastDate )
    	{
    		return -1;
    	}
    	else
    	{
    		return 1;
    	}
    }
}

class SlibSMSComparator implements Comparator<t_SlibSMS>
{
    @Override
    public int compare(t_SlibSMS a, t_SlibSMS b)
    {
    	if ( a.m_Date > b.m_Date )
    	{
    		return -1;
    	}
    	else if ( a.m_Date < b.m_Date )
    	{
    		return 1;
    	}
    	else
		{
			return 0;
		}
    }
}

class SlibBirthdayComparator implements Comparator<t_SLibBirthday>
{
	@Override
	public int compare(t_SLibBirthday a, t_SLibBirthday b)
	{
		if ( a.m_DayLeft < b.m_DayLeft )
		{
			return -1;
		}
		else
		{
			return 1;
		}
	}
}



class tAddr
{
	String m_name;
	String m_tel;
}
class tRetAddrData
{
	ArrayList<String> m_ClassList;
	ArrayList<tAddr> m_AddrList;
}


class tSLibCallhis
{
	String m_Tel;
	String m_Name;
	Date m_Date;
	int m_Sec;
	String m_strSec;
	int m_State;	// 0:in-ok 1:in-err 2:out-ok 3:out-err  

}
class tSLibRetCallhisData
{
	ArrayList<tSLibCallhis> m_List;
}


class t_SlibSMS
{
	long m_Date;
	String m_strDate;
	int m_Type;	// 1:in 2:out 3:draft ...
	String m_Text;
}
class t_SlibAddrSMSs
{
	String m_Name;
	String m_Tel;
	long m_LastDate;
	String m_strLastDate;
	String m_LastText;
	ArrayList<t_SlibSMS> m_SMSList;
}
class t_SlibRetSMS
{
	ArrayList<t_SlibAddrSMSs> m_AddrSMSsList;
}


class t_SLibMemo
{
	long	m_rowid;
	String	m_Text;
	long	m_Date;
	String	m_strDate;
}


class t_SLibBirthday
{
	String	m_Name;
	int		m_IsLunar;
	int		m_Year;
	int		m_Month;
	int		m_Day;
	long	m_CreateTime;
	Solar	m_BirSolar;
	Lunar	m_BirLunar;
	Solar	m_NextSolar;
	Lunar	m_NextLunar;
	int		m_DayLeft;
	int		m_Age;
}
