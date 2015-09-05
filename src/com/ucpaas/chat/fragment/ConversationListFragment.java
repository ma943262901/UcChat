package com.ucpaas.chat.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.activity.ConversationActivity;
import com.ucpaas.chat.adapter.ConversationListAdapter;
import com.ucpaas.chat.base.BaseApplication;
import com.ucpaas.chat.base.BaseFragment;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.IConversationListener;
import com.yzxtcp.data.UcsReason;
import com.yzxtcp.listener.ILoginListener;

/**
 * 聊天
 * @author tangqi
 * @date 2015年9月5日下午2:18:35
 */

public class ConversationListFragment extends BaseFragment implements OnItemClickListener, IConversationListener {

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
		// TODO Auto-generated method stub
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
		Intent intent = new Intent(getActivity(), ConversationActivity.class);
		intent.putExtra("conversation", conversationinfo);
		startActivity(intent);
	}

	/**
	 * 初始化IMManager
	 */
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

		// sendMessage();
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

	@Override
	public void onCreateConversation(ConversationInfo cinfo) {
		// TODO Auto-generated method stub

		// 把cinfo添加到会话列表中，更新界面
		mConversationLists.add(cinfo);
		sync();
	}

	@Override
	public void onDeleteConversation(ConversationInfo cinfo) {
		// TODO Auto-generated method stub

		// 把cinfo从会话列表中移除，更新界面
		mConversationLists.remove(cinfo);
		sync();

	}

	@Override
	public void onUpdateConversation(ConversationInfo cinfoSrc) {
		// TODO Auto-generated method stub
		// updataCinfo(cinfoSrc, cinfoDest);
		updateCinfo(cinfoSrc);
	}

	private void sync() {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				mAdapter.notifyDataSetChanged();
			}
		});
	}

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

	private void updataCinfo(ConversationInfo cinfoSrc, ConversationInfo cinfoDest) {
		mConversationLists.remove(cinfoDest);
		cinfoDest.setDraftMsg(cinfoSrc.getDraftMsg());
		cinfoDest.setLastTime(cinfoSrc.getLastTime());
		if (cinfoSrc.getCategoryId() != CategoryId.GROUP) {
			cinfoDest.setConversationTitle(cinfoSrc.getConversationTitle());
		}
		if (cinfoDest.getIsTop()) {
			mConversationLists.add(0, cinfoDest);
		} else {
			// conversationLists.add(topNum, cinfoDest);
		}
		// 更新会话列表
	}

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
}
