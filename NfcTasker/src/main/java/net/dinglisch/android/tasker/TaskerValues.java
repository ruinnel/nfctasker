package net.dinglisch.android.tasker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ruinnel
 * Date: 2014. 1. 24.
 * Time: 오후 11:02
 * To change this template use File | Settings | File Templates.
 */
public class TaskerValues {
	private static final String TAG = TaskerValues.class.getSimpleName();

	public static List<String> getTasks(Context context) {
		List<String> result = null;
		Cursor cursor = context.getContentResolver().query(Uri.parse("content://net.dinglisch.android.tasker/tasks"), null, null, null, null);

		if (cursor != null) {
			int nameCol = cursor.getColumnIndex("name");
			//int projNameCol = cursor.getColumnIndex("project_name");

			while (cursor.moveToNext()) {
				if (result == null) {
					result = new ArrayList<String>();
				}
				//Log.d(TAG, cursor.getString(nameCol));
				result.add(cursor.getString(nameCol));
			}
			cursor.close();
		}
		return result;
	}
}
