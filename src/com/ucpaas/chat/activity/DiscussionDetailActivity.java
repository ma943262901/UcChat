/** Copyright © 2015-2020 100msh.com All Rights Reserved */
package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.DiscussionGridAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.bean.DiscussionMember;
import com.ucpaas.chat.listener.ConfirmListener;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.ucpaas.chat.view.EditDialog;
import com.yzxIM.IMManager;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionInfo;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 讨论组详情
 * 
 * @author Frank
 * @date 2015年9月6日下午3:49:33
 */

public class DiscussionDetailActivity extends BaseActivity implements OnItemClickListener {

	private GridView mGridView;
	private DiscussionGridAdapter mAdpater;
	private ConversationInfo mConversationInfo;
	private IMManager mIMManager;
	private DiscussionInfo mDiscussionInfo;
	private List<DiscussionMember> mDiscussionMemberList;

	private TextView mTvImName;

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
		if (mConversationInfo != null) {
			mDiscussionInfo = mIMManager.getDiscussionInfo(mConversationInfo.getTargetId());
			String members = mDiscussionInfo.getDiscussionMembers();
			mDiscussionMemberList = getDiscussionMemberList(members);
			LogUtil.log("members:" + members);
		} else {
			mDiscussionMemberList = new ArrayList<DiscussionMember>();
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
		mAdpater = new DiscussionGridAdapter(this, mDiscussionMemberList);
		mGridView.setAdapter(mAdpater);
		mGridView.setOnItemClickListener(this);
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		ToastUtil.show(this, "position:" + arg2);
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
	public List<DiscussionMember> getDiscussionMemberList(String members) {
		List<DiscussionMember> discussionMemberList = new ArrayList<DiscussionMember>();

		if (!TextUtils.isEmpty(members)) {
			String[] memberList = members.split(",");
			if (memberList != null) {
				for (String member : memberList) {
					DiscussionMember discussionMember = new DiscussionMember();
					discussionMember.setUserId(member);
					discussionMemberList.add(discussionMember);
				}
			}
		}
		return discussionMemberList;
	}

	/**
	 * 退出讨论组
	 */
	private void exitDiscussion() {
		// TODO Auto-generated method stub
		mIMManager.quitDiscussionGroup(mDiscussionInfo.getDiscussionId());
		ToastUtil.show(this, "退出成功");
		finish();
	}

}
