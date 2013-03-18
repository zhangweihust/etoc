package com.zhangwei.speakloudly.fragment;

import java.io.InputStream;
import java.util.ArrayList;

import com.iflytek.speech.*;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;

import com.zhangwei.speakloudly.R;
import com.zhangwei.speakloudly.activity.MscActivity;

import com.zhangwei.speakloudly.utils.RuntimeLog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SpeakFragment extends Fragment {
	private Activity mActivity;
	private SpeakFragmentNotify mSpeakFragmentNotify;
	
	private TextView mSpeakText;
	private Button mBeginRecognize;
	private Toast mToast;

	private final String APP_ID = "512c57b1";// orig:4d6774d0 my 512c57b1s
	private final static String KEY_GRAMMAR_ID = "grammar_id";
	private RecognizerDialog recognizerDialog = null;
	private String grammarText = null;
	private String grammarID = null;
	private SpeechListener loginListener;
	private RecognizerDialogListener mRecoListener;
	private SpeechListener uploadListener;
	
	public interface SpeakFragmentNotify{
		void SpeakFragmentDone(int status);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RuntimeLog.log("SpeakFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.speak_fragment, container, false);

		mSpeakText = (TextView) view.findViewById(R.id.speak_text);
		mBeginRecognize = (Button) view.findViewById(R.id.btn_begin_recognize);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		RuntimeLog.log("SpeakFragment - onActivityCreated");

		mToast = Toast.makeText(mActivity, "", Toast.LENGTH_LONG);
		grammarText = readAbnfFile(mActivity);
		mSpeakText.setText(R.string.instruction);

		/**
		 * 用户登录回调监听器.
		 */
		loginListener = new SpeechListener() {

			@Override
			public void onData(byte[] arg0) {
			}

			@Override
			public void onEnd(SpeechError error) {
				if (error != null) {
					//mToast.setText("登录失败");
					//mToast.show();
				} else {
					//mToast.setText("登录成功");
					//mToast.show();
				}
			}

			@Override
			public void onEvent(int arg0, Bundle arg1) {
			}
		};

		/**
		 * 识别监听回调
		 */
		mRecoListener = new RecognizerDialogListener() {
			@Override
			public void onResults(ArrayList<RecognizerResult> results,
					boolean isLast) {
				String text = "";
/*				for (int i = 0; i < results.size(); i++) {
					RecognizerResult result = results.get(i);
					text += result.text + " confidence=" + result.confidence
							+ "\n";
				}*/
				
				RecognizerResult result = results.get(0);
				
				//mSpeakText.setText(result.text);
				
				if(result.text.compareTo("联通上网真快")==0 || result.text.compareTo("联通3G 上网真快")==0){
					mSpeakFragmentNotify.SpeakFragmentDone(0);
				}else{
					mSpeakText.setText(R.string.instruction_error);
				}
				
				/*
				 * EditText tmsg = (EditText)findViewById(R.id.edit_text);
				 * tmsg.setText(text);
				 * tmsg.setSelection(tmsg.getText().length());
				 */
			}

			@Override
			public void onEnd(SpeechError error) {
			}
		};

		/**
		 * 上传语法文件 回调监听器.
		 */
		uploadListener = new SpeechListener() {
			@Override
			public void onEnd(SpeechError arg0) {
				if (arg0 != null) {
					mToast.setText(arg0.toString());
					mToast.show();
				}
			}

			@Override
			public void onData(byte[] arg0) {
				grammarID = new String(arg0);
				// 保存语法ID
				SharedPreferences preference = mActivity.getSharedPreferences(
						"abnf", mActivity.MODE_PRIVATE);
				preference.edit().putString(KEY_GRAMMAR_ID, grammarID).commit();

				mToast.setText("语法文件ID：" + grammarID);
				mToast.show();
			}

			@Override
			public void onEvent(int arg0, Bundle arg1) {
			}

		};

		mBeginRecognize.setOnClickListener(new OnClickListener() {
			/*
			 * case R.id.btn_upload: try { //上传数据格式gb2312 byte[] datas =
			 * grammarText.getBytes("gb2312"); DataUploader uploader = new
			 * DataUploader(); uploader.uploadData(this, uploadListener, "abnf",
			 * "subject=asr,data_type=abnf",datas);
			 * mToast.setText("开始上传语法...."); mToast.show(); } catch (Exception
			 * e) { e.printStackTrace(); } break;
			 */
			/*
			 * case R.id.btn_recognizeGrammar: if(TextUtils.isEmpty(grammarID))
			 * { mToast.setText("上传语法后才可以使用."); mToast.show(); }else { tmsg =
			 * (EditText)findViewById(R.id.edit_text);
			 * tmsg.setText(grammarText);
			 * recognizerDialog.setListener(mRecoListener);
			 * recognizerDialog.setEngine("asr", null, grammarID);
			 * recognizerDialog.show(); } break;
			 */

			@Override
			public void onClick(View v) {
				// mSpeakText.setText(text);
				recognizerDialog.setListener(mRecoListener);
				recognizerDialog.setEngine(null, "grammar_type=abnf",
						grammarText);
				recognizerDialog.show();
			}

		});

		recognizerDialog = new RecognizerDialog(mActivity, "appid=" + APP_ID);
		SpeechUser.getUser().login(mActivity, null, null, "appid=" + APP_ID,
				loginListener);

	}
	
	@Override
	public void onResume(){
		super.onResume();
		mSpeakText.setText(R.string.instruction);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		RuntimeLog.log("SpeakFragment - onAttach");

		try {
			mActivity = (MscActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
			mSpeakFragmentNotify = (SpeakFragmentNotify) activity;
			
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnGotoPageListener");
		}

	}

	private void lockInput() {
		/*
		 * welcomeAcceptBtn.setText(""); welcomeAcceptBtn.setClickable(false);
		 * welcomeAcceptBtn
		 * .setBackgroundResource(R.anim.welcome_loading_process);
		 * loadingAnimation =
		 * (AnimationDrawable)welcomeAcceptBtn.getBackground();
		 * loadingAnimation.start();
		 */

	}

	public void unLockInput() {
		/*
		 * welcomeAcceptBtn.setText(R.string.accept);
		 * welcomeAcceptBtn.setClickable(true);
		 * welcomeAcceptBtn.setBackgroundDrawable
		 * (getResources().getDrawable(R.drawable.login_welcome_bg_btn)); if
		 * (loadingAnimation != null && loadingAnimation.isRunning()){
		 * loadingAnimation.stop(); }
		 */

	}

	public void showBtns(String airtelNumber) {
		/* this.airtelNumber = airtelNumber; */
		// final Button signUpBtn =(Button)findViewById(R.id.mWelcomeSignUpBtn);
		// final Button signInBtn =(Button)findViewById(R.id.mWelcomeSignInBtn);
		// signUpBtn.setVisibility(View.VISIBLE);
		// signInBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 读取语法文件
	 * 
	 * @return
	 */
	private String readAbnfFile(Activity activity) {
		int len = 0;
		byte[] buf = null;
		String grammar = "";
		try {
			InputStream in = activity.getAssets().open(
					"gm_continuous_digit.abnf");
			len = in.available();
			buf = new byte[len];
			in.read(buf, 0, len);

			grammar = new String(buf, "gb2312");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return grammar;

	}

}
