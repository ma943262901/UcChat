package com.ucpaas.chat.fragment;

import java.util.ArrayList;
import java.util.List;

import com.ucpaas.chat.R;
import com.ucpaas.chat.activity.ConversationActivity;
import com.ucpaas.chat.activity.DiscussionListActivity;
import com.ucpaas.chat.activity.GroupListActivity;
import com.ucpaas.chat.adapter.ContactListAdapter;
import com.ucpaas.chat.base.BaseFragment;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.db.ContactDb;
import com.ucpaas.chat.db.impl.ContactDbImpl;
import com.ucpaas.chat.listener.ConfirmListener;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.StringUtils;
import com.ucpaas.chat.util.ToastUtil;
import com.ucpaas.chat.view.EditDialog;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.db.ConversationInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 通信录
 * 
 * @author tangqi
 * @data 2015年8月9日上午11:07:09
 */

public class ContactFragment extends BaseFragment implements OnClickListener, OnItemClickListener {

	protected static final int MSG_UPDATE_CONTACT = 1;
	private View rootView;
	private ContactDb mDb;

	private ListView mListView;
	private ContactListAdapter mAdapter;
	private List<UserInfo> mUserInfoList;
	private List<ConversationInfo> mConversationInfoList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_contact, container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		initView(rootView);
		return rootView;
	}

	private void initView(View view) {
		// TODO Auto-generated method stub
		hideBackButton(view);
		TextView mTitleView = (TextView) view.findViewById(R.id.tv_title);
		mTitleView.setText("通讯录");

		// 新的朋友，群组，讨论组
		view.findViewById(R.id.ll_person).setOnClickListener(this);
		view.findViewById(R.id.ll_group).setOnClickListener(this);
		view.findViewById(R.id.ll_discussion).setOnClickListener(this);
		mListView = (ListView) view.findViewById(R.id.lv_contact);

		// 初始化联系人
		initContact();
	}

	/**
	 * 初始化联系人
	 */
	private void initContact() {
		// TODO Auto-generated method stub
		mDb = new ContactDbImpl(getActivity());
		mConversationInfoList = IMManager.getInstance(getActivity()).getConversationList();
		mUserInfoList = new ArrayList<UserInfo>();
		mAdapter = new ContactListAdapter(getActivity(), mUserInfoList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		boolean isFirstLogin = SpOperation.getIsFirstLogin(getActivity());

		if (isFirstLogin) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					List<UserInfo> userInfoList = getContactList();
					mDb.insert(userInfoList);
					SpOperation.setIsFirstLogin(getActivity(), false);
					mHandler.sendEmptyMessage(MSG_UPDATE_CONTACT);
				}
			}).start();
			;
		} else {
			mHandler.sendEmptyMessage(MSG_UPDATE_CONTACT);
		}
	}

	/**
	 * 获取默认联系人数据
	 * 
	 * @return
	 */
	private List<UserInfo> getContactList() {
		List<UserInfo> userInfoList = new ArrayList<UserInfo>();

		UserInfo userInfo3 = new UserInfo();
		userInfo3.setPhone("13632809273");
		userInfo3.setNickname("test3");
		userInfoList.add(userInfo3);

		UserInfo userInfo2 = new UserInfo();
		userInfo2.setPhone("13632809272");
		userInfo2.setNickname("test2");
		userInfoList.add(userInfo2);

		UserInfo userInfo1 = new UserInfo();
		userInfo1.setPhone("13632809271");
		userInfo1.setNickname("test1");
		userInfoList.add(userInfo1);

		UserInfo userInfo11 = new UserInfo();
		userInfo11.setPhone("15019288493");
		userInfo11.setNickname("石头");
		userInfoList.add(userInfo11);

		UserInfo userInfo12 = new UserInfo();
		userInfo12.setPhone("13632809278");
		userInfo12.setNickname("小唐");
		userInfoList.add(userInfo12);

		return userInfoList;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {

		// 添加联系人
		case R.id.ll_person:
			addContactPerson();
			break;

		// 群组
		case R.id.ll_group:
			intent = new Intent(getActivity(), GroupListActivity.class);
			intent.putExtra("categoryId", CategoryId.GROUP);
			break;

		// 讨论组
		case R.id.ll_discussion:
			intent = new Intent(getActivity(), DiscussionListActivity.class);
			intent.putExtra("categoryId", CategoryId.DISCUSSION);
			break;

		default:
			break;
		}

		if (intent != null) {
			startActivity(intent);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		UserInfo userInfo = (UserInfo) parent.getAdapter().getItem(position);

		// 判断会话是否已存在
		boolean isExist = false;
		if (mConversationInfoList != null) {
			for (ConversationInfo conversationInfo : mConversationInfoList) {
				if (conversationInfo.getTargetId().equals(userInfo.getPhone())) {
					// 会话已创建
					doContact(conversationInfo);
					isExist = true;
					break;
				}
			}
		}

		// 会话不存在，新建
		if (!isExist) {
			ConversationInfo conversationInfo = new ConversationInfo();
			conversationInfo.setTargetId(userInfo.getPhone());
			conversationInfo.setCategoryId(CategoryId.PERSONAL);
			if (!TextUtils.isEmpty(userInfo.getNickname())) {
				conversationInfo.setConversationTitle(userInfo.getNickname());
			} else {
				conversationInfo.setConversationTitle(userInfo.getPhone());
			}

			doContact(conversationInfo);
		}
	}

	/**
	 * 跳转聊天界面
	 * 
	 * @param conversationInfo
	 */
	public void doContact(ConversationInfo conversationInfo) {
		Intent intent = new Intent(getActivity(), ConversationActivity.class);
		intent.putExtra("conversation", conversationInfo);
		startActivity(intent);
	}

	/**
	 * 添加联系人
	 */
	private void addContactPerson() {
		EditDialog editDialog = new EditDialog(getActivity(), "输入对方手机号", "");
		editDialog.show();

		editDialog.setConfirmListener(new ConfirmListener() {

			@Override
			public void confirm(final String result) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(result)) {
					ToastUtil.show(getActivity(), "手机号为空");
				} else if (StringUtils.isMobileNO(result)) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							UserInfo userInfo = new UserInfo();
							userInfo.setPhone(result);
							userInfo.setNickname(result);
							mDb.insert(userInfo);
							mHandler.sendEmptyMessage(MSG_UPDATE_CONTACT);
						}
					}).start();
				} else {
					ToastUtil.show(getActivity(), "手机号输入有误");
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
		mAdapter.setList(mUserInfoList);
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
