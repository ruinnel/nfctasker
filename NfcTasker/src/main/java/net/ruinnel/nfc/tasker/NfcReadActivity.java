/*
 * Filename	: NfcReadActivity.java
 * Function	:
 * Comment 	:
 * History	: 2015/02/22, ruinnel, Create
 *
 * Version	: 1.0
 * Author   : Copyright (c) 2015 by JC Square Inc. All Rights Reserved.
 */

package net.ruinnel.nfc.tasker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.dinglisch.android.tasker.TaskerIntent;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.data.DatabaseManager;
import net.ruinnel.nfc.tasker.data.TableDef;
import net.ruinnel.nfc.tasker.func.Function;
import net.ruinnel.nfc.tasker.func.Tasker;
import net.ruinnel.nfc.tasker.util.ByteUtil;
import net.ruinnel.nfc.tasker.widget.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NfcReadActivity extends BaseActivity {
	private static final String TAG = NfcReadActivity.class.getSimpleName();

	public static final String IS_REG_MODE = "is_reg_mode";
	public static final String EXTRA_UID = "uid";

	private NfcAdapter mNfcAdapter;
	private DatabaseManager mDbMgr;

	private LinearLayout mLayout;
	private TextView mTxtMsg;

	private String[] mTechList;
	private IntentFilter mFilter;

	private Point mScreenSize;

	private boolean mIsRegMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_read);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mDbMgr = DatabaseManager.getInstance(this);

		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mScreenSize = new Point();
		display.getSize(mScreenSize);

		mIsRegMode = getIntent().getBooleanExtra(IS_REG_MODE, false);

		mLayout = (LinearLayout) findViewById(R.id.layout_nfc_read);
		mTxtMsg = (TextView) findViewById(R.id.txt_nfc_read_msg);

		// touch 이벤트가 ovrride 됨.
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {}
		});

		if (mIsRegMode) {
			mTxtMsg.setText(R.string.msg_please_touch_tag);
		}

		List<String> techList = new ArrayList<String>();
		if (Build.VERSION.SDK_INT > 10) { // GINGERBREAD_MR1
			techList.add(android.nfc.tech.IsoDep.class.getName());
			techList.add(android.nfc.tech.MifareClassic.class.getName());
			techList.add(android.nfc.tech.MifareUltralight.class.getName());
			techList.add(android.nfc.tech.NfcA.class.getName());
			techList.add(android.nfc.tech.NfcB.class.getName());
			techList.add(android.nfc.tech.NfcF.class.getName());
			techList.add(android.nfc.tech.NfcV.class.getName());
			techList.add(android.nfc.tech.NdefFormatable.class.getName());
			techList.add(Ndef.class.getName());
		}
		if (Build.VERSION.SDK_INT > 17) { // JELLY_BEAN_MR1
			techList.add(android.nfc.tech.NfcBarcode.class.getName());
		}
		mTechList = techList.toArray(new String[]{});

		mFilter = new IntentFilter();
		mFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
		mFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
		mFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);

		Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			refresh(tag);
		}
	}

	@Override
	protected void onResume() {
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{mFilter}, new String[][]{mTechList});
		super.onResume();
	}

	@Override
	protected void onPause() {
		mNfcAdapter.disableForegroundDispatch(this);
		super.onPause();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		refresh(tag);
		super.onNewIntent(intent);
	}

	private void refresh(Tag tag) {
		byte[] uid = tag.getId();
		if (!mIsRegMode) {
			NdefMessage ndefMsg = null;
			NdefRecord record = null;
			try {
				Ndef ndef = Ndef.get(tag);
				ndef.connect();
				ndefMsg = ndef.getNdefMessage();
				ndef.close();
				if (ndefMsg != null && ndefMsg.getRecords() != null && ndefMsg.getRecords().length > 0) {
					record = ndefMsg.getRecords()[0];
				}
			} catch (Exception e) {
				Log.w(TAG, "tag read error!");//, e);
			}

			Log.i(TAG, "record = " + record);
			if (record != null && record.getTnf() == NdefRecord.TNF_MIME_MEDIA) {
				String mimeType = new String(record.getType());
				if (getString(R.string.mime_type_tasker).equals(mimeType)) {
					// for mimetype (application/vnd.tasker-launcher) // using before ver 0.5
					byte[] data = record.getPayload();
					if (data != null && data.length > 0) {
						String taskName = "";
						try {
							taskName = new String(data, NfcWriteActivity.UTF_8);
						} catch (UnsupportedEncodingException e) {}

						mTxtMsg.setText(String.format(getString(R.string.format_msg_launch_task), taskName));

						if (!launchTaskerTask(taskName)) {
							showToast(R.string.msg_please_check_tasker_setting);
							finish();
							return;
						} else {
							showToast(R.string.msg_launch_complate);
							finish();
						}
					}
				} else if (getString(R.string.mime_type_nfc_tasker).equals(mimeType)) {
					// for mimetype (application/vnd.nfc-tasker)
					String data = new String(record.getPayload());
					String[] splits = data.split(NfcWriteActivity.DELIMITER);
					if (splits != null && splits.length > 0) {
						String function = String.format("%s.%s", Function.class.getPackage().getName(), splits[0]);
						String params = (splits.length >= 2 ? splits[1] : "");

						Task task = new Task();
						task.function = function;
						task.params = params;

						mTxtMsg.setText(String.format(getString(R.string.format_msg_launch_task), splits[0]));
						launchTask(task);
						finish();
					}
				}
			}

			// find from uid
			String where = TableDef.Task.COL_UID + " = '" + ByteUtil.toHexString(uid, "") + "'";
			List <Task> tasks = mDbMgr.getTasks(where);
			Log.i(TAG, "tasks = " + tasks);
			if (tasks != null && tasks.size() > 0) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < tasks.size(); i++) {
					buf.append(tasks.get(i).name);
					if (i != (tasks.size() - 1)) {
						buf.append("\n");
					}
				}
				mTxtMsg.setText(String.format(getString(R.string.format_msg_launch_task), buf.toString()));

				launchTasks(tasks);
			} else {
				showToast(R.string.msg_task_not_exist);
				finish();
			}
		} else {
			Intent result = new Intent();
			result.putExtra(EXTRA_UID, ByteUtil.toHexString(uid, ""));
			setResult(RESULT_OK, result);
			finish();
		}
	}

	private void launchTasks(List<Task> tasks) {
		for (Task task : tasks) {
			launchTask(task);
		}
		showToast(R.string.msg_launch_complate);
		finish();
	}

	private void launchTask(Task task) {
		if (Tasker.class.getName().equals(task.function)) {
			if (!launchTaskerTask(task.name)) {
				showToast(R.string.msg_please_check_tasker_setting);
				finish();
				return;
			}
		} else {
			task.run(this);
		}
	}

	private boolean launchTaskerTask(String taskName) {
		if (TaskerIntent.testStatus(this).equals(TaskerIntent.Status.OK) ) {
			TaskerIntent intent = new TaskerIntent(taskName);
			sendBroadcast(intent);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int)event.getX();
			int y = (int)event.getY();
			Bitmap bitmapScreen = Bitmap.createBitmap(mScreenSize.x, mScreenSize.y, Bitmap.Config.ARGB_8888);
			if (x < 0 || y < 0)
			return false;
			int ARGB = bitmapScreen.getPixel(x, y);
			if (Color.alpha(ARGB) == 0) {
				finish();
			}
			return true;
		}
		return false;
	}
}
