package com.ucpaas.chat.adapter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.activity.ImageActivity;
import com.ucpaas.chat.util.ExpressionUtil;
import com.ucpaas.chat.util.ImageLoaderUtils;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.data.db.ChatMessage;

/**
 * 聊天记录Adapter
 * 
 * @author tangqi
 * @date 2015年9月4日上午12:31:28
 */
public class ConversationReplyAdapter extends BaseAdapter {

	private final int VIEW_TYPE_COUNT = 7;
	// private final int VIEW_TYPE_USER = 0;
	// private final int VIEW_TYPE_DEV = 1;
	private final int VIEW_TEXT_DAULT = 0;
	private final int VIEW_TEXT_L = 1;
	private final int VIEW_TEXT_R = 2;
	private final int VIEW_IMG_L = 3;
	private final int VIEW_IMG_R = 4;
	private final int VIEW_VOICE_L = 5;
	private final int VIEW_VOICE_R = 6;

	private List<ChatMessage> mList;
	private Context mContext;

	public ConversationReplyAdapter(Context context,
			List<ChatMessage> mChatMessages) {
		this.mList = mChatMessages;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public ChatMessage getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getViewTypeCount() {
		// 两种不同的Tiem布局
		return VIEW_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
		// 获取单条回复
		int type = 0;
		ChatMessage chatMessage = getItem(position);
		if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_TEXT
				|| chatMessage.getMsgType() == MSGTYPE.MSG_DATA_SYSTEM) {
			if (chatMessage.getIsFromMyself()) {
				// 开发者回复Item布局
				type = VIEW_TEXT_R;
			} else {
				// 用户反馈、回复Item布局
				type = VIEW_TEXT_L;
			}
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
			if (chatMessage.getIsFromMyself()) {
				// 开发者回复Item布局
				type = VIEW_IMG_R;
			} else {
				// 用户反馈、回复Item布局
				type = VIEW_IMG_L;
			}
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
			if (chatMessage.getIsFromMyself()) {
				// 开发者回复Item布局
				type = VIEW_VOICE_R;
			} else {
				// 用户反馈、回复Item布局
				type = VIEW_VOICE_L;
			}
		}
		return type;
	}

	private ViewHolder holder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatMessage chatMessage = getItem(position);
		convertView = initConvertView(position, convertView, chatMessage);

		setData(position, chatMessage);

