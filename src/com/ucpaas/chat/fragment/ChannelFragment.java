package com.ucpaas.chat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseFragment;

/**
 * 通信录
 * 
 * @author tangqi
 * @data 2015年8月9日上午11:07:09
 */

public class ChannelFragment extends BaseFragment{

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_channel, container, false);
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
		mTitleView.setText("通讯录");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
