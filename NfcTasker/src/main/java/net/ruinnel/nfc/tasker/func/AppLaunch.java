package net.ruinnel.nfc.tasker.func;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppLaunch extends Function {
	private static final String TAG = AppLaunch.class.getSimpleName();

	private final String[] params;
	private List<ResolveInfo> mResolveInfos;

	public AppLaunch(Context context, String[] params) {
		super(context);
		this.params = params;
		if (params == null) {	// only load add task
			Log.v(TAG, "call loadInstalledApplicationInfo");
			loadInstalledApplicationInfo();
		}
	}

	private void loadInstalledApplicationInfo() {
		final PackageManager pm = context.getPackageManager();

		final Intent main = new Intent(Intent.ACTION_MAIN);
		main.addCategory(Intent.CATEGORY_LAUNCHER);

		mResolveInfos = pm.queryIntentActivities(main, 0);

		Collections.sort(mResolveInfos, new Comparator<ResolveInfo>() {

			@Override
			public int compare(ResolveInfo lhs, ResolveInfo rhs) {
				String lName = lhs.loadLabel(pm).toString();
				String rName = rhs.loadLabel(pm).toString();

				return lName.compareToIgnoreCase(rName);
			}
		});
	}

	private String[] getInstalledApplicationLabels() {
		final PackageManager pm = context.getPackageManager();
		String[] names = new String[mResolveInfos.size()];
		for (int i = 0; i < mResolveInfos.size(); i++) {
			String name = mResolveInfos.get(i).loadLabel(pm).toString();
			names[i] = name;
		}
		return names;
	}

	private String[] getInstalledApplicationPackages() {
		final PackageManager pm = context.getPackageManager();
		String[] names = new String[mResolveInfos.size()];
		for (int i = 0; i < mResolveInfos.size(); i++) {
			String name = mResolveInfos.get(i).activityInfo.packageName;
			names[i] = name;
		}
		return names;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		String[] items = getInstalledApplicationPackages();
		String[] showItems = getInstalledApplicationLabels();
		params.add(new Param(context.getString(R.string.label_param_value), ParamType.SPINNER, items, showItems));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 1) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (params[0] == null || params[0].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_selction), context.getString(R.string.label_param_value));
		}
		return null;
	}

	@Override
	public void run() {
		final PackageManager pm = context.getPackageManager();
		String pkgName = (params != null && params.length > 0 ? params[0] : "");
		Log.v(TAG, String.format("Launch App(%s)", pkgName));
		if (pkgName != null && pkgName.length() > 0) {
			try {
				Intent launch = pm.getLaunchIntentForPackage(pkgName);
				if (launch == null)
					throw new PackageManager.NameNotFoundException();
				launch.addCategory(Intent.CATEGORY_LAUNCHER);
				context.startActivity(launch);
			} catch (PackageManager.NameNotFoundException e) {

			}
		}
	}
}
