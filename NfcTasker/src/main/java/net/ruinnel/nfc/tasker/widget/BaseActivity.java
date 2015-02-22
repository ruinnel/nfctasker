package net.ruinnel.nfc.tasker.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import net.ruinnel.nfc.tasker.NfcTasker;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.Settings;
import net.ruinnel.nfc.tasker.data.DatabaseManager;

abstract public class BaseActivity extends FragmentActivity {
	private static final String TAG = BaseActivity.class.getSimpleName();

	protected NfcTasker mApp;
	protected Settings mSettings;
	protected DatabaseManager mDbMgr;

	protected Toast mToast;
	protected NfcTaskerDialog mDialogProgress;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
		return super.dispatchTouchEvent(ev);
	}

	public void showToast(int strResId) {
		showToast(getString(strResId));
	}

	public void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	public void showProgress() {
		hideProgress();
		mDialogProgress = new NfcTaskerDialog(this);
		mDialogProgress.setProgressMode(true);
		mDialogProgress.setMessage(R.string.msg_please_wait);
		mDialogProgress.show();
	}

	public void hideProgress() {
		if (mDialogProgress != null) {
			mDialogProgress.dismiss();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbMgr = DatabaseManager.getInstance(this);
		mApp = (NfcTasker) getApplication();
		mSettings = Settings.getInstance(this);
	}
}
