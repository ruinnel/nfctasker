package net.ruinnel.nfc.tasker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import net.ruinnel.nfc.tasker.bean.Task;
import net.ruinnel.nfc.tasker.func.Tasker;
import net.ruinnel.nfc.tasker.util.FileUtil;

import java.io.File;
import java.util.List;

public class DataHelper extends SQLiteOpenHelper {
	private static final String TAG = DataHelper.class.getSimpleName();

	private final Context mContext;

	public DataHelper(Context context) {
		super(context, TableDef.DB_NAME, null, TableDef.VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.setVersion(TableDef.VERSION);

		// Task
		db.execSQL("CREATE TABLE " + TableDef.Task.NAME
				+ " (" + TableDef.Task._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TableDef.Task.COL_NAME + " text not null, "
				+ TableDef.Task.COL_UID + " text not null, "
				+ TableDef.Task.COL_FUNC + " text not null, "
				+ TableDef.Task.COL_PARAMS + " text, "
				+ TableDef.Task._CREATED + " integer not null"
				+ ");");
	}


	public void deleteAllTables(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE " + TableDef.Task.NAME);
		} catch (Exception e) {
			Log.e(TAG, "SQL error", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "DB Version changed - " + oldVersion + " >> " + newVersion);

		String dbFile = backupDatabaseToSdcard();
		SQLiteDatabase oldDb = null;
		DatabaseManager oldDbMgr = null;
		List<Task> tasks = null;
		if (dbFile != null) {
			oldDb = SQLiteDatabase.openDatabase(dbFile, null, SQLiteDatabase.OPEN_READONLY);
			oldDbMgr = new DatabaseManager(oldDb);

			tasks = oldDbMgr.getTasks(null);
		}

		deleteAllTables(db);

		onCreate(db);

		if (dbFile != null) {
			DatabaseManager dbMgr = new DatabaseManager(db);
			for (int i = 0; i < tasks.size(); i++) {
				Task task = tasks.get(i);
				// tasker의 task 이름을 name => task.params 로 이동.
				task.setParams(new String[]{task.name});
				task.function = Tasker.class.getName();

				dbMgr.saveTask(task);
			}
			oldDb.close();
			File file = new File(dbFile);
			if (file.exists()) {
				file.delete();
			}
		}
	}

	private String backupDatabaseToSdcard() {
		String dbPath = mContext.getApplicationInfo().dataDir + File.separator + "databases" + File.separator + TableDef.DB_NAME;
		String backupFile = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + TableDef.DB_NAME;

		Log.d(TAG, "from : " + dbPath + ", to : " + backupFile);
		if (FileUtil.copyFile(dbPath, backupFile)) {
			return backupFile;
		} else {
			return null;
		}
	}
}