package net.ruinnel.nfc.tasker.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.bean.Category;
import net.ruinnel.nfc.tasker.func.Tasker;

import java.util.List;

public class AddGridAdapter extends ArrayAdapter<Category> {
	private static final String TAG = AddGridAdapter.class.getSimpleName();

	private LayoutInflater mInflater;
	public AddGridAdapter(Context context, List<Category> datas) {
		super(context, -1, datas);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add, null);
			holder = new ViewHolder();

			holder.imgIcon = (ImageView) convertView.findViewById(R.id.img_add_item_icon);
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_add_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Category item = getItem(position);
		if (item.function != null && item.function.equals(Tasker.class.getName())) {
			holder.imgIcon.setImageDrawable(getTaskerIcon());
		} else {
			if (item.icon >= 0) {
				holder.imgIcon.setImageResource(item.icon);
			}
		}
		holder.txtName.setText(item.name);

		return convertView;
	}

	public Drawable getTaskerIcon() {
		try {
			PackageManager pk = getContext().getPackageManager();
			return pk.getApplicationIcon(getContext().getString(R.string.pkg_tasker));
		} catch (Exception e) {
			Log.w(TAG, "getTaskerIcon fail", e);
			return getContext().getResources().getDrawable(R.drawable.icon_tasker);
		}
	}

	private class ViewHolder {
		ImageView imgIcon;
		TextView txtName;
	}
}