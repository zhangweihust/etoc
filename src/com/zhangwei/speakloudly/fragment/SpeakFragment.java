package com.zhangwei.speakloudly.fragment;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.activity.MscActivity;
import com.zhangwei.speakloudly.utils.RuntimeLog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SpeakFragment extends Fragment {
	MscActivity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RuntimeLog.log("SpeakFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view =  inflater.inflate(R.layout.speak_fragment, container, false);
		//view.setId(0x7F04FFF0);
/*		typeFace = Typeface.createFromAsset(mActivity.getAssets(),"font/Edmondsans-Regular.otf");
		typeFace1 = Typeface.createFromAsset(mActivity.getAssets(), "font/HelveticaNeue-Roman.otf");
		mTermTextView = (Button)view.findViewById(R.id.mWelcomeTermOfUseImageView);
		welcomeAcceptBtn =(Button)view.findViewById(R.id.mWelcomeAcceptBtn);
		welcomeView = (ImageView)view.findViewById(R.id.login_welcome_image);*/
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		RuntimeLog.log("SpeakFragment - onActivityCreated");
/*		welcomeAcceptBtn.setTypeface(typeFace1);
		
		mTermTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mActivity.goToPage(mActivity.LOGIN_VIEW_TERMOFSERVICE, true);
				
			}
		});
	
		welcomeAcceptBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lockInput();
				mActivity.checkUser();

			}
		});*/
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		RuntimeLog.log("SpeakFragment - onAttach");

		try {
			mActivity =  (MscActivity)activity;
			//onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGotoPageListener");
		}
	}
	
	private void lockInput(){
/*		welcomeAcceptBtn.setText("");
		welcomeAcceptBtn.setClickable(false);			
		welcomeAcceptBtn.setBackgroundResource(R.anim.welcome_loading_process);
		loadingAnimation = (AnimationDrawable)welcomeAcceptBtn.getBackground();
		loadingAnimation.start();*/
		
	}
	
	public void unLockInput(){
/*		welcomeAcceptBtn.setText(R.string.accept);
		welcomeAcceptBtn.setClickable(true);
		welcomeAcceptBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_welcome_bg_btn));	
		if (loadingAnimation != null && loadingAnimation.isRunning()){
			loadingAnimation.stop();
		}*/
		
	}
	
	public void showBtns(String airtelNumber){
/*		this.airtelNumber = airtelNumber;*/
		//final Button signUpBtn =(Button)findViewById(R.id.mWelcomeSignUpBtn);
		//final Button signInBtn =(Button)findViewById(R.id.mWelcomeSignInBtn);
		//signUpBtn.setVisibility(View.VISIBLE);
		//signInBtn.setVisibility(View.VISIBLE);
	}
}