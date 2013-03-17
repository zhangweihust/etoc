package com.zhangwei.speakloudly.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.client.ResponseWrapper;
import com.zhangwei.speakloudly.client.UnicomClient;
import com.zhangwei.speakloudly.client.ServerSideErrorMsg;
import com.zhangwei.speakloudly.utils.RuntimeLog;

import android.app.Activity;
import android.content.Context;
//import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PhoneNumFragment extends Fragment {

	public static final int VERIFYING_PHONENUM = 4;
	public static final int STOP_VFY_PHONENUM = 5;

	public static final int TIMEOUT_DELAY = 60 * 1000;
	public static final int RUNTIME_DELAY = 10000;

	private Activity mActivity;
	private MyHandler mHandler;
	private PhoneNumFragmentNotify mPhoneNumFragmentNotify;
	
	// UI components
	private EditText mEdit;
	private TextView mError;
	private Button mNext;
	private AnimationDrawable verifyingPhoneNumberAnimation;

	public PhoneNumFragment() {
	}
	
	public interface PhoneNumFragmentNotify{
		void phoneNumfragmentDone(int status);
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
		/* mNext.setOnClickListener(new OnClickListener() {}); */

		mHandler = new MyHandler(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		RuntimeLog.log("PhoneNumFragment - onAttach");

		try {
			mActivity = (Activity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
			mPhoneNumFragmentNotify = (PhoneNumFragmentNotify) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGotoPageListener");
		}
	}

	public static class MyHandler extends Handler {

		WeakReference<PhoneNumFragment> wFrag;

		MyHandler(PhoneNumFragment frag) {
			wFrag = new WeakReference<PhoneNumFragment>(frag);
		}

		@Override
		public void handleMessage(Message msg) {
			PhoneNumFragment theFrag = wFrag.get();

			if (theFrag == null) {
				RuntimeLog.log("handleMessage - theFrag null");
				return;
			}

			// super.handleMessage(msg);
			RuntimeLog.log("PhoneNumFragment handleMessage - msg.what:"
					+ msg.what);
			switch (msg.what) {
			case VERIFYING_PHONENUM:
				theFrag.LockInput();
				break;
			case STOP_VFY_PHONENUM:
				if ((String) (msg.obj) == "error") {
					theFrag.unLockInput("手机号校验错误");
				} else {
					theFrag.unLockInput("手机号校验超时");
				}

				break;
			default:
				RuntimeLog.log("case default - msg.what:" + msg.what);
				break;
			}

		}
	};

	public class PaymentTask extends AsyncTask<String, Void, ResponseWrapper> {

		@Override
		protected ResponseWrapper doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<Header> headers = new ArrayList<Header>();
			String phoneNum = params[0];

			//give the phoneNum to server
			final ResponseWrapper resp = UnicomClient.sendGetRequest(UnicomClient.VFY_PHONENUM_URL,
					null, headers);
			
			return resp;
		}
		
		@Override
		protected void onPostExecute(ResponseWrapper result) {

			//change UI according to the status
			
			
			handleNetworkError(mActivity, result);
			// if(resp!=null && resp.isValid() && resp.isNoError()){
			if (true) {

				mHandler.removeMessages(PhoneNumFragment.STOP_VFY_PHONENUM);
				mPhoneNumFragmentNotify.phoneNumfragmentDone(0);//notify activity to do next
				

			} else {
				if (result != null) {
					String errMsg = ServerSideErrorMsg.getMsg(result
							.getStatus());
					if (errMsg != null) {
						unLockInput(errMsg);
					}
				}
				mHandler.removeMessages(PhoneNumFragment.STOP_VFY_PHONENUM);
				Message msg = new Message();
				msg.what = PhoneNumFragment.STOP_VFY_PHONENUM;
				msg.obj = "error";
				mHandler.sendMessageDelayed(msg,
						PhoneNumFragment.RUNTIME_DELAY);
				// unLockInput(null);
			}
		}

		
	}
	
	public void onNext(View view) {

		// mSpeakText.setText(text);
		String phoneNum = mEdit.getText().toString();
		if (validateNumber(phoneNum)) {
			// seems ok, then verify phone number through unicom server
			mHandler.sendEmptyMessage(PhoneNumFragment.VERIFYING_PHONENUM);

			// set the pull pin time out event
			Message msg = new Message();
			msg.what = PhoneNumFragment.STOP_VFY_PHONENUM;
			msg.obj = "timeout";
			mHandler.sendMessageDelayed(msg, TIMEOUT_DELAY);
			
			PaymentTask task = new PaymentTask();
			task.execute(phoneNum);


		} else {
			unLockInput(phoneNum + "不是一个正确的手机号码");
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

	public void handleNetworkError(Context c, ResponseWrapper resp) {
		if (resp == null) {
			Toast.makeText(c, "Network Problem!", Toast.LENGTH_LONG).show();
		}
	}

}
