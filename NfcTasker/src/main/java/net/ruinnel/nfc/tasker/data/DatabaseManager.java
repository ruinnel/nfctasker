package net.ruinnel.nfc.tasker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import net.ruinnel.nfc.tasker.bean.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
	private static final String TAG = DatabaseManager.class.getSimpleName();

	private int defaultConflict = SQLiteDatabase.CONFLICT_NONE;
	public SQLiteDatabase db = null;
	private DataHelper dbHelper = null;

	private static DatabaseManager mInstance = null;

	private DatabaseManager(Context context) {
		this.dbHelper = new DataHelper(context);
		this.db = this.dbHelper.getWritableDatabase();
		this.db.setVersion(TableDef.VERSION);
	}

	public static DatabaseManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DatabaseManager(context);
		}
		return mInstance;
	}

	/**
	 * DataHelper.onUpgrade() 에서 데이터 복구 시에만 사용하세요.
	 */
	public DatabaseManager(SQLiteDatabase db) {
		this.db = db;
	}

	/**
	 * @param conflictAlgorithm - use in android.database.sqlite.SQLiteDatabase.CONFLICT_*
	 */
	public void setConflictAlgorithm(int conflictAlgorithm) {
		this.defaultConflict = conflictAlgorithm;
	}

	// method for select
	public Cursor query(String table, String selection, String[] selectionArgs, String orderBy, String limit) {
		return db.query(table, null, selection, selectionArgs, null, null, orderBy, limit);
	}

	// method for insert
	public long insert(String table, ContentValues values) {
		return db.insertWithOnConflict(table, null, values, defaultConflict);
	}

	// method for delete
	public int delete(String table, String whereClause, String[] whereArgs) {
		return db.delete(table, whereClause, whereArgs);
	}

	// method for update
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return db.update(table, values, whereClause, whereArgs);
	}


	public List<Task> getTasks(String where) {
		List<Task> tasks = new ArrayList<Task>();

		Cursor cursor = this.query(TableDef.Task.NAME, where, null, null, null);
		while (cursor.moveToNext()) {
			Task task = new Task();
			int colIdx = cursor.getColumnIndex(TableDef.Task._ID);
			if (colIdx >= 0) {
				task.id = cursor.getLong(colIdx);
			}

			colIdx = cursor.getColumnIndex(TableDef.Task.COL_NAME);
			if (colIdx >= 0) {
				task.name = cursor.getString(colIdx);
			}

			colIdx = cursor.getColumnIndex(TableDef.Task.COL_UID);
			if (colIdx >= 0) {
				task.uid = cursor.getString(colIdx);
			}

			colIdx = cursor.getColumnIndex(TableDef.Task.COL_FUNC);
			if (colIdx >= 0) {
				task.function = cursor.getString(colIdx);
			}

			colIdx = cursor.getColumnIndex(TableDef.Task.COL_PARAMS);
			if (colIdx >= 0) {
				task.params = cursor.getString(colIdx);
			}
			tasks.add(task);
		}
		cursor.close();

		return tasks;
	}

	public void saveTask(Task task) {
		ContentValues vals = new ContentValues();
		vals.put(TableDef.Task.COL_NAME, task.name);
		vals.put(TableDef.Task.COL_UID, task.uid);
		vals.put(TableDef.Task.COL_FUNC, task.function);
		vals.put(TableDef.Task.COL_PARAMS, task.params);
		vals.put(TableDef.Task._CREATED, (task.created != null ? task.created.getTime() : System.currentTimeMillis()));
		this.insert(TableDef.Task.NAME, vals);
	}

	public void deleteTask(Task task) {
		String where = TableDef.Task._ID + " = " + task.id;
		this.delete(TableDef.Task.NAME, where, null);
	}
}