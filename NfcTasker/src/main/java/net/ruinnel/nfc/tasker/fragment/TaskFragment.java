package net.ruinnel.nfc.tasker.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import net.ruinnel.nfc.tasker.AddActivity;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.adapter.TaskListAdapter;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.data.DatabaseManager;
import net.ruinnel.nfc.tasker.widget.NfcTaskerDialog;
import net.ruinnel.nfc.tasker.widget.NfcTaskerFragment;

import java.util.List;

public class TaskFragment extends NfcTaskerFragment implements AdapterView.OnItemClickListener {
	private static final String TAG = TaskFragment.class.getSimpleName();

	private static final int REQ_ADD = 15125;

	private DatabaseManager mDbMgr;
	private NfcAdapter mNfcAdapter;

	private View mView;
	private ListView mListView;
	private TextView mTxtEmpty;
	private TaskListAdapter mAdapter;
	private List<Task> mTasks;

	public TaskFragment() { }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mAdapter = null;
		mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		mDbMgr = DatabaseManager.getInstance(getActivity());
		mView = inflater.inflate(R.layout.frag_task, null);

		mListView = (ListView) mView.findViewById(R.id.list_task_list);
		mTxtEmpty = (TextView) mView.findViewById(R.id.txt_task_list_empty);

		mListView.setOnItemClickListener(this);

		refresh();
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!getUserVisibleHint()) {
			return;
		}

		refresh();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isResumed()) {
			onResume();
		}
	}

	private void refresh() {
		mTasks = mDbMgr.getTasks(null);
		if (mTasks != null && mTasks.size() > 0) {
			mListView.setVisibility(View.VISIBLE);
			mTxtEmpty.setVisibility(View.GONE);

			mAdapter = new TaskListAdapter(getActivity(), mTasks);
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		} else {
			mListView.setVisibility(View.GONE);
			mTxtEmpty.setVisibility(View.VISIBLE);
		}
	}

	public void requestAddTask() {
		if (mNfcAdapter.isEnabled()) {
			Intent add = new Intent(getActivity(), AddActivity.class);
			startActivityForResult(add, REQ_ADD);
		} else {
			showToast(R.string.msg_please_turn_on_nfc);
		}
	}

	public void requestDeleteMode() {
		if (mAdapter != null) {
			mAdapter.setEditable(!mAdapter.isEditable());
		}
	}

	public void requestDeleteMode(boolean editable) {
		if (mAdapter != null) {
			mAdapter.setEditable(editable);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mAdapter.isEditable()) {
			final Task task = mAdapter.getItem(position);
			NfcTaskerDialog dialog = new NfcTaskerDialog(mAct);
			dialog.setMessage(R.string.msg_task_delete_confirm);
			dialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {
					mDbMgr.deleteTask(task);
					refresh();
				}
			});
			dialog.setNegativeButton(R.string.cancel, null);
			dialog.show();
		}
	}
}
