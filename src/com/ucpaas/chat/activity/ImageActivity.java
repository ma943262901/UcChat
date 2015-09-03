package com.ucpaas.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.polites.android.GestureImageView;
import com.ucpaas.chat.R;
import com.ucpaas.chat.util.FileUtils;
import com.ucpaas.chat.util.HttpUtil;
import com.ucpaas.chat.util.ImageLoaderUtils;
import com.ucpaas.chat.util.LogUtil;
import com.ucpaas.chat.util.ToastUtil;
import com.yzxIM.data.db.ChatMessage;

/**
 * 2014/8/13 显示图片的界面
 * 
 * @author wwj_748
 * 
 */
@SuppressLint("NewApi")
public class ImageActivity extends Activity {

	private String url; // 图片地址
	private ChatMessage chatMessage; // 图片地址

	private GestureImageView imageView; // 图片组件
	private ProgressBar progressBar; // 进度条

	private ImageView backBtn; // 回退按钮
	private ImageView downLoadBtn; // 下载按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_image_page);

		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("url", "");
		chatMessage = (ChatMessage) getIntent().getParcelableExtra("chatMessage");
		LogUtil.log("url:" + url);
		LogUtil.log("chatMessage:" + chatMessage);

		imageView = (GestureImageView) findViewById(R.id.image);
		progressBar = (ProgressBar) findViewById(R.id.loading);

		backBtn = (ImageView) findViewById(R.id.back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		downLoadBtn = (ImageView) findViewById(R.id.download);
		downLoadBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImage();
			}
		});

		if (chatMessage != null) {
			url = chatMessage.getPath();
			if (url.contains("http")) {
				// 消息接收方从网络取图片
				new MainTask().execute(url);
			} else {
				// 消息发送方从本地取图片
				ImageLoaderUtils.displaySdcardImg(chatMessage.getPath(), imageView);
				progressBar.setVisibility(View.GONE);
			}
		} else {
			// 普通下载
			new MainTask().execute(url);
		}

		// loadImage();
	}

	private void saveImage() {
		imageView.setDrawingCacheEnabled(true);
		if (FileUtils.writeSDCard(FileUtils.getExternalCacheDir(ImageActivity.this), url, imageView.getDrawingCache())) {
			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT).show();
		}
		imageView.setDrawingCacheEnabled(false);
	}

	private void loadImage() {
		// TODO Auto-generated method stub
		ImageLoaderUtils.displayImg(url, imageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				loadingStarted();
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				// TODO Auto-generated method stub
				loadingFinish();
				ToastUtil.show(ImageActivity.this, "加载失败");
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				loadingFinish();
				// ToastUtil.show(ImageActivity.this, "加载完成");
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				// TODO Auto-generated method stub
				loadingFinish();
				ToastUtil.show(ImageActivity.this, "取消加载");
			}
		});
	}

	private void loadingStarted() {
		progressBar.setVisibility(View.VISIBLE);
	}

	private void loadingFinish() {
		progressBar.setVisibility(View.GONE);
	}

	private class MainTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap temp = HttpUtil.HttpGetBmp(params[0]);
			return temp;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				Toast.makeText(ImageActivity.this, "网络信号不佳", Toast.LENGTH_LONG).show();
				ImageLoaderUtils.displayImg(url, imageView);
			} else {
				imageView.setImageBitmap(result);
			}
			progressBar.setVisibility(View.GONE);
			super.onPostExecute(result);
		}
	}
}
