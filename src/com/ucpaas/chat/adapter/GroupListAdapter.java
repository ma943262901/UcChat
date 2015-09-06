package com.ucpaas.chat.adapter;

import java.util.List;

import com.ucpaas.chat.R;
import com.ucpaas.chat.bean.GroupInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 群组列表
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class GroupListAdapter extends BaseAdapter {

	private Context context;
	private List<GroupInfo> list;

	public GroupListAdapter(Context context, List<GroupInfo> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public void setList(List<GroupInfo> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return list.size();
	}

	@Override
	public GroupInfo getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupInfo groupInfo = getItem(position);
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

		holder.ivGroup.setImageResource(R.drawable.person_group);
		holder.tvGroupName.setText(groupInfo.getGroupName());

		return convertView;
	}

	static class ViewHolder {
		ImageView ivGroup;
		TextView tvGroupName;
	}

}
