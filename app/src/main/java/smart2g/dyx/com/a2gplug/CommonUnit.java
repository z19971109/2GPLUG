package smart2g.dyx.com.a2gplug;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUnit {

	public static boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		return false;
	}

	public static boolean isWifiConnect(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}

	public static boolean is3GConnect(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return m3G.isConnected();
	}

	public static List<String> stringsToList(String[] items) {
		List<String> list = new ArrayList<String>();
		if (items != null) {
			for (String item : items) {
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * 将时间分秒转为HH : NN
	 * 
	 * @param hour
	 * @param min
	 * @return
	 */
	public static String toTime(int hour, int min) {
		return String.format("%02d:%02d", hour, min);
	}

	/**
	 * 将时间分秒转为HH : NN
	 * 
	 * @param hour
	 * @param min
	 * @return
	 */
	public static String toTime(int hour, int min, int second) {
		return String.format("%02d:%02d:%02d", hour, min, second);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getWeekByDate_tomorrow() {

		int ret = getWeekByDate();
		if (ret != 6) {
			ret += 1;
		} else {
			ret = 0;
		}

		return ret;
	}

	/**
	 * 清理临时文件
	 */
//	public static final void deleteTempFile() {
//		File tempFolder = new File(Settings.CACHE_PATH);
//		if (tempFolder.exists()) {
//			File[] tempFiles = tempFolder.listFiles();
//			for (File tempFile : tempFiles) {
//				tempFile.delete();
//			}
//		}
//	}

	public static final int getPhoneHour() {
		Date curDate = new Date(System.currentTimeMillis());
		return curDate.getHours();
	}

	public static final int getPhoneMin() {
		Date curDate = new Date(System.currentTimeMillis());
		return curDate.getMinutes();
	}

	public static final int getPhoneSeconds() {
		Date curDate = new Date(System.currentTimeMillis());
		return curDate.getSeconds();
	}

	public static final String getCurrentTime() {
		Date curDate = new Date(System.currentTimeMillis());
		return String.format("%d:%d", curDate.getHours(), curDate.getMinutes());
	}

	/** 将时间转为long型 **/
	public static final long changeDataToMill(int hour, int min) {
		// try {
		// Date curDate = new Date(System.currentTimeMillis());
		// String data = String.format("%d-%d-%d %d:%d:%d", curDate.getYear(),
		// curDate.getMonth() + 1, curDate.getDay(), hour, min, 30);
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// return df.parse(data).getTime();
		// } catch (ParseException e) {
		// e.printStackTrace();
		// return System.currentTimeMillis();
		// }

		try {
			Date curDate = new Date(System.currentTimeMillis());
			String data = String.format("%d-%d-%d %d:%d:%d", curDate.getYear() + 1900, curDate.getMonth() + 1, curDate.getDate(), hour, min, 30);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(data).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}

	/** 将时间转为long型 **/
	public static final long changeDataToMill(int hour, int min, int sec) {
		// try {
		// Date curDate = new Date(System.currentTimeMillis());
		// String data = String.format("%d-%d-%d %d:%d:%d", curDate.getYear(),
		// curDate.getMonth() + 1, curDate.getDay(), hour, min, 30);
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// return df.parse(data).getTime();
		// } catch (ParseException e) {
		// e.printStackTrace();
		// return System.currentTimeMillis();
		// }

		try {
			Date curDate = new Date(System.currentTimeMillis());
			String data = String.format("%d-%d-%d %d:%d:%d", curDate.getYear() + 1900, curDate.getMonth() + 1, curDate.getDate(), hour, min, sec);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(data).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}

	/** 将时间转为long型 **/
	public static final long changeDataToMill(int yr, int month, int day, int hour, int min) {
		try {
			String data = String.format("%d-%d-%d %d:%d:%d", yr, month, day, hour, min, 30);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(data).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}

	public static final long changeDataToMill(int yr, int month, int day, int hour, int min, int second) {
		try {
			String data = String.format("%d-%d-%d %d:%d:%d", yr, month, day, hour, min, second);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(data).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}

	public static final int getYearByMill(long mill) {
		Date curDate = new Date(mill);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		return calendar.get(Calendar.YEAR);
	}

	public static final int getMonthByMill(long mill) {
		Date curDate = new Date(mill);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static final int getDayByMill(long mill) {
		Date curDate = new Date(mill);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(curDate);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static final int getHourByMill(long mill) {
		Date curDate = new Date(mill);
		return curDate.getHours();
	}

	public static final int getMinByMill(long mill) {
		Date curDate = new Date(mill);
		return curDate.getMinutes();
	}

	public static final int getSecondByMill(long mill) {
		Date curDate = new Date(mill);
		return curDate.getSeconds();
	}

	/**
	 * 根据日期取得星期几
	 *
	 * @param date
	 * @return 0星期天 1星期一 2星期二 3星期三
	 */
	public static int getWeekByDate() {
		int[] weeks = { 0, 1, 2, 3, 4, 5, 6 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return weeks[week_index];
	}

	public static boolean checkTimerNever(int[] weeks) {
		for (int week : weeks) {
			if (week == 1) {
				return false;
			}
		}
		return true;
	}

	public static int getMonthByDate() {
		int[] months = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(System.currentTimeMillis()));
		int week_index = cal.get(Calendar.MONTH);
		if (week_index < 0) {
			week_index = 0;
		}
		return months[week_index];
	}

	/**
	 * 计算经纬度距离
	 *
	 * @param startLat
	 * @param startLng
	 * @param endLat
	 * @param endLng
	 * @return
	 */
	public static double getLocationDistance(double startLat, double startLng, double endLat, double endLng) {
		double EARTH_RADIUS = 6378137.0;
		double radLat1 = (startLat * Math.PI / 180.0);
		double radLat2 = (endLat * Math.PI / 180.0);

		double a = radLat1 - radLat2;
		double b = (startLng - endLng) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));

		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**************************************************************************
	 *
	 * Toast Show
	 *
	 * @param context
	 * @param message
	 *
	 *            **************************************************************
	 *            ***********
	 */
	public static void toastShow(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/*************************************************************************
	 *
	 * Toast Show
	 *
	 * @param context
	 * @param message
	 *
	 *            **************************************************************
	 *            ***********
	 */
	public static void toastShow(Context context, int messageId) {
		Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show();
	}

	public static int getWeek(Date date) {
		GregorianCalendar g = new GregorianCalendar();
		g.setTime(date);
		return g.get(Calendar.WEEK_OF_YEAR);// 获得周数
	}

	public static boolean languageIsZhCN() {
		String language = getLanguageEnv();

		if (language != null && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW")))
			return true;
		else
			return false;
	}

	private static String getLanguageEnv() {
		Locale l = Locale.getDefault();
		String language = l.getLanguage();
		String country = l.getCountry().toLowerCase();
		if ("zh".equals(language)) {
			if ("cn".equals(country)) {
				language = "zh-CN";
			} else if ("tw".equals(country)) {
				language = "zh-TW";
			}
		} else if ("pt".equals(language)) {
			if ("br".equals(country)) {
				language = "pt-BR";
			} else if ("pt".equals(country)) {
				language = "pt-PT";
			}
		}
		return language;
	}

	public static boolean isEn(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("en"))
			return true;
		else
			return false;
	}

	public static boolean isJP(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("ja"))
			return true;
		else
			return false;
	}

	public static boolean isRu(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("ru"))
			return true;
		else
			return false;
	}

	public static boolean isZh(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		if (language.endsWith("zh"))
			return true;
		else
			return false;
	}

	public static boolean isZh_TW(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		if (locale.getCountry().toUpperCase(Locale.getDefault()).endsWith("TW"))
			return true;
		else
			return false;
	}

	public static boolean isZh_HK(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		if (locale.getCountry().toUpperCase(Locale.getDefault()).endsWith("HK"))
			return true;
		else
			return false;
	}

	/***
	 * 保留2位小数点
	 *
	 * @param data
	 *            0.00000
	 * @return 0.00
	 */
	public static float decimalFormat(float data) {
		return (float) (Math.round(data * 100) / 100.0);
	}

	/***
	 * 保留2位小数点
	 *
	 * @param data
	 *            0.00000
	 * @param formatData
	 *            10->0.1 100->0.01
	 * @return 0.0
	 */
	public static float decimalFormat(float data, float formatData) {
		return (float) (Math.round(data * formatData) / formatData);
	}

	/**
	 * 将解释到的数据转为String
	 *
	 * @param receiverDate
	 * @param receiverLength
	 * @return
	 */
//	public static String parseData(byte[] receiverDate, long receiverLength) {
//		StringBuffer re = new StringBuffer();
//		for (int i = 0; i < receiverLength; i++) {
//			re.append(Other.to16(receiverDate[i]));
//		}
//
//		return re.toString();
//	}

	/**
	 * 字符串转为 btye 数组
	 *
	 * @param dataString转化的16进制字符串
	 * @return byte数组
	 */
	public static byte[] parseStringToByte(String dataString) {
		int subPosition = 0;
		int byteLenght = dataString.length() / 2;

		byte[] result = new byte[byteLenght];

		for (int i = 0; i < byteLenght; i++) {
			String s = dataString.substring(subPosition, subPosition + 2);
			result[i] = (byte) Integer.parseInt(s, 16);
			subPosition = subPosition + 2;
		}

		return result;
	}

	/**
	 * 将解释到的数据转为String
	 *
	 * @param receiverDate
	 * @param receiverLength
	 * @return
	 */
	public static String parseData(byte[] receiverDate) {
		StringBuffer re = new StringBuffer();
		for (int i = 0; i < receiverDate.length; i++) {
			re.append(to16(receiverDate[i]));
		}

		return re.toString();
	}

	// 2进制 转 16进制
	public static String to16(int b) {
		String s = Integer.toHexString(b);
		int lenth = s.length();
		if (lenth == 1) {
			s = "0" + s;
		}
		if (lenth > 2) {
			s = s.substring(lenth - 2, lenth);
		}
		return s.toString();
	}

	public static String tenTo16(int i) {
		String re = "";
		re = Integer.toHexString(i);
		if (re.length() % 2 != 0) {
			re = "0" + re;
		}

		return re;
	}

	public static boolean isUrl(String str) {
		String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		return match(regex, str);
	}

	/**
	 * @param regex
	 *            正则表达式字符串
	 * @param str
	 *            要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/************
	 * Mac转换带：
	 */
	public static String toMacFormat(String mac) {
		if (mac.length() == 12) {
			StringBuffer sb = new StringBuffer(mac);
			sb.insert(10, ':');
			sb.insert(8, ':');
			sb.insert(6, ':');
			sb.insert(4, ':');
			sb.insert(2, ':');
			return sb.toString();
		}
		return "";
	}

	public static String toYLYMacFormat(String mac) {
		if (mac.length() == 12) {
			StringBuffer sb = new StringBuffer(mac);
			sb.insert(10, '-');
			sb.insert(8, '-');
			sb.insert(6, '-');
			sb.insert(4, '-');
			sb.insert(2, '-');
			return sb.toString();
		}
		return "";
	}

	public static ArrayList deepCopy(ArrayList src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		ArrayList dest = (ArrayList) in.readObject();
		return dest;
	}

	/**
	 * 域名转Ip地址
	 *
	 * @param host
	 *            域名
	 *
	 * @return ip地址
	 *
	 */
	public static String getInetAddress(String host) {
		String IPAddress = null;
		InetAddress ReturnStr1 = null;
		try {
			ReturnStr1 = InetAddress.getByName(host);
			IPAddress = ReturnStr1.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return IPAddress;
	}

	public static String getUrlHost(String curl) {
		String host = "";
		try {
			URL url = new URL(curl);
			host = url.getHost();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return host;
	}

	/**
	 * 修改字体
	 *
	 * @param root
	 * @param mContext
	 */
//	public static void changeFonts(ViewGroup root, Context mContext) {
//
//		Typeface tf = Typeface.createFromAsset(mContext.getAssets(), Constants.APP_FONT_PATH);
//
//		for (int i = 0; i < root.getChildCount(); i++) {
//			View v = root.getChildAt(i);
//			if (v instanceof TextView) {
//				((TextView) v).setTypeface(tf);
//			} else if (v instanceof Button) {
//				((Button) v).setTypeface(tf);
//			} else if (v instanceof EditText) {
//				((EditText) v).setTypeface(tf);
//			} else if (v instanceof ViewGroup) {
//				changeFonts((ViewGroup) v, mContext);
//			}
//		}
//
//	}

	public static ViewGroup getRootView(Activity context) {
		return (ViewGroup) ((ViewGroup) (context.findViewById(android.R.id.content))).getChildAt(0);
	}

	public static void toActivity(Context mContext, Class T) {
		Intent intent = new Intent(mContext, T);
		mContext.startActivity(intent);
	}

//	public static void toActivityRollUp(Context mContext, Class T) {
//		Intent intent = new Intent(mContext, T);
//		mContext.startActivity(intent);
//		if (mContext instanceof Activity) {
//			((Activity) mContext).overridePendingTransition(R.anim.roll_up, R.anim.roll);
//		}
//	}
//
//	public static void toActivityRollLeft(Context mContext, Class T) {
//		Intent intent = new Intent(mContext, T);
//		mContext.startActivity(intent);
//		if (mContext instanceof Activity) {
//			((Activity) mContext).overridePendingTransition(R.anim.roll_left, R.anim.roll_left_out);
//		}
//	}

	public static String getAppInfo(Context mContext) {
		try {
			String pkName = mContext.getPackageName();
			String versionName = mContext.getPackageManager().getPackageInfo(pkName, 0).versionName;

			int versionCode = mContext.getPackageManager().getPackageInfo(pkName, 0).versionCode;

			return pkName + "   " + versionName + "  " + versionCode;
		} catch (Exception e) {

		}
		return null;
	}

	public static String getVersionName(Context mContext) {
		try {
			String pkName = mContext.getPackageName();
			String versionName = mContext.getPackageManager().getPackageInfo(pkName, 0).versionName;
			return versionName;
		} catch (Exception e) {

		}
		return null;
	}

	public static int getVersionCode(Context mContext) {
		try {
			String pkName = mContext.getPackageName();
			int versionCode = mContext.getPackageManager().getPackageInfo(pkName, 0).versionCode;
			return versionCode;
		} catch (Exception e) {
		}
		return 0;
	}

	public static String getWifiGatWay(Context mContext) {
		WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		return Formatter.formatIpAddress(dhcpInfo.gateway);
	}

	public static String getLocalIp(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	public static void setTextViewTopDrawable(Context mContext, TextView v, Drawable drawable) {
		drawable.setBounds(0, 0, dip2px(mContext, 60f), dip2px(mContext, 60f));
		v.setCompoundDrawables(null, drawable, null, null);
	}

	public static int getWeekDayByMill(long mill) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(mill);
		return c.get(Calendar.DAY_OF_WEEK) - 1;
	}

	public static String getCurrentZoneHour(int hour, int min) {
		int newHour = 0;
		int newMin = 2;

		TimeZone tz = TimeZone.getDefault();
		// int zoneHour = tz.getRawOffset() / 1000 / 3600;
		// int zoneMin = tz.getRawOffset() / 1000 / 60 % 60;

		int zoneHour = tz.getRawOffset() / 1000 / 3600;
		int zoneMin = tz.getRawOffset() / 1000 / 60 % 60;

		int dif = hour - (8 - zoneHour);

		if (dif < 0) {
			if (zoneMin == 0) {
				newHour = 24 + dif;
				newMin = min;
			} else {
				if (min >= 30) {
					newHour = 24 + dif;
					newMin = min - 30;
				} else {
					newHour = 24 + dif - 1;
					newMin = min + 60 - 30;
				}
			}
		} else if (dif < 24) {
			if (zoneMin == 0) {
				newHour = dif;
				newMin = min;
			} else if (zoneMin == 45) {
				if (min >= 15) {
					newHour = dif + 1;
					newMin = min - 15;
				} else {
					newHour = dif;
					newMin = min + 45;
				}
			} else {
				if (min >= 30) {
					newHour = dif + 1;
					newMin = min - 30;
				} else {
					newHour = dif;
					newMin = min + 30;
				}
			}
		} else {
			if (zoneMin == 0) {
				newHour = dif - 24;
				newMin = min;
			} else if (zoneMin == 45) {
				if (min >= 15) {
					newHour = dif - 24 + 1;
					newMin = min - 15;
				} else {
					newHour = dif - 24;
					newMin = min + 45;
				}
			} else {
				if (min >= 30) {
					newHour = dif - 24 + 1;
					newMin = min - 30;
				} else {
					newHour = dif - 24;
					newMin = min + 30;
				}
			}
		}

		return toTime(newHour, newMin);
	}

	// public static int[] getNewWeeksFromPhoneToDevice(int[] weeks) {
	// int newWeeks[] = new int[7];
	// if (RmtApplaction.mDiffDay == 0) {
	// return weeks;
	// } else if (RmtApplaction.mDiffDay == 1) {
	// for (int i = 0; i < weeks.length; i++) {
	// if (i == 0) {
	// newWeeks[i] = weeks[6];
	// } else {
	// newWeeks[i] = weeks[i - 1];
	// }
	// }
	//
	// } else if (RmtApplaction.mDiffDay == -1) {
	// for (int i = 0; i < weeks.length; i++) {
	// if (i == 6) {
	// newWeeks[i] = weeks[0];
	// } else {
	// newWeeks[i] = weeks[i + 1];
	// }
	// }
	// }
	// return newWeeks;
	// }
	//
	// public static int[] getNewWeeksFromDeviceToPhone(int[] weeks) {
	// int newWeeks[] = new int[7];
	// if (RmtApplaction.mDiffDay == 0) {
	// return weeks;
	// } else if (RmtApplaction.mDiffDay == 1) {
	// for (int i = 0; i < weeks.length; i++) {
	// if (i == 6) {
	// newWeeks[i] = weeks[0];
	// } else {
	// newWeeks[i] = weeks[i + 1];
	// }
	// }
	// } else if (RmtApplaction.mDiffDay == -1) {
	// for (int i = 0; i < weeks.length; i++) {
	// if (i == 0) {
	// newWeeks[i] = weeks[6];
	// } else {
	// newWeeks[i] = weeks[i - 1];
	// }
	// }
	// }
	// return newWeeks;
	// }

	public static int[] getNewWeeksFromPhoneToDevice(int[] weeks, int diffDay) {
		int newWeeks[] = new int[7];
		if (diffDay == 0) {
			return weeks;
		} else if (diffDay == 1) {
			for (int i = 0; i < weeks.length; i++) {
				if (i == 0) {
					newWeeks[i] = weeks[6];
				} else {
					newWeeks[i] = weeks[i - 1];
				}
			}

		} else if (diffDay == -1) {
			for (int i = 0; i < weeks.length; i++) {
				if (i == 6) {
					newWeeks[i] = weeks[0];
				} else {
					newWeeks[i] = weeks[i + 1];
				}
			}
		}
		return newWeeks;
	}

	public static int[] getNewWeeksFromDeviceToPhone(int[] weeks, int diffDay) {
		int newWeeks[] = new int[7];
		if (diffDay == 0) {
			return weeks;
		} else if (diffDay == 1) {
			for (int i = 0; i < weeks.length; i++) {
				if (i == 6) {
					newWeeks[i] = weeks[0];
				} else {
					newWeeks[i] = weeks[i + 1];
				}
			}
		} else if (diffDay == -1) {
			for (int i = 0; i < weeks.length; i++) {
				if (i == 0) {
					newWeeks[i] = weeks[6];
				} else {
					newWeeks[i] = weeks[i - 1];
				}
			}
		}
		return newWeeks;
	}

	/**
	 * 计算某个时间与现在的时间是否是同一天 即星期几是否一样
	 *
	 * @param time
	 * @return
	 */
//	public static int getDiffDay(long time1, long time2) {
//		int mDiffDay = 0;
//		int time1WeekDay = com.zwxt.yly.common.CommonUnit.getWeekDayByMill(time1);
//		int time2WeekDay = com.zwxt.yly.common.CommonUnit.getWeekDayByMill(time2);
//		if (time1WeekDay > time2WeekDay) {
//			mDiffDay = 1;
//		} else if (time1WeekDay < time2WeekDay) {
//			if (time1WeekDay == 0 && time2WeekDay == 6) {
//				mDiffDay = 1;
//			} else {
//				mDiffDay = -1;
//			}
//		} else {
//			mDiffDay = 0;
//		}
//		return mDiffDay;
//	}

	public static boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
			return true;
		}
		return false;
	}

	public static int getTimeZone() {
		return TimeZone.getDefault().getRawOffset() / (1000 * 3600);
	}

//	public static String getDistrictUrl(String formatUrl) {
//		String districtUrl = formatUrl;
//		if (RmtApplaction.mDistrict != null) {
//			districtUrl = String.format(formatUrl, RmtApplaction.mDistrict);
//		} else {
//			districtUrl = String.format(formatUrl, "cn");
//		}
//		return districtUrl;
//	}

	public static void setTextViewLeftDrawable(Context mContext, TextView v, Drawable drawable, int width, int height) {
		drawable.setBounds(0, 0, dip2px(mContext, width), dip2px(mContext, height));
		v.setCompoundDrawables(drawable, null, null, null);
	}

	public static void setTextViewRightDrawable(Context mContext, TextView v, Drawable drawable, int width, int height) {
		drawable.setBounds(0, 0, dip2px(mContext, width), dip2px(mContext, height));
		v.setCompoundDrawables(null, null, drawable, null);
	}

	public static boolean isEmail(String str) {
		if (TextUtils.isEmpty(str))
			return false;
		// String regex =
		// "^[a-zA-Z0-9_-]{1,}@[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9_]{2,})+$";
		String regex = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(regex, str);
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public static byte[] decrypBase64(String inStr) {
		return Base64.decode(inStr, Base64.NO_PADDING);
	}

	/**
	 * 是否使用华氏度
	 * 
	 * @param mSettingUnit
	 * @return
	 */
//	public static boolean isUseFahrenheit(SettingUnit mSettingUnit) {
//		if (mSettingUnit.getAppDistrict().equals("LY") || mSettingUnit.getAppDistrict().equals("US") || mSettingUnit.getAppDistrict().equals("MM")) {// 是美国，缅甸，利比亚
//			return true;
//		}
//		return false;
//	}

	/**
	 * 将对应字符串变色
	 * 
	 * @param srcText
	 *            字符串
	 * @param changeText
	 *            需要变颜色的字符串
	 * @param colorResId
	 *            需要变的颜色
	 * @return
	 */
	public static SpannableStringBuilder setTextColor(String srcText, String changeText, int colorResId) {
		try {
			int index = srcText.indexOf(changeText);

			SpannableStringBuilder style = new SpannableStringBuilder(srcText);
			style.setSpan(new ForegroundColorSpan(colorResId), index, index + changeText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
			return style;
		} catch (Exception e) {
			return null;
		}
	}

	/***
	 * 字符传是否包含双字节字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean strContainCNChar(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		for (int i = 0; i < str.length(); i++) {
			if (p.matcher(String.valueOf(str.charAt(i))).matches()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取手机语言
	 * 
	 * @return <br>
	 *         zh_Hant 中文繁体 <br>
	 *         zh_Hans 中文简体 <br>
	 *         en 英文
	 */
	public static String getLanguage() {
		Locale locale = Locale.getDefault();
		String country = locale.getCountry();

		StringBuffer language = new StringBuffer(locale.getLanguage());
		language.append("-");
		language.append(country);
		return language.toString().toLowerCase();
	}

	/**
	 * 获取手机唯一标识
	 * 
	 * @param context
	 * @return
	 */

//	public static String getDeviceId(Context context) {
//		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		return manager.getDeviceId();
//	}
	
	
	public InetAddress getBroadcastAddress(Context context) throws IOException {
		WifiManager myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo myDhcpInfo = myWifiManager.getDhcpInfo();
		if (myDhcpInfo == null) {
			System.out.println("Could not get broadcast address");
			return null;
		}
		int broadcast = (myDhcpInfo.ipAddress & myDhcpInfo.netmask) | ~myDhcpInfo.netmask;
		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++)
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		return InetAddress.getByAddress(quads);
	}


	/**
	 * week最高位用起来的那个版本
	 * @param deviceType
	 * @param localFwVersion   sp当前的固件版本号
	 * @return
	 */
//	public static boolean isSpNewFw(int deviceType , int localFwVersion){
//		if(deviceType == DeviceType.SP_MINI_V2){
//			if(localFwVersion >= 10022){
//				return true;
//			}
//		}else if(deviceType == DeviceType.SP_MINI_CC){
//			if(localFwVersion >= 10006){
//				return true;
//			}
//		}else if(deviceType == DeviceType.SP3 ||deviceType == DeviceType.SP3S_EU
//				|| deviceType == DeviceType.SP3S_US){
//			return true;
//		}else if(deviceType == DeviceType.SP_MINI_HAIBEI){
//			if(localFwVersion >= 10001){
//				return true;
//			}
//		}else if(deviceType == DeviceType.SP_MINI_PLUS){
//			if(localFwVersion >= 10007){
//				return true;
//			}
//		}else if(deviceType == DeviceType.HONYAR_SP2_10A_2 || deviceType == DeviceType.HONYAR_SP2_16A_2){
//			if(localFwVersion >= 10003){
//				return true;
//			}
//		}
//		return false;
//	}


	public static String parseDataToBase64String(byte[] receiverDate) {
		try {
			Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			Method mainMethod = clazz.getMethod("encode", byte[].class);
			mainMethod.setAccessible(true);
			Object retObj = mainMethod.invoke(null, new Object[] { receiverDate });
			return (String) retObj;

		} catch (Exception e) {

		}
		return null;
	}

	public static byte[] parseBase64StringToByte(String dataString) {
		try {
			Class clazz = Class.forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
			Method mainMethod = clazz.getMethod("decode", String.class);
			mainMethod.setAccessible(true);
			Object retObj = mainMethod.invoke(null, dataString);
			return (byte[]) retObj;
		} catch (Exception e) {

		}
		return null;
	}

//	public static int pidToDeviceType(String pid) {
//		if (pid == null)
//			return -1;
//		String type = pid.substring(24);
//		return (int) Other.hexto10(type);
//	}
//
//	public static String deviceTypeToPid(int deviceType) {
//		String pid = "00000000000000000000000011110000";
//		String type = Other.tenTo16_2(Long.parseLong(String.valueOf(deviceType)));
//		return pid.replaceFirst(pid.substring(24, 24 + type.length()), type);
//	}

	public static String macToDid(String mac) {
		return "00000000000000000000" + mac;
	}

	/**
	 * 描述：获取milliseconds表示的日期时间的字符串.
	 *
	 * @param milliseconds
	 *            the milliseconds
	 * @param format
	 *            格式化字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 日期时间字符串
	 */
	public static String getStringByFormat(long milliseconds, String format) {
		String thisDateTime = null;
		try {
			SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
			thisDateTime = mSimpleDateFormat.format(milliseconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return thisDateTime;
	}

	public static final long dateToMillis(String format, String data) {
		try {
			SimpleDateFormat df = new SimpleDateFormat(format);
			return df.parse(data).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return System.currentTimeMillis();
		}
	}

	/**
	 * 得到某年某周的第一天
	 *
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getFirstDayOfWeek(int year, int week) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set (Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, 1);
		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE, week * 7);
		return getFirstDayOfWeek(cal.getTime());
	}


	/**
	 * 得到某年某周的最后一天
	 *
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date getLastDayOfWeek(int year, int week) {
		Calendar c = new GregorianCalendar();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DATE, 1);
		Calendar cal = (GregorianCalendar) c.clone();
		cal.add(Calendar.DATE , week * 7);
		return getLastDayOfWeek(cal.getTime());
	}

	/**
	 * 取得指定日期所在周的最后一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
		return c.getTime();
	}


	/**
	 * 取得指定日期所在周的第一天
	 *
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
		return c.getTime();
	}

	/**
	 *
	 * @return 当前手机年
	 *
	 */
	public static final int getCurrrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

}
