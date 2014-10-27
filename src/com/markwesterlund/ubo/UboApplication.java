package com.markwesterlund.ubo;

import android.app.Application;

import com.markwesterlund.ubo.ui.MainActivity;
import com.markwesterlund.ubo.utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

public class UboApplication extends Application {

	@Override
	public void onCreate() {
		  Parse.initialize(this, "9GbMUX6UCJ1g0fTvgN4eb6BM18OZEMfIUKmcRJNC", "bN1nG0NKxNJZkeV2HIAjvNyaMurYQhl3DqQOSNoo");
		  
		 /* ParseObject testObject = new ParseObject("TestObject");
		  testObject.put("foo", "bar");
		  testObject.saveInBackground();*/
			
			PushService.setDefaultPushCallback(this, MainActivity.class);
			ParseInstallation.getCurrentInstallation().saveInBackground();
		}
		
		public static void updateParseInstallation(ParseUser user){
			ParseInstallation installation = ParseInstallation.getCurrentInstallation();
			installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
			installation.saveInBackground();
		}
}
