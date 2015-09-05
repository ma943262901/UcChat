package com.ucpaas.chat.db.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.ucpaas.chat.bean.UserInfo;
import com.ucpaas.chat.db.ContactDb;
import com.ucpaas.chat.support.CacheManager;

/**
 * 联系人数据库
 * 
 * @author tangqi
 * @date 2015年9月5日下午6:37:32
 */

public class ContactDbImpl implements ContactDb {

	private DbUtils db;

	public ContactDbImpl(Context context) {
		// TODO Auto-generated constructor stub
		db = DbUtils.create(context, CacheManager.getFriendDbPath(context), "contact");
	}

	@Override
	public void insert(UserInfo userInfo) {
		// TODO Auto-generated method stub
		try {
			UserInfo findItem = db.findFirst(Selector.from(UserInfo.class).where("phone", "=", userInfo.getPhone()));
			if (findItem != null) {
				db.update(userInfo, WhereBuilder.b("phone", "=", userInfo.getPhone()));
			} else {
				db.save(userInfo);
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void insert(List<UserInfo> userInfoList) {
		if (userInfoList != null) {
			for (int i = 0; i < userInfoList.size(); i++) {
				UserInfo userInfo = userInfoList.get(i);
				insert(userInfo);
			}
		}
	}

	@Override
	public List<UserInfo> queryAll() {
		// TODO Auto-generated method stub
		try {
			return db.findAll(Selector.from(UserInfo.class).orderBy("id", true));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<UserInfo>();
	}

}
