package com.markwesterlund.ubo.utils;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.markwesterlund.ubo.ui.MainActivity;
import com.markwesterlund.ubo.ui.MessageActivity;
import com.parse.ParsePushBroadcastReceiver;

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

	@Override
    public void onPushOpen(Context context, Intent intent) {
        Log.d("Push", "Clicked");
        
        JSONObject data = null;
        
        try {
			data = new JSONObject(intent.getExtras().getString("com.parse.Data"));
			Iterator itr = data.keys();
		      while (itr.hasNext()) {
		        String key = (String) itr.next();
		        Log.d("push", "..." + key + " => " + data.getString(key));
		      }
		      
		      if(data.getString("type").equals("Direct")){
		    	  Intent i = new Intent(context, MessageActivity.class);
		    	  i.putExtra(ParseConstants.KEY_USERNAMES, data.getString("user"));
		    	  i.putExtra(ParseConstants.KEY_USER_ID, data.getString("userId"));
		          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		          context.startActivity(i);
		        }
		      else {
		    	  Intent i = new Intent(context, MainActivity.class);
		          i.putExtras(intent.getExtras());
		          i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		          context.startActivity(i);
		    	  
		      }
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
        
        
        
        
        
        
    }
	
}
