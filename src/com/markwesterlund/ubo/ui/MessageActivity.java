package com.markwesterlund.ubo.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.markwesterlund.ubo.R;
import com.markwesterlund.ubo.adapters.MessageAdapter;
import com.markwesterlund.ubo.utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageActivity extends Activity {
	
	public static final String TAG = MessageActivity.class.getSimpleName();
	
	protected MenuItem mSendMenuItem;
	
	protected List<ParseObject> mMessages; 
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected EditText mMessageText;
	protected Button mSendButton;
	protected TextView mCharCount;
	protected ListView mListView;
	protected String toUser;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		
		//mListView.setEmptyView(findViewById(android.R.id.empty));
		mListView = (ListView) findViewById(R.id.messageList);
		
		Intent intent = getIntent();
		
		toUser = intent.getStringExtra(ParseConstants.KEY_USERNAMES);
		
		mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swipeRefresh1, 
				R.color.swipeRefresh2,
				R.color.swipeRefresh3, 
				R.color.swipeRefresh4);
		
		
		mCharCount = (TextView)findViewById(R.id.charCount);
		mMessageText = (EditText)findViewById(R.id.messageText);
		mMessageText.addTextChangedListener(mTextWatcher);
		mMessageText.setSingleLine(false);
		
		mSendButton = (Button)findViewById(R.id.sendButton);
		mSendButton.setOnClickListener(mSendOnClickListener);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setProgressBarIndeterminateVisibility(true);
		
		retrieveMessages();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		
		mSendMenuItem = menu.getItem(0);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		
		return super.onOptionsItemSelected(item);
		
		
	}
	
	protected OnClickListener mSendOnClickListener = new OnClickListener () {

		@Override
		public void onClick(View v) {
			
			
			//Toast.makeText(getActivity(), "Test Message Button", Toast.LENGTH_LONG).show();
			
			setProgressBarIndeterminateVisibility(true);
			ParseObject message = createMessage();
			if(message == null){
				// error
				AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
				
				builder.setMessage(R.string.error_selecting_file)
					.setTitle(R.string.error_selecting_file_title)
					.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				send(message);
				//finish();
			}
			
			
		}
		
	};
	
	protected TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
			
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			
			if(s.length() == 0){
				return;
			} else {
				String text = s.toString();
				int textCount = (int)text.length();
				int textLimit = 140 - textCount;
				mCharCount.setText(" " + textLimit);
				
			}
			
		}
		
	};
	
	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		message.put(ParseConstants.KEY_RECICPIENT_NAME, toUser );
		
		
		String messageText = mMessageText.getText().toString();
		
		if(messageText.length() == 0){
			return null;
		}
		else {
			if(messageText.length() > 140){
				
				messageText = messageText.substring(0, 139);
			}
			
			message.put(ParseConstants.MESSAGE_TO_ALL_TEXT, messageText);
			return message;
		}
		
		
	}
	
	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// success!!
					Toast.makeText(MessageActivity.this, R.string.success_message, Toast.LENGTH_LONG).show();
					mMessageText.setText("");
					mCharCount.setText("140");
					//InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					//inputManager.hideSoftInputFromInputMethod((IBinder) getWindow(), InputMethodManager.HIDE_NOT_ALWAYS);
					//sendPushNotifications();
					
				} else {
					// error
					AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
					
					builder.setMessage(R.string.error_sending_message)
						.setTitle(R.string.error_selecting_file_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				
			}
		});
	}
	
	protected OnRefreshListener mOnRefreshListener = new OnRefreshListener() {
		
		@Override
		public void onRefresh() {
			retrieveMessages();
			
		}
	};
	
	private void retrieveMessages() {
		ParseQuery<ParseObject> queryMe = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		queryMe.whereEqualTo(ParseConstants.KEY_RECICPIENT_NAME, ParseUser.getCurrentUser().getUsername());
		queryMe.whereEqualTo(ParseConstants.KEY_SENDER_NAME, toUser);
		
		ParseQuery<ParseObject> queryYou = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		queryYou.whereEqualTo(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		queryYou.whereEqualTo(ParseConstants.KEY_RECICPIENT_NAME, toUser);
		
		
		List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
		queries.add(queryMe);
		queries.add(queryYou);
		ParseQuery<ParseObject> query = ParseQuery.or(queries);
		query.addAscendingOrder(ParseConstants.KEY_CREATED_AT);
		
		query.setLimit(100);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				
				if(mSwipeRefreshLayout.isRefreshing()){
					mSwipeRefreshLayout.setRefreshing(false);
				}
				
				if(e == null){
					//Success!!  We found messages!
					mMessages = messages;
					
					String[] usernames = new String[mMessages.size()];
					
					int i = 0;
					
					for(ParseObject message : mMessages){
						usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					
					}
					if( mListView.getAdapter() == null ) {
						MessageAdapter adapter = new MessageAdapter(
								mListView.getContext(),
								mMessages);
						mListView.setAdapter(adapter);
					} else {
						// refill the adapter!
						
						((MessageAdapter)mListView.getAdapter()).refill(mMessages);
					}
				}
				
			}
		});
	}
}
