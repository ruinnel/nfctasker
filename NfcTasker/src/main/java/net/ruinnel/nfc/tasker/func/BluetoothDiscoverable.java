package net.ruinnel.nfc.tasker.func;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDiscoverable extends Function {
	private static final String TAG = BluetoothDiscoverable.class.getSimpleName();

	private final String[] params;
	public BluetoothDiscoverable(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		params.add(new Param(context.getString(R.string.label_param_value), ParamType.INT, context.getString(R.string.hint_param_bluetooth_discoverable)));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 1) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isNumber(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_number), context.getString(R.string.label_param_value));
		}
		return null;
	}

	@Override
	public void run() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		String param = (params != null && params.length > 0 ? params[0].toLowerCase() : "");
		int duration = Integer.parseInt(param);
		if (adapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Log.v(TAG, "BluetoothDiscoverable()");
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
			context.startActivity(discoverableIntent);
		} else {
			Log.v(TAG, "BluetoothDiscoverable() - now discoverable!");
		}
	}
}
