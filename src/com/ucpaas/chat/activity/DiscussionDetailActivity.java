/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.DiscussionGridAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.listener.ConfirmListener;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.ucpaas.chat.view.EditDialog;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;

/**
 * 讨论组详情
 * 
 * @author Frank
 * @date 2015年9月6日下午3:49:33
 */

public class DiscussionDetailActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private GridView mGridView;
	private DiscussionGridAdapter mAdpater;
	private ConversationInfo mConversationInfo;
	private IMManager mIMManager;
	private DiscussionInfo mDiscussionInfo;
	private List<UserInfo> mUserInfoList;
	private TextView mTvImName;

	private final static int REQUEST_CODE_ADD = 1;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion_detail);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mConversationInfo = (ConversationInfo) getIntent().getSerializableExtra("conversation");
		mIMManager = IMManager.getInstance(this);
		initUserInfo();
	}

	private void initUserInfo() {
		if (mConversationInfo != null) {
			mDiscussionInfo = mIMManager.getDiscussionInfo(mConversationInfo.getTargetId());
			if (mDiscussionInfo != null) {
				String members = mDiscussionInfo.getDiscussionMembers();
				mUserInfoList = getUserInfoList(members);
				LogUtil.log("members:" + members);
			} else {
				mUserInfoList = new ArrayList<UserInfo>();
			}
		} else {
			mUserInfoList = new ArrayList<UserInfo>();
		}
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		// 退出
		Button btnImDetailExit = (Button) findViewById(R.id.btn_im_detail_exit);
		btnImDetailExit.setOnClickListener(this);

		// 标题
		LinearLayout llImName = (LinearLayout) findViewById(R.id.ll_im_name);
		llImName.setOnClickListener(this);
		mTvImName = (TextView) findViewById(R.id.tv_im_name);
		if (mDiscussionInfo != null) {
			mTvImName.setText(mDiscussionInfo.getDiscussionName());
		}

		// 成员
		mGridView = (GridView) findViewById(R.id.gv_im_member);
		mAdpater = new DiscussionGridAdapter(this, mUserInfoList);
		mGridView.setAdapter(mAdpater);
		mGridView.setOnItemClickListener(this);
		mGridView.setOnItemLongClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("讨论组详情");
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_im_name:
			midifyImName();
			break;

		case R.id.btn_im_detail_exit:
			exitDiscussion();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if (position == parent.getAdapter().getCount() - 1) {
			// 增加讨论组成员
			addUserInfo();
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if (position != parent.getAdapter().getCount() - 1) {
			// 删除讨论组成员
			UserInfo UserInfo = (UserInfo) parent.getAdapter().getItem(position);
			deleteUserInfo(UserInfo);
			return true;
		}
		return false;
	}

	/**
	 * 删除讨论组成员
	 * 
	 * @param UserInfo
	 */
	private void deleteUserInfo(UserInfo UserInfo) {
		// TODO Auto-generated method stub
		List<String> memberList = new ArrayList<String>();
		memberList.add(UserInfo.getPhone());
		mIMManager.delDiscussionGroupMember(mDiscussionInfo.getDiscussionId(), memberList);
		mUserInfoList.remove(UserInfo);
		sync();
		ToastUtil.show(this, "删除成功");
	}

	/**
	 * 增加讨论组成员
	 */
	private void addUserInfo() {
		StringBuilder sb = new StringBuilder();
		if (mUserInfoList != null && mUserInfoList.size() != 0) {
			for (UserInfo UserInfo : mUserInfoList) {
				sb.append(UserInfo.getPhone() + ",");
			}

			Intent intent = new Intent(DiscussionDetailActivity.this, DiscussionAddActivity.class);
			intent.putExtra("discussionId", mDiscussionInfo.getDiscussionId());
			intent.putExtra("title", "邀请好友");
			intent.putExtra("members", sb.toString());
			startActivityForResult(intent, REQUEST_CODE_ADD);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_ADD:

			// 更新群成员
			if (Activity.RESULT_OK == resultCode) {
				String members = data.getStringExtra("data");
				if (!TextUtils.isEmpty(members)) {
					String[] memberArray = members.split(",");
					if (memberArray != null) {
						for (int i = 0; i < memberArray.length; i++) {
							String userId = memberArray[i];
							if (!TextUtils.isEmpty(userId)) {
								UserInfo UserInfo = new UserInfo();
								UserInfo.setPhone(userId);
								mUserInfoList.add(UserInfo);
							}
						}
					}
				}
				sync();
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 修改讨论组名字
	 */
	private void midifyImName() {
		// TODO Auto-generated method stub
		String oldName = mTvImName.getText().toString();
		EditDialog editDialog = new EditDialog(this, "修改讨论组名字", oldName);
		editDialog.show();
		editDialog.setConfirmListener(new ConfirmListener() {

			@Override
			public void confirm(String result) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(result)) {
					mIMManager.modifyDiscussionTitle(mDiscussionInfo.getDiscussionId(), result);
					mTvImName.setText(result);
					Intent data = new Intent();
					data.putExtra("title", result);
					setResult(1, data);
					ToastUtil.show(DiscussionDetailActivity.this, "修改成功");
				} else {
					ToastUtil.show(DiscussionDetailActivity.this, "名字不能为空");
				}
			}
		});
	}

	/**
	 * 获取讨论组成员
	 * 
	 * @param members
	 * @return
	 */
	public List<UserInfo> getUserInfoList(String members) {
		List<UserInfo> UserInfoList = new ArrayList<UserInfo>();

		if (!TextUtils.isEmpty(members)) {
			String[] memberList = members.split(",");
			if (memberList != null) {
				for (String member : memberList) {
					UserInfo UserInfo = new UserInfo();
					UserInfo.setPhone(member);
					UserInfoList.add(UserInfo);
				}
			}
		}
		return UserInfoList;
	}

	/**
	 * 退出讨论组
	 */
	private void exitDiscussion() {
		// TODO Auto-generated method stub
		mIMManager.quitDiscussionGroup(mDiscussionInfo.getDiscussionId());
		ToastUtil.show(this, "退出成功");
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 同上数据
	 */
	private void sync() {
		mAdpater.notifyDataSetChanged();
	}
}
