package com.markwesterlund.ubo.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class InboxFragment extends android.app.ListFragment {


	protected List<ParseObject> mMessages; 
	protected SwipeRefreshLayout mSwipeRefreshLayout;
	protected EditText mMessageText;
	protected Button mSendButton;
	protected TextView mCharCount;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(
				R.color.swipeRefresh1, 
				R.color.swipeRefresh2,
				R.color.swipeRefresh3, 
				R.color.swipeRefresh4);
		
		mCharCount = (TextView)rootView.findViewById(R.id.charCount);
		mMessageText = (EditText)rootView.findViewById(R.id.messageText);
		mMessageText.addTextChangedListener(mTextWatcher);
		mMessageText.setSingleLine(false);
		
		mSendButton = (Button)rootView.findViewById(R.id.sendButton);
		mSendButton.setOnClickListener(mSendOnClickListener);
		
		return rootView;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().setProgressBarIndeterminateVisibility(true);
		
		retrieveMessages();
		
	}


	private void retrieveMessages() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES_TO_ALL);
		
		//query.whereEqualTo(ParseConstants.KEY_RECICPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				
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
					if( getListView().getAdapter() == null ) {
						MessageAdapter adapter = new MessageAdapter(
								getListView().getContext(),
								mMessages);
						setListAdapter(adapter);
					} else {
						// refill the adapter!
						
						((MessageAdapter)getListView().getAdapter()).refill(mMessages);
					}
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
	
	protected OnClickListener mSendOnClickListener = new OnClickListener () {

		@Override
		public void onClick(View v) {
			
			
			//Toast.makeText(getActivity(), "Test Message Button", Toast.LENGTH_LONG).show();
			
			getActivity().setProgressBarIndeterminateVisibility(true);
			ParseObject message = createMessage();
			if(message == null){
				// error
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setMessage(R.string.error_selecting_file)
					.setTitle(R.string.error_selecting_file_title)
					.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog = builder.create();
				dialog.show();
			} else {
				send(message);
				//retrieveMessages();
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
		ParseObject messageToAll = new ParseObject(ParseConstants.CLASS_MESSAGES_TO_ALL);
		messageToAll.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId());
		messageToAll.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername());
		
		String messageText = mMessageText.getText().toString();
		
		
		
		if(messageText.length() == 0){
			return null;
		}
		else {
			if(messageText.length() > 140){
				
				messageText = messageText.substring(0, 139);
			}
			
			messageToAll.put(ParseConstants.MESSAGE_TO_ALL_TEXT, messageText);
			return messageToAll;
		}
		
		
	}
	
	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					// success!!
					Toast.makeText(getActivity(), R.string.success_message, Toast.LENGTH_LONG).show();
					mMessageText.setText("");
					mCharCount.setText("140");
					InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					//sendPushNotifications();
					
				} else {
					// error
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					
					builder.setMessage(R.string.error_sending_message)
						.setTitle(R.string.error_selecting_file_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				
			}
		});
	}
	
	
}
