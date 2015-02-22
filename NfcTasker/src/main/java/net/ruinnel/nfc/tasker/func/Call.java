package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class Call extends Function {
	private static final String TAG = Call.class.getSimpleName();

	private final String[] params;
	public Call(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		params.add(new Param(context.getString(R.string.label_param_phone_num), ParamType.STRING, context.getString(R.string.hint_param_phone_num)));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 1) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isPhoneNum(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_phone_num), context.getString(R.string.label_param_phone_num));
		}
		return null;
	}

	@Override
	public void run() {
		String phoneNum = (params != null && params.length > 0 ? params[0] : "");
		if (phoneNum != null && phoneNum.length() > 0) {
			Intent call = new Intent(Intent.ACTION_CALL);
			call.setData(Uri.parse(String.format("tel:%s", phoneNum)));
			context.startActivity(call);
		}
	}
}
