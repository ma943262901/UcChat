package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.GroupListAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.util.LogUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxtcp.data.UcsReason;

/**
 * 讨论组列表
 * 
 * @author tangqi
 * @date 2015年9月3日下午3:19:23
 */

public class DiscussionListActivity extends BaseActivity implements OnItemClickListener, DiscussionGroupCallBack {
	private ListView mListView;
	private GroupListAdapter mAdapter;

	private IMManager mIMManager;
	private DiscussionInfo mDiscussionInfo;
	private CategoryId mCategoryId;
	private List<ConversationInfo> mConversationLists;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion_list);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mCategoryId = (CategoryId) getIntent().getSerializableExtra("categoryId");
		mIMManager = IMManager.getInstance(this);
		mIMManager.setDiscussionGroup(this);
		mDiscussionInfo = new DiscussionInfo();

		mConversationLists = new ArrayList<ConversationInfo>();
		List<ConversationInfo> conversationLists = mIMManager.getConversationList();
		if (conversationLists != null) {
			for (ConversationInfo conversationInfo : conversationLists) {
				if (CategoryId.DISCUSSION == conversationInfo.getCategoryId()) {
					mConversationLists.add(conversationInfo);
				}
			}
		}

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_discussion);
		mListView.setOnItemClickListener(this);

		mAdapter = new GroupListAdapter(this, mConversationLists);
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		if (CategoryId.DISCUSSION.equals(mCategoryId)) {
			setTitle("讨论组列表");
		} else if (CategoryId.GROUP.equals(mCategoryId)) {
			setTitle("群组列表");
		}

		showRightMenu();
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ConversationInfo conversationinfo = mConversationLists.get(position);
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("conversation", conversationinfo);
		startActivity(intent);
	}

	private void createDiscussionGroup() {
		// TODO Auto-generated method stub
		List<String> memberList = new ArrayList<String>();
		memberList.add("15019288493");
		mIMManager.createDiscussionGroup("1363280", memberList);
	}

	private void addDiscussionGroupMember() {
		// TODO Auto-generated method stub
		List<String> memberList = new ArrayList<String>();
		memberList.add("15019288493");
		mIMManager.addDiscussionGroupMember(mDiscussionInfo.getDiscussionId(), memberList);
	}

	private void delDiscussionGroupMember() {
		// TODO Auto-generated method stub
		List<String> memberList = new ArrayList<String>();
		memberList.add("15019288493");
		mIMManager.delDiscussionGroupMember(mDiscussionInfo.getDiscussionId(), memberList);
	}

	private void quitDiscussionGroup() {
		// TODO Auto-generated method stub
		mIMManager.quitDiscussionGroup(mDiscussionInfo.getDiscussionId());
	}

	private void modifyDiscussionTitle() {
		// TODO Auto-generated method stub
		mIMManager.modifyDiscussionTitle(mDiscussionInfo.getDiscussionId(), "avit-modify");
	}

	@Override
	public void onCreateDiscussion(UcsReason reason, DiscussionInfo dInfo) {
		// TODO Auto-generated method stub
		LogUtil.log("onCreateDiscussion result:" + reason.getReason());
		if (reason.getReason() == 0) {
			// 创建成功
			ConversationInfo info = IMManager.getInstance(this).getConversation(dInfo.getDiscussionId());
			if (null != info) {
				LogUtil.log("创建讨论组成功");
				mDiscussionInfo = dInfo;
			} else {
				LogUtil.log("获得讨论组会话为空");
			}
		} else {
			LogUtil.log("创建讨论组失败");
		}

	}

	@Override
	public void onDiscussionAddMember(UcsReason reason) {
		// TODO Auto-generated method stub
		if (reason.getReason() == 0) {
			LogUtil.log("加人成功");
		} else {
			LogUtil.log("加人失败");
		}

	}

	@Override
	public void onDiscussionDelMember(UcsReason reason) {
		// TODO Auto-generated method stub
		if (reason.getReason() == 0) {
			LogUtil.log("踢人成功");
		} else {
			LogUtil.log("踢人失败");
		}

	}

	@Override
	public void onQuiteDiscussion(UcsReason reason) {
		// TODO Auto-generated method stub
		if (reason.getReason() == 0) {
			LogUtil.log("退出成功");
		} else {
			LogUtil.log("退出失败");
		}
	}

}
