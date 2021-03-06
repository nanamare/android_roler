package com.buttering.roler.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.FirebaseApp;

/**
 * Created by kinamare on 2016-12-17.
 */

public class MyApplication extends Application {

	public static final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication sInstance;

	public static synchronized MyApplication getInstance() {
		if (sInstance != null)
			return sInstance;
		return null;
	}

	public Context getContext() {
		return this.getApplicationContext();
	}


	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);

		MultiDex.install(this);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
		FirebaseApp.initializeApp(this);

	}


}
