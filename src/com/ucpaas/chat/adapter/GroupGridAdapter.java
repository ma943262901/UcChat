package com.ucpaas.chat.adapter;

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
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.support.SpOperation;

/**
 * 讨论组成员-适配器
 * 
 * @author tangqi
 * @data 2015年8月9日下午2:01:25
 */

public class GroupGridAdapter extends BaseAdapter {

	private Context context;
	private List<UserInfo> list;
	private String mUserId;

	public GroupGridAdapter(Context context, List<UserInfo> list) {
		super();
		this.context = context;
		this.list = list;
		this.mUserId = SpOperation.getUserId(context);
	}

	public void setList(List<UserInfo> list) {
		this.list = list;
	}

	public int getCount() {
		return list.size() + 2;
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.griditem_im_detail, null);
			holder = new ViewHolder();
			holder.ivMember = (ImageView) convertView.findViewById(R.id.iv_member);
			holder.tvMemberName = (TextView) convertView.findViewById(R.id.tv_member_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == getCount() - 2) {
			holder.tvMemberName.setText("");
			holder.ivMember.setImageResource(R.drawable.member_add);
		} else if (position == getCount() - 1) {
			holder.tvMemberName.setText("");
			holder.ivMember.setImageResource(R.drawable.member_delete);
		} else {
			UserInfo userInfo = getItem(position);
			String userId = userInfo.getPhone();
			if (userId.equals(mUserId)) {
				holder.tvMemberName.setText("我");
			} else {
				String nickName = userInfo.getNickname();
				if (!TextUtils.isEmpty(nickName)) {
					holder.tvMemberName.setText(nickName);
				} else {
					holder.tvMemberName.setText(userInfo.getPhone());
				}
			}

		}

		return convertView;
	}

	static class ViewHolder {
		ImageView ivMember;
		TextView tvMemberName;
	}

}
