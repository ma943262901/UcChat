/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.DiscussionGridAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.GroupInfo;
import com.ucpaas.chat.bean.ResultInfo;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.ResultCode;
import com.ucpaas.chat.support.RequestFactory;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.JSONUtils;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.OkHttpClientManager.ResultCallback;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;

/**
 * 群组详情
 * 
 * @author Frank
 * @date 2015年9月6日下午3:49:33
 */

public class GroupDetailActivity extends BaseActivity implements OnItemClickListener {

	private TextView mTvImName;
	private GridView mGridView;
	private DiscussionGridAdapter mAdapter;

	private ConversationInfo mConversationInfo;
	private IMManager mIMManager;
	private List<GroupInfo> mGroupInfoList;
	private List<UserInfo> mUserInfoList;
	private GroupInfo mGroupInfo;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_group_detail);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mConversationInfo = (ConversationInfo) getIntent().getSerializableExtra("conversation");
		mIMManager = IMManager.getInstance(this);
		mUserInfoList = new ArrayList<UserInfo>();
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
		mTvImName.setText(mConversationInfo.getConversationTitle());

		// 成员
		mGridView = (GridView) findViewById(R.id.gv_im_member);
		mAdapter = new DiscussionGridAdapter(this, mUserInfoList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("群组详情");
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sync();
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_im_name:
			break;

		case R.id.btn_im_detail_exit:
			exitGroup();
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

	/**
	 * 增加讨论组成员
	 */
	private void addUserInfo() {
		StringBuilder sb = new StringBuilder();
		if (mGroupInfo != null && mUserInfoList != null && mUserInfoList.size() != 0) {
			for (UserInfo UserInfo : mUserInfoList) {
				sb.append(UserInfo.getPhone() + ",");
			}

			Intent intent = new Intent(GroupDetailActivity.this, DiscussionAddActivity.class);
			intent.putExtra("groupId", mGroupInfo.getGroupID());
			intent.putExtra("title", "邀请好友");
			intent.putExtra("members", sb.toString());
			startActivity(intent);
		}
	}

	/**
	 * 同步数据
	 */
	private void sync(){
		getData();
	}
	
	/**
	 * 获取群组数据
	 */
	private void getData() {
		String userName = SpOperation.getUserId(this);
		String url = RequestFactory.getInstance().getQueryGroup(userName);
		OkHttpClientManager.getAsyn(url, new ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(GroupDetailActivity.this, "查询失败");
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				// ToastUtil.show(GroupListActivity.this, "查询成功");
				mGroupInfoList = JSONUtils.parseGroupInfo(response);
				if (mGroupInfoList != null) {
					for (GroupInfo groupInfo : mGroupInfoList) {
						if (mConversationInfo.getTargetId().equals(groupInfo.getGroupID())) {
							mGroupInfo = groupInfo;
							mUserInfoList = groupInfo.getUserLists();
							if (mUserInfoList == null) {
								mUserInfoList = new ArrayList<UserInfo>();
							}
							sync(null);
							break;
						}
					}
				}
			}
		});
	}

	/**
	 * 更新数据
	 * 
	 * @param msg
	 */
	private void sync(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(msg)) {
					ToastUtil.show(GroupDetailActivity.this, msg);
				}
				mAdapter.setList(mUserInfoList);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 退出群组
	 */
	private void exitGroup() {
		// TODO Auto-generated method stub
		if (mGroupInfo != null) {
			String userName = SpOperation.getUserId(this);
			String groupId = mGroupInfo.getGroupID();
			String url = RequestFactory.getInstance().getQuitGroup(userName, groupId);
			OkHttpClientManager.getAsyn(url, new ResultCallback<String>() {

				@Override
				public void onError(Request request, Exception e) {
					// TODO Auto-generated method stub
					ToastUtil.show(GroupDetailActivity.this, "退出群组失败");
				}

				@Override
				public void onResponse(String response) {
					// TODO Auto-generated method stub

					ResultInfo resultInfo = JSONUtils.parseObject(response, ResultInfo.class);
					if (resultInfo != null && ResultCode.OK.equals(resultInfo.getResult())) {
						ToastUtil.show(GroupDetailActivity.this, "退出成功");
						setResult(RESULT_OK);
						mIMManager.clearMessages(mConversationInfo);
						finish();
					} else {
						ToastUtil.show(GroupDetailActivity.this, "退出群组失败");
					}
				}
			});

		}

	}
}
