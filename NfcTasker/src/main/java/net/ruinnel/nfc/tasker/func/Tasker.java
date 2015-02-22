package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import net.dinglisch.android.tasker.TaskerIntent;
import net.dinglisch.android.tasker.TaskerValues;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.ValidUtil;

import java.util.ArrayList;
import java.util.List;

public class Tasker extends Function {
	private static final String TAG = Tasker.class.getSimpleName();

	private final String[] params;
	public Tasker(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		List<String> tasks = TaskerValues.getTasks(context);
		String[] items = (tasks != null ? tasks.toArray(new String[0]) : new String[0]);
		params.add(new Param(context.getString(R.string.label_param_tasker_task), ParamType.SPINNER, items));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 1) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (!ValidUtil.isAlphaNumeric(params[0])) {
			return String.format(context.getString(R.string.msg_invalid_selction), context.getString(R.string.label_param_tasker_task));
		}
		return null;
	}

	@Override
	public void run() {
		if (params != null && params.length > 0) {
			for (String taskName : params) {
				if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.OK)) {
					TaskerIntent intent = new TaskerIntent(taskName);
					context.sendBroadcast(intent);
				}
			}
		}
	}
}
