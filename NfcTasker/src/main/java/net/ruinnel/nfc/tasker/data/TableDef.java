package net.ruinnel.nfc.tasker.data;

import android.provider.BaseColumns;

public class TableDef {
	public static final String DB_NAME = "NfcTasker.db";
	public static final int VERSION = 3;

	private static class Base implements BaseColumns {
		public static final String _CREATED = "_created";
	}

	public static final class Task extends Base {
		public static final String NAME = "task";

		public static final String COL_NAME = "name";		// from ver 1
		public static final String COL_UID = "uid";			// from ver 1
		public static final String COL_FUNC = "function";	// from ver 2
		public static final String COL_PARAMS = "params";	// from ver 2
	}
}
