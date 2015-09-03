package com.ucpaas.chat.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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

	private final int VIEW_TYPE_COUNT = 2;
	private final int VIEW_TYPE_USER = 0;
	private final int VIEW_TYPE_DEV = 1;

	private List<ChatMessage> mList;
	private Context mContext;

	public ConversationReplyAdapter(Context context, List<ChatMessage> mChatMessages) {
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
		ChatMessage chatMessage = getItem(position);
		if (chatMessage.getIsFromMyself()) {
			// 开发者回复Item布局
			return VIEW_TYPE_DEV;
		} else {
			// 用户反馈、回复Item布局
			return VIEW_TYPE_USER;
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// 获取单条回复
		final ChatMessage chatMessage = getItem(position);
		if (convertView == null) {
			// 根据Type的类型来加载不同的Item布局

			if (chatMessage.getIsFromMyself()) {
				// 开发者的回复
				convertView = LayoutInflater.from(mContext).inflate(R.layout.umeng_fb_custom_dev_reply, null);
			} else {
				// 用户的反馈、回复
				convertView = LayoutInflater.from(mContext).inflate(R.layout.umeng_fb_custom_user_reply, null);
			}

			// 创建ViewHolder并获取各种View
			holder = new ViewHolder();
			holder.replyContent = (TextView) convertView.findViewById(R.id.fb_reply_content);
			holder.replyImgSmall = (ImageView) convertView.findViewById(R.id.img_small_pic);
			holder.replyProgressBar = (ProgressBar) convertView.findViewById(R.id.fb_reply_progressBar);
			holder.replyStateFailed = (ImageView) convertView.findViewById(R.id.fb_reply_state_failed);
			holder.replyData = (TextView) convertView.findViewById(R.id.fb_reply_date);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 以下是填充数据
		if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_TEXT) {
			holder.replyContent.setText(chatMessage.getContent());
			holder.replyContent.setVisibility(View.VISIBLE);
			holder.replyImgSmall.setVisibility(View.GONE);
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_IMAGE) {
			holder.replyContent.setText(chatMessage.getContent());
			holder.replyContent.setVisibility(View.GONE);
			holder.replyImgSmall.setVisibility(View.VISIBLE);
			ImageLoaderUtils.displaySdcardImg(chatMessage.getContent(), holder.replyImgSmall);

			holder.replyImgSmall.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, ImageActivity.class);
					intent.putExtra("url", chatMessage.getPath());
					mContext.startActivity(intent);
				}
			});
		} else if (chatMessage.getMsgType() == MSGTYPE.MSG_DATA_VOICE) {
			holder.replyContent.setText(chatMessage.getContent());
			holder.replyContent.setVisibility(View.VISIBLE);
			holder.replyImgSmall.setVisibility(View.GONE);
		} else {
			holder.replyContent.setText(chatMessage.getContent());
			holder.replyContent.setVisibility(View.VISIBLE);
			holder.replyImgSmall.setVisibility(View.GONE);
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
		return convertView;
	}

	class ViewHolder {
		TextView replyContent;
		ProgressBar replyProgressBar;
		ImageView replyStateFailed, replyImgSmall;
		TextView replyData;
	}
}
