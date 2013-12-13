package com.example.pmt;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SQLiteDatabase db;
	  	db = openOrCreateDatabase("PMT.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
		Button CreateDatabase = (Button)findViewById(R.id.update_button);
		CreateDatabase.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("ShowToast")
			public void onClick(View view)
            {
            	
            	//SQLiteDatabase db;
        	  	//db = openOrCreateDatabase("PMT",SQLiteDatabase.CREATE_IF_NECESSARY,null);
        	  	//db.execSQL("drop table buses");
        	  	/*db.execSQL("create table if not exists stations(station_id integer(4) primary key, station_name Text)");
        	  	db.execSQL("create table if not exists routes(route_id integer(4) primary key, route_number integer(4)," +
        	  			" source Text, destination Text, intermediate_stations Text, time_offsets Text)");
        	  	db.execSQL("create table if not exists route_timings(route_timing_id Intger(4) primary key, routeid Integer(4), schedules Text, " +
        	  			"foreign key(routeid) references routes(route_id))");*/
        	  	
        	  	// code by abhijeet
            	
            	//start
            	Thread downloadThread = new Thread() {                     
            	    public void run() {                                    
            	    	
            	    	
            	   
            	        try {                                              
            	        		
            	        	String sql;
                    	  	SQLiteDatabase db;
                    	  	db = openOrCreateDatabase("PMT.db",SQLiteDatabase.CREATE_IF_NECESSARY,null);
                    	  	db.execSQL("drop table if exists stations");
                    	  	db.execSQL("drop table if exists routes");
                    	  	db.execSQL("drop table if exists route_timings");
                    	  	
                    	  	db.execSQL("create table if not exists stations(station_id Integer primary key , station_name Text unique)");
                    	  	db.execSQL("create table if not exists routes(route_id integer primary key , route_number Integer," +
                    	  	" source Text, destination Text, intermediate_stations Text, time_offsets Text)");
                    	  	db.execSQL("create table if not exists route_timings(route_timing_id Integer primary key , routeid Integer, schedules Text, " +
                    	  	"foreign key(routeid) references routes(route_id))");
                    	  	int route_counter = 0;
	                    	  	for(int i= 326; i<= 400;i++)
	                    	  	{
	                    	  		int k = 0;
	                    	  		int l = 0;
	                    	  		
	            	        	  	String route = "r" + i +".html";
	            	        	  	String []dest = new String[2];
	            	        	  	String []src = new String[2] ;
	            	        	  	try
	            	        	  	{
		            	        	  	Document doc = Jsoup.connect("http://punebusguide.org/sites/default/files/time_table/route/en/"+route).get();
		            	        	  //	Toast.makeText(getApplicationContext(), "Connection Successful!", 1000).show();
	            	        	  		Elements stat = (Elements)doc.select("div.enTripGroupInfo");
	            		        	  	for (Element link : stat)
	            		        	  	{
		            		        	  	String linkText = link.text();
		            		        	  	src[k] = (linkText.substring(12, linkText.indexOf("Days of Week")));
		            		        	  	src[k] = (src[k].substring(0,src[k].indexOf("- To -"))).toLowerCase();
		            		
		            		           		
		            		        	  	dest[k] = (linkText.substring(linkText.indexOf("- To -") + 7,linkText.indexOf("Days of Week"))).toLowerCase();
		            		        	  	System.out.println(src[k]+" "+dest[k]);
		            		        	  	k++;
		            		       	  	}
	            		        	  	k = 0;
	            		        	  	stat = (Elements)doc.select("table[border=1] td");
	            		        	  	String inter = "";
	            		        	  	String timestr = "";
	            		        	  	String intertime = "";
	            		        	  	int flag = 0; // 0-Its is in text 1-Its in time
	            		        	  	int j = 0;
	            		        	  	for (Element link : stat)
	            		        	  	{
	            		        	  		String linkText = link.text();
	            		        	  		if(linkText.matches(".*\\d.*"))
	            			        	  	{
	            		        	  			if(linkText.contains(":"))
	            		        	  			{
	            		        	  				flag = 1;
	            		        	  				if(j != 0)
	            		        	  				{
	            		        	  					db.execSQL("INSERT or replace INTO routes(route_number,source,destination,intermediate_stations,time_offsets) VALUES("+i+",'"+src[k]+"','"+dest[k]+"','"+inter+"', '"+timestr+"')");
	            			        	  	//System.out.println("INSERT or replace INTO routes(route_number,source,destination,intermediate_stations,time_offsets) VALUES("+i+",'"+src[k]+"','"+dest[k]+"','"+inter+"', '"+timestr+"')");
	            		        	  					k++;
				            			        	  	j = 0;
				            			        	  	l = 1;
				            			        	  	inter = "";
				            			        	  	timestr = "";
				            			        	}
				            			        	intertime = intertime + "-"+ linkText;
	            		        	  			}
		            			        	  	else
		            			        	  		flag = 0;
	            			        	  	}
	            		        	  		else
	            		        	  		{
		            			        	  	if(l == 1)
		            			        	  	{
		            			        	  		route_counter++;
			            			        	  	db.execSQL("INSERT or replace INTO route_timings(routeid,schedules) VALUES("+route_counter+",'"+intertime+"')");
			            			        	  	//System.out.println("route_timings"+intertime);
			            			        	  	intertime = "";
			            			        	  	flag = 10;
			            			        	  	l = 0;
			            			        	 }
		            			        	  	sql = "INSERT or replace INTO stations(station_name) VALUES('"+linkText.toLowerCase()+"')" ;
		            			        	  	db.execSQL(sql);
		            		        	  	//System.out.println(sql);
		            			        	  	inter = inter + "-" + linkText;
		            			        	  	if(j == 0)
		            			        	  		timestr = "0";
		            			        	  	else
		            			        	  		timestr = timestr + "- 10" ;
		            			        	  	j++;
            		
            		
	            		        	  		}
	            		        	  	}
	            		        	  	route_counter++;
	            		        	  	db.execSQL("INSERT or replace INTO route_timings(routeid,schedules) VALUES("+route_counter+",'"+intertime+"')");
	            		        	  	//System.out.println("route_timings"+intertime);
            		
	            	        	  	} 
	            	        	  	catch (Exception ex)
	            	        	  	{
	            	        	  		 Log.e(ex.getMessage() + i,"failed");
	            	        	  		//Toast.makeText(getApplicationContext(), e.getMessage(), 1000).show();
	            	        	  		ex.printStackTrace();
	            	        	  	}
            	
            	        	}                       
            	        }
            	        catch (Exception e)
            	        {                          
            	        	//Toast.makeText(getApplicationContext(), e.getMessage(), 1000).show();
            	        }                                                  
            	    }                                                      
            	};                                                         
            	downloadThread.start();
    
        	  	
        	  	
        	  	//Toast.makeText(getApplicationContext(), "Added successfully!", 1000).show();
            }
          
        });
		 
		/*AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autocomplete_Source);
		// Get the string array
		if(!findViewById(R.id.src_text).equals(""))
		{
			String fetch_query = "select station_name from stations where station_name like '%"+(EditText)findViewById(R.id.src_text)+"%'";
			Cursor c = db.rawQuery(fetch_query,null);
			c.moveToFirst();
			String[] arrSource = new String[c.getCount()];
			int iCount = 0;
			while(!c.isAfterLast())
		  	{
				arrSource[iCount] = c.getString(0);
				iCount++;
				c.moveToNext();
		  	}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrSource);
			textView.setAdapter(adapter);
		}*/
		Button  rtn = (Button)findViewById(R.id.find_button);
		rtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
            	Intent intent = new Intent(view.getContext(), ShowBuses.class);
            	String[] arrData = new String[2];
            	EditText EditTextSource = (EditText)findViewById(R.id.src_text);
            	String strSource = EditTextSource.getText().toString();
        		EditText EditTextDest = (EditText)findViewById(R.id.dest_text);
        		String strDest = EditTextDest.getText().toString();
        		if(strDest != null)
        			arrData[1] = strDest;
        		else{
        			Toast.makeText(getApplicationContext(), "Source string is empty. Please fill it.", 1000).show();
        			arrData[1] = null;
        		}
        		if(strSource != null)
        			arrData[0] = strSource;
        		else
        		{
        			Toast.makeText(getApplicationContext(), "Destination string is empty. Please fill it.", 1000).show();
        			arrData[0] = null;
        		}
        		if(arrData[0] != null && arrData[1] != null){
        			intent.putExtra("Data",arrData);
        			startActivity(intent);
        		}
   
        		
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
