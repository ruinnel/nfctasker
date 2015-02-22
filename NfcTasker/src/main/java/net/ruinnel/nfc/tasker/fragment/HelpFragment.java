package net.ruinnel.nfc.tasker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.yasesprox.android.transcommusdk.TransCommuActivity;
import net.ruinnel.nfc.tasker.R;

public class HelpFragment extends Fragment implements View.OnClickListener {
	private static final String TAG = HelpFragment.class.getSimpleName();

	private View mView;
	private LinearLayout mLayout;

	private Button mBtnContribute;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.frag_help, null);
		mLayout = (LinearLayout) mView.findViewById(R.id.layout_help_container);
		mBtnContribute = (Button) mView.findViewById(R.id.btn_help_trans_commu);

		mLayout.setVisibility(View.VISIBLE);

		mBtnContribute.setOnClickListener(this);

		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!getUserVisibleHint()) {
			return;
		}

		// TODO
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isResumed()) {
			onResume();
		}
	}

	@Override
	public void onDestroyView() {
		mLayout.setVisibility(View.GONE);
		super.onDestroy();
	}

	private void transCommu() {
		Intent intent = new Intent(getActivity(), TransCommuActivity.class);
		intent.putExtra(TransCommuActivity.APPLICATION_CODE_EXTRA, getString(R.string.trans_commu_app_code));
		this.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.btn_help_trans_commu) {
			transCommu();
		}
	}
}
