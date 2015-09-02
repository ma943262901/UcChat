package com.ucpaas.chat.base;

import java.util.ArrayList;
import java.util.List;

import com.yzxIM.IMManager;
import com.yzxtcp.UCSManager;
import com.yzxtcp.listener.ILoginListener;

import android.app.Activity;
import android.app.Application;

/**
 * 应用Application类
 * 
 * @author tangqi
 * @data 2015年7月8日下午11:47:10
 */

public class BaseApplication extends Application {

	private static BaseApplication mInstance;
	private List<Activity> mActivities = new ArrayList<Activity>();

	/**
	 * 单例模式中获取唯一的Application实例
	 * 
	 * @return
	 */
	public static BaseApplication getInstance() {
		if (null == mInstance) {
			mInstance = new BaseApplication();
		}
		return mInstance;

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// TODO Auto-generated method stub
		initUCService();
		initImageLoader();
		initCrashReport();
	}

	/**
	 * 初始化云之讯服务
	 */
	private void initUCService() {
		UCSManager.init(this);// 初始化核心服务
		IMManager.getInstance(this);// 必须要加上
	}

	/**
	 * 登录UCS服务器
	 * 
	 * @param token
	 * @param loginListener
	 */
	public void connectUCSManager(String token, ILoginListener loginListener) {
		UCSManager.connect(token, loginListener);
	}

	/**
	 * 注销登录UCS服务器
	 */
	public void disconnectUCSManager() {
		UCSManager.disconnect();
	}

	/**
	 * 初始化ImageLoader
	 */
	private void initImageLoader() {

	}

	/**
	 * 初始化CrashHandler(保存在本地)
	 */
	@SuppressWarnings("unused")
	private void initCrashHandler() {

	}

	/**
	 * 初始化崩溃上传(腾讯BUGLY)
	 */
	private void initCrashReport() {
	}

	/**
	 * 把Activity加入历史堆栈
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		mActivities.add(activity);
	}

	/**
	 * 结束
	 */
	@Override
	public void onTerminate() {
		super.onTerminate();

		for (Activity activity : mActivities) {
			activity.finish();
		}

		System.exit(0);
	}
}
