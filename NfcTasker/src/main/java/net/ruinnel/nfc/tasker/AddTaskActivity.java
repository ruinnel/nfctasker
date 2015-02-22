package net.ruinnel.nfc.tasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.func.Function;
import net.ruinnel.nfc.tasker.widget.BaseActivity;

import java.util.*;

public class AddTaskActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
	private static final String TAG = AddTaskActivity.class.getSimpleName();

	public static final String EXTRA_TASK = "task";
	private static final int REQUEST_GET_UID = 412;
	private static final int REQUEST_TAG_WRITE = REQUEST_GET_UID + 1;

	private TextView mTxtTitle;
	private ImageView mImgClose;

	private TextView mTxtFunction;
	private LinearLayout mLayoutParams;
	private EditText mEditName;
	private Button mBtnWrite, mBtnReg;

	private Task mTask;

	private String[] mParamVals;
	private Map<Integer, EditText> mEditTexts;
	private Map<Integer, String[]> mValues;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_task);

		mImgClose = (ImageView) findViewById(R.id.img_add_task_close);
		mTxtTitle = (TextView) findViewById(R.id.txt_add_task_title);
		mTxtFunction = (TextView) findViewById(R.id.txt_add_task_funcion);
		mLayoutParams = (LinearLayout) findViewById(R.id.layout_add_task_param_container);
		mEditName = (EditText) findViewById(R.id.edit_add_task_name);
		mBtnWrite = (Button) findViewById(R.id.btn_add_task_write);
		mBtnReg = (Button) findViewById(R.id.btn_add_task_register);

		mImgClose.setOnClickListener(this);
		mEditName.setSelectAllOnFocus(true);
		mBtnWrite.setOnClickListener(this);
		mBtnReg.setOnClickListener(this);

		mTask = getIntent().getParcelableExtra(EXTRA_TASK);
		if (mTask != null) {
			String simpleName = mTask.getSimpleFunctionName();
			mTxtFunction.setText(simpleName);
			mEditName.setText((mTask.name != null && mTask.name.length() > 0 ? mTask.name : simpleName));
		}

		setTitle(R.string.tit_add_task);
		initParam();
	}

	private void initParam() {
		List<Function.Param> params = mTask.getParamTypes(this);
		mLayoutParams.removeAllViews();
		mParamVals = new String[(params != null ? params.size() : 0)];
		mEditTexts = new HashMap<Integer, EditText>();
		mValues = new HashMap<Integer, String[]>();
		for (int i = 0; (params != null && i < params.size()); i++) {
			Function.Param param = params.get(i);
			switch (param.type) {
				case INT: {
					View view = getLayoutInflater().inflate(R.layout.param_int, null);
					TextView label = (TextView) view.findViewById(R.id.txt_add_task_param_label);
					EditText edit = (EditText) view.findViewById(R.id.edit_add_task_param_value);
					label.setText(param.name);
					if (param.hint != null) {
						edit.setHint(param.hint);
					}
					mEditTexts.put(Integer.valueOf(i), edit);
					mLayoutParams.addView(view);
				}
				break;
				case STRING: {
					View view = getLayoutInflater().inflate(R.layout.param_string, null);
					TextView label = (TextView) view.findViewById(R.id.txt_add_task_param_label);
					EditText edit = (EditText) view.findViewById(R.id.edit_add_task_param_value);
					label.setText(param.name);
					if (param.hint != null) {
						edit.setHint(param.hint);
					}
					mEditTexts.put(Integer.valueOf(i), edit);
					mLayoutParams.addView(view);
				}
				break;
				case SPINNER: {
					View view = getLayoutInflater().inflate(R.layout.param_spinner, null);
					TextView label = (TextView) view.findViewById(R.id.txt_add_task_param_label);
					Spinner spinner = (Spinner) view.findViewById(R.id.spin_add_task_param_value);
					label.setText(param.name);

					String[] items = (param.showValues != null ? param.showValues : param.values);
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner.setAdapter(adapter);
					spinner.setTag(Integer.valueOf(i));
					spinner.setOnItemSelectedListener(this);

					mValues.put(Integer.valueOf(i), param.values);
					mLayoutParams.addView(view);
				}
				break;
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			mEditName.clearFocus();
			mTxtFunction.requestFocus();
		}
	}

	@Override
	public void onClick(View view) {
		if (mEditTexts != null && mEditTexts.size() > 0) {
			Set<Integer> keys = mEditTexts.keySet();
			Iterator<Integer> itr = keys.iterator();
			while (itr.hasNext()) {
				int idx = itr.next();
				EditText edit = mEditTexts.get(Integer.valueOf(idx));
				mParamVals[idx] = edit.getText().toString();
			}
		}
		mTask.setParams(mParamVals);

		String errorMsg = mTask.getErrorMessage(this);
		switch (view.getId()) {
			case R.id.img_add_task_close: {
				finish();
			}
			break;
			case R.id.btn_add_task_write : {
				if (errorMsg != null) {
					showToast(errorMsg);
					return;
				}
				Intent intent = new Intent(this, NfcWriteActivity.class);
				intent.putExtra(NfcWriteActivity.EXTRA_TASK, mTask);
				startActivityForResult(intent, REQUEST_TAG_WRITE);
			}
			break;
			case R.id.btn_add_task_register : {
				if (errorMsg != null) {
					showToast(errorMsg);
					return;
				}
				mTask.name = mEditName.getText().toString();
				Intent intent = new Intent(this, NfcReadActivity.class);
				intent.putExtra(NfcReadActivity.IS_REG_MODE, true);
				startActivityForResult(intent, REQUEST_GET_UID);
			}
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
		if (adapterView != null && adapterView.getTag() != null) {
			int idx = (Integer) adapterView.getTag();
			String[] vals = mValues.get(idx);
			mParamVals[idx] = vals[position];
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {
//		int idx = (Integer) view.getTag();
//		String val = (String) ((Spinner) view).getAdapter().getItem(position);
//		mParamVals[idx] = val;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GET_UID) {
			if (resultCode == Activity.RESULT_OK) {
				String uid = data.getStringExtra(NfcReadActivity.EXTRA_UID);
				mTask.uid = uid;
				mDbMgr.saveTask(mTask);

				showToast(R.string.msg_task_registerd);
				finish();
			}
		} else if (requestCode == REQUEST_TAG_WRITE && resultCode == Activity.RESULT_OK) {
			finish();
		}
	}
}
