package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.util.LogUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;
import com.yzxIM.listener.DiscussionGroupCallBack;
import com.yzxtcp.data.UcsReason;

/**
 * 讨论组
 * 
 * @author tangqi
 * @date 2015年9月3日下午3:19:23
 */

public class DiscussionActivity extends BaseActivity implements OnItemClickListener, DiscussionGroupCallBack {
	private ListView mListView;
	private IMManager mIMManager;
	private DiscussionInfo mDiscussionInfo;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_discussion);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mIMManager = IMManager.getInstance(this);
		mIMManager.setDiscussionGroup(this);
		mDiscussionInfo = new DiscussionInfo();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_discussion);
		mListView.setOnItemClickListener(this);

		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData()));
	}

	private List<String> getData() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("创建讨论组");
		list.add("添加讨论组成员");
		list.add("删除讨论组成员");
		list.add("退出讨论组");
		list.add("修改讨论组名称");
		return list;
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("讨论组");
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			createDiscussionGroup();
			break;

		case 1:
			addDiscussionGroupMember();
			break;

		case 2:
			delDiscussionGroupMember();
			break;

		case 3:
			quitDiscussionGroup();
			break;

		case 4:
			modifyDiscussionTitle();
			break;

		default:
			break;
		}
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
		LogUtil.log("onCreateDiscussion result:"+reason.getReason());
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
