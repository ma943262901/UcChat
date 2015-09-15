package com.ucpaas.chat.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ucpaas.chat.R;
import com.ucpaas.chat.activity.ConversationActivity;
import com.ucpaas.chat.adapter.ConversationListAdapter;
import com.ucpaas.chat.base.BaseApplication;
import com.ucpaas.chat.base.BaseFragment;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.RingtoneUtils;
import com.ucpaas.chat.util.ToastUtil;
import com.ucpaas.chat.util.VibratorUtils;
import com.yzxIM.IMManager;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.IConversationListener;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 会话列表
 * 
 * @author tangqi
 * @date 2015年9月5日下午2:18:35
 */

public class ConversationListFragment extends BaseFragment implements OnItemClickListener, IConversationListener, Comparator<ConversationInfo> {

	private View rootView;
	private ListView mListView;
	private IMManager mIMManager;
	private ConversationListAdapter mAdapter;
	private List<ConversationInfo> mConversationLists;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mConversationLists = new ArrayList<ConversationInfo>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_conversation_list, container, false);
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
		mTitleView.setText("消息");

		mListView = (ListView) view.findViewById(R.id.lv_home);
		mListView.setOnItemClickListener(this);

		connectServer();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ConversationInfo conversationinfo = mConversationLists.get(position);
		// 清除未读消息标记
		mIMManager.clearMessagesUnreadStatus(conversationinfo);
		mConversationLists.get(position).setMsgUnRead(0);
		Intent intent = new Intent(getActivity(), ConversationActivity.class);
		intent.putExtra("conversation", conversationinfo);
		getActivity().startActivityForResult(intent, 1000);
	}

	/**
	 * 初始化IMManager
	 */
	@SuppressWarnings("unchecked")
	private void initIMManger() {
		// TODO Auto-generated method stub
		mIMManager = IMManager.getInstance(getActivity()); // 获得IMManager类
		mIMManager.setConversationListener(this);
		mConversationLists = new ArrayList<ConversationInfo>();// 定义会话列表

		// 获取会列表
		mConversationLists = mIMManager.getConversationList();
		LogUtil.log("conversationLists:" + mConversationLists.size());
		if (mConversationLists != null) {
			mAdapter = new ConversationListAdapter(getActivity(), mConversationLists);
			mListView.setAdapter(mAdapter);
		}
	}

	/**
	 * 连接服务器
	 */
	private void connectServer() {
		String token = SpOperation.getToken(getActivity());
		BaseApplication.getInstance().connectUCSManager(token, new ILoginListener() {

			@Override
			public void onLogin(final UcsReason arg0) {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (arg0.getReason() == 0) {
							ToastUtil.show(getActivity(), "连接服务器成功");
							initIMManger();
						} else {
							ToastUtil.show(getActivity(), "连接服务器失败");
						}
					}
				});

			}
		});
	}

	/**
	 * 创建会话-回调
	 */
	@Override
	public void onCreateConversation(ConversationInfo cinfo) {
		mConversationLists.add(cinfo);
		sync();
	}

	/**
	 * 删除会话-回调
	 */
	@Override
	public void onDeleteConversation(ConversationInfo cinfo) {
		mConversationLists.remove(cinfo);
		sync();

	}

	/**
	 * 更新会话-回调
	 */
	@Override
	public void onUpdateConversation(ConversationInfo cinfoSrc) {
		updateCinfo(cinfoSrc);
		noticeUser(cinfoSrc);
	}

	/**
	 * 更新会话列表
	 * 
	 * @param cinfoSrc
	 */
	private void updateCinfo(ConversationInfo cinfoSrc) {
		if (mConversationLists != null && cinfoSrc != null) {
			int findNum = -1;
			int size = mConversationLists.size();
			for (int i = 0; i < size; i++) {
				ConversationInfo conversationInfo = mConversationLists.get(i);
				if (cinfoSrc.getTargetId().equals(conversationInfo.getTargetId())) {
					findNum = i;
					break;
				}
			}

			if (findNum != -1) {
				mConversationLists.set(findNum, cinfoSrc);
			} else {
				mConversationLists.add(0, cinfoSrc);
			}

			sync();
		}
	}

	/**
	 * 提醒用户-默认震动和语音
	 * 
	 * @param cinfoSrc
	 */
	private void noticeUser(ConversationInfo cinfoSrc) {
		if (SpOperation.getUserId(getActivity()).equals(cinfoSrc.getTargetId())) {
			RingtoneUtils.playDefault(getActivity());
			VibratorUtils.vibrate(getActivity());
		}
	}

	/**
	 * 同步会话列表数据
	 */
	private void sync() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Collections.sort(mConversationLists, ConversationListFragment.this);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 发送消息测试
	 */
	@SuppressWarnings("unused")
	private void sendMessage() {
		ChatMessage msg = null;
		msg = new SingleChat();// 创建单聊消息
		msg.setTargetId("15019288493");
		msg.setSenderId("15019288493");
		msg.setMsgType(MSGTYPE.MSG_DATA_TEXT);// 设置消息类型为文本
		msg.setContent("石头你好"); // 设置消息内容
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) { // 发送消息成功返回true
			// 发送成功后把消息添加到消息列表中，收到消息发送回调后刷新界面
			ToastUtil.show(getActivity(), "消息发送成功");
			LogUtil.log("消息发送成功");
			// currentMsgList.add(msg);
		} else {
			ToastUtil.show(getActivity(), "消息发送失败");
			LogUtil.log("消息发送失败");
		}
	}

	/**
	 * 会话排序（按照更新时间倒序）
	 */
	@Override
	public int compare(ConversationInfo lhs, ConversationInfo rhs) {
		// TODO Auto-generated method stub
		return lhs.getLastTime() >= rhs.getLastTime() ? -1 : 1;
	}

	/**
	 * 更新数据
	 * 
	 * @param titleName
	 */
	public void refreshData(String titleName) {
		mAdapter.setActivityBackTitleName(titleName);
		mAdapter.notifyDataSetChanged();
	}
}