		return convertView;
	}

	/**
	 * 
	 */
	private View initConvertView(int position, View convertView,
			ChatMessage chatMessage) {

		// 根据Type的类型来加载不同的Item布局
		if (convertView == null) {
			// 文本消息
			if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_TEXT
					|| chatMessage.getMsgType() == MSGTYPE.MSG_DATA_SYSTEM) {
				if (chatMessage.getIsFromMyself()) {
					// 本人发送
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_text_right, null);
				} else {
					// 他人回复
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_text_left, null);
				}
				// 图片消息
			} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
				if (chatMessage.getIsFromMyself()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_image_right, null);
				} else {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_image_left, null);
				}
				// 语音消息
			} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_VOICE) {
				if (chatMessage.getIsFromMyself()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_voice_right, null);
				} else {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_voice_left, null);
				}
			} else {
				if (chatMessage.getIsFromMyself()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_text_right, null);
				} else {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.conversation_text_left, null);
				}
			}

			// 创建ViewHolder并获取各种View
			holder = new ViewHolder();

			holder.replyContent = (TextView) convertView
					.findViewById(R.id.fb_reply_content);
			holder.replyProgressBar = (ProgressBar) convertView
					.findViewById(R.id.fb_reply_progressBar);
			holder.replyStateFailed = (ImageView) convertView
					.findViewById(R.id.fb_reply_state_failed);
			holder.replyData = (TextView) convertView
					.findViewById(R.id.fb_reply_date);

			// 图片
			holder.replyImgSmall = (ImageView) convertView
					.findViewById(R.id.img_small_pic);

			// 语音
			holder.imgVoiceSrc = (ImageView) convertView
					.findViewById(R.id.img_voice);
			holder.txvVoiceTime = (TextView) convertView
					.findViewById(R.id.tv_voice_time);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	/**
	 * 
	 * @param position
	 */
	private boolean playStatus = false;
	private MediaPlayer mMediaPlayer;

	private void setData(int position, ChatMessage chatMessage) {

		// 文本消息
		if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_TEXT
				|| chatMessage.getMsgType() == MSGTYPE.MSG_DATA_SYSTEM) {

			// 填充表情
			SpannableString span = ExpressionUtil.getInstace()
					.getExpressionString(mContext, chatMessage.getContent());
			holder.replyContent.setText(span);

			// 图片消息
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
			ImageLoaderUtils.displaySdcardImg(chatMessage.getContent(),
					holder.replyImgSmall);

			final ChatMessage data = chatMessage;
			holder.replyImgSmall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ImageActivity.class);
					intent.putExtra("chatMessage", data);
					mContext.startActivity(intent);
				}
			});
			// 语音消息
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_VOICE) {
			String str = chatMessage.getContent();
			holder.txvVoiceTime.setText(chatMessage.getContent() + "\"");
			playRecord(chatMessage);
		} else {
			// holder.replyContent.setText(chatMessage.getContent());
		}

		// 在App应用界面，对于开发者的Reply来讲status没有意义
		if (!chatMessage.getIsFromMyself()) {
			// 根据Reply的状态来设置replyStateFailed的状态
			// if (Reply.STATUS_NOT_SENT.equals(reply.status)) {
			// holder.replyStateFailed.setVisibility(View.VISIBLE);
			// } else {
			// holder.replyStateFailed.setVisibility(View.GONE);
			// }
			//
			// // 根据Reply的状态来设置replyProgressBar的状态
			// if (Reply.STATUS_SENDING.equals(reply.status)) {
			// holder.replyProgressBar.setVisibility(View.VISIBLE);
			// } else {
			// holder.replyProgressBar.setVisibility(View.GONE);
			// }
		}

		// 回复的时间数据，这里仿照QQ两条Reply之间相差100000ms则展示时间
		if ((position + 1) < getCount()) {
			ChatMessage nextChatMessage = getItem(position + 1);
			if (nextChatMessage.getSendTime() - chatMessage.getSendTime() > 300000) {
				Date replyTime = new Date(chatMessage.getSendTime());
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
				holder.replyData.setText(sdf.format(replyTime));
				holder.replyData.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 
	 */
	private AnimationDrawable mAnitmation;

	private void playRecord(final ChatMessage chatMessage) {
		// 播放语音
		final ChatMessage data = chatMessage;
		holder.imgVoiceSrc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!playStatus) {
					mMediaPlayer = new MediaPlayer();
					try {
						mMediaPlayer.setDataSource(data.getPath());
						mMediaPlayer.prepare();
						playStatus = true;
						mMediaPlayer.start();
						final View tmpView = v;
						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										if (playStatus) {
											playStatus = false;
										}
										if (mAnitmation != null
												&& mAnitmation.isRunning()) {
											mAnitmation.stop();
										}
										if (data.getIsFromMyself()) {
											tmpView.setBackgroundResource(R.drawable.right_audio3);
										} else {
											tmpView.setBackgroundResource(R.drawable.left_audio3);
										}
									}
								});
						// 设置动画

						if (data.getIsFromMyself()) {
							v.setBackgroundResource(R.anim.im_right_voice);
						} else {
							v.setBackgroundResource(R.anim.im_left_voice);
						}
						mAnitmation = (AnimationDrawable) v.getBackground();
						mAnitmation.setOneShot(false);
						if (!mAnitmation.isRunning()) {
							mAnitmation.start();
						}

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if (mMediaPlayer.isPlaying()) {
						mMediaPlayer.stop();
						playStatus = false;
					} else {
						playStatus = false;
					}
					if (mAnitmation != null && mAnitmation.isRunning()) {
						mAnitmation.stop();
					}
				}
			}
		});
	}

	class ViewHolder {
		TextView replyContent;
		ProgressBar replyProgressBar;
		ImageView replyStateFailed, replyImgSmall;
		TextView replyData;

		// voice
		ImageView imgVoiceSrc;
		TextView txvVoiceTime;

	}
}
