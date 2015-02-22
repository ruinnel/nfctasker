package net.ruinnel.nfc.tasker;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import net.dinglisch.android.tasker.TaskerIntent;
import net.ruinnel.nfc.tasker.adapter.ViewPagerAdapter;
import net.ruinnel.nfc.tasker.fragment.TaskFragment;
import net.ruinnel.nfc.tasker.widget.BaseActivity;
import net.ruinnel.nfc.tasker.widget.NfcTaskerDialog;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	public static final String EXTRA_IDX = "idx";
	private static final int IDX_MY_TASK = 0;
	private static final int IDX_TASKER = 1;
	private static final int IDX_HELP = 2;

	private static final String KEY_SHOW_HELP = "show_help";

	private Settings mSettings;
	private NfcAdapter mNfcAdapter;
	private SharedPreferences mPref;

	private ViewPager mPager;
	private ViewPagerAdapter mAdapter;
	private int mIdx = 0;

	private NfcTaskerDialog mNfcChkDialog;
	private NfcTaskerDialog mTaskerChkDialog;
	private boolean mNfcCheckCanceled = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSettings = Settings.getInstance(this);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);

		mPager = (ViewPager) findViewById(R.id.main_viewpager);
		mAdapter = new ViewPagerAdapter(this, getSupportFragmentManager());
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		initTabs();

		boolean helpShow = mPref.getBoolean(KEY_SHOW_HELP, false);
		if (helpShow) {
			setPage(IDX_MY_TASK);
		} else {
			setPage(IDX_HELP);
			SharedPreferences.Editor editor = mPref.edit();
			editor.putBoolean(KEY_SHOW_HELP, true);
			editor.commit();
		}

//		Log.i(TAG, "mIdx - " + mIdx);
	}

	private void initTabs() {
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
				mPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {}

			@Override
			public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {}
		};
		ActionBar actionBar = getActionBar();
		actionBar.addTab(actionBar.newTab().setText(R.string.tab_task).setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText(R.string.tab_help).setTabListener(tabListener));
	}

	@Override
	protected void onResume() {
		if (checkNfc()) {
			if (!mSettings.isTaskerGotIt()) {
				checkTasker();
			}
		}
		super.onResume();
	}

	private void checkTasker() {
		final TaskerIntent.Status taskerStatus = TaskerIntent.testStatus(this);
		String title = "";
		String msg = "";
		String btnConfirm = "";
		if (taskerStatus.equals(TaskerIntent.Status.NotInstalled)) {
			title = getString(R.string.notification);
			msg = getString(R.string.msg_please_check_tasker_installed);
			btnConfirm = getString(R.string.install);
		} else if (!taskerStatus.equals(TaskerIntent.Status.OK)){
			title = getString(R.string.notification);
			msg = getString(R.string.msg_please_check_tasker_setting);
			btnConfirm = getString(R.string.confirm);
		}

		if (title.length() > 0) {
			if (mTaskerChkDialog != null && mTaskerChkDialog.isShowing()) {
				mTaskerChkDialog.dismiss();
			}
			mTaskerChkDialog = new NfcTaskerDialog(this);
			mTaskerChkDialog.setTitle(title);
			mTaskerChkDialog.setMessage(msg);
			mTaskerChkDialog.setPositiveButton(btnConfirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					if (taskerStatus.equals(TaskerIntent.Status.NotInstalled)) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=" + getString(R.string.pkg_tasker)));
						startActivity(intent);
					} else {
						Intent intent = getPackageManager().getLaunchIntentForPackage(getString(R.string.pkg_tasker));
						startActivity(intent);
					}
				}
			});
			mTaskerChkDialog.setNegativeButton(R.string.got_it, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					mSettings.setTaskerGotIt(true);
				}
			});
			mTaskerChkDialog.show();
		}
		Log.i(TAG, "taskerStatus = " + taskerStatus);
	}

	private boolean checkNfc() {
		if (mNfcChkDialog != null && mNfcChkDialog.isShowing()) {
			mNfcChkDialog.dismiss();
		}
		// cehck Nfc
		if (!mNfcAdapter.isEnabled() && !mNfcCheckCanceled) {
			mNfcChkDialog = new NfcTaskerDialog(this);
			mNfcChkDialog.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					Intent setnfc = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(setnfc);
				}
			});
			mNfcChkDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					mNfcCheckCanceled = true;
				}
			});
			mNfcChkDialog.setTitle(R.string.notification);
			mNfcChkDialog.setMessage(R.string.msg_please_turn_on_nfc);
			mNfcChkDialog.setCancelable(false);
			mNfcChkDialog.show();

			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(EXTRA_IDX, mIdx);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem add = menu.findItem(R.id.menu_add);
		MenuItem del = menu.findItem(R.id.menu_del);
		if (mPager.getCurrentItem() != IDX_MY_TASK) {
			add.setVisible(false);
			del.setVisible(false);
		} else {
			add.setVisible(true);
			del.setVisible(true);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Fragment current = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_viewpager + ":" + mPager.getCurrentItem());
		switch (item.getItemId()) {
			case R.id.menu_add : {
				if (mPager.getCurrentItem() == IDX_MY_TASK && current != null && current instanceof TaskFragment) {
					((TaskFragment)current).requestAddTask();
					((TaskFragment)current).requestDeleteMode(false);
				}
			}
			break;
			case R.id.menu_del : {
				if (mPager.getCurrentItem() == IDX_MY_TASK && current != null && current instanceof TaskFragment) {
					((TaskFragment)current).requestDeleteMode();
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setPage(int idx) {
		mPager.setCurrentItem(idx);
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onPageSelected(int position) {
		getActionBar().setSelectedNavigationItem(position);
		invalidateOptionsMenu();
	}
}