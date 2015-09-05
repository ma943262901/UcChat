package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.squareup.okhttp.Request;
import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.GroupListAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.GroupInfo;
import com.ucpaas.chat.support.RequestFactory;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.JSONUtils;
import com.ucpaas.chat.util.OkHttpClientManager;
import com.ucpaas.chat.util.OkHttpClientManager.ResultCallback;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;

/**
 * 群组列表
 * 
 * @author tangqi
 * @date 2015年9月3日下午3:19:23
 */

public class GroupListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
	private ListView mListView;
	private GroupListAdapter mAdapter;

	private IMManager mIMManager;
	private List<ConversationInfo> mConversationInfoList;
	private List<GroupInfo> mGroupInfoList;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion_list);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mIMManager = IMManager.getInstance(this);
		mGroupInfoList = new ArrayList<GroupInfo>();
		mConversationInfoList = getDiscussionList();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_discussion);
		mAdapter = new GroupListAdapter(this, mGroupInfoList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("群组列表");
		showRightMenu();

		getData();
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_menu:
			addDiscussionGroup();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		GroupInfo groupInfo = mGroupInfoList.get(position);
		ConversationInfo conversationInfo = getGroupConversation(groupInfo);
		if (conversationInfo == null) {
			conversationInfo = new ConversationInfo();
			conversationInfo.setCategoryId(CategoryId.GROUP.ordinal());
			conversationInfo.setTargetId(groupInfo.getGroupID());
			conversationInfo.setConversationTitle(groupInfo.getGroupName());
		}
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("conversation", conversationInfo);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		return true;
	}

	private List<ConversationInfo> getDiscussionList() {
		List<ConversationInfo> conversationInfoList = mIMManager.getConversationList(CategoryId.GROUP.ordinal());
		if (conversationInfoList == null) {
			conversationInfoList = new ArrayList<ConversationInfo>();
		}
		return conversationInfoList;
	}

	private ConversationInfo getGroupConversation(GroupInfo groupInfo) {
		if (mConversationInfoList != null) {
			for (ConversationInfo conversationInfo : mConversationInfoList) {
				if (groupInfo.getGroupID().equals(conversationInfo.getTargetId())) {
					return conversationInfo;
				}
			}
		}
		return null;
	}

	private void getData() {
		String userName = SpOperation.getUserId(this);
		String url = RequestFactory.getInstance().getQueryGroup(userName);
		OkHttpClientManager.getAsyn(url, new ResultCallback<String>() {

			@Override
			public void onError(Request request, Exception e) {
				// TODO Auto-generated method stub
				ToastUtil.show(GroupListActivity.this, "查询失败");
			}

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				// ToastUtil.show(GroupListActivity.this, "查询成功");
				mGroupInfoList = JSONUtils.parseGroupInfo(response);
				sync(null);
			}
		});
	}

	private void sync(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(msg)) {
					ToastUtil.show(GroupListActivity.this, msg);
				}
				mAdapter.setList(mGroupInfoList);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	private void addDiscussionGroup() {
		// TODO Auto-generated method stub
	}
}
