package com.fm.commons.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fm.commons.http.ContextHolder;
import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.DevIMESManager;
import com.fm.commons.logic.LocalStorage;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;

public class DeviceInfoUtils {

	private static TelephonyManager telephonyManager;
	private static LocalStorage localStorage = BeanFactory.getBean(LocalStorage.class);
	
	private static TelephonyManager getTelephonyManager() {
		if (null == telephonyManager)
			telephonyManager = (TelephonyManager) ContextHolder.get()
					.getSystemService(Context.TELEPHONY_SERVICE);
		
		return telephonyManager;
	}

	public static String getSimNumber() {
		try {
			return getTelephonyManager().getSimSerialNumber();
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * TODO 1.device id
	 * 
	 * @return
	 */
	public static String getIMSI() {
		try{
			return DevIMESManager.getImsi(ContextHolder.get());
		}catch(Exception e){
			return StringUtils.random(15);
		}
	}

	public static String getIMEI() {
		String imei;

		try {
			imei = DevIMESManager.getImei(ContextHolder.get());

		} catch (Exception e) {
			try {
				imei = android.provider.Settings.Secure.getString(ContextHolder
								.get().getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			} catch (Exception exp) {
				imei = localStorage.get(getPackageName(),"imei");
			}
		}
		if(!StringUtils.isEmpty(imei)){
			imei = StringUtils.random(20);
			localStorage.put(new String[]{"imei"},new Object[]{imei});
		}
		return imei;
	}

	public static String getPhoneMode() {
		return android.os.Build.MODEL;
	}

	public static String getManufacturer(){
		return android.os.Build.MANUFACTURER;
	}
	
	public static int getPhoneType() {
		return getTelephonyManager().getPhoneType();
	}

	public static String getPhoneNumber() {
		return getTelephonyManager().getLine1Number();
	}

	public static String getSimOperator() {
		return getTelephonyManager().getSimOperator();
	}

	// 中国移动
	public static boolean isCMCC() {
		String operator = getSimOperator();
		return operator.equals("46000") || operator.equals("46002")
				|| operator.equals("46007");
	}

	// 中国联通
	public static boolean isCMUC() {
		String operator = getSimOperator();
		return operator.equals("46001");
	}

	// 电信
	public static boolean isCMTC() {
		String operator = getSimOperator();
		return operator.equals("46003");
	}

	public static int getCarrier() {
		int type = -1;
		if (isCMCC())
			type = 0;
		if (isCMUC())
			type = 1;
		if (isCMTC())
			type = 2;
		return type;
	}

	// 判断机器 Android是否已经root，即是否获取root权限
	public static boolean haveRoot() {
		File su = new File("/system/bin/su");
		return su.exists();
	}

	public static Drawable getIcon() {
		ApplicationInfo info = ContextHolder.get().getApplicationInfo();
		return info.loadIcon(ContextHolder.get().getPackageManager());
	}
	

	public static int getNetworkType() {
		ConnectivityManager connectivityManager = (ConnectivityManager)ContextHolder.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = (NetworkInfo) connectivityManager.getActiveNetworkInfo();
		
		 int manacturer = -1; 
		 if (info.getType() == ConnectivityManager.TYPE_WIFI) { 
			 manacturer = 1;
		 } 
		 else if (info.getType() == ConnectivityManager.TYPE_MOBILE) { 
			 int subType = info.getSubtype(); 
			 if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					 || subType == TelephonyManager.NETWORK_TYPE_GPRS || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				 //2g
				 manacturer = 2;
			 }else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || 
					 subType == TelephonyManager.NETWORK_TYPE_HSDPA || 
					 subType == TelephonyManager.NETWORK_TYPE_EVDO_A || 
					 subType == TelephonyManager.NETWORK_TYPE_EVDO_0 || 
					 subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				 //3g
				 manacturer = 3; 
			} else if(subType == TelephonyManager.NETWORK_TYPE_LTE) {
				// LTE是3g到4g的过渡，是3.9G的全球标准 
				//type = "4g"; 
				manacturer = 4;
			} 
		}
		 return manacturer;
	}

	public static void autoOpenWifi(WifiManager wifiManager) {
		try {
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("DeviceInfoUtils", "fail to open wifi");
		}
	}

	public static boolean isWifi(WifiManager wifiManager) {
		return wifiManager.isWifiEnabled();
	}

	public static String getStation() {
		return "";
	}

	public static boolean isWifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ContextHolder
				.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiNetworkInfo.isConnected();
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

	public static boolean isConnectedNet() {
		ConnectivityManager cwjManager = (ConnectivityManager) ContextHolder
				.get().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		return (info != null && info.isAvailable());
	}

	public static String getVersion() {
		try {
			PackageManager manager = ContextHolder.get().getPackageManager();
			PackageInfo info = manager.getPackageInfo(ContextHolder.get()
					.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getPackageName() {
		try {
			PackageManager manager = ContextHolder.get().getPackageManager();
			PackageInfo info = manager.getPackageInfo(ContextHolder.get()
					.getPackageName(), 0);
			return info.packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getVersionCode() {
		try {
			PackageManager manager = ContextHolder.get().getPackageManager();
			PackageInfo info = manager.getPackageInfo(ContextHolder.get()
					.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getSysVersion() {
		// String handSetInfo= "手机型号:" + android.os.Build.MODEL + ME525
		// ",SDK版本:" + android.os.Build.VERSION.SDK + 8
		// ",系统版本:" + android.os.Build.VERSION.RELEASE; 2.2.1
		return android.os.Build.VERSION.SDK_INT;
	}

	public static String getSysReleaseVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	// 2.3 以上的版本
	public static boolean aboveSDK() {
		return getSysVersion() >= 9;
	}

	public static String getCurrentAppPath() {
		return ContextHolder.get().getFilesDir()
				.getAbsolutePath();
	}

	public static String getApkPn(String fileName) {
		if (!FileUtils.exist(fileName))
			return null;
		PackageManager pm = ContextHolder.get().getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(
				FileUtils.SDPATH + fileName, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			return appInfo.packageName; // 得到安装包名称
		}
		return null;
	}

	public static boolean isAvilible(String packageName) {
		final PackageManager packageManager = ContextHolder.get()
				.getPackageManager();
		// 获取所有已安装程序的包信息
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
				return true;
		}
		return false;
	}

	public static String getInstalledPackages() {
		final PackageManager packageManager = ContextHolder.get()
				.getPackageManager();
		// 获取所有已安装程序的包信息
		StringBuilder builder = new StringBuilder();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		for (int i = 0; i < pinfo.size(); i++) {
			PackageInfo packageInfo = pinfo.get(i);
			if (packageInfo.applicationInfo.flags != ApplicationInfo.FLAG_SYSTEM) {
				builder.append(packageInfo.packageName + ":"
						+ packageInfo.versionName + "_"
						+ packageInfo.versionCode + ";");
			}
		}
		return builder.toString();
	}
}
