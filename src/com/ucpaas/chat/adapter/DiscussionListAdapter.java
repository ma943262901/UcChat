package com.ucpaas.chat.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;

/**
 * 讨论组列表
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class DiscussionListAdapter extends BaseAdapter {

	private Context context;
	private List<ConversationInfo> list;

	public DiscussionListAdapter(Context context, List<ConversationInfo> list) {
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
		ConversationInfo conversationInfo = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_group, null);
			holder = new ViewHolder();
			holder.ivGroup = (ImageView) convertView.findViewById(R.id.iv_group);
			holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (CategoryId.DISCUSSION == conversationInfo.getCategoryId()) {
			holder.ivGroup.setImageResource(R.drawable.person_discussion);
		} else if (CategoryId.GROUP == conversationInfo.getCategoryId()) {
			holder.ivGroup.setImageResource(R.drawable.person_group);
		} else if (CategoryId.PERSONAL == conversationInfo.getCategoryId()) {
			holder.ivGroup.setImageResource(R.drawable.person);
		} else {
			holder.ivGroup.setImageResource(R.drawable.person);
		}
		
		holder.tvGroupName.setText(conversationInfo.getConversationTitle());
		
		return convertView;
	}

	static class ViewHolder {
		ImageView ivGroup;
		TextView tvGroupName;
	}

}
