package net.ruinnel.nfc.tasker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.bean.Task;

import java.util.List;

public class TaskListAdapter extends ArrayAdapter<Task> {
	private static final String TAG = TaskListAdapter.class.getSimpleName();

	private LayoutInflater mInflater;
	private List<Task> mTasks;
	private boolean mEditable = false;
	public TaskListAdapter(Context context, List<Task> datas) {
		super(context, -1, datas);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTasks = datas;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_task, null);
			holder = new ViewHolder();

			holder.txtName = (TextView) convertView.findViewById(R.id.txt_task_item_name);
			holder.txtUid = (TextView) convertView.findViewById(R.id.txt_task_item_uid);
			holder.imgDel = (ImageView) convertView.findViewById(R.id.img_task_item_del);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Task task = mTasks.get(position);
		holder.txtName.setText(task.name);
		if (task.params != null && task.params.length() > 0) {
			holder.txtUid.setText(String.format(getContext().getString(R.string.format_task_desc_params), task.uid, task.getSimpleFunctionName(), task.params));
		} else {
			holder.txtUid.setText(String.format(getContext().getString(R.string.format_task_desc), task.uid, task.getSimpleFunctionName()));
		}

		if (mEditable) {
			holder.imgDel.setVisibility(View.VISIBLE);
		} else {
			holder.imgDel.setVisibility(View.GONE);
		}

		return convertView;
	}

	public void setEditable(boolean editable) {
		mEditable = editable;
		this.notifyDataSetChanged();
	}

	public boolean isEditable() {
		return mEditable;
	}

	private class ViewHolder {
		TextView txtName, txtUid;
		ImageView imgDel;
	}
}