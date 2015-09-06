package com.ucpaas.chat.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ucpaas.chat.R;
import com.ucpaas.chat.bean.UserInfo;

/**
 * 讨论组增加
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class DiscussionAddAdapter extends BaseAdapter {

	private Context context;
	private List<UserInfo> list;

	public DiscussionAddAdapter(Context context, List<UserInfo> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public void setList(List<UserInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public UserInfo getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		UserInfo userInfo = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listitem_discussion_add, null);
			holder = new ViewHolder();
			holder.ivGroup = (ImageView) convertView.findViewById(R.id.iv_group);
			holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
			holder.tbGroupAdd = (ToggleButton) convertView.findViewById(R.id.tb_group_add);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvGroupName.setText(userInfo.getNickname());
		holder.tbGroupAdd.setTag(userInfo);
		holder.tbGroupAdd.setChecked(userInfo.isChecked());
		holder.tbGroupAdd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				UserInfo userInfo = (UserInfo) buttonView.getTag();
				userInfo.setChecked(isChecked);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView ivGroup;
		TextView tvGroupName;
		ToggleButton tbGroupAdd;
	}

}
