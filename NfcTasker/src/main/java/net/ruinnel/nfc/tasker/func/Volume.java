package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.media.AudioManager;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class Volume extends Function {
	private static final String TAG = Volume.class.getSimpleName();

	private static final int IDX_ALARM = 0;
	private static final int IDX_DTMF = 1;
	private static final int IDX_MUSIC = 2;
	private static final int IDX_NOTIFICATION = 3;
	private static final int IDX_RING = 4;
	private static final int IDX_SYSTEM = 5;
	private static final int IDX_VOICE_CALL = 6;

	private final String[] params;
	public Volume(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		String[] vals = context.getResources().getStringArray(R.array.param_volume_type);
		params.add(new Param(context.getString(R.string.label_param_type), ParamType.SPINNER, vals));
		params.add(new Param(context.getString(R.string.label_param_value), ParamType.INT, context.getString(R.string.hint_param_volume)));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 2) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (params[0] == null || params.length == 0) {
			return String.format(context.getString(R.string.msg_invalid_selction), context.getString(R.string.label_param_tasker_task));
		}

		if (!ValidUtil.isNumber(params[1])) {
			return String.format(context.getString(R.string.msg_invalid_number), context.getString(R.string.label_param_value));
		}
		int volume = Integer.parseInt(params[1]);
		if (volume < 0 && volume > 100) {
			return String.format(context.getString(R.string.msg_invalid_number_range), context.getString(R.string.label_param_value), 1, 100);
		}
		return null;
	}

	private int getStreamType(String type) {
		String[] types = context.getResources().getStringArray(R.array.param_volume_type);
		int idx = -1;
		for (int i = 0; i < types.length; i++) {
			if (types[i].equalsIgnoreCase(type)) {
				idx = i;
				break;
			}
		}

		switch (idx) {
			case IDX_ALARM :
				return AudioManager.STREAM_ALARM;
			case IDX_DTMF :
				return AudioManager.STREAM_DTMF;
			case IDX_MUSIC :
				return AudioManager.STREAM_MUSIC;
			case IDX_NOTIFICATION :
				return AudioManager.STREAM_NOTIFICATION;
			case IDX_RING :
				return AudioManager.STREAM_RING;
			case IDX_SYSTEM :
				return AudioManager.STREAM_SYSTEM;
			case IDX_VOICE_CALL :
				return AudioManager.STREAM_VOICE_CALL;
		}
		return -1;
	}

	@Override
	public void run() {
		AudioManager audioMgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		String type = (params != null && params.length > 0 ? params[0] : "");
		int volume = (params != null && params.length > 1 ? Integer.parseInt(params[1]) : -1);
		int streamType = getStreamType(type);
		int max;

		if (streamType > 0) {
			max = audioMgr.getStreamMaxVolume(streamType);
			audioMgr.setStreamVolume(streamType, (int) (volume * (max / 100.0)), AudioManager.FLAG_SHOW_UI);
			Log.v(TAG, String.format("Volume(%s, %d)", type, volume));
		}
	}
}
