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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentFragment extends Fragment {

	public static final int VERIFYING_PHONENUM = 4;
	public static final int STOP_VFY_PHONENUM = 5;

	public static final int TIMEOUT_DELAY = 60 * 1000;
	public static final int RUNTIME_DELAY = 10000;

	private Activity mActivity;
	private PaymentFragmentNotify mPaymentFragmentNotify;
	
	// UI components
	private TextView result_text;
	private Button mBack;
	private AnimationDrawable verifyingPhoneNumberAnimation;

	public PaymentFragment() {
	}
	
	public interface PaymentFragmentNotify{
		void PaymentfragmentDone(int status);
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RuntimeLog.log("PhoneNumFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.payment_fragment, container,
				false);

		result_text = (TextView) view.findViewById(R.id.result_text);
		mBack = (Button) view.findViewById(R.id.btn_back);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RuntimeLog.log("PhoneNumFragment - onActivityCreated");
		/* mNext.setOnClickListener(new OnClickListener() {}); */

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		RuntimeLog.log("PhoneNumFragment - onAttach");

		try {
			mActivity = (Activity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
			mPaymentFragmentNotify = (PaymentFragmentNotify) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGotoPageListener");
		}
	}



	
	public void onBack(View view) {
		mPaymentFragmentNotify.PaymentfragmentDone(0);
	}


}
