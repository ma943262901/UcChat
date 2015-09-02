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
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.view.CircleImageView;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;

/**
 * 博主列表
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class ConversationListAdapter extends BaseAdapter {

	private Context context;
	private List<ConversationInfo> list;

	public ConversationListAdapter(Context context, List<ConversationInfo> list) {
		super();
		this.context = context;
		this.list = list;
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

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_conversation, null);
			holder = new ViewHolder();
			holder.image = (CircleImageView) convertView.findViewById(R.id.imv_conversation);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_conversation_title);
			holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_conversation_desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvTitle.setText(getItem(position).getTargetId());
		List<ChatMessage> currentMsgList = new ArrayList<ChatMessage>();
		try {
			currentMsgList = getItem(position).getLastestMessages(0, 1);
			String lastMessage = currentMsgList.get(0).getContent();
			if (!TextUtils.isEmpty(lastMessage)) {
				holder.tvDesc.setText(lastMessage);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}

	static class ViewHolder {
		CircleImageView image;
		TextView tvTitle, tvDesc;
	}

}
