/*
 * Filename	: JcLog.java
 * Function	:
 * Comment 	:
 * History	: 2014/08/29, ruinnel, Create
 *
 * Version	: 1.0
 * Author   : Copyright (c) 2014 by JC Square Inc. All Rights Reserved.
 */

package net.ruinnel.nfc.tasker.util;

import net.ruinnel.nfc.tasker.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * User: kimdohyeong
 * Date: 13. 8. 28.
 * Time: 오후 2:34
 * To change this template use File | Settings | File Templates.
 */
public class Log {

	public static final int LOG_LEVEL = (BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO);

	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;
	public static final int VERBOSE = 5;


	public static int v(final String tag, final String msg) {
		int ret = -1;
		if (LOG_LEVEL >= VERBOSE) {
			ret = android.util.Log.v(tag, msg);
		}
		return ret;
	}

	public static int v(final String tag, final String msg, final Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= VERBOSE) {
			ret = android.util.Log.v(tag, msg, tr);
		}
		return ret;
	}

	public static int d(final String tag,final  String msg) {
		int ret = -1;
		if (LOG_LEVEL >= DEBUG) {
			ret = android.util.Log.d(tag, msg);
		}
		return ret;
	}

	public static int d(final String tag, final String msg, final Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= DEBUG) {
			ret = android.util.Log.d(tag, msg, tr);
		}
		return ret;
	}

	public static int i(final String tag,final  String msg) {
		int ret = -1;
		if (LOG_LEVEL >= INFO) {
			ret = android.util.Log.i(tag, msg);
		}
		return ret;
	}

	public static int i(final String tag,final  String msg,final  Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= INFO) {
			ret = android.util.Log.i(tag, msg, tr);
		}
		return ret;
	}

	public static int w(final String tag,final  String msg) {
		int ret = -1;
		if (LOG_LEVEL >= WARN) {
			ret = android.util.Log.w(tag, msg);
		}
		return ret;
	}

	public static int w(final String tag, final String msg,final  Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= WARN) {
			ret = android.util.Log.w(tag, msg, tr);
		}
		return ret;
	}

	public static int w(final String tag, final Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= WARN) {
			ret = android.util.Log.w(tag, tr);
		}
		return ret;
	}

	public static int e(final String tag,final  String msg) {
		int ret = -1;
		if (LOG_LEVEL >= ERROR) {
			ret = android.util.Log.e(tag, msg);
		}
		return ret;
	}

	public static int e(final String tag, final String msg, final Throwable tr) {
		int ret = -1;
		if (LOG_LEVEL >= ERROR) {
			ret = android.util.Log.e(tag, msg, tr);
		}
		return ret;
	}
}
