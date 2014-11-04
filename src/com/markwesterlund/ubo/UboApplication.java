package com.markwesterlund.ubo;

import android.app.Application;
import android.util.Log;

import com.markwesterlund.ubo.ui.MainActivity;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

public class UboApplication extends Application {

	@Override
	public void onCreate() {
		  Parse.initialize(this, "9GbMUX6UCJ1g0fTvgN4eb6BM18OZEMfIUKmcRJNC", "bN1nG0NKxNJZkeV2HIAjvNyaMurYQhl3DqQOSNoo");
		  
		 /* ParseObject testObject = new ParseObject("TestObject");
		  testObject.put("foo", "bar");
		  testObject.saveInBackground();*/
		  
		  Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
		  
		  //PushService.setDefaultPushCallback(this, MainActivity.class);
			
		  ParsePush.subscribeInBackground("", new SaveCallback() {
			  
		

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				 if (e != null) {
				      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
				    } else {
				      Log.e("com.parse.push", "failed to subscribe for push: ", e);
				    }
			}
			});
			
		}
		
	
}
