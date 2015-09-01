package com.ucpaas.chat.activity;

import java.util.ArrayList;
import java.util.List;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ucpaas.chat.R;
import com.ucpaas.chat.base.BaseActivity;
import com.ucpaas.chat.util.ToastUtil;

/**
 * 接口测试
 * 
 * @author tangqi
 * @date 2015年9月1日下午10:39:18
 */

public class TestApiActivity extends BaseActivity implements OnItemClickListener {

	private ListView mListView;
	private long mExitTime;
	private final static long TIME_DIFF = 2 * 1000;

	@Override
	public void setContentLayout() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_test_api);
	}

	@Override
	public void beforeInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		mListView = (ListView) findViewById(R.id.lv_test_api);
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData()));
	}

	private List<String> getData() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("创建群组");
		list.add("加入群组");
		list.add("退出群组");
		list.add("查询群组");
		return list;
	}

	@Override
	public void afterInitView() {
		// TODO Auto-generated method stub
		setTitle("接口测试");
	}

	@Override
	public void onViewClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		ToastUtil.show(TestApiActivity.this, "position:" + position);
		switch (position) {
		case 0:

			break;

		default:
			break;
		}
	}

	/**
	 * 再按一次退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > TIME_DIFF) {
				ToastUtil.show(TestApiActivity.this, "再按一次退出程序");
				mExitTime = System.currentTimeMillis();
			} else {
				System.exit(0);
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

}
