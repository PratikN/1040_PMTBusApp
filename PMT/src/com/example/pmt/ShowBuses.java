package com.example.pmt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowBuses extends Activity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_buses);
		Bundle b = getIntent().getExtras();
		String[] arrExtras = b.getStringArray("Data");
		String Source = arrExtras[0];
		String Dest = arrExtras[1];
		TextView ViewSource = (TextView)findViewById(R.id.labelSource);
		ViewSource.setText(Source);
	  	TextView ViewDest = (TextView)findViewById(R.id.labelDest);
	  	ViewDest.setText(Dest);
	  	SQLiteDatabase db;
	  	db = openOrCreateDatabase("PMT.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
	  	String fetch_query = "select route_id, source, destination, schedules from routes ,route_timings where(intermediate_stations like '%"+Source+"%"+Dest+"%' and route_timings.routeid = routes.route_id)";
	  	//db.execSQL("select station_name from Stations where (lower)station_name='magarpatta'");
	  	//Cursor c1=db.rawQuery("select * from stations where station_name='Magarpatta'",null);
	  	Cursor c = db.rawQuery(fetch_query, null);
	  	
	  	if(c == null)
	  		Toast.makeText(getApplicationContext(), "Coming Data1" + c, 1000).show();
	  	c.moveToFirst();
	  	int iCount = 0;
	  	while(!c.isAfterLast())
	  	{
	  		TableLayout tl = (TableLayout) findViewById(R.id.layout);
	  		int route_number1 = c.getColumnIndex("route_id");
	  		int route_number = c.getInt(route_number1);
	  		String src = c.getString(1);
	  		String dest = c.getString(2);
	  		String timings = c.getString(3);
	  		
	  		String name_string = src + " to " + dest;
	  		
	  		//table row
	  		TableRow tr_head = new TableRow(this);
	  		tr_head.setId(10 + iCount);
	  		tr_head.setBackgroundColor(Color.GRAY);
	  		tr_head.setLayoutParams(new LayoutParams(
	  		LayoutParams.MATCH_PARENT,
	  		LayoutParams.WRAP_CONTENT));
	  		
	  		//contents
	  		TextView entry1 = new TextView(this);
	         entry1.setId(100 + iCount);
	         entry1.setText(route_number+"");
	         entry1.setTextColor(Color.WHITE);
	         //entry1.setPadding(5, 5, 5, 5);
	         entry1.setWidth(BIND_ADJUST_WITH_ACTIVITY);
	         tr_head.addView(entry1);
	  		
	         TextView entry2 = new TextView(this);
	         entry2.setId(200 + iCount);
	         entry2.setWidth(BIND_ADJUST_WITH_ACTIVITY);
	         entry2.setText(name_string+"");
	         entry2.setTextColor(Color.WHITE);
	         //entry2.setPadding(5, 5, 5, 5);
	         tr_head.addView(entry2);

	         String[] arrTimings = timings.split("-");
	         for(String strTiming : arrTimings)
	         {
	        	 if(compareDates(strTiming))
	        	 {
	        		 timings = strTiming;
	        		 break;
	        	 }
	         }
	         
	         
	         TextView entry3 = new TextView(this);
	         entry3.setId(300 + iCount);
	         entry3.setWidth(BIND_ADJUST_WITH_ACTIVITY);
	         entry3.setText(timings+"");
	         entry3.setTextColor(Color.WHITE);
	         //entry3.setPadding(5, 5, 5, 5);
	         tr_head.addView(entry3);
	         
	         //tl.addView(tr_head, 200, 200);
	         
	         //add row to table
	         tl.addView(tr_head, new TableLayout.LayoutParams(
	                 LayoutParams.MATCH_PARENT,
	                 LayoutParams.WRAP_CONTENT));
	         iCount++;
	  		c.moveToNext();
	  	}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_show_buses, menu);
		return true;
	}
	
	public static final String inputFormat = "HH:mm";

	private Date date;
	
	SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.UK);
	
	private Boolean compareDates(String compareStringOne){
	    Calendar now = Calendar.getInstance();

	    int hour = now.get(Calendar.HOUR);
	    int minute = now.get(Calendar.MINUTE);

	    date = parseDate(hour + ":" + minute);
	   // Toast.makeText(getApplicationContext(), hour + ":" + minute, 10000).show();
	    Date dateCompareOne = parseDate(compareStringOne);

	    if ( dateCompareOne.after( date )) {
	        return true;
	    }
	    return false;
	}
	
	private Date parseDate(String date) {

	    try {
	        return inputParser.parse(date);
	    } catch (java.text.ParseException e) {
	        return new Date(0);
	    }
	}


}
