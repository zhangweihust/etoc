package com.zhangwei.speakloudly.utils;


import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import com.zhangwei.speakloudly.account.E2CAccount;
import com.zhangwei.speakloudly.account.E2CAccountManager;
import com.zhangwei.speakloudly.utils.Constants;

import android.content.Context;


/**
 * @author ISS_mwrao
 * 2012-10-24 
 */
public class CookieUtil {
	
	public static CookieStore  getCookieStore(Context mContext) {
		CookieStore cookieStore = new BasicCookieStore();
		E2CAccountManager mHikeAccountManager = new E2CAccountManager(mContext);
		E2CAccount mHikeAccount = mHikeAccountManager.getAccount();
		
		String passport = mHikeAccount.getPassport();
		String passportCookieValue = mHikeAccount.getPassportCookieValue();
		String name = passportCookieValue.substring(0,passportCookieValue.indexOf("="));
		
		BasicClientCookie  MyCookie = new BasicClientCookie(name, passport);
		MyCookie.setPath("/");
		//  problem  can't add  cookie to cookieStore
		cookieStore.addCookie(MyCookie);
		return cookieStore ; 
	}
	
	
	public  static  String  getCookiePassport(Context mContext) {
		E2CAccountManager mHikeAccountManager = new E2CAccountManager(mContext);
		E2CAccount mHikeAccount = mHikeAccountManager.getAccount();
		return  mHikeAccount.getPassport();
	}
	
	public static Header genPassportCookieHeader(String passport){
		Header header = new BasicHeader("Cookie",CookieUtil.crossCookieName("passport") + "=\"" + passport + "\"");
		return header;
	}
	public static Header genLoginTraceCookieHeader(String loginTrace){
		Header header = new BasicHeader("Cookie",CookieUtil.crossCookieName("logintrace") + "=\"" + loginTrace + "\"");
		return header;
	}
	
	public static String crossCookieName(String cookieName){
		return cookieName + Constants.ROOT_DOMAIN;
	}
	

}
