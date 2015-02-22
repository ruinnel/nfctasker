package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.provider.Settings;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class ScreenRotateToggle extends Function {
	private static final String TAG = ScreenRotateToggle.class.getSimpleName();

	private final String[] params;
	public ScreenRotateToggle(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		String[] vals = context.getResources().getStringArray(R.array.param_on_off_toggle);
		params.add(new Param(context.getString(R.string.label_param_action), ParamType.SPINNER, vals));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 1) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isOnOffToogle(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_selction), context.getString(R.string.label_param_action));
		}
		return null;
	}

	@Override
	public void run() {
		String param = (params != null && params.length > 0 ? params[0].toLowerCase() : "");
		if ("toggle".equals(param) || param.length() == 0) {
			boolean isEnabled = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
			Log.v(TAG, String.format("ScreenRotateToggle(%s)", String.valueOf(!isEnabled)));
			Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, (!isEnabled ? 1 : 0));
		} else {
			Log.v(TAG, String.format("ScreenRotateToggle(%s)", param));
			Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, ("true".equalsIgnoreCase(param) ? 1 : 0));
		}
	}
}
