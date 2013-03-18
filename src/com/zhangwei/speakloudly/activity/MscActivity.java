/*
 * Copyright (C) 2009-2010, iFLYTEK Ltd.
 *
 * All rights reserved.
 *
 */
package com.zhangwei.speakloudly.activity;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;
import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.client.ResponseWrapper;
import com.zhangwei.speakloudly.fragment.PaymentFragment;
import com.zhangwei.speakloudly.fragment.PaymentFragment.PaymentFragmentNotify;
import com.zhangwei.speakloudly.fragment.PhoneNumFragment;
import com.zhangwei.speakloudly.fragment.PhoneNumFragment.PhoneNumFragmentNotify;
import com.zhangwei.speakloudly.fragment.SpeakFragment;
import com.zhangwei.speakloudly.fragment.SpeakFragment.SpeakFragmentNotify;
import com.zhangwei.speakloudly.utils.RuntimeLog;

/**
 * @author zhangwei
 */
public class MscActivity extends FragmentActivity 
		implements PhoneNumFragmentNotify, SpeakFragmentNotify, PaymentFragmentNotify {

	// fragment types:
	public final static int SPEAK_LOUDLY_VIEW = 0; // to welcome
	public final static int PHONE_NUM_VIEW = 1; //
	public final static int PAYMENT_VIEW = 2; //

	// handler status:
	public static final int LOGINING_UNICOM = 0;
	public static final int UNICOM_TIMEOUT = 1;
	
	public static final int LOGINING_IFLY = 2;
	public static final int IFLY_TIMEOUT = 3;
	
	public static final int VERIFYING_PHONENUM = 4;
	public static final int STOP_VFY_PHONENUM = 5;
	
	public static final int PAYING_PHONENUM = 6;
	public static final int PAYING_TIMEOUT = 7;
	
	public static final int TIMEOUT_DELAY = 60*1000;
	public static final int RUNTIME_DELAY = 10000;
	
	private final String APP_ID = "512c57b1";

	private Toast mToast;

	private FragmentManager fm;
	private SpeakFragment spkFrag;
	private PhoneNumFragment phoneFrag;
	private PaymentFragment paymentFrag;

	private MyHandler mHandler;
	
	private SynthesizerPlayer player;
	private SynthesizerPlayerListener synbgListener;

	/**
	 * @author zhangwei This Handler class should be static or leaks might occur
	 */

	public static class MyHandler extends Handler {

		WeakReference<MscActivity> wActivity;

		MyHandler(MscActivity activity) {
			wActivity = new WeakReference<MscActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MscActivity theActivity = wActivity.get();

			if (theActivity == null) {
				RuntimeLog.log("handleMessage - theActivity null");
				return;
			}

			// super.handleMessage(msg);
			RuntimeLog.log("handleMessage - msg.what:" + msg.what);
			switch (msg.what) {
				case LOGINING_UNICOM:
					
					break;
				case UNICOM_TIMEOUT:
					
					break;
				case LOGINING_IFLY:
					break;
				case PAYING_PHONENUM:
					break;
	            default:
	            	RuntimeLog.log("case default - msg.what:" + msg.what);
	            	break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msc_layout);

		spkFrag = new SpeakFragment();
		phoneFrag = new PhoneNumFragment();
		paymentFrag = new PaymentFragment();
		fm = getSupportFragmentManager();
		
		mHandler = new MyHandler(this);
		player = SynthesizerPlayer.createSynthesizerPlayer(this, "appid=" + APP_ID);
		synbgListener = new SynthesizerPlayerListener (){
			@Override
			public void onPlayBegin(){
			// 播放开始回调，表示已获得第一块缓冲区音频开始播放
			}
			
			@Override
			public void onBufferPercent(int percent,int beginPos,int endPos){
			// 缓冲回调，通知当前缓冲进度
			}
			
			@Override
			public void onPlayPaused(){
			// 暂停通知，表示缓冲数据播放完成，后续的音频数据正在获取
			}
			
			@Override
			public void onPlayResumed(){
			// 暂停通知后重新获取到后续音频，表示重新开始播放
			}
			
			@Override
			public void onPlayPercent(int percent,int beginPos,int endPos){
			// 播放回调，通知当前播放进度
			}
			
			@Override
			public void onEnd(SpeechError error){
			}
		};
		
		
		
		goToPage(SPEAK_LOUDLY_VIEW, false);
		speak(R.string.instruction);
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

		} else if (type == PAYMENT_VIEW) {

			dst = paymentFrag;
			mViewName = "payment";

	    }else {

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
	
	@Override
	public 	void phoneNumfragmentDone(int status){
		speak(R.string.instruction_payment);
		goToPage(MscActivity.PAYMENT_VIEW, true);

	}
	
	@Override
	public 	void SpeakFragmentDone(int status){
		speak(R.string.instruction_phonenum);
		goToPage(MscActivity.PHONE_NUM_VIEW, true);

	}
	
	@Override
	public void PaymentfragmentDone(int status) {
		// TODO Auto-generated method stub
		speak(R.string.instruction);
		goToPage(MscActivity.SPEAK_LOUDLY_VIEW, true);

		
	}
	
	public void onNext(View v){
		if(phoneFrag!=null){
			phoneFrag.onNext(v);
		}
		
	}
	
	public void onBack(View v){
		if(paymentFrag!=null){
			paymentFrag.onBack(v);
		}
		
	}
	
	public void handleNetworkError(ResponseWrapper resp){
		if(resp==null){
			Toast.makeText(this,"Network Problem!", Toast.LENGTH_LONG).show();
		}
	}
	
	
	public void speak(int ResID){
		
		player.setVoiceName("xiaoyan");
		player.playText(getString(ResID), "tts_buffer_time=2000",  synbgListener);
	}
	
	@Override
	public void SpeakFragmentSpeak(int resID){
		speak(resID);
	}


}