package com.ucpaas.chat.base;

import com.ucpaas.chat.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Fragment-基类
 * 
 * @author tangqi
 * @data 2015年8月9日上午10:28:46
 */

public class BaseFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 隐藏返回按钮
	 */
	protected void hideBackButton(View view) {
		try {
			ImageView btnBack = (ImageView) view.findViewById(R.id.btn_back);
			ImageView imvDivider = (ImageView) view.findViewById(R.id.imv_actionbar_divider);

			btnBack.setVisibility(View.GONE);
			imvDivider.setVisibility(View.GONE);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
