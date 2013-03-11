package com.zhangwei.speakloudly.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.activity.MscActivity;
import com.zhangwei.speakloudly.client.Callback;
import com.zhangwei.speakloudly.client.ResponseWrapper;
import com.zhangwei.speakloudly.client.UnicomClient;
import com.zhangwei.speakloudly.client.ServerSideErrorMsg;
import com.zhangwei.speakloudly.utils.RuntimeLog;

import android.app.Activity;
//import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneNumFragment extends Fragment {
	MscActivity mActivity;

	// UI components
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
		View view = inflater.inflate(R.layout.phonenum_fragment, container,
				false);

		mEdit = (EditText) view.findViewById(R.id.phonenum);
		mError = (TextView) view.findViewById(R.id.error);
		mNext = (Button) view.findViewById(R.id.next);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RuntimeLog.log("PhoneNumFragment - onActivityCreated");
		mNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// mSpeakText.setText(text);
				String phoneNum = mEdit.getText().toString();
				if (validateNumber(phoneNum)) {
					// seems ok, then verify phone number through unicom server
					mActivity.mHandler
							.sendEmptyMessage(MscActivity.VERIFYING_PHONENUM);

					// set the pull pin time out event
					Message msg = new Message();
					msg.what = MscActivity.STOP_VFY_PHONENUM;
					msg.obj = "timeout";
					mActivity.mHandler.sendMessageDelayed(msg,
							MscActivity.TIMEOUT_DELAY);

					UnicomClient.performVerfiyNum(null, mActivity.mHandler,
							new Callback() {
								public void call(ResponseWrapper resp) {

									mActivity.handleNetworkError(resp);
									// if(resp!=null && resp.isValid() &&
									// resp.isNoError()){
									if (true) {

										mActivity.mHandler
												.removeMessages(MscActivity.STOP_VFY_PHONENUM);
										mActivity.goToPage(
												MscActivity.SPEAK_LOUDLY_VIEW,
												true);

									} else {
										if (resp != null) {
											String errMsg = ServerSideErrorMsg
													.getMsg(resp.getStatus());
											if (errMsg != null) {
												unLockInput(errMsg);
											}
										}
										mActivity.mHandler
												.removeMessages(MscActivity.STOP_VFY_PHONENUM);
										Message msg = new Message();
										msg.what = MscActivity.STOP_VFY_PHONENUM;
										msg.obj = "error";
										mActivity.mHandler.sendMessageDelayed(
												msg, MscActivity.RUNTIME_DELAY);
										// unLockInput(null);
									}

								}
							});

				} else {
					unLockInput(phoneNum + "不是一个正确的手机号码");
				}

			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		RuntimeLog.log("PhoneNumFragment - onAttach");

		try {
			mActivity = (MscActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGotoPageListener");
		}
	}

	public void LockInput() {
		RuntimeLog.log("PhoneNumFragment - LockInput - IN");
		mNext.setText("");
		mNext.setClickable(false);
		mNext.setBackgroundResource(R.anim.verifying_phonenumber);
		verifyingPhoneNumberAnimation = (AnimationDrawable) mNext
				.getBackground();
		verifyingPhoneNumberAnimation.start();

		mError.setVisibility(View.GONE);

	}

	public void unLockInput(String errMsg) {
		RuntimeLog.log("PhoneNumFragment - unLockInput - IN, errMsg:" + errMsg);
		if ((verifyingPhoneNumberAnimation != null)
				&& verifyingPhoneNumberAnimation.isRunning()) {
			verifyingPhoneNumberAnimation.stop();
		}
		mNext.setText(R.string.next);
		mNext.setClickable(true);
		mNext.setBackgroundResource(R.drawable.btn_shape_white_rec);

		if (errMsg != null) {
			mError.setText(errMsg);
			mError.setTextColor(getResources().getColor(R.color.red));
			mError.setVisibility(View.VISIBLE);
		}

	}

	private boolean validateNumber(String number) {
		// Pattern
		// pattern=Pattern.compile("(^\\+91\\d{10}$|^0091\\d{10}$|^0\\d{10}$|^\\d{10}$)");
		// Pattern pattern=Pattern.compile("(^\\d{10}$)");
		Pattern pattern = Pattern
				.compile("(^13\\d{9}$|^15\\d{9}$|^16\\d{9}$|^18\\d{9}$)");
		Matcher matcher = pattern.matcher(number);
		if (!matcher.find()) {
			return false;
		}
		return true;
	}

}
