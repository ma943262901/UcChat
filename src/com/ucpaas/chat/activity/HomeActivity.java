package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.ConversationListAdapter;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.base.BaseApplication;
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
 * 主页
 * 
 * @author tangqi
 * @date 2015年9月1日下午10:39:18
 */

public class HomeActivity extends BaseActivity implements OnItemClickListener, IConversationListener {

	private ListView mListView;
	private IMManager mIMManager;
	private ConversationListAdapter mAdapter;
	private List<ConversationInfo> mConversationLists;

	private long mExitTime;
	private final static long TIME_DIFF = 2 * 1000;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_home);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub
		mConversationLists = new ArrayList<ConversationInfo>();
	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_home);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("聊天列表");
		hideBackButton();

		connectServer();
		initIMManger();
		
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

	/**
	 * 初始化IMManager
	 */
	private void initIMManger() {
		// TODO Auto-generated method stub
		mIMManager = IMManager.getInstance(this); // 获得IMManager类
		mIMManager.setConversationListener(this);
		mConversationLists = new ArrayList<ConversationInfo>();// 定义会话列表

		// 获取会列表
		mConversationLists = mIMManager.getConversationList();
		LogUtil.log("conversationLists:" + mConversationLists.size());
		if (mConversationLists != null) {
			mAdapter = new ConversationListAdapter(this, mConversationLists);
			mListView.setAdapter(mAdapter);
		}

		 sendMessage();
	}

	/**
	 * 连接服务器
	 */
	private void connectServer() {
		String token = SpOperation.getToken(this);
		BaseApplication.getInstance().connectUCSManager(token, new ILoginListener() {

			@Override
			public void onLogin(final UcsReason arg0) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (arg0.getReason() == 0) {
							ToastUtil.show(HomeActivity.this, "连接服务器成功");
						} else {
							ToastUtil.show(HomeActivity.this, "连接服务器失败");
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
		runOnUiThread(new Runnable() {

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
				if(cinfoSrc.getTargetId().equals(conversationInfo.getTargetId())){
					findNum = i;
					break;
				}
			}
			
			if(findNum != -1){
				mConversationLists.set(findNum, cinfoSrc);
			}else{
				mConversationLists.add(0,cinfoSrc);
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
			ToastUtil.show(this, "消息发送成功");
			LogUtil.log("消息发送成功");
			// currentMsgList.add(msg);
		} else {
			ToastUtil.show(this, "消息发送失败");
			LogUtil.log("消息发送失败");
		}
	}

	/**
	 * 再按一次退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > TIME_DIFF) {
				ToastUtil.show(HomeActivity.this, "再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
