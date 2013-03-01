/*
 * Copyright (C) 2009-2010, iFLYTEK Ltd.
 *
 * All rights reserved.
 *
 */
package com.zhangwei.speakloudly.activity;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.fragment.PhoneNumFragment;
import com.zhangwei.speakloudly.fragment.SpeakFragment;
import com.zhangwei.speakloudly.utils.RuntimeLog;

/**
 * @author zhangwei
 */
public class MscActivity extends FragmentActivity {

	public final int PHONE_NUM_VIEW = 0; // to welcome
	public final int SPEAK_LOUDLY_VIEW = 1; //

	private Toast mToast;

	private FragmentManager fm;
	private SpeakFragment spkFrag;
	private PhoneNumFragment phoneFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.msc_layout);

		spkFrag = new SpeakFragment();
		phoneFrag = new PhoneNumFragment();
		fm = getSupportFragmentManager();

		goToPage(PHONE_NUM_VIEW, false);
	}

	public void goToPage(int type, boolean record) {
		Fragment dst;
		String mViewName;
		dst = phoneFrag;
		if (type == PHONE_NUM_VIEW) {

			dst = phoneFrag;
			mViewName = "phoneNum";

		} else if (type == SPEAK_LOUDLY_VIEW) {

			dst = spkFrag;
			mViewName = "speakloudly";

		} else {

			dst = phoneFrag;
			mViewName = "phoneNum";
			RuntimeLog
					.log("type is not supported yet, just use phoneNum! type:"
							+ type);
		}

		FragmentTransaction ft = fm.beginTransaction();

		// check login_container if null
		if (fm.findFragmentById(R.id.msc_container) != null) {
			RuntimeLog.log("replace login_container dst:" + dst.toString());
			ft.replace(R.id.msc_container, dst);

		} else {
			RuntimeLog.log("add login_container dst:" + dst.toString());
			ft.add(R.id.msc_container, dst);
			// ft.replace(R.id.login_container, dst);
		}

		if (record) {
			ft.addToBackStack(null);
		}

		ft.commit();
		// ft.commitAllowingStateLoss();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}