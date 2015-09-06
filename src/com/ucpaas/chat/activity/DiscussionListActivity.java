package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.DiscussionListAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxtcp.data.UcsReason;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/**
 * 讨论组列表
 * 
 * @author tangqi
 * @date 2015年9月3日下午3:19:23
 */

public class DiscussionListActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener,
		DiscussionGroupCallBack {
	private ListView mListView;
	private DiscussionListAdapter mAdapter;

	private IMManager mIMManager;
	private List<ConversationInfo> mConversationInfoList;
	private DiscussionInfo mDiscussionInfo;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion_list);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mIMManager = IMManager.getInstance(this);
		mIMManager.setDiscussionGroup(this);
		mDiscussionInfo = new DiscussionInfo();
		mConversationInfoList = new ArrayList<ConversationInfo>();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_discussion);
		mAdapter = new DiscussionListAdapter(this, mConversationInfoList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("讨论组列表");

		showRightMenu();
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

	/**
	 * 跳转聊天界面
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ConversationInfo conversationInfo = mConversationInfoList.get(position);
		Intent intent = new Intent(this, ConversationActivity.class);
		intent.putExtra("conversation", conversationInfo);
		startActivity(intent);
	}

	/**
	 * 长按删除会话
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		// ConversationInfo conversationInfo =
		// mConversationInfoList.get(position);
		// DiscussionInfo discussionInfo = mIMManager.getDiscussionInfo(new
		// String(conversationInfo.getTargetId()));
		// mIMManager.quitDiscussionGroup(discussionInfo.getDiscussionId());
		// mConversationInfoList.remove(conversationInfo);
		return false;
	}

	/**
	 * 获取所有讨论组
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ConversationInfo> getDiscussionList() {
		List<ConversationInfo> conversationInfoList = mIMManager.getConversationList(CategoryId.DISCUSSION.ordinal());
		if (conversationInfoList == null) {
			conversationInfoList = new ArrayList<ConversationInfo>();
		}
		return conversationInfoList;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mConversationInfoList = getDiscussionList();
		mAdapter.setList(mConversationInfoList);
		sync("");
		super.onResume();
	}

	/**
	 * 同步更新数据
	 * 
	 * @param msg
	 */
	private void sync(final String msg) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(msg)) {
					ToastUtil.show(DiscussionListActivity.this, msg);
				}
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 新建讨论组
	 */
	private void addDiscussionGroup() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, DiscussionAddActivity.class);
		intent.putExtra("title", "创建讨论组");
		startActivity(intent);
		// createDiscussionGroup();
	}

	private void createDiscussionGroup() {
		// TODO Auto-generated method stub
		List<String> memberList = new ArrayList<String>();
		memberList.add("15019288493");
		mIMManager.createDiscussionGroup("上沙椰树村", memberList);
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
				mConversationInfoList.add(info);
				sync("创建讨论组成功");
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
			sync("退出成功");
		} else {
			LogUtil.log("退出失败");
		}
	}

}
