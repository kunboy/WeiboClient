package com.kunboy.weiboclient.debug;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DebugLog {

	protected final String TAG = getClass().getSimpleName();

	public static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static boolean isDebug = true;


	/**
	 * set the value of isDebug. default is false;
	 * 
	 * @param b
	 */
	public static void setIsDebug(boolean b) {
		isDebug = b;
	}

	public static boolean isDebug() {
		return isDebug;
	}

	public static void log(String LOG_CLASS_NAME, Object msg) {
		if (!TextUtils.isEmpty(LOG_CLASS_NAME) && null != msg) {
			if (isDebug) {
				Log.i(LOG_CLASS_NAME,
						"[INFO " + LOG_CLASS_NAME + "] " + String.valueOf(msg));
			}
		}
	}

	public static void log(String TAG, String LOG_CLASS_NAME, Object msg) {
		if (!TextUtils.isEmpty(TAG) && null != msg) {
			if (isDebug) {
				Log.i(TAG,
						"[INFO " + LOG_CLASS_NAME + "] " + String.valueOf(msg));
			}
		}
	}

	protected void Log(Object msg) {
		log(TAG, msg);
	}

	public static void v(String tag, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + message;

			Log.v(tag, message);
		}
	}

	public static void i(String tag, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + message;

			Log.i(tag, message);
		}
	}

	public static void d(String tag, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + message;

			Log.d(tag, message);
		}
	}

	public static void w(String tag, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + message;

			Log.w(tag, message);
		}
	}

	public static void e(String tag, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + message;

			Log.e(tag, message);
		}
	}

	public static void v(String tag, String category, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + category
					+ ">> " + message;

			Log.v(tag, message);
		}
	}

	public static void i(String tag, String category, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + category
					+ ">> " + message;

			Log.i(tag, message);
		}
	}

	public static void d(String tag, String category, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + category
					+ ">> " + message;

			Log.d(tag, message);
		}
	}

	public static void w(String tag, String category, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + category
					+ ">> " + message;

			Log.w(tag, message);
		}
	}

	public static void e(String tag, String category, String message) {
		if (isDebug) {
			StackTraceElement stack[] = Thread.currentThread().getStackTrace();
			message = stack[3].getClassName() + "." + stack[3].getMethodName()
					+ "()<" + stack[3].getLineNumber() + "> : " + category
					+ ">> " + message;

			Log.e(tag, message);
		}
	}
}