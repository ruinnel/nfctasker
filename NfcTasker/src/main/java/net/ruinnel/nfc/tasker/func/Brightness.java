package net.ruinnel.nfc.tasker.func;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;
import net.ruinnel.nfc.tasker.widget.BrightnessActivity;

import java.util.ArrayList;
import java.util.List;

public class Brightness extends Function {
	private static final String TAG = Brightness.class.getSimpleName();

	private final String[] params;
	public Brightness(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		params.add(new Param(context.getString(R.string.label_param_value), ParamType.INT, context.getString(R.string.hint_param_brightness)));
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
		int brightness = Integer.parseInt(params[0]);
		if (brightness < 0 && brightness > 100) {
			return String.format(context.getString(R.string.msg_invalid_number_range), context.getString(R.string.label_param_value), 1, 100);
		}
		return null;
	}

	@Override
	public void run() {
		String param = (params != null && params.length > 0 ? params[0] : "");
		int brightness = -2;
		try {
			brightness = Integer.parseInt(param);
		} catch (Exception e) {
			Log.w(TAG, "brightness - parse fail!", e);
			return;
		}
		Intent intent = new Intent(context, BrightnessActivity.class);
		intent.putExtra(BrightnessActivity.EXTRA_BRIGHTNESS, brightness);

		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		try {
			Log.v(TAG, String.format("brightness(%d)", brightness));
			pi.send();
		} catch (PendingIntent.CanceledException e) {
			Log.w(TAG, "brightness control fail!", e);
		}
	}
}
