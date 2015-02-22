/*
 * Filename	: DialogActivity.java
 * Function	:
 * Comment 	:
 * History	: 2014/05/08, ruinnel, Create
 *
 * Version	: 1.0
 * Author   : Copyright (c) 2014 by JC Square Inc. All Rights Reserved.
 */

package net.ruinnel.nfc.tasker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import net.dinglisch.android.tasker.TaskerIntent;
import net.ruinnel.nfc.tasker.adapter.AddGridAdapter;
import net.ruinnel.nfc.tasker.bean.Category;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.func.Tasker;
import net.ruinnel.nfc.tasker.widget.BaseActivity;
import net.ruinnel.nfc.tasker.widget.NfcTaskerDialog;

import java.util.List;
import java.util.Stack;

public class AddActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
	private static final String TAG = AddActivity.class.getSimpleName();

	private static final int REQUEST_TAG_WRITE = 849;

	private LinearLayout mLayout;

	private TextView mTxtTitle;
	private ImageView mImgBack;
	private ImageView mImgClose;

	private GridView mGridView;
	private AddGridAdapter mAdapter;
	private List<Category> mCategories;

	private Point mScreenSize;

	private Stack<Category> mStack;

	private Task mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mScreenSize = new Point();
		display.getSize(mScreenSize);

		mLayout = (LinearLayout) findViewById(R.id.layout_add);
		mTxtTitle = (TextView) findViewById(R.id.txt_add_title);
		mImgBack = (ImageView) findViewById(R.id.img_add_back);
		mImgClose = (ImageView) findViewById(R.id.img_add_close);

		mImgBack.setOnClickListener(this);
		mImgClose.setOnClickListener(this);

		mGridView = (GridView) findViewById(R.id.grid_add);
		mGridView.setOnItemClickListener(this);

		// touch 이벤트가 ovrride 됨.
		mLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {}
		});

		mCategories = mApp.mCategories;
		mStack = new Stack<Category>();
		refresh();
	}

	private void refresh() {
		if (mStack.size() == 0) {
			mImgBack.setVisibility(View.GONE);
		} else {
			mImgBack.setVisibility(View.VISIBLE);
		}

		if (mCategories == null || mCategories.size() == 0) {
			showToast(R.string.msg_task_not_exist);
		} else {
			mAdapter = new AddGridAdapter(this, mCategories);
			mGridView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.img_add_back :
				onBackPressed();
				break;
			case R.id.img_add_close :
				setResult(RESULT_CANCELED);
				finish();
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if (mStack.size() == 0) {
			super.onBackPressed();
		} else {
			Category category = mStack.pop();
			if (mStack.size() == 0) {
				mTxtTitle.setText(R.string.tit_add_task);
				mCategories = mApp.mCategories;
			} else {
				mTxtTitle.setText(String.format(getString(R.string.tit_add_task_format), category.name));
				mCategories = category.sub;
			}
			refresh();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Category category = mAdapter.getItem(position);
		if (category.sub != null) {
			mTxtTitle.setText(String.format(getString(R.string.tit_add_task_format), category.name));
			mStack.push(category);
			mCategories = category.sub;
			refresh();
		} else {
			if (category.function != null && category.function.length() > 0) {
				Category parentCategory = (mStack.size() == 0 ? null : mStack.get(mStack.size() -1));
				mTask = new Task(parentCategory, category);

				// check tasker installed
				if (Tasker.class.getName().equals(category.function)) {
					TaskerIntent.Status taskerStatus = TaskerIntent.testStatus(this);
					if (taskerStatus == TaskerIntent.Status.NotInstalled) {
						showToast(R.string.msg_please_check_tasker_installed);
						return;
					}
				}

				Intent add = new Intent(AddActivity.this, AddTaskActivity.class);
				add.putExtra(AddTaskActivity.EXTRA_TASK, mTask);
				startActivity(add);
				finish();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int x = (int)event.getX();
			int y = (int)event.getY();
			Bitmap bitmapScreen = Bitmap.createBitmap(mScreenSize.x, mScreenSize.y, Bitmap.Config.ARGB_8888);
			if(x < 0 || y < 0)
				return false;
			int ARGB = bitmapScreen.getPixel(x, y);
			if(Color.alpha(ARGB) == 0) {
				finish();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAG_WRITE) {
			if (resultCode == Activity.RESULT_OK) {
				finish();
			}
		}
	}
}
