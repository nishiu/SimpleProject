package com.fm.commons.logic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class DevIMESManager {

	private static ZhanxunDoubleInfo initSpreadDoubleSim(Context mContext) {
		ZhanxunDoubleInfo zhanxunInfo = new ZhanxunDoubleInfo();
		try {
			Class<?> c = Class
					.forName("com.android.internal.telephony.PhoneFactory");
			Method m = c.getMethod("getServiceName", String.class, int.class);
			String spreadTmService = (String) m.invoke(c, Context.TELEPHONY_SERVICE, 1);
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			zhanxunInfo.setImei_1(tm.getDeviceId());
			zhanxunInfo.setImsi_1(tm.getSubscriberId());
			TelephonyManager tm1 = (TelephonyManager) mContext
					.getSystemService(spreadTmService);

			zhanxunInfo.setImei_2(tm1.getDeviceId());
			zhanxunInfo.setImsi_2(tm1.getSubscriberId());
			

			zhanxunInfo.setDefaultImsi(zhanxunInfo.getImsi_1());
			if (TextUtils.isEmpty(zhanxunInfo.getImsi_1()) && (!TextUtils.isEmpty(zhanxunInfo.getImsi_2()))) {
				zhanxunInfo.setDefaultImsi(zhanxunInfo.getImsi_2());
			}
			if (TextUtils.isEmpty(zhanxunInfo.getImsi_2()) && (!TextUtils.isEmpty(zhanxunInfo.getImsi_1()))) {
				zhanxunInfo.setDefaultImsi(zhanxunInfo.getImsi_1());
			}

			zhanxunInfo.setDefaultImei(zhanxunInfo.getImei_1());
			if (TextUtils.isEmpty(zhanxunInfo.getImei_1()) && (!TextUtils.isEmpty(zhanxunInfo.getImei_2()))) {
				zhanxunInfo.setDefaultImsi(zhanxunInfo.getImei_2());
			}
			if (TextUtils.isEmpty(zhanxunInfo.getImei_2()) && (!TextUtils.isEmpty(zhanxunInfo.getImei_1()))) {
				zhanxunInfo.setDefaultImsi(zhanxunInfo.getImei_1());
			}
		} catch (Exception e) {
			zhanxunInfo.setZhanxunDoubleSim(false);
			return zhanxunInfo;
		}
		zhanxunInfo.setZhanxunDoubleSim(true);
		return zhanxunInfo;
	}

	private static MtkDoubleInfo initMtkDoubleSim(Context mContext) {
		MtkDoubleInfo mtkDoubleInfo = new MtkDoubleInfo();
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			Field fields1 = c.getField("GEMINI_SIM_1");
			fields1.setAccessible(true);
			mtkDoubleInfo.setSimId_1((Integer) fields1.get(null));
			Field fields2 = c.getField("GEMINI_SIM_2");
			fields2.setAccessible(true);
			mtkDoubleInfo.setSimId_2((Integer) fields2.get(null));
			Method m = TelephonyManager.class.getDeclaredMethod(
					"getSubscriberIdGemini", int.class);
			mtkDoubleInfo.setImsi_1((String) m.invoke(tm,
					mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setImsi_2((String) m.invoke(tm,
					mtkDoubleInfo.getSimId_2()));

			Method m1 = TelephonyManager.class.getDeclaredMethod(
					"getDeviceIdGemini", int.class);
			mtkDoubleInfo.setImei_1((String) m1.invoke(tm,
					mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setImei_2((String) m1.invoke(tm,
					mtkDoubleInfo.getSimId_2()));

			Method mx = TelephonyManager.class.getDeclaredMethod(
					"getPhoneTypeGemini", int.class);
			mtkDoubleInfo.setPhoneType_1((Integer) mx.invoke(tm,
					mtkDoubleInfo.getSimId_1()));
			mtkDoubleInfo.setPhoneType_2((Integer) mx.invoke(tm,
					mtkDoubleInfo.getSimId_2()));

			mtkDoubleInfo.setDefaultImsi(mtkDoubleInfo.getImsi_1());
			if (TextUtils.isEmpty(mtkDoubleInfo.getImsi_1())
					&& (!TextUtils.isEmpty(mtkDoubleInfo.getImsi_2()))) {
				mtkDoubleInfo.setDefaultImsi(mtkDoubleInfo.getImsi_2());
			}
			if (TextUtils.isEmpty(mtkDoubleInfo.getImsi_2())
					&& (!TextUtils.isEmpty(mtkDoubleInfo.getImsi_1()))) {
				mtkDoubleInfo.setDefaultImsi(mtkDoubleInfo.getImsi_1());
			}

			mtkDoubleInfo.setDefaultImei(mtkDoubleInfo.getImei_1());
			if (TextUtils.isEmpty(mtkDoubleInfo.getImei_1())
					&& (!TextUtils.isEmpty(mtkDoubleInfo.getImei_2()))) {
				mtkDoubleInfo.setDefaultImei(mtkDoubleInfo.getImei_2());
			}
			if (TextUtils.isEmpty(mtkDoubleInfo.getImei_2())
					&& (!TextUtils.isEmpty(mtkDoubleInfo.getImei_1()))) {
				mtkDoubleInfo.setDefaultImei(mtkDoubleInfo.getImei_1());
			}
		} catch (Exception e) {
			mtkDoubleInfo.setMtkDoubleSim(false);
			return mtkDoubleInfo;
		}
		mtkDoubleInfo.setMtkDoubleSim(true);
		return mtkDoubleInfo;
	}

	private static GaotongDoubleInfo initQualcommDoubleSim(Context mContext) {
		GaotongDoubleInfo gaotongDoubleInfo = new GaotongDoubleInfo();
		gaotongDoubleInfo.setSimId_1(0);
		gaotongDoubleInfo.setSimId_2(1);
		try {
			Class<?> cx = Class
					.forName("android.telephony.MSimTelephonyManager");
			Object obj = mContext.getSystemService("phone_msim");

			Method md = cx.getMethod("getDeviceId", int.class);
			Method ms = cx.getMethod("getSubscriberId", int.class);

			gaotongDoubleInfo.setImei_1((String) md.invoke(obj,
					gaotongDoubleInfo.getSimId_1()));
			gaotongDoubleInfo.setImei_2((String) md.invoke(obj,
					gaotongDoubleInfo.getSimId_2()));
			gaotongDoubleInfo.setImsi_1((String) ms.invoke(obj,
					gaotongDoubleInfo.getSimId_1()));
			gaotongDoubleInfo.setImsi_2((String) ms.invoke(obj,
					gaotongDoubleInfo.getSimId_2()));

			gaotongDoubleInfo.setDefaultImsi(gaotongDoubleInfo.getImsi_1());
			if (TextUtils.isEmpty(gaotongDoubleInfo.getImsi_1())
					&& (!TextUtils.isEmpty(gaotongDoubleInfo.getImsi_2()))) {
				gaotongDoubleInfo.setDefaultImsi(gaotongDoubleInfo.getImsi_2());
			}
			if (TextUtils.isEmpty(gaotongDoubleInfo.getImsi_2())
					&& (!TextUtils.isEmpty(gaotongDoubleInfo.getImsi_1()))) {
				gaotongDoubleInfo.setDefaultImsi(gaotongDoubleInfo.getImsi_1());
			}
			
			gaotongDoubleInfo.setDefaultImei(gaotongDoubleInfo.getImei_1());
			if (TextUtils.isEmpty(gaotongDoubleInfo.getImei_1())
					&& (!TextUtils.isEmpty(gaotongDoubleInfo.getImei_2()))) {
				gaotongDoubleInfo.setDefaultImei(gaotongDoubleInfo.getImei_2());
			}
			if (TextUtils.isEmpty(gaotongDoubleInfo.getImei_2())
					&& (!TextUtils.isEmpty(gaotongDoubleInfo.getImei_1()))) {
				gaotongDoubleInfo.setDefaultImei(gaotongDoubleInfo.getImei_1());
			}
		} catch (Exception e) {
			gaotongDoubleInfo.setGaotongDoubleSim(false);
			return gaotongDoubleInfo;
		}
		return gaotongDoubleInfo;
	}

	public static String getImei(Context context) {
		MtkDoubleInfo mtkInfo = initMtkDoubleSim(context);
		if (mtkInfo.isMtkDoubleSim())
			return mtkInfo.getDefaultImei();
		GaotongDoubleInfo gaotongInfo = initQualcommDoubleSim(context);
		if (gaotongInfo.isGaotongDoubleSim())
			return gaotongInfo.getDefaultImei();
		
		ZhanxunDoubleInfo zhanxunInfo = initSpreadDoubleSim(context);
		if(zhanxunInfo.isZhanxunDoubleSim()) return zhanxunInfo.getDefaultImsi();
		else {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getDeviceId();
		}
	}

	public static String getImsi(Context context) {
		MtkDoubleInfo mtkInfo = initMtkDoubleSim(context);
		if (mtkInfo.isMtkDoubleSim())
			return mtkInfo.getDefaultImsi();
		GaotongDoubleInfo gaotongInfo = initQualcommDoubleSim(context);
		if (gaotongInfo.isGaotongDoubleSim())
			return gaotongInfo.getDefaultImsi();
		ZhanxunDoubleInfo zhanxunInfo = initSpreadDoubleSim(context);
		if(zhanxunInfo.isZhanxunDoubleSim()) return zhanxunInfo.getDefaultImsi();
		else {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getSubscriberId();
		}
	}
}

class ZhanxunDoubleInfo extends CardInfo {
	private boolean zhanxunDoubleSim;

	public boolean isZhanxunDoubleSim() {
		return zhanxunDoubleSim;
	}

	public void setZhanxunDoubleSim(boolean zhanxunDoubleSim) {
		this.zhanxunDoubleSim = zhanxunDoubleSim;
	}

}

class GaotongDoubleInfo extends CardInfo {
	private boolean gaotongDoubleSim;

	public boolean isGaotongDoubleSim() {
		return gaotongDoubleSim;
	}

	public void setGaotongDoubleSim(boolean gaotongDoubleSim) {
		this.gaotongDoubleSim = gaotongDoubleSim;
	}
}

class CardInfo {
	private Integer simId_1;
	private Integer simId_2;

	private String defaultImsi;
	private String imsi_1;
	private String imsi_2;

	private String defaultImei;
	private String imei_1;
	private String imei_2;

	public Integer getSimId_1() {
		return simId_1;
	}

	public void setSimId_1(Integer simId_1) {
		this.simId_1 = simId_1;
	}

	public Integer getSimId_2() {
		return simId_2;
	}

	public void setSimId_2(Integer simId_2) {
		this.simId_2 = simId_2;
	}

	public String getImsi_1() {
		return imsi_1;
	}

	public void setImsi_1(String imsi_1) {
		this.imsi_1 = imsi_1;
	}

	public String getImsi_2() {
		return imsi_2;
	}

	public void setImsi_2(String imsi_2) {
		this.imsi_2 = imsi_2;
	}

	public String getDefaultImsi() {
		return defaultImsi;
	}

	public void setDefaultImsi(String defaultImsi) {
		this.defaultImsi = defaultImsi;
	}

	public String getImei_1() {
		return imei_1;
	}

	public void setImei_1(String imei_1) {
		this.imei_1 = imei_1;
	}

	public String getImei_2() {
		return imei_2;
	}

	public void setImei_2(String imei_2) {
		this.imei_2 = imei_2;
	}

	public String getDefaultImei() {
		return defaultImei;
	}

	public void setDefaultImei(String defaultImei) {
		this.defaultImei = defaultImei;
	}
}

class MtkDoubleInfo extends CardInfo {

	private Integer phoneType_1;
	private Integer phoneType_2;

	private boolean mtkDoubleSim;

	public Integer getPhoneType_1() {
		return phoneType_1;
	}

	public void setPhoneType_1(Integer phoneType_1) {
		this.phoneType_1 = phoneType_1;
	}

	public Integer getPhoneType_2() {
		return phoneType_2;
	}

	public void setPhoneType_2(Integer phoneType_2) {
		this.phoneType_2 = phoneType_2;
	}

	public boolean isMtkDoubleSim() {
		return mtkDoubleSim;
	}

	public void setMtkDoubleSim(boolean mtkDoubleSim) {
		this.mtkDoubleSim = mtkDoubleSim;
	}
}
