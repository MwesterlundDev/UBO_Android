package com.markwesterlund.ubo.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.markwesterlund.ubo.R;
import com.markwesterlund.ubo.utils.ParseConstants;
import com.parse.ParseObject;

public class MessageAdapter extends ArrayAdapter<ParseObject> {
	
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages){
		super(context, R.layout.message_item, messages);
		
		mContext = context;
		mMessages = messages;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			//holder.senderImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.senderLabel);
			holder.textLabel = (TextView)convertView.findViewById(R.id.messageText);
			holder.timeLabel = (TextView)convertView.findViewById(R.id.timeLabel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		Date createdAt = message.getCreatedAt();
		long now = new Date().getTime();
		String convertedDate = DateUtils.getRelativeTimeSpanString(
				createdAt.getTime(),
				now,
				DateUtils.SECOND_IN_MILLIS).toString();
				
		holder.timeLabel.setText(convertedDate);
		
		
			//holder.senderImageView.setImageResource(R.drawable.ic_picture);
		
			
		
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		holder.textLabel.setText(message.getString(ParseConstants.MESSAGE_TO_ALL_TEXT));
		
		
		return convertView;
	}
	
	private static class ViewHolder{
		ImageView senderImageView;
		TextView nameLabel;
		TextView textLabel;
		TextView timeLabel;
	}
	
	
	public void refill(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();
	}
}
