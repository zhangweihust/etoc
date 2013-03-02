package com.zhangwei.speakloudly.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.activity.MscActivity;
import com.zhangwei.speakloudly.utils.RuntimeLog;

import android.app.Activity;
//import android.app.Fragment;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PhoneNumFragment  extends Fragment {
	MscActivity mActivity;
	//OnGotoPageListener onGotoPageListener;
	
	//UI components
/*	private String airtelNumber;
	private AnimationDrawable loadingAnimation;
	Typeface typeFace; 
	Typeface typeFace1;
	private Button mTermTextView;
	private Button welcomeAcceptBtn;
	private RelativeLayout mRelativeLayout;
	private ImageView welcomeView; */
	private EditText mEdit;
	private TextView mError;
	private Button mNext;
	private AnimationDrawable verifyingPhoneNumberAnimation;

	public PhoneNumFragment() {
	}


	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RuntimeLog.log("PhoneNumFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view =  inflater.inflate(R.layout.phonenum_fragment, container, false);

/*		typeFace = Typeface.createFromAsset(mActivity.getAssets(),"font/Edmondsans-Regular.otf");
		typeFace1 = Typeface.createFromAsset(mActivity.getAssets(), "font/HelveticaNeue-Roman.otf");
*/
		mEdit = (EditText)view.findViewById(R.id.phonenum);
		mError = (TextView)view.findViewById(R.id.error);
		mNext = (Button)view.findViewById(R.id.next);
		
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		RuntimeLog.log("PhoneNumFragment - onActivityCreated");
		mNext.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// mSpeakText.setText(text);
				lockInput();
/*				if(validateNumber(phonenumber)){
					
				}*/
				mActivity.goToPage(MscActivity.SPEAK_LOUDLY_VIEW, true);
			}
		});
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

/*	@Override
	public void onSaveInstanceState(Bundle outState) {
	    //No call for super(). Bug on API Level > 11.
	}*/
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		RuntimeLog.log("PhoneNumFragment - onAttach");

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
		mNext.setText("");
		mNext.setClickable(false);			
		mNext.setBackgroundResource(R.anim.verifying_phonenumber);
		verifyingPhoneNumberAnimation = (AnimationDrawable)mNext.getBackground();
		verifyingPhoneNumberAnimation.start();
		
		mError.setVisibility(View.GONE);
		
	}
	
	public void unLockInput(String errMsg){
		if (verifyingPhoneNumberAnimation.isRunning()){
			verifyingPhoneNumberAnimation.stop();
		}
		
		if(errMsg!=null){
			mError.setText(errMsg);
			mError.setTextColor(getResources().getColor(R.color.red));
			mError.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void showBtns(String airtelNumber){
/*		this.airtelNumber = airtelNumber;*/
		//final Button signUpBtn =(Button)findViewById(R.id.mWelcomeSignUpBtn);
		//final Button signInBtn =(Button)findViewById(R.id.mWelcomeSignInBtn);
		//signUpBtn.setVisibility(View.VISIBLE);
		//signInBtn.setVisibility(View.VISIBLE);
	}
	
	private boolean validateNumber(String number){
    	//Pattern pattern=Pattern.compile("(^\\+91\\d{10}$|^0091\\d{10}$|^0\\d{10}$|^\\d{10}$)");
    	//Pattern pattern=Pattern.compile("(^\\d{10}$)");
    	Pattern pattern=Pattern.compile("(^13\\d{9}$|^15\\d{9}$|^16\\d{9}$|^18\\d{9}$)");
		Matcher matcher=pattern.matcher(number);
		if(!matcher.find()){
			//return "Your phone number should be 10-14 digits long, only numbers and \"+\".";
			return false; //"Your phone number should be 10 digits long, only numbers.";
		}
		return true;
	}
	
	

}
