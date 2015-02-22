/*
 * Filename	: NfcWriteActivity.java
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
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.widget.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NfcWriteActivity extends BaseActivity {
	private static final String TAG = NfcWriteActivity.class.getSimpleName();

	public static final String DELIMITER = ",";
	public static final String UTF_8 = "UTF-8";
	public static final String EXTRA_TASK = "task";

	public String MIME_TYPE_TASKER;
	public String MIME_TYPE_NFC_TASKER;

	private NfcAdapter mNfcAdapter;

	private LinearLayout mLayout;
	private TextView mTxtMsg;

	private String[] mTechList;
	private IntentFilter mFilter;

	private Point mScreenSize;

	private Task mTask;
	private NdefMessage mNdefMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc_read);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		MIME_TYPE_TASKER = getString(R.string.mime_type_tasker);
		MIME_TYPE_NFC_TASKER = getString(R.string.mime_type_nfc_tasker);

		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mScreenSize = new Point();
		display.getSize(mScreenSize);

		mLayout = (LinearLayout) findViewById(R.id.layout_nfc_read);
		mTxtMsg = (TextView) findViewById(R.id.txt_nfc_read_msg);

		// touch 이벤트가 ovrride 됨.
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {}
		});

		List<String> techList = new ArrayList<String>();
		if (Build.VERSION.SDK_INT > 10) { // GINGERBREAD_MR1
			techList.add(android.nfc.tech.IsoDep.class.getName());
			techList.add(android.nfc.tech.MifareClassic.class.getName());
			techList.add(android.nfc.tech.MifareUltralight.class.getName());
			techList.add(android.nfc.tech.NfcA.class.getName());
			techList.add(android.nfc.tech.NfcB.class.getName());
			techList.add(android.nfc.tech.NfcF.class.getName());
			techList.add(android.nfc.tech.NfcV.class.getName());
			techList.add(NdefFormatable.class.getName());
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

		mTask = getIntent().getParcelableExtra(EXTRA_TASK);
		if (mTask == null) {
			showToast(R.string.msg_task_name_empty);
			setResult(RESULT_CANCELED);
			finish();
		} else {
			try {
				String shortName = mTask.getSimpleFunctionName();
				byte[] data = String.format("%s%s%s", shortName, DELIMITER,  mTask.params).getBytes(UTF_8);
				NdefRecord aar = NdefRecord.createApplicationRecord(getApplicationContext().getPackageName());
				NdefRecord mime = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, MIME_TYPE_NFC_TASKER.getBytes(), null, data);
				mNdefMessage = new NdefMessage(new NdefRecord[]{mime, aar});
			} catch (UnsupportedEncodingException e) {
				// UnsupportedEncodingException - (utf-8) 발생안함.
				setResult(RESULT_CANCELED);
				finish();
			}
		}

		mTxtMsg.setText(String.format(getString(R.string.format_please_touch_tag), mNdefMessage.toByteArray().length));
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
		writeTag(tag);
		super.onNewIntent(intent);
	}

	private void writeTag(Tag tag) {
		Ndef ndef = null;
		NdefFormatable formatable = null;
		try {
			ndef = Ndef.get(tag);
			ndef.connect();
			ndef.writeNdefMessage(mNdefMessage);
			ndef.close();

			showToast(R.string.msg_write_complate);
			setResult(RESULT_OK);
			finish();
		} catch (Exception e) {
			try {
				if (ndef != null) {
					ndef.close();
				}
				formatable = NdefFormatable.get(tag);
				formatable.connect();
				formatable.format(mNdefMessage);
				formatable.close();

				showToast(R.string.msg_write_complate);
				setResult(RESULT_OK);
				finish();
			} catch (Exception e1) {
				showToast(R.string.msg_write_fail);
				setResult(RESULT_CANCELED);
				finish();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int)event.getX();
			int y = (int)event.getY();
			Bitmap bitmapScreen = Bitmap.createBitmap(mScreenSize.x, mScreenSize.y, Bitmap.Config.ARGB_8888);
			if(x < 0 || y < 0)
			return false;
			int ARGB = bitmapScreen.getPixel(x, y);
			if(Color.alpha(ARGB) == 0) {
				setResult(RESULT_CANCELED);
				finish();
			}
			return true;
		}
		return false;
	}
}
