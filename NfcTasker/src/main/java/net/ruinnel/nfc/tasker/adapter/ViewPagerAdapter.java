package net.ruinnel.nfc.tasker.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.fragment.HelpFragment;
import net.ruinnel.nfc.tasker.fragment.TaskFragment;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private static final String TAG = ViewPagerAdapter.class.getSimpleName();

	private List<Class<? extends Fragment>> mFragments = new ArrayList<Class<? extends Fragment>>();
	private List<String> mTitles = new ArrayList<String>();

	private Map<Integer, Fragment> mInstances = new HashMap<Integer, Fragment>();

	public ViewPagerAdapter(Context context, FragmentManager fm) {
		super(fm);

		mFragments.add(TaskFragment.class);
		mFragments.add(HelpFragment.class);

		mTitles.add(context.getString(R.string.tab_task));
		mTitles.add(context.getString(R.string.tab_help));
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitles.get(position);
	}

	@Override
	public Fragment getItem(int position) {
		Class<? extends Fragment> fragmentClass = mFragments.get(position);
		Fragment fragment = null;
		Constructor<? extends Fragment> constructor;
		try {
			fragment = mInstances.get(position);
			if (fragment == null) {
				constructor = fragmentClass.getConstructor();
				fragment = constructor.newInstance();
				mInstances.put(position, fragment);
			}

		} catch (Exception e) {
			Log.w(TAG, "fragment load fail!", e);
		}

		return fragment;
	}
}
