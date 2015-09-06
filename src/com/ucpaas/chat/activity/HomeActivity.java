package com.ucpaas.chat.activity;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseFragmentActivity;
import com.ucpaas.chat.fragment.ContactFragment;
/**
 * 主页
 * 
 * @author tangqi
 * @data 2015年7月8日下午9:20:20
 *
 */
import com.ucpaas.chat.fragment.ConversationListFragment;
import com.ucpaas.chat.fragment.MeFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

/**
 * 首页
 * 
 * @author smile
 * 
 */
public class HomeActivity extends BaseFragmentActivity implements OnCheckedChangeListener {

	private RadioGroup mGroup;
	private ConversationListFragment mFirstFragment;
	private ContactFragment mSecondFragment;
	// private FindFragment mThirdFragment;
	private MeFragment mFourthFragment;

	private String mFormerTag;
	private final static String FIRST_TAG = "FirstFragment";
	private final static String SECOND_TAG = "SecondFragment";
	// private final static String THIRD_TAG = "ThirdFragment";
	private final static String FOURTH_TAG = "FourthFragment";

	private long exitTime;
	private final static long TIME_DIFF = 2 * 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mGroup = (RadioGroup) findViewById(R.id.main_radio);
		mFirstFragment = new ConversationListFragment();
		mSecondFragment = new ContactFragment();
		// mThirdFragment = new FindFragment();
		mFourthFragment = new MeFragment();
		mFormerTag = FIRST_TAG;
		getSupportFragmentManager().beginTransaction().add(R.id.main_content, mFirstFragment, FIRST_TAG).commit();

		mGroup.setOnCheckedChangeListener(this);

		hideBackButton();
	}

	/**
	 * 首页导航切换
	 */
	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
		mTransaction.hide(getSupportFragmentManager().findFragmentByTag(mFormerTag));

		switch (checkedId) {

		// 消息
		case R.id.radiobutton_blogger:
			mFormerTag = FIRST_TAG;
			if (mFirstFragment.isAdded()) {
				mTransaction.show(mFirstFragment).commit();
			} else {
				mTransaction.add(R.id.main_content, mFirstFragment, mFormerTag).commit();
			}
			break;

		// 通信录
		case R.id.radiobutton_channel:
			mFormerTag = SECOND_TAG;
			if (mSecondFragment.isAdded()) {
				mTransaction.show(mSecondFragment).commit();
			} else {
				mTransaction.add(R.id.main_content, mSecondFragment, mFormerTag).commit();
			}
			break;

		// 发现
		case R.id.radiobutton_find:
			// mFormerTag = THIRD_TAG;
			// if (mThirdFragment.isAdded()) {
			// mTransaction.show(mThirdFragment).commit();
			// } else {
			// mTransaction.add(R.id.main_content, mThirdFragment,
			// mFormerTag).commit();
			// }
			break;

		// 我
		case R.id.radiobutton_me:
			mFormerTag = FOURTH_TAG;
			if (mFourthFragment.isAdded()) {
				mTransaction.show(mFourthFragment).commit();
			} else {
				mTransaction.add(R.id.main_content, mFourthFragment, mFormerTag).commit();
			}
			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if ((System.currentTimeMillis() - exitTime) > TIME_DIFF) {
				Toast.makeText(HomeActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1000) {
			if (resultCode == Activity.RESULT_OK) {
				String titleName = data.getExtras().getString("ConversationTitle");
				mFirstFragment.refreshData(titleName);
			}

		}
	}

	
}
