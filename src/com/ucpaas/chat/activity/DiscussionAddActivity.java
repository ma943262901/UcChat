package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.DiscussionAddAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.ResultInfo;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.config.ResultCode;
import com.ucpaas.chat.db.ContactDb;
import com.ucpaas.chat.db.impl.ContactDbImpl;
import com.ucpaas.chat.support.RequestFactory;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.JSONUtils;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.OkHttpClientManager.ResultCallback;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;

/**
 * 讨论组增加成员
 * 
 * @author tangqi
 * @date 2015年9月3日下午3:19:23
 */

public class DiscussionAddActivity extends BaseActivity implements OnItemClickListener {
	protected static final int MSG_UPDATE_CONTACT = 1;
	private ListView mListView;
	private DiscussionAddAdapter mAdapter;

	private IMManager mIMManager;
	private List<UserInfo> mUserInfoList;
	private ContactDb mDb;
	private String mTitle;
	private String mMembers;
	private String mDiscussionId;
	private String mGroupId;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion_add);
		mTitle = getIntent().getStringExtra("title");
		mMembers = getIntent().getStringExtra("members");
		mDiscussionId = getIntent().getStringExtra("discussionId");
		mGroupId = getIntent().getStringExtra("groupId");
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mDb = new ContactDbImpl(this);
		mIMManager = IMManager.getInstance(this);
		mUserInfoList = new ArrayList<UserInfo>();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		TextView tvConfirm = (TextView) findViewById(R.id.tv_confirm);
		tvConfirm.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.lv_discussion);
		mAdapter = new DiscussionAddAdapter(this, mUserInfoList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(mTitle)) {
			setTitle(mTitle);
		}
		mHandler.sendEmptyMessage(MSG_UPDATE_CONTACT);
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_confirm:
			confirm();
			break;

		default:
			break;
		}
	}

	/**
	 * ListView点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	private void confirm() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append(SpOperation.getNickName(this));
		List<String> memberList = new ArrayList<String>();
		if (mUserInfoList != null) {
			for (UserInfo userInfo : mUserInfoList) {
				if (userInfo.isChecked() && !userInfo.getPhone().equals(SpOperation.getUserId(this))) {
					memberList.add(userInfo.getPhone());
					sb.append("," + userInfo.getNickname());
				}
			}
		}

		if (memberList.size() > 0) {
			if (mMembers != null) {
				// 讨论组邀请成员
				if (mDiscussionId != null) {
					addDiscussionMember(memberList);
				}

				// 群组邀请成员
				if (mGroupId != null) {
					if (memberList.size() == 1) {
						addGroupMember(memberList.get(0));
					} else {
						ToastUtil.show(this, "每次只能添加一名成员");
					}
				}
			} else {
				// 创建讨论组
				mIMManager.createDiscussionGroup(sb.toString(), memberList);
				finish();
			}

		} else {
			ToastUtil.show(this, "至少选择一名好友");
		}
	}

	/**
	 * 增加讨论组成员
	 * 
	 * @param memberList
	 */
	private void addDiscussionMember(List<String> memberList) {
		mIMManager.addDiscussionGroupMember(mDiscussionId, memberList);
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : memberList) {
			stringBuilder.append(string + ",");
		}

		Intent dataIntent = new Intent();
		dataIntent.putExtra("data", stringBuilder.toString());
		setResult(RESULT_OK, dataIntent);
		finish();
	}

	/**
	 * 添加群组成员
	 */
	private void addGroupMember(final String userId) {
		// TODO Auto-generated method stub
		String url = RequestFactory.getInstance().getJoinGroup(userId, mGroupId);
		OkHttpClientManager.getAsyn(url, new ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(DiscussionAddActivity.this, "添加成员失败");
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				ResultInfo resultInfo = JSONUtils.parseObject(response, ResultInfo.class);
				if (resultInfo != null && ResultCode.OK.equals(resultInfo.getResult())) {
					ToastUtil.show(DiscussionAddActivity.this, "添加成员成功");
					mMembers = mMembers + userId + ",";
					mHandler.sendEmptyMessage(MSG_UPDATE_CONTACT);
				} else {
					ToastUtil.show(DiscussionAddActivity.this, "添加成员失败");
				}
			}
		});
	}

	/**
	 * 更新聊天人列表
	 */
	private void updateListView() {
		// TODO Auto-generated method stub
		mUserInfoList = mDb.queryAll();
		if (mMembers != null) {
			iteratorRemove(mMembers);
		}
		mAdapter.setList(mUserInfoList);
	}

	/**
	 * 过滤已添加的成员
	 * 
	 * @param members
	 */
	public void iteratorRemove(String members) {
		Iterator<UserInfo> iterator = mUserInfoList.iterator();
		while (iterator.hasNext()) {
			UserInfo userInfo = iterator.next();
			if (members.contains(userInfo.getPhone()))
				iterator.remove();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case MSG_UPDATE_CONTACT:
				updateListView();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

}
