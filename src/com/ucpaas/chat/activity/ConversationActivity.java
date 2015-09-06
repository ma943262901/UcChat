package com.ucpaas.chat.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ucpaas.chat.R;
import com.ucpaas.chat.adapter.ConversationReplyAdapter;
import com.ucpaas.chat.support.SpOperation;
import com.ucpaas.chat.util.AudioRecorder;
import com.ucpaas.chat.util.ExpressionUtil;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.IMManager;
import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;
import com.yzxIM.data.db.ConversationInfo;
import com.yzxIM.data.db.DiscussionChat;
import com.yzxIM.data.db.GroupChat;
import com.yzxIM.data.db.SingleChat;
import com.yzxIM.listener.MessageListener;

/**
 * 自定义反馈界面
 * 
 * @author tangqi
 * 
 */
@SuppressLint({ "InflateParams", "SimpleDateFormat" })
public class ConversationActivity extends Activity implements MessageListener, OnClickListener {

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
		mConversationInfo = (ConversationInfo) getIntent().getSerializableExtra("conversation");
		mChatMessages = mConversationInfo.getAllMessage();
		mUserId = SpOperation.getUserId(this);

		initView();
		mAdapter = new ConversationReplyAdapter(ConversationActivity.this, mChatMessages);
		mListView.setAdapter(mAdapter);
		sync();
	}

	private void initView() {
		// 标题
		TextView mTitleView = (TextView) findViewById(R.id.tv_title);
		mTitleView.setText(mConversationInfo.getConversationTitle());

		// 会话详情
		ImageView btnMenu = (ImageView) findViewById(R.id.btn_menu);
		btnMenu.setImageResource(R.drawable.selector_btn_iminfo);
		btnMenu.setVisibility(View.VISIBLE);
		btnMenu.setOnClickListener(this);

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

		// // 下拉刷新组件
		// mSwipeRefreshLayout = (SwipeRefreshLayout)
		// findViewById(R.id.fb_reply_refresh);
		//
		// // 下拉刷新
		// mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
		// @Override
		// public void onRefresh() {
		//
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// mSwipeRefreshLayout.setRefreshing(false);
		// }
		// }, 2000);
		//
		// // sync();
		// }
		// });
		//
		// // 顶部刷新的样式
		// mSwipeRefreshLayout.setColorSchemeResources(
		// android.R.color.holo_red_light,
		// android.R.color.holo_green_light,
		// android.R.color.holo_blue_bright,
		// android.R.color.holo_orange_light);

		initBottonMenu();

		initExpression();
		showData();
	}

	/**
	 * 发送消息
	 * 
	 * @param content
	 */
	private void sendTextMessage(String content) {
		ChatMessage msg = null;
		msg = getChatMessage();
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_TEXT);
		msg.setContent(content);
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) {
			// 发送成功后把消息添加到消息列表中，收到消息发送回调后刷新界面
			// ToastUtil.show(this, "消息发送成功");

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}

	private void sendImageMessage(String path, String smallPath) {
		ChatMessage msg = null;
		msg = getChatMessage();
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_IMAGE);
		msg.setContent(smallPath);
		msg.setPath(path);
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) {

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}

	private void sendVoiceMessage(String path, String time) {
		ChatMessage msg = null;
		msg = getChatMessage();
		msg.setTargetId(mConversationInfo.getTargetId());
		msg.setSenderId(mUserId);
		msg.setMsgType(MSGTYPE.MSG_DATA_VOICE);
		msg.setPath(path);
		msg.setContent(time);
		LogUtil.log("sendMessage");
		if (mIMManager.sendmessage(msg)) {

		} else {
			ToastUtil.show(this, "消息发送失败");
		}
	}

	private ChatMessage getChatMessage() {
		ChatMessage msg = null;
		if (CategoryId.PERSONAL == mConversationInfo.getCategoryId()) {
			msg = new SingleChat();
		} else if (CategoryId.DISCUSSION == mConversationInfo.getCategoryId()) {
			msg = new DiscussionChat();
		} else if (CategoryId.GROUP == mConversationInfo.getCategoryId()) {
			msg = new GroupChat();
		} else {
			msg = new SingleChat();
		}

		return msg;
	}

	@Override
	public void onDownloadAttachedProgress(String arg0, String arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	/**
	 * 收到消息-回调
	 */
	@Override
	public void onReceiveMessage(final List arg0) {
		// TODO Auto-generated method stub
		LogUtil.log("onReceiveMessage getMsgType:" + ((ChatMessage) arg0.get(0)).getMsgType());

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
		LogUtil.log("onSendMsgRespone status = " + msg.getSendStatus());
		int mm = msg.getSendStatus();
		mChatMessages.add(msg);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				sync();
			}
		});
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
	private RelativeLayout im_ll_images;
	private LinearLayout im_ll_more;
	private LinearLayout im_ll_record;
	private LinearLayout im_ll_shot;

	private ImageView img_record_btn;
	private TextView textView_record_time;

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
		im_ll_images = (RelativeLayout) findViewById(R.id.im_ll_images);
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
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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

		//
		img_record_btn = (ImageView) findViewById(R.id.im_iv_record);
		img_record_btn.setOnClickListener(this);
		textView_record_time = (TextView) findViewById(R.id.im_tv_record);
		textView_record_time.setText("按住说话");

		img_record_btn.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				startRecord(v, event);
				return false;
			}
		});
	}

	/**
	 * 开始录音
	 * 
	 * @param v
	 * @param event
	 */
	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制

	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音
	private static final String RECORD_FILENAME = "record"; // 录音文件名
	private int recordState = 0; // 录音状态
	private float recodeTime = 0.0f; // 录音时长
	private double voiceValue = 0.0; // 录音的音量值
	private boolean playState = false; // 录音的播放状态
	private boolean moveState = false; // 手指是否移动
	private float downY;

	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private MediaPlayer mMediaPlayer;
	private Thread mRecordThread;

	private TextView mTvRecordDialogTxt;
	private ImageView mIvRecVolume;

	private void startRecord(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 按下按钮
			if (recordState != RECORD_ON) {
				downY = event.getY();
				deleteOldFile();
				mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
				recordState = RECORD_ON;
				try {
					mAudioRecorder.start();
					recordTimethread();
					showVoiceDialog(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE: // 滑动手指
			float moveY = event.getY();
			if (downY - moveY > 50) {
				moveState = true;
				showVoiceDialog(1);
			}
			if (downY - moveY < 20) {
				moveState = false;
				showVoiceDialog(0);
			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: // 松开手指
			if (recordState == RECORD_ON) {
				recordState = RECORD_OFF;
				if (mRecordDialog.isShowing()) {
					mRecordDialog.dismiss();
				}
				try {
					mAudioRecorder.stop();
					mRecordThread.interrupt();
					voiceValue = 0.0;
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (!moveState) {
					if (recodeTime < MIN_RECORD_TIME) {
						showWarnToast("时间太短  录音失败");
					} else {
						textView_record_time.setText("录音时间：" + ((int) recodeTime));
						String path = mAudioRecorder.getRecordPath();
						sendVoiceMessage(path, (int) recodeTime + "");
						// mTvRecordPath.setText("文件路径：" + getAmrPath());
					}
				}
				moveState = false;
			}
			break;
		}
	}

	// 删除老文件
	void deleteOldFile() {
		File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
		if (file.exists()) {
			file.delete();
		}
	}

	// 录音时显示Dialog
	void showVoiceDialog(int flag) {
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(ConversationActivity.this, R.style.DialogStyle);
			mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mRecordDialog.setContentView(R.layout.record_dialog);
			mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
			mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
			mIvRecVolume.setImageResource(R.drawable.record_cancel);
			mTvRecordDialogTxt.setText("松开手指可取消录音");
			break;

		default:
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
			mTvRecordDialogTxt.setText("向上滑动可取消录音");
			break;
		}
		mTvRecordDialogTxt.setTextSize(14);
		mRecordDialog.show();
	}

	// 录音时间太短时Toast显示
	void showWarnToast(String toastText) {
		Toast toast = new Toast(ConversationActivity.this);
		LinearLayout linearLayout = new LinearLayout(ConversationActivity.this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(ConversationActivity.this);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(ConversationActivity.this);
		mTv.setText(toastText);
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// 字体颜色

		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// 内容居中
		linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
		toast.show();
	}

	// // 获取文件手机路径
	// private String getAmrPath() {
	// File file = new File(Environment.getExternalStorageDirectory(),
	// "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
	// return file.getAbsolutePath();
	// }

	// 录音计时线程
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		if (voiceValue < 600.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 600.0 && voiceValue < 1000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 12000.0) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_14);
		}
	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				// 限制录音时长
				// if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
				// imgHandle.sendEmptyMessage(0);
				// } else
				{
					try {
						Thread.sleep(150);
						recodeTime += 0.15;
						// 获取音量，更新dialog
						if (!moveState) {
							voiceValue = mAudioRecorder.getAmplitude();
							recordHandler.sendEmptyMessage(1);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public Handler recordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setDialogImage();
		}
	};

	// 表情
	private ImageView ivIcon = null;
	private LinearLayout llBottonLin = null;

	private int[] eImages;
	private String[] eImageNames;
	private int[] eImages1;
	private String[] eImageNames1;
	private int[] eImages2;
	private String[] eImageNames2;
	private ImageView emoji;
	private SimpleAdapter simpleAdapter;
	private List<Map<String, Object>> listItems;
	private ViewPager viewPager;
	private ArrayList<GridView> grids;
	private GridView gView1;
	private GridView gView2;
	private GridView gView3;
	private ImageView page0;
	private ImageView page1;
	private ImageView page2;

	/**
	 * 
	 */
	private void initExpression() {
		llBottonLin = (LinearLayout) findViewById(R.id.ll_botton_lin);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		page0 = (ImageView) findViewById(R.id.page0_select);
		page1 = (ImageView) findViewById(R.id.page1_select);
		page2 = (ImageView) findViewById(R.id.page2_select);
	}

	// 展示表情
	private void showData() {
		eImages = ExpressionUtil.getIntense().expressionImgs;
		eImageNames = ExpressionUtil.getIntense().expressionImgNames;
		eImages1 = ExpressionUtil.getIntense().expressionImgs1;
		eImageNames1 = ExpressionUtil.getIntense().expressionImgNames1;
		eImages2 = ExpressionUtil.getIntense().expressionImgs2;
		eImageNames2 = ExpressionUtil.getIntense().expressionImgNames2;
		LayoutInflater inflater = LayoutInflater.from(this);
		grids = new ArrayList<GridView>();
		gView1 = (GridView) inflater.inflate(R.layout.grid1, null);
		listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < 24; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("image", eImages[i]);
			listItems.add(listItem);
		}
		simpleAdapter = new SimpleAdapter(ConversationActivity.this, listItems, R.layout.single_expression, new String[] { "image" },
				new int[] { R.id.image });
		gView1.setAdapter(simpleAdapter);
		grids.add(gView1);

		gView2 = (GridView) inflater.inflate(R.layout.grid2, null);
		grids.add(gView2);

		gView3 = (GridView) inflater.inflate(R.layout.grid3, null);
		grids.add(gView3);

		// 填充ViewPager的数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return grids.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager) container).removeView(grids.get(position));
			}

			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager) container).addView(grids.get(position));
				return grids.get(position);
			}
		};

		viewPager.setAdapter(mPagerAdapter);
		// viewPager.setAdapter();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
	}

	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			System.out.println("页面滚动" + arg0);

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			System.out.println("换页了" + arg0);
		}

		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				page0.setImageDrawable(getResources().getDrawable(R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(R.drawable.page_unfocused));

				break;
			case 1:
				page1.setImageDrawable(getResources().getDrawable(R.drawable.page_focused));
				page0.setImageDrawable(getResources().getDrawable(R.drawable.page_unfocused));
				page2.setImageDrawable(getResources().getDrawable(R.drawable.page_unfocused));
				List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
				// 生成24个表情
				for (int i = 0; i < 24; i++) {
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", eImages1[i]);
					listItems.add(listItem);
				}

				SimpleAdapter simpleAdapter = new SimpleAdapter(ConversationActivity.this, listItems, R.layout.single_expression,
						new String[] { "image" }, new int[] { R.id.image });
				gView2.setAdapter(simpleAdapter);
				break;
			case 2:
				page2.setImageDrawable(getResources().getDrawable(R.drawable.page_focused));
				page1.setImageDrawable(getResources().getDrawable(R.drawable.page_unfocused));
				page0.setImageDrawable(getResources().getDrawable(R.drawable.page_unfocused));
				List<Map<String, Object>> listItems1 = new ArrayList<Map<String, Object>>();
				// 生成24个表情
				for (int i = 0; i < 24; i++) {
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", eImages2[i]);
					listItems1.add(listItem);
				}

				SimpleAdapter simpleAdapter1 = new SimpleAdapter(ConversationActivity.this, listItems1, R.layout.single_expression,
						new String[] { "image" }, new int[] { R.id.image });
				gView3.setAdapter(simpleAdapter1);
				break;

			}
		}
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
			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, 1);
		}
			break;

		// 会话详情
		case R.id.btn_menu:
			jumpConversationDetail();
			break;

		default:
			break;
		}
	}

	/**
	 * 跳转会话详情页面
	 */
	private void jumpConversationDetail() {
		// TODO Auto-generated method stub
		Intent intent = null;
		if (CategoryId.PERSONAL == mConversationInfo.getCategoryId()) {

		} else if (CategoryId.DISCUSSION == mConversationInfo.getCategoryId()) {
			intent = new Intent(this, DiscussionDetailActivity.class);
			intent.putExtra("conversation", mConversationInfo);
		} else if (CategoryId.GROUP == mConversationInfo.getCategoryId()) {
			
		}

		if (intent != null) {
			startActivity(intent);
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String path = null;
			Uri uri = data.getData();
			Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
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
