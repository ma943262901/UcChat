package com.ucpaas.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.activity.AboutActivity;
import com.ucpaas.chat.activity.FeedbackActivity;
import com.ucpaas.chat.base.BaseFragment;
import com.ucpaas.chat.support.SpOperation;

/**
 * 我
 * 
 * @author tangqi
 * @data 2015年8月9日上午11:08:36
 */

public class MeFragment extends BaseFragment implements OnClickListener {

	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_me, container, false);
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
		TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
		TextView tvUserId = (TextView) view.findViewById(R.id.tv_user_id);
		tvUserName.setText(SpOperation.getNickName(getActivity()));
		tvUserId.setText(SpOperation.getUserId(getActivity()));

		TextView mTitleView = (TextView) view.findViewById(R.id.tv_title);
		mTitleView.setText(R.string.user_center);

		LinearLayout mMeView01 = (LinearLayout) view.findViewById(R.id.ll_me_01);
		LinearLayout mMeView02 = (LinearLayout) view.findViewById(R.id.ll_me_02);
		LinearLayout mMeView03 = (LinearLayout) view.findViewById(R.id.ll_me_03);
		LinearLayout mMeView04 = (LinearLayout) view.findViewById(R.id.ll_me_04);
		LinearLayout mMeView05 = (LinearLayout) view.findViewById(R.id.ll_me_05);

		mMeView01.setOnClickListener(this);
		mMeView02.setOnClickListener(this);
		mMeView03.setOnClickListener(this);
		mMeView04.setOnClickListener(this);
		mMeView05.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		// 意见反馈
		case R.id.ll_me_03:
			feedback();
			break;

		// 关于
		case R.id.ll_me_04:
			about();
			break;

		default:
			break;
		}
	}

	/**
	 * 意见反馈
	 */
	private void feedback() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), FeedbackActivity.class);
		startActivity(intent);
	}

	/**
	 * 关于
	 */
	private void about() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getActivity(), AboutActivity.class);
		startActivity(intent);
	}
}
