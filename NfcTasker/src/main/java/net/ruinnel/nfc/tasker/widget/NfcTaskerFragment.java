package net.ruinnel.nfc.tasker.widget;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import net.ruinnel.nfc.tasker.NfcTasker;
import net.ruinnel.nfc.tasker.Settings;
import net.ruinnel.nfc.tasker.data.DatabaseManager;

public abstract class NfcTaskerFragment extends Fragment {
	private static final String TAG = NfcTaskerFragment.class.getSimpleName();

	protected NfcTasker mApp;
	protected Settings mSettings;
	protected BaseActivity mAct;
	protected DatabaseManager mDbMgr;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAct = (BaseActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbMgr = DatabaseManager.getInstance(mAct);
		mApp = (NfcTasker) mAct.getApplication();
		mSettings = Settings.getInstance(mAct);
	}

	public void requestRefresh() {
		if (getUserVisibleHint() && isResumed()) {
			onResume();
		}
	}

	public void showToast(int strResId) {
		mAct.showToast(strResId);
	}

	public void showToast(String msg) {
		mAct.showToast(msg);
	}

	public void showProgress() {
		mAct.showProgress();
	}

	public void hideProgress() {
		mAct.hideProgress();
	}
}
