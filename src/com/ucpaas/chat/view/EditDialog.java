package com.ucpaas.chat.view;

import com.ucpaas.chat.R;
import com.ucpaas.chat.listener.CancleListener;
import com.ucpaas.chat.listener.ConfirmListener;
import com.ucpaas.chat.util.KeyBoardUtils;
import com.ucpaas.chat.util.LogUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 编辑对话框
 * 
 */
public class EditDialog extends Dialog {

	private TextView mTvTitle;
	private EditText mTvMessage;
	private Button mBtnCancle, mBtnConfirm;
	private String mTitle, mMessage;
	private ConfirmListener mConfirmListener;// 确定监听
	private CancleListener mCancleListener;// 取消监听
	private Context mContext;

	public EditDialog(Context context, String title, String message) {
		super(context, R.style.PopDialog);
		this.mContext = context;
		this.mTitle = title;
		this.mMessage = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit);

		initView();
		initListener();
	}

	private void initView() {
		mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
		mBtnCancle = (Button) findViewById(R.id.btn_cancle);
		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mTvMessage = (EditText) findViewById(R.id.et_message);

		mTvTitle.setText(mTitle);
		mTvMessage.setText(mMessage);

		// 编辑框焦点移到最后
		Editable ea = mTvMessage.getText();
		mTvMessage.setSelection(ea.length());
		mTvMessage.setSelectAllOnFocus(true);
		KeyBoardUtils.openKeybord(mTvMessage, mContext);
	}

	private void initListener() {

		mTvMessage.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					mBtnConfirm.setEnabled(false);

				} else {
					mBtnConfirm.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// 取消按钮
		mBtnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();

				if (mCancleListener != null) {
					mCancleListener.cancle(null);
				}
			}
		});

		// 确定按钮
		mBtnConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();

				if (mConfirmListener != null) {
					mConfirmListener.confirm(mTvMessage.getText().toString());
				}
			}
		});
	}

	/**
	 * 外部方法，设置确定监听器
	 * 
	 * @param dialogListener
	 */
	public void setConfirmListener(ConfirmListener confirmListener) {
		this.mConfirmListener = confirmListener;
	}

	/**
	 * 外部方法，设置取消监听器
	 * 
	 * @param dialogListener
	 */
	public void setCancleistener(CancleListener cancleListener) {
		this.mCancleListener = cancleListener;
	}

	/**
	 * 外部方法，显示对话框
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		LogUtil.log("width:"+width);
		getWindow().setLayout((int) (width * 9 / 10), LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 隐藏对话框
	 */
	@Override
	public void dismiss() {
		// TODO Auto-generated method stub

		// 隐藏输入法
		KeyBoardUtils.closeKeybord(mTvMessage, mContext);
		super.dismiss();
	}
}
