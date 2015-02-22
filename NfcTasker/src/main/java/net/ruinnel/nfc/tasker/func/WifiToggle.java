package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.net.wifi.WifiManager;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class WifiToggle extends Function {
	private static final String TAG = WifiToggle.class.getSimpleName();

	private final String[] params;
	public WifiToggle(Context context, String[] params) {
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
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		if ("toggle".equals(param) || param.length() == 0) {
			Log.v(TAG, String.format("wifiToggle(%s)", String.valueOf(!wifiManager.isWifiEnabled())));
			wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
		} else {
			Log.v(TAG, String.format("wifiToggle(%s)", param));
			wifiManager.setWifiEnabled("true".equalsIgnoreCase(param));
		}
	}
}
