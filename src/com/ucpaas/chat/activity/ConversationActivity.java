package com.ucpaas.chat.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.ConversationReplyAdapter;
import com.ucpaas.chat.config.AppConstants;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.support.UcUtils;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.MessageListener;

/**
 * 自定义反馈界面
 * 
 * @author tangqi
 * 
 */
@SuppressLint({ "InflateParams", "SimpleDateFormat" })
public class ConversationActivity extends Activity implements MessageListener,
		OnClickListener {

	private IMManager mIMManager;
	private ConversationInfo mConversationInfo;
	private List<ChatMessage> mChatMessages;
	private ListView mListView;
	private Context mContext;
	private ConversationReplyAdapter mAdapter;

	private SwipeRefreshLayout mSwipeRefreshLayout;

	private String mUserId;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_ex);
		mContext = this;

		mIMManager = IMManager.getInstance(this); // 获得IMManager类
		mIMManager.setSendMsgListener(this);
		mConversationInfo = (ConversationInfo) getIntent()
				.getSerializableExtra("conversation");
		mChatMessages = mConversationInfo.getAllMessage();
		mUserId = SpOperation.getUserId(this);

		initView();
		mAdapter = new ConversationReplyAdapter(ConversationActivity.this,
				mChatMessages);
		mListView.setAdapter(mAdapter);
		sync();

	}

	private void initView() {
		TextView mTitleView = (TextView) findViewById(R.id.tv_title);
		mTitleView.setText(mConversationInfo.getConversationTitle());

		ImageView mBackBtn = (ImageView) findViewById(R.id.btn_back);
		mBackBtn.setVisibility(View.VISIBLE);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mListView = (ListView) findViewById(R.id.fb_reply_list);

		// 下拉刷新组件
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.fb_reply_refresh);

		// 下拉刷新
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {

				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mSwipeRefreshLayout.setRefreshing(false);
					}
				}, 2000);

				// sync();
			}
		});

		// 顶部刷新的样式
		mSwipeRefreshLayout.setColorSchemeResources(
				android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);

		initBottonMenu();
	}

	/**
	 * 发送消息
	 * 
	 * @param content
	 */
	private void sendTextMessage(String content) {
		ChatMessage msg = null;
		msg = new SingleChat();// 创建单聊消息
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_TEXT);// 设置消息类型为文本
		msg.setContent(content); // 设置消息内容
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) { // 发送消息成功返回true
			// 发送成功后把消息添加到消息列表中，收到消息发送回调后刷新界面
			// ToastUtil.show(this, "消息发送成功");

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}

	private void sendImageMessage(String path, String smallPath) {
		ChatMessage msg = null;
		msg = new SingleChat();// 创建单聊消息
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_IMAGE);
		msg.setContent(smallPath);
		msg.setPath(path);
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) { // 发送消息成功返回true

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}
	
	private void sendVoiceMessage(String path, String time) {
		ChatMessage msg = null;
		msg = new SingleChat();// 创建单聊消息
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_VOICE);
		msg.setPath(path);
		msg.setContent(time);
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) { // 发送消息成功返回true

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}

	@Override
	public void onDownloadAttachedProgress(String arg0, String arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 收到消息-回调
	 */
	@Override
	public void onReceiveMessage(final List arg0) {
		// TODO Auto-generated method stub
		LogUtil.log("onReceiveMessage:" + arg0.size());
		LogUtil.log("onReceiveMessage arg0.get(0):" + ((ChatMessage)arg0.get(0)).getMsgType());
		
		if(MSGTYPE.MSG_DATA_TEXT == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_TEXT");
		}else if(MSGTYPE.MSG_DATA_IMAGE == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_IMAGE");
		}else if(MSGTYPE.MSG_DATA_VOICE == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_VOIC");
		}else if(MSGTYPE.MSG_DATA_VIDEO == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_VIDEO");
		}else if(MSGTYPE.MSG_DATA_SYSTEM == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_SYSTEM");
		}else if(MSGTYPE.MSG_DATA_NONE == ((ChatMessage)arg0.get(0)).getMsgType()){
			LogUtil.log("onReceiveMessage MSG_DATA_NONE");
		}
		
		mChatMessages.addAll(arg0);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sync();
			}
		});

	}

	/**
	 * 发送消息-回调
	 */
	@Override
	public void onSendMsgRespone(ChatMessage msg) {
		// TODO Auto-generated method stub
		LogUtil.log("onSendMsgRespone");

		LogUtil.log("status = " + msg.getSendStatus());
		int mm = msg.getSendStatus();
		mChatMessages.add(msg);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sync();
			}
		});
		// sync();
	}

	/**
	 * 数据同步
	 */
	private void sync() {
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mAdapter.getCount());
		LogUtil.log("sync mChatMessages:" + mChatMessages.size());
	}

	private Button mSendBtn;
	private EditText mInputEdit;
	private RelativeLayout mBtnMore;
	private RelativeLayout mBtnAudio;
	private ImageView mBtnExpression;

	private LinearLayout mLayoutBottom;
	private LinearLayout im_ll_file;
	private LinearLayout im_ll_images;
	private LinearLayout im_ll_more;
	private LinearLayout im_ll_record;
	private LinearLayout im_ll_shot;

	private void initBottonMenu() {
		mLayoutBottom = (LinearLayout) findViewById(R.id.ll_bottom);
		mBtnMore = (RelativeLayout) findViewById(R.id.im_more);
		mBtnMore.setOnClickListener(this);
		mBtnAudio = (RelativeLayout) findViewById(R.id.im_audio);
		mBtnAudio.setOnClickListener(this);
		mBtnExpression = (ImageView) findViewById(R.id.im_expression_image);
		mBtnExpression.setOnClickListener(this);

		im_ll_more = (LinearLayout) findViewById(R.id.im_ll_more);
		im_ll_record = (LinearLayout) findViewById(R.id.im_ll_record);
		im_ll_images = (LinearLayout) findViewById(R.id.im_ll_images);
		im_ll_shot = (LinearLayout) findViewById(R.id.im_ll_shot);
		im_ll_shot.setOnClickListener(this);
		im_ll_file = (LinearLayout) findViewById(R.id.im_ll_file);
		im_ll_file.setOnClickListener(this);

		mSendBtn = (Button) findViewById(R.id.fb_send_btn);
		mSendBtn.setOnClickListener(this);
		mInputEdit = (EditText) findViewById(R.id.fb_send_content);

		mInputEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// 隐藏其他布局
				im_ll_more.setVisibility(View.GONE);
				im_ll_images.setVisibility(View.GONE);
				im_ll_record.setVisibility(View.GONE);
			}
		});

		mInputEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				mSendBtn.setVisibility(View.VISIBLE);
				mBtnMore.setVisibility(View.GONE);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mSendBtn.getVisibility() != View.VISIBLE) {
					mSendBtn.setVisibility(View.VISIBLE);
					mBtnMore.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 发送文本/表情消息
		case R.id.fb_send_btn: {
			String content = mInputEdit.getText().toString();
			mInputEdit.getEditableText().clear();
			if (!TextUtils.isEmpty(content)) {
				sendTextMessage(content);
			}
			if (mSendBtn.getVisibility() == View.VISIBLE) {
				mSendBtn.setVisibility(View.GONE);
				mBtnMore.setVisibility(View.VISIBLE);
			}

		}
			break;
		// 语音按钮
		case R.id.im_audio: {
			if (im_ll_record.getVisibility() == View.VISIBLE) {
				im_ll_record.setVisibility(View.GONE);
			} else {
				im_ll_more.setVisibility(View.GONE);
				im_ll_images.setVisibility(View.GONE);
				im_ll_record.setVisibility(View.VISIBLE);
			}
		}
			break;
		// 表情按钮
		case R.id.im_expression_image: {
			if (im_ll_images.getVisibility() == View.VISIBLE) {
				im_ll_images.setVisibility(View.GONE);
			} else {
				im_ll_more.setVisibility(View.GONE);
				im_ll_record.setVisibility(View.GONE);
				im_ll_images.setVisibility(View.VISIBLE);
			}
		}
			break;
		// 更多按钮
		case R.id.im_more: {
			if (im_ll_more.getVisibility() == View.VISIBLE) {
				im_ll_more.setVisibility(View.GONE);
			} else {
				im_ll_record.setVisibility(View.GONE);
				im_ll_images.setVisibility(View.GONE);
				im_ll_more.setVisibility(View.VISIBLE);
			}

		}
			break;
		// 图片
		case R.id.im_ll_file: {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1);
		}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String path = null;
			Uri uri = data.getData();
			Cursor cursor = this.getContentResolver().query(uri, null, null,
					null, null);
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getColumnCount(); i++) {// 取得图片uri的列名和此列的详细信息
																// String str =
																// i + "-" +
																// cursor.getColumnName(i)
																// + "-" +
																// cursor.getString(i);
				// System.out.println(i + "-" + cursor.getColumnName(i) + "-" +
				// cursor.getString(i));
				int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
				path = cursor.getString(index);				
			}
			if (path != null) {
				sendImageMessage(path, path);
			}
		}
	}

}
