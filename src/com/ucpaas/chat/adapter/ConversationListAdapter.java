package com.ucpaas.chat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.util.DateUtil;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;

/**
 * 会话列表
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class ConversationListAdapter extends BaseAdapter {

	private Context context;
	private List<ConversationInfo> list;

	private String mTitle = null;
	
	public ConversationListAdapter(Context context, List<ConversationInfo> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public void setActivityBackTitleName(String name){
		mTitle = name;
	}
	
	public void setList(List<ConversationInfo> list) {
		this.list = list;
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public ConversationInfo getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ConversationInfo conversationInfo = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.listitem_conversation, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.imv_conversation);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_conversation_title);
			holder.tvDesc = (TextView) convertView
					.findViewById(R.id.tv_conversation_desc);
			holder.tvLastTime = (TextView) convertView
					.findViewById(R.id.tv_last_time);

			holder.imgUpdateTips = (ImageView) convertView
					.findViewById(R.id.img_update);
			holder.txvUnreadMsgNum = (TextView) convertView
					.findViewById(R.id.txtv_unread_msg_num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (CategoryId.DISCUSSION == conversationInfo.getCategoryId()) {
			holder.image.setImageResource(R.drawable.person_discussion);
		} else if (CategoryId.GROUP == conversationInfo.getCategoryId()) {
			holder.image.setImageResource(R.drawable.person_group);
		} else if (CategoryId.PERSONAL == conversationInfo.getCategoryId()) {
			holder.image.setImageResource(R.drawable.person);
		} else {
			holder.image.setImageResource(R.drawable.person);
		}

		holder.tvTitle.setText(conversationInfo.getConversationTitle());
		holder.tvLastTime.setText(DateUtil.format(DateUtil.MMDD_HHMM,
				conversationInfo.getLastTime()));

		List<ChatMessage> currentMsgList = new ArrayList<ChatMessage>();
		try {
			currentMsgList = conversationInfo.getLastestMessages(0, 1);
			ChatMessage lastMessage = currentMsgList.get(0);
			String lastContent = lastMessage.getContent();
			if (MSGTYPE.MSG_DATA_TEXT == lastMessage.getMsgType()) {
				if (!TextUtils.isEmpty(lastContent)) {
					holder.tvDesc.setText(lastContent);
				}
			} else if (MSGTYPE.MSG_DATA_IMAGE == lastMessage.getMsgType()) {
				holder.tvDesc.setText("[图片]");
			} else if (MSGTYPE.MSG_DATA_VOICE == lastMessage.getMsgType()) {
				holder.tvDesc.setText("[语音]");
			} else {
				if (!TextUtils.isEmpty(lastContent)) {
					holder.tvDesc.setText(lastContent);
				}
			}
			int unReadNum = conversationInfo.getMsgUnRead();
			int Count = conversationInfo.getUnreadCount();
			if (mTitle != null && conversationInfo.getConversationTitle().equals(mTitle)) {
				holder.txvUnreadMsgNum.setText(0 + "");
				holder.imgUpdateTips.setVisibility(View.GONE);
				holder.txvUnreadMsgNum.setVisibility(View.GONE);
			}else {
				if (unReadNum <= 0) {
					holder.txvUnreadMsgNum.setText(0 + "");
					holder.imgUpdateTips.setVisibility(View.GONE);
					holder.txvUnreadMsgNum.setVisibility(View.GONE);
				}else if (unReadNum > 0 && unReadNum <= 99) {
					holder.imgUpdateTips.setVisibility(View.VISIBLE);
					holder.txvUnreadMsgNum.setVisibility(View.VISIBLE);
					holder.txvUnreadMsgNum.setText(unReadNum + "");

				} else if (unReadNum > 99) {
					holder.imgUpdateTips.setVisibility(View.VISIBLE);
					holder.txvUnreadMsgNum.setVisibility(View.VISIBLE);
					holder.txvUnreadMsgNum.setText(99+ "");
				}
			}


		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView image;
		TextView tvTitle, tvDesc, tvLastTime;
		ImageView imgUpdateTips;
		TextView txvUnreadMsgNum;
	}

}
