package com.zhangwei.speakloudly.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.zhangwei.speakloudly.client.Callback;
import com.zhangwei.speakloudly.client.ResponseWrapper;
import com.zhangwei.speakloudly.utils.Constants;
import com.zhangwei.speakloudly.utils.CookieUtil;
import android.os.Handler;
import android.util.Log;

public class UnicomClient {
	private static final String UNICOM_URL = "http://www.unicom.example/";
	private static final String VFY_PHONENUM_URL = UNICOM_URL + "verify";
	private static final String PAY_PHONENUM_URL = UNICOM_URL + "payment";
	private static final String TAG = "UnicomClient";

	private static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
				}
			}
		};
		t.start();
		return t;
	}

	private static ResponseWrapper sendGetRequest(String url,
			CookieStore reqCookieStore, List<Header> headers) {
		Log.i(TAG, "sendGetRequest() begin url=" + url + ",reqCookieStore="
				+ reqCookieStore);
		HttpGet httpGet = new HttpGet(url);
		if (headers != null) {
			for (Header header : headers) {
				httpGet.addHeader(header);
			}
		}
		DefaultHttpClient httpClient = newDefaultHttpClient();
		if (reqCookieStore != null) {
			httpClient.setCookieStore(reqCookieStore);
		}
		HttpResponse resp;
		CookieStore cookieStore;
		try {
			resp = httpClient.execute(httpGet);
			cookieStore = httpClient.getCookieStore();
		} catch (ClientProtocolException e) {
			Log.e(TAG, "", e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, "", e);
			return null;
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		int statusCode = resp.getStatusLine().getStatusCode();
		Log.i(TAG, "sendGetRequest() statusCode=" + statusCode);
		if (statusCode == HttpStatus.SC_OK) {
			return new ResponseWrapper(resp, cookieStore);
		} else {
			return null;
		}
	}

	private static ResponseWrapper sendPostRequest(String url,
			String[] paraKeys, String[] paraValues, CookieStore reqCookieStore,
			List<Header> headers) {
		Log.i(TAG, "sendPostRequest() begin, url=" + url);
		HttpPost httpPost = new HttpPost(url);
		if (headers != null) {
			for (Header header : headers) {
				httpPost.addHeader(header);
			}
		}
		if (paraKeys != null && paraKeys.length > 0 && paraValues != null
				&& paraValues.length > 0) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			int paraLen = paraKeys.length;
			for (int i = 0; i < paraLen; i++) {
				Log.i(TAG, "sendPostRequest() " + paraKeys[i] + "="
						+ paraValues[i]);
				params.add(new BasicNameValuePair(paraKeys[i], paraValues[i]));
			}
			HttpEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(params);
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "", e);
				return null;
			}
			httpPost.setEntity(entity);
		}

		DefaultHttpClient httpClient = newDefaultHttpClient();
		if (reqCookieStore != null) {
			httpClient.setCookieStore(reqCookieStore);
		}

		HttpResponse resp;
		CookieStore respCookieStore;
		try {
			resp = httpClient.execute(httpPost);
			respCookieStore = httpClient.getCookieStore();
		} catch (ClientProtocolException e) {
			Log.e(TAG, "", e);
			return null;
		} catch (IOException e) {
			Log.e(TAG, "", e);
			return null;
		} finally {
			// httpClient.getConnectionManager().shutdown();
		}

		int statusCode = resp.getStatusLine().getStatusCode();
		Log.i(TAG, "sendPostRequest() statusCode=" + statusCode);
		if (statusCode == HttpStatus.SC_OK) {
			return new ResponseWrapper(resp, respCookieStore);
		} else {
			return null;
		}
	}

	private static DefaultHttpClient newDefaultHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpParams params = httpClient.getParams();
		ConnManagerParams.setTimeout(params, 1000);
		HttpConnectionParams.setConnectionTimeout(params, 2000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		params.setBooleanParameter("http.protocol.expect-continue", false);
		BasicCredentialsProvider bcp = new BasicCredentialsProvider();
		bcp.setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(
						Constants.CREDENTIALS_USER_NAME,
						Constants.CREDENTIALS_PASSWORD));
		httpClient.setCredentialsProvider(bcp);
		HttpClientParams.setCookiePolicy(httpClient.getParams(),
				CookiePolicy.BROWSER_COMPATIBILITY);
		return httpClient;
	}

	private static Header genPassportCookieHeader(String passport) {
		Header header = new BasicHeader("Cookie",
				CookieUtil.crossCookieName("passport") + "=\"" + passport
						+ "\"");
		return header;
	}

	public static Thread performVerfiyNum(final String passport,
			final Handler handler, final Callback callback) {
		return performOnBackgroundThread(new Runnable() {
			public void run() {
				List<Header> headers = new ArrayList<Header>();
				if (passport != null && !passport.equals("")) {
					headers.add(genPassportCookieHeader(passport));
				}

				final ResponseWrapper resp = sendGetRequest(VFY_PHONENUM_URL,
						null, headers);

				if (handler != null) {
					handler.post(new Runnable() {
						public void run() {
							callback.call(resp);
						}
					});
				}
			}
		});
	}

	public static Thread performPayment(final String passport,
			final Handler handler, final Callback callback) {
		return performOnBackgroundThread(new Runnable() {
			public void run() {
				List<Header> headers = new ArrayList<Header>();
				if (passport != null && !passport.equals("")) {
					headers.add(genPassportCookieHeader(passport));
				}

				final ResponseWrapper resp = sendGetRequest(PAY_PHONENUM_URL,
						null, headers);

				if (handler != null) {
					handler.post(new Runnable() {
						public void run() {
							callback.call(resp);
						}
					});
				}
			}
		});
	}
}
