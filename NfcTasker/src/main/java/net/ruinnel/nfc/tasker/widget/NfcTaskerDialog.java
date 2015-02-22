package net.ruinnel.nfc.tasker.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import net.ruinnel.nfc.tasker.R;

public class NfcTaskerDialog extends Dialog {
	private static final String TAG = NfcTaskerDialog.class.getSimpleName();

	private RelativeLayout mLayoutTitle;
	private LinearLayout mLayoutContent, mLayoutBtnArea;
	private TextView mTxtMessage, mTxtTitle;
	private Button mBtnOk, mBtnCancel;

	private int mTxtMessageGravity = -1;

	private CharSequence mTitle, mMessage;

	private boolean mUseOk = false, mUseCancel = false;
	private CharSequence mOk, mCancel;
	private OnClickListener mOkListener, mCancelListener;

	private boolean mPosDismissWhenClick, mNegDismissWhenClick;

	protected View mContentView;

	protected LayoutInflater mInflater;
	private View mView;
	private LayoutParams mParam;

	private boolean mProgressMode = false;
	private ProgressBar mProgressBar;

	protected Toast mToast;

	public NfcTaskerDialog(Context context) {
		super(context, R.style.NfcTasker_Dialog);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(R.layout.dialog, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mView = mInflater.inflate(R.layout.dialog, null);

		// remove shadow
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		super.setContentView(mView, param);

		mLayoutTitle = (RelativeLayout) mView.findViewById(R.id.layout_dialog_title_area);
		mLayoutContent = (LinearLayout) mView.findViewById(R.id.layout_dialog_content_area);
		mLayoutBtnArea = (LinearLayout) mView.findViewById(R.id.layout_dialog_button_area);

		mProgressBar = (ProgressBar) mView.findViewById(R.id.prog_dialog_progressbar);

		mTxtTitle = (TextView) mView.findViewById(R.id.txt_dialog_title);
		mTxtMessage = (TextView) mView.findViewById(R.id.txt_dialog_msg);
		if (mTxtMessageGravity != -1) {
			mTxtMessage.setGravity(mTxtMessageGravity);
		}

		mBtnOk = (Button) mView.findViewById(R.id.btn_dialog_ok);
		mBtnCancel = (Button) mView.findViewById(R.id.btn_dialog_cancel);

		mTxtTitle.setText(mTitle);
		mTxtMessage.setText(mMessage);

		if (mUseOk) {
			mLayoutBtnArea.setVisibility(View.VISIBLE);
			mBtnOk.setText(mOk);
			mBtnOk.setVisibility(View.VISIBLE);
			mBtnOk.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mOkListener != null) {
						mOkListener.onClick(NfcTaskerDialog.this, DialogInterface.BUTTON_POSITIVE);
					}
					if (mPosDismissWhenClick) {
						NfcTaskerDialog.this.dismiss();
					}
				}
			});
		}

		if (mUseCancel) {
			mLayoutBtnArea.setVisibility(View.VISIBLE);
			mBtnCancel.setText(mCancel);
			mBtnCancel.setVisibility(View.VISIBLE);
			mBtnCancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCancelListener != null) {
						mCancelListener.onClick(NfcTaskerDialog.this, DialogInterface.BUTTON_NEGATIVE);
					}
					if (mNegDismissWhenClick) {
						NfcTaskerDialog.this.dismiss();
					}
				}
			});
		}

		if (mContentView != null) {
			mLayoutContent.removeAllViews();

			if (mParam != null) {
				mLayoutContent.addView(mContentView, mParam);
			} else {
				mLayoutContent.addView(mContentView);
			}
		}

		if (mProgressMode) {
			mProgressBar.setVisibility(View.VISIBLE);
		} else {
			mProgressBar.setVisibility(View.GONE);
		}
	}

	protected void showToast(int msgId) {
		if (mToast == null) {
			mToast = Toast.makeText(getContext(), msgId, Toast.LENGTH_LONG);
		} else {
			mToast.setText(msgId);
		}
		mToast.show();
	}

	protected void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(getContext(), msg, Toast.LENGTH_LONG);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}

	public void setProgressMode(boolean mode) {
		mProgressMode = mode;
	}

	@Override
	public void setTitle(int resId) {
		mTitle = getContext().getString(resId);
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setMessage(int msgId) {
		mMessage = getContext().getString(msgId);
	}

	public void setMessage(String msg) {
		mMessage = msg;
	}

	public void setMessageGravity(int gravity) {
		mTxtMessageGravity = gravity;
	}

	@Override
	public void setContentView(int layoutResId) {
		mContentView = mInflater.inflate(layoutResId, null);
	}

	@Override
	public void setContentView(View view) {
		mContentView = view;
	}

	@Override
	public void setContentView(View view, LayoutParams param) {
		mContentView = view;
		mParam = param;
	}

	public View getContentView() {
		return mContentView;
	}

	public void setPositiveButton(int resId, final OnClickListener listener) {
		mOk = getContext().getText(resId);
		mPosDismissWhenClick = true;
		mUseOk = true;
		mOkListener = listener;
	}

	public void setPositiveButton(CharSequence str, final OnClickListener listener) {
		mOk = str;
		mPosDismissWhenClick = true;
		mUseOk = true;
		mOkListener = listener;
	}

	public void setPositiveButton(int resId, final OnClickListener listener, boolean dismissWhenClick) {
		mOk = getContext().getText(resId);
		mPosDismissWhenClick = dismissWhenClick;
		mUseOk = true;
		mOkListener = listener;
	}

	public void setPositiveButton(CharSequence str, final OnClickListener listener, boolean dismissWhenClick) {
		mOk = str;
		mPosDismissWhenClick = dismissWhenClick;
		mUseOk = true;
		mOkListener = listener;
	}

	public void setNegativeButton(int resId, final OnClickListener listener) {
		mCancel = getContext().getText(resId);
		mNegDismissWhenClick = true;
		mUseCancel = true;
		mCancelListener = listener;
	}

	public void setNegativeButton(CharSequence str, final OnClickListener listener) {
		mCancel = str;
		mNegDismissWhenClick = true;
		mUseCancel = true;
		mCancelListener = listener;
	}

	public void setNegativeButton(int resId, final OnClickListener listener, boolean dismissWhenClick) {
		mCancel = getContext().getText(resId);
		mNegDismissWhenClick = dismissWhenClick;
		mUseCancel = true;
		mCancelListener = listener;
	}

	public void setNegativeButton(CharSequence str, final OnClickListener listener, boolean dismissWhenClick) {
		mCancel = str;
		mNegDismissWhenClick = dismissWhenClick;
		mUseCancel = true;
		mCancelListener = listener;
	}

}
