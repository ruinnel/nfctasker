package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.telephony.SmsManager;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class Sms extends Function {
	private static final String TAG = Sms.class.getSimpleName();

	private final String[] params;
	public Sms(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		params.add(new Param(context.getString(R.string.label_param_phone_num), ParamType.STRING, context.getString(R.string.hint_param_phone_num)));
		params.add(new Param(context.getString(R.string.label_param_text), ParamType.STRING, context.getString(R.string.hint_param_sms)));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 2) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isPhoneNum(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_phone_num), context.getString(R.string.label_param_phone_num));
		}
		if (params[1] == null || params[1].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_need_value), context.getString(R.string.label_param_text));
		}
		return null;
	}

	@Override
	public void run() {
		SmsManager smsMgr = SmsManager.getDefault();
		String phoneNum = (params != null && params.length >= 1 ? params[0].toLowerCase() : "");
		String text = (params != null && params.length >= 2 ? params[1] : "");
		if (phoneNum != null && phoneNum.length() > 0) {
			smsMgr.sendTextMessage(phoneNum, null, text, null, null);
		}
	}
}
