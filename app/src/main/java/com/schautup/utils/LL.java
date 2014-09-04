/**
 * Copyright (C) 2012 Cellular GmbH 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.schautup.utils;

import android.util.Log;

/**
 * My logger class
 * 
 * @author Chris Xinyue Zhao <hasszhao@gmail.com>
 */
public final class LL {

	private static boolean mDebugable = true;
	private static final String DEBUG_TAG = "#!#!";
	private static LL sLogger = new LL();

	private static String mkMessage(String msg) {
		return DEBUG_TAG + msg + DEBUG_TAG;
	}

	private static String mkTag() {
		int stackDepth = 5;
		String[] arrClassName = Thread.currentThread().getStackTrace()[stackDepth].getClassName().split("\\.");
		String className = arrClassName[arrClassName.length - 1];
		String methodName = Thread.currentThread().getStackTrace()[stackDepth].getMethodName();
		int lineNumber = Thread.currentThread().getStackTrace()[stackDepth].getLineNumber();
		return className + "." + methodName + " @line: " + lineNumber;
	}

	public void info(String _msg) {
		if (mDebugable) {
			Log.i(mkTag(), mkMessage(_msg));
		}
	}

	public void info(String _msg, Throwable _t) {
		if (mDebugable) {
			Log.i(mkTag(), mkMessage(_msg), _t);
		}
	}

	public void warn(String _msg) {
		if (mDebugable) {
			Log.w(mkTag(), mkMessage(_msg));
		}
	}

	public void warn(String _msg, Throwable _t) {
		if (mDebugable) {
			Log.w(mkTag(), mkMessage(_msg), _t);
		}
	}

	public void debug(String _msg) {
		if (mDebugable) {
			Log.d(mkTag(), mkMessage(_msg));
		}
	}

	public void debug(String _msg, Throwable _t) {
		if (mDebugable) {
			Log.d(mkTag(), mkMessage(_msg), _t);
		}
	}

	public void error(String _msg) {
		if (mDebugable) {
			Log.e(mkTag(), mkMessage(_msg));
		}
	}

	public void error(String _msg, Throwable _t) {
		if (mDebugable) {
			Log.e(mkTag(), mkMessage(_msg), _t);
		}
	}

	public void fatal(String _msg) {
		if (mDebugable) {
			Log.e(mkTag(), mkMessage(_msg));
		}
	}

	public void fatal(String _msg, Throwable _t) {
		if (mDebugable) {
			Log.e(mkTag(), mkMessage(_msg), _t);
		}
	}

	public static void i(String _tag, String _info) {
		sLogger.info(_info);
	}

	public static void i(String _tag, String _info, Throwable _throwable) {
		sLogger.info(_info, _throwable);
	}

	public static void d(String _tag, String _debug) {
		sLogger.debug(_debug);
	}

	public static void d(String _tag, String _debug, Throwable _throwable) {
		sLogger.debug(_debug, _throwable);
	}

	public static void e(String _tag, String _error) {
		sLogger.error(_error);
	}

	public static void e(String _tag, String _error, Throwable _throwable) {
		sLogger.error(_error, _throwable);
	}

	public static void v(String _tag, String _verbose) {
		sLogger.info(_verbose);
	}

	public static void v(String _tag, String _verbose, Throwable _throwable) {
		sLogger.info(_verbose, _throwable);
	}

	public static void w(String _tag, String _warning) {
		sLogger.warn(_warning);
	}

	public static void w(String _tag, String _warning, Throwable _throwable) {
		sLogger.warn(_warning, _throwable);
	}

	public static void i(String _info) {
		sLogger.info(_info);
	}

	public static void i(String _info, Throwable _throwable) {
		sLogger.info(_info, _throwable);
	}

	public static void d(String _debug) {
		sLogger.debug(_debug);
	}

	public static void d(String _debug, Throwable _throwable) {
		sLogger.debug(_debug, _throwable);
	}

	public static void e(String _error) {
		sLogger.error(_error);
	}

	public static void e(String _error, Throwable _throwable) {
		sLogger.error(_error, _throwable);
	}

	public static void v(String _verbose) {
		sLogger.info(_verbose);
	}

	public static void v(String _verbose, Throwable _throwable) {
		sLogger.info(_verbose, _throwable);
	}

	public static void w(String _warning) {
		sLogger.warn(_warning);
	}

	public static void w(String _warning, Throwable _throwable) {
		sLogger.warn(_warning, _throwable);
	}

	public static String getStackTraceString(Throwable _e) {
		return Log.getStackTraceString(_e);
	}

	public static LL getLogger() {
		return sLogger;
	}

	public static void setDebugable(boolean _debugable) {
		mDebugable = _debugable;
	}
}
