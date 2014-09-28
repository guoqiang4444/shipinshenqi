package com.lzc.pineapple.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lzc.pineapple.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Utils {
	public static final SimpleDateFormat Format_Wall_D = new SimpleDateFormat(
			"MM-dd HH:mm", Locale.ENGLISH);

	public static final SimpleDateFormat Format_Wall_T = new SimpleDateFormat(
			"HH:mm", Locale.ENGLISH);

	public static final SimpleDateFormat Format_Star_Trace = new SimpleDateFormat(
			"yyyy MM dd", Locale.ENGLISH);

	public static final SimpleDateFormat Format_VIP = new SimpleDateFormat(
			"yyyy/MM/dd", Locale.ENGLISH);

	public static final SimpleDateFormat Format_BIRTH = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);

	public static final SimpleDateFormat Format_NEW_MSG = new SimpleDateFormat(
			"yyyy年MM月dd日", Locale.CHINESE);

	private static float app_density = 0.1f;

	private static int screen_width;

	private static int screen_height;

	public static boolean isEmpty(String str) {
		if (null == str || str.trim().equals("")) {
			return true;
		}
		return false;
	}

	public static boolean isNetValid(Context context) {
		if (null == context) {
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
			return true;
		} else if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		}
		return false;
	}

	public static String generateTime(long sec) {
		Date date = new Date(sec);
		return Format_Wall_D.format(date);
	}

	public static String generateChatTime(long sec) {
		if (sec != 0) {
			return Format_Wall_D.format(sec);
		}
		return "";
	}

	public static String generateVipTime(long sec) {
		if (sec != 0) {
			return Format_VIP.format(sec);
		}
		return "";
	}

	public static String generateTraceTime(String sec) {
		if (!isEmpty(sec)) {
			long seconds = Long.parseLong(sec);
			return Format_Star_Trace.format(seconds);
		}
		return "";
	}

	public static String generateTime(Date date) {
		Calendar curTime = Calendar.getInstance();
		Calendar msgTime = Calendar.getInstance();
		msgTime.setTime(date);
		if (curTime.get(Calendar.DAY_OF_YEAR) == msgTime
				.get(Calendar.DAY_OF_YEAR)) {
			if (curTime.get(Calendar.HOUR_OF_DAY) == msgTime
					.get(Calendar.HOUR_OF_DAY)) {
				int min = curTime.get(Calendar.MINUTE)
						- msgTime.get(Calendar.MINUTE);
				if (min < 1) {
					min = 1;
				}
				return min + "";
			} else {
				return Format_Wall_T.format(date);
			}
		} else {
			return Format_Wall_D.format(date);
		}
	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static boolean isFile(String str) {
		if (Utils.isEmpty(str)) {
			return false;
		}
		File file = new File(str);
		return file.isFile();
	}

	public static boolean isExist(String str) {
		if (Utils.isEmpty(str)) {
			return false;
		}
		File file = new File(str);
		return file.exists();
	}

	public static boolean makesureFileExist(String filePath) {
		if (Utils.isEmpty(filePath)) {
			return false;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			File parent = file.getParentFile();
			if ((parent != null) && (!parent.exists()))
				parent.mkdirs();

			try {
				file.createNewFile();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	public static void enableStrictMode() {
		if (Utils.hasGingerbread()) {
			StrictMode.ThreadPolicy.Builder threadPolicyBuilder = new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog();
			StrictMode.VmPolicy.Builder vmPolicyBuilder = new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog();

			if (Utils.hasHoneycomb()) {
				threadPolicyBuilder.penaltyFlashScreen();
			}
			StrictMode.setThreadPolicy(threadPolicyBuilder.build());
			StrictMode.setVmPolicy(vmPolicyBuilder.build());
		}
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static int dip2px(Context context, float dpValue) {
		if (app_density == 0.1f) {
			if (null == context) {
				return (int) dpValue;
			}
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			app_density = metrics.density;
			screen_width = metrics.widthPixels;
			screen_height = metrics.heightPixels;
		}
		return (int) (dpValue * app_density + 0.5f);
	}

	public static int dip2px(Context context, int dpValue) {
		if (app_density == 0.1f) {
			if (null == context) {
				return (int) dpValue;
			}
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			app_density = metrics.density;
			screen_width = metrics.widthPixels;
			screen_height = metrics.heightPixels;
		}
		return (int) (dpValue * app_density + 0.5f);
	}

	public static void initDip2px(Context context) {
		if (null != context) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			app_density = metrics.density;
			screen_width = metrics.widthPixels;
			screen_height = metrics.heightPixels;
		}
	}

	public static int getScreenWidth() {
		return screen_width;
	}

	public static int getScreenHeight() {
		return screen_height;
	}

	public static int px2dip(int px) {
		return (int) (px / app_density + 0.5f);
	}

	public static float getTextNum(Editable s) {
		float charNum = 0;
		final int length = s.length();
		char[] dest = new char[length];
		s.getChars(0, length, dest, 0);
		for (char c : dest) {
			if (c > 127) {
				charNum++;
			} else {
				charNum += 0.5;
			}
		}
		return charNum;
	}

	public static float getTextNum(String src) {
		float charNum = 0;
		final int length = src.length();
		char[] dest = new char[length];
		src.getChars(0, length, dest, 0);
		for (char c : dest) {
			if (c > 127) {
				charNum++;
			} else {
				charNum += 0.5;
			}
		}
		return charNum;
	}

	public static String parseHtml(String source) {
		if (!Utils.isEmpty(source)) {
			return source.replace("&amp;", "&").replace("&quot;", "\"")
					.replace("&lt;", "<").replace("&gt;", ">")
					.replace("&apos;", "\'").replace("&nbsp;", " ")
					.replace("&#39", "\'");
		}
		return source;
	}

	/**
	 * 显示dialog
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param indeterminate
	 * @param cancelable
	 * @return ProgressDialog
	 * @Title: showProgress
	 * @date 2013-10-14 下午3:46:21
	 */
	public static ProgressDialog showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
        try{
        	dialog.show();
        }catch(Exception e){
        	
        }
		return dialog;
	}

	/**
	 * 取消dialog
	 * 
	 * @param loading
	 * @Title: dismissDialog
	 * @date 2013-10-14 下午3:46:21
	 */
	public static void dismissDialog(ProgressDialog loading) {
		if (loading != null && loading.isShowing()) {
			loading.dismiss();
			loading.cancel();
		}
	}

	/**
	 * 保存rom值
	 * 
	 * @param key
	 * @param value
	 * @return void
	 * @Title: savePreference
	 * @date 2013-10-14 下午3:46:36
	 */
	public static void savePreference(Context context, String key, String value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, value).commit();

	}

	/**
	 * 获取保存在preference里的值
	 * 
	 * @param key
	 * @return String
	 * @Title: getPreference
	 * @date 2013-10-14 下午3:46:59
	 */
	public static String getPreference(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(key, null);
	}

	public static void setBooleanPrefrence(Context context, String key,
			boolean value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(key, value).commit();
	}

	public static boolean getBooleanPrefrence(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(key, true);

	}

	public static boolean getDefaultFalsePrefrence(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(key, false);

	}

	/**
	 * 将对应key的value删除
	 * 
	 * @param key
	 * @return void
	 * @Title: removePreference
	 * @date 2013-10-14 下午3:47:24
	 */
	public static void removePreference(Context context, String key) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.remove(key).commit();
	}

	public static void savePreference(Context context, String key,
			Set<String> value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putStringSet(key, value).commit();
	}

	public static Set<String> getPreferenceSet(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getStringSet(key, null);
	}

	public static void savePreferenceInt(Context context, String key, int value) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(key, value).commit();
	}

	public static int getPreferenceInt(Context context, String key) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				key, -1);
	}

	public static int getPreferenceInt(Context context, String key, int defValue) {
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(
				key, defValue);
	}

	public static void saveJSONObject(Context context, String key,
			JSONObject object) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, object.toString()).commit();

	}

	public static void saveJSONArray(Context context, String key,
			JSONArray array) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(key, array.toString()).commit();

	}

	public static JSONObject loadJSONObject(Context context, String key)
			throws JSONException {

		return new JSONObject(PreferenceManager.getDefaultSharedPreferences(
				context).getString(key, "{}"));
	}

	public static JSONArray loadJSONArray(Context context, String key)
			throws JSONException {

		return new JSONArray(PreferenceManager.getDefaultSharedPreferences(
				context).getString(key, "[]"));
	}

	public static void remove(Context context, String prefName, String key) {
		SharedPreferences settings = context.getSharedPreferences(prefName, 0);
		if (settings.contains(key)) {
			SharedPreferences.Editor editor = settings.edit();
			editor.remove(key);
			editor.commit();
		}
	}

	/**
	 * 是否有网络
	 * 
	 * @param context
	 * @return boolean
	 * @Title: hasInternet
	 * @date 2013-10-14 下午3:51:40
	 */
	public static boolean hasInternet(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			// 判断APN类型
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * 是否是wifi网络
	 * 
	 * @param context
	 * @return boolean
	 * @Title: isWifi
	 * @date 2013-10-14 下午3:52:19
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}
    public static boolean isNotWifi(Context context){
    	ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() != ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
    }
	/**
	 * 获取Activity的根view
	 * 
	 * @param activity
	 * @return View
	 * @Title: getRootView
	 * @date 2013-10-14 下午3:54:11
	 */
	public static View getRootView(Activity activity) {
		return ((ViewGroup) activity.findViewById(android.R.id.content))
				.getChildAt(0);
	}

	/**
	 * 实现设置图片渐变效果
	 * 
	 * @param imageView
	 * @param bitmap
	 * @param mContext
	 * @return void
	 * @Title: setImageBitmap
	 * @date 2013-10-14 下午3:55:27
	 */
	public static void setImageBitmap(ImageView imageView, Bitmap bitmap,
			Context mContext) {
		// Use TransitionDrawable to fade in.
		final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
				new ColorDrawable(android.R.color.transparent),
				new BitmapDrawable(mContext.getResources(), bitmap) });
		// noinspection deprecation
		imageView.setBackgroundDrawable(imageView.getDrawable());
		imageView.setImageDrawable(td);
		td.startTransition(200);
	}

	/**
	 * 判断点是否在view中
	 * 
	 * @param point
	 * @param view
	 * @return boolean
	 * @throws IllegalArgumentException
	 * @Title: isPointInsideViewBoundsOnScreen
	 * @date 2013-10-14 下午3:56:15
	 */
	public static boolean isPointInsideViewBoundsOnScreen(Point point, View view)
			throws IllegalArgumentException {

		if (point == null || view == null)
			throw new IllegalArgumentException(
					"point or view should not be null !");

		int pointX = point.x;
		int pointY = point.y;

		int viewLocation[] = new int[2];
		view.getLocationOnScreen(viewLocation);
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();

		if ((pointX >= viewLocation[0] && pointX <= viewLocation[0] + viewWidth)
				&& (pointY >= viewLocation[1] && pointY <= viewLocation[1]
						+ viewHeight)) {
			return true;
		}

		return false;

	}

	/**
	 * 检查是否有sd卡
	 * 
	 * @return boolean
	 * @Title: checkSDCard
	 * @date 2013-10-14 下午4:11:01
	 */
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 手机震动
	 * 
	 * @param context
	 * @param milliseconds
	 * @return void
	 * @Title: Vibrate
	 * @date 2013-10-18 上午10:11:41
	 */
	public static void Vibrate(final Context context, long milliseconds) {

		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(milliseconds);

	}

	public static void showExitDialog(Context context,
			final OnDialogDoneListener onDialogDoneListener) {

		new AlertDialog.Builder(context).setTitle("提示").setMessage("确定要退出吗？")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}

				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();

						onDialogDoneListener.onDialogDone();
					}
				})

				.show();
	}

	public static void showDialog(Context context, String msg,
			final OnDialogDoneListener onDialogDoneListener) {
		showDialog(context,msg,true,onDialogDoneListener);
	}
	public static void showDialog(Context context, String msg,boolean cancelable,
			final OnDialogDoneListener onDialogDoneListener) {

		new AlertDialog.Builder(context)
		        .setTitle("提示")
		        .setMessage(msg)
		        .setCancelable(cancelable)
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						onDialogDoneListener.onDialogCancel();
					}

				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();

						onDialogDoneListener.onDialogDone();
					}
				}).show();
	}

	public interface OnDialogDoneListener {
		public void onDialogDone();
		public void onDialogCancel();
	}

	public static byte[] getPhotoBytes(Bitmap bitmap) {
		byte[] datas = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		try {
			datas = baos.toByteArray();
			baos.flush();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return datas;
	}

	public static Bitmap getBitmap(Context context, Uri uri) {

		Bitmap bitmap = null;
		InputStream is = null;
		try {

			is = getInputStream(context, uri);

			bitmap = BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
		return bitmap;

	}

	/**
	 * 此处写方法描述
	 * 
	 * @Title: getInputStream
	 * @param mUri
	 * @return
	 * @throws IOException
	 * @return InputStream
	 * @date 2012-12-17 下午2:00:52
	 */
	public static InputStream getInputStream(Context context, Uri mUri)
			throws IOException {
		try {
			if (mUri.getScheme().equals("file")) {
				return new java.io.FileInputStream(mUri.getPath());
			} else {
				return context.getContentResolver().openInputStream(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	public static void showTips(Context context, String str) {
		final Toast t = new Toast(context);
		t.setGravity(Gravity.TOP, 0, 0);
		t.setDuration(Toast.LENGTH_SHORT);
		final LayoutInflater mInflater = LayoutInflater.from(context);
		final View v = mInflater.inflate(R.layout.pineapple_tips, null);
		final TextView text = (TextView) v.findViewById(R.id.tips_text);
		text.setText(str);
		v.setMinimumWidth(9999);
		v.setMinimumHeight(dip2px(context, 48));
		t.setView(v);
		t.show();
	}

	public static AlertDialog.Builder getHoloAlertDialog(Context context) {
		AlertDialog.Builder builder;
		builder = new AlertDialog.Builder(context)
				.setIcon(R.drawable.ic_launcher);
		return builder;
	}

	/**
	 * 把一个jsonObject转换成一个bean
	 * 
	 * @param jo
	 * @param cls
	 * @return
	 */
	public static Object toBean(JSONObject jo, Class<?> cls) {
		Iterator<String> it = jo.keys();
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (Exception e) {
		}
		while (it.hasNext()) {
			try {
				String name = it.next();
				Object o = jo.opt(name);
				setter(obj, updateFrist(name), o, cls.getDeclaredField(name)
						.getType());
			} catch (Exception e) {
			}
		}
		return obj;
	}

	/**
	 * @param obj
	 *            操作的对象
	 * @param att
	 *            操作的属性
	 * @param value
	 *            设置的值
	 * @param type
	 *            参数的属性
	 * */
	private static void setter(Object obj, String att, Object value,
			Class<?> type) {
		try {
			Method method = obj.getClass().getMethod("set" + att, type);
			method.invoke(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行get方法
	 * 
	 * @param obj
	 * @param att
	 */
	private static void getter(Object obj, String att) {
		try {
			Method method = obj.getClass().getMethod("get" + att);
			System.out.println("======" + method.invoke(obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将首字母改成大写
	 * 
	 * @param fldName
	 * @return
	 */
	private static String updateFrist(String fldName) {
		String first = fldName.substring(0, 1).toUpperCase();
		String rest = fldName.substring(1, fldName.length());
		String newStr = new StringBuffer(first).append(rest).toString();
		return newStr;
	}

	public static String getJsonValue(JSONObject object, String name) {
		try {
			return !object.isNull(name) ? object.getString(name) : "";
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getSysNowTime() {
		// Date now = new Date();
		// System.currentTimeMillis();
		java.text.DateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:MM:ss");
		String formatTime = format.format(System.currentTimeMillis());
		return formatTime;
	}
    public static void displayPosterImage(String url,final ImageView imageview){
    	ImageLoader.getInstance().displayImage(url, imageview,
				ImageLoaderConfig.getPosterImageOption(),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						// TODO Auto-generated method stub
						// imageView.setScaleType(ScaleType.CENTER_INSIDE);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
					}
				});
    }
	public static void displayImage(String url, final ImageView imageView) {
		
		ImageLoader.getInstance().displayImage(url, imageView,
				ImageLoaderConfig.getVerticalImageOption(),
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						// TODO Auto-generated method stub
						// imageView.setScaleType(ScaleType.CENTER_INSIDE);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
					}
				});
	}

	public static String encodeUrl(String url) {
//		url = UrlDecodeUtils.decrypt(Constant.key, url);
		try {
			url = URLEncoder.encode(url, "utf-8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 编码之后的路径中的“/”也变成编码的东西了 所有还有将其替换回来 这样才是完整的路径
		url = url.replaceAll("%3A", ":").replaceAll("%2F", "/");

		return url;
	}
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		return deviceId;
	}
}
