package net.ruinnel.nfc.tasker.func;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class BluetoothToggle extends Function {
	private static final String TAG = BluetoothToggle.class.getSimpleName();

	private final String[] params;
	public BluetoothToggle(Context context, String[] params) {
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
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		String param = (params != null && params.length > 0 ? params[0].toLowerCase() : "");
		if ("toggle".equals(param) || param.length() == 0) {
			if (adapter.isEnabled()) {
				adapter.disable();
				Log.v(TAG, "bluetoothToogle(false)");
			} else {
				adapter.enable();
				Log.v(TAG, "bluetoothToogle(true)");
			}
		} else {
			Log.v(TAG, String.format("bluetoothToogle(%s)", param));
			if ("true".equalsIgnoreCase(param)) {
				adapter.enable();
			} else {
				adapter.disable();
			}
		}
	}
}
