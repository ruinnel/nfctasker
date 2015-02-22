package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;

public class FunctionManager {
	private static final String TAG = FunctionManager.class.getSimpleName();

	private static FunctionManager mInstance;

	public static FunctionManager getInstance() {
		if (mInstance == null) {
			mInstance = new FunctionManager();
		}
		return mInstance;
	}

	private FunctionManager() {}

	public Function getFunction(Context context, String clsName, String ... params) {
		try {
			Class cls = Class.forName(clsName);
			Class supr = null;
			while ((supr = cls.getSuperclass()) != null && !Object.class.getName().equals(supr.getName())) {
//				Log.d(TAG, "supr = " + supr.getName());
				if (supr.getName().equals(Function.class.getName())) {
//					Constructor[] constructors = cls.getConstructors();
					Constructor[] constructors = cls.getDeclaredConstructors();
					for (Constructor cons : constructors) {
						Class[] paramTypes = cons.getParameterTypes();
//						Log.d(TAG, "paramTypes = " + Arrays.toString(paramTypes));
//						Log.d(TAG, "params = " + Arrays.toString(params));
						if (paramTypes == null || paramTypes.length == 0) {
							return (Function) cons.newInstance();
						} else if (paramTypes.length == 1) {	// context 파라미터를 사용하는 생성자만 있을 경우
							return (Function) cons.newInstance(context);
						} else {
							return (Function) cons.newInstance(context, params);
						}
					}
					break;
				}
			}
			return null;
		} catch (Exception e) {
			Log.w(TAG, "class load fail!", e);
			return null;
		}
	}
}
