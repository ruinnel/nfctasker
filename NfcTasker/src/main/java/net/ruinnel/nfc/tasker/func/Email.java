package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.content.Intent;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class Email extends Function {
	private static final String TAG = Email.class.getSimpleName();

	private final String[] params;
	public Email(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		params.add(new Param(context.getString(R.string.label_param_email), ParamType.STRING, context.getString(R.string.hint_param_email)));
		params.add(new Param(context.getString(R.string.label_param_subject), ParamType.STRING));
		params.add(new Param(context.getString(R.string.label_param_text), ParamType.STRING, context.getString(R.string.hint_param_sms)));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 3) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isEmail(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_email), context.getString(R.string.label_param_phone_num));
		}
		if (params[1] == null || params[1].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_need_value), context.getString(R.string.label_param_subject));
		}

		if (params[2] == null || params[2].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_need_value), context.getString(R.string.label_param_text));
		}
		return null;
	}

	@Override
	public void run() {
		String email = (params != null && params.length >= 1 ? params[0].toLowerCase() : "");
		String subject = (params != null && params.length >= 2 ? params[1] : "");
		String text = (params != null && params.length >= 3 ? params[2] : "");

		Intent send = new Intent(Intent.ACTION_SEND);
		send.setType("message/rfc822");
		send.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
		send.putExtra(Intent.EXTRA_SUBJECT, subject);
		send.putExtra(Intent.EXTRA_TEXT, text);
		try {
			context.startActivity(send);
		} catch (android.content.ActivityNotFoundException ex) {
			Log.w(TAG, context.getString(R.string.msg_email_send_fail));
		}
	}
}
