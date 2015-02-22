package net.ruinnel.nfc.tasker.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import net.ruinnel.nfc.tasker.func.Function;
import net.ruinnel.nfc.tasker.func.FunctionManager;

import java.util.Date;
import java.util.List;

public class Task implements Parcelable {
	private static final String TAG = Task.class.getSimpleName();

	public static final String DELIMITER = ":::";

	public long id;
	public String name;
	public String uid;
	public String function;
	public String params;
	public Date created;

	public static final Creator<Task> CREATOR = new Creator<Task>()  {
		public Task createFromParcel(Parcel src) {
			return new Task(src);
		}

		@Override
		public Task[] newArray(int size) {
			return new Task[size];
		}
	};

	public Task() {}

	public Task(Parcel src) {
		this.id = src.readLong();
		this.name = src.readString();
		this.uid = src.readString();
		this.function = src.readString();
		this.params = src.readString();

		long temp = src.readLong();
		if (temp > 0) {
			created = new Date(temp);
		}
	}

	public Task(long id, String name, String uid, String function, String params, long created) {
		this.id = id;
		this.name = name;
		this.uid = uid;
		this.function = function;
		this.params = params;
		this.created = new Date(created);
	}

	public Task(Category parent, Category category) {
		this.id = -1;
		this.name = (parent != null ? String.format("%s(%s)", parent.name, category.name) : category.name);
		this.function = category.function;
		this.params = category.params;
		this.uid = null;
		this.created = null;
	}

	public String[] getParams() {
		if (params == null || params.length() == 0) {
			return null;
		} else {
			return params.split(DELIMITER);
		}
	}

	public void setParams(String[] params) {
		if (params == null || params.length == 0) {
			this.params = null;
		} else {
			StringBuffer buf = new StringBuffer();
			for (String param :params) {
				if (buf.length() > 0) {
					buf.append(DELIMITER);
				}
				buf.append(param);
			}
			this.params = buf.toString();
		}
	}

	public String getSimpleFunctionName() {
		if (function != null && function.length() > 0) {
			String[] splits = function.split("\\.");
			return (splits != null && splits.length > 0 ? splits[splits.length - 1] : "");
		} else {
			return "";
		}
	}

	public List<Function.Param> getParamTypes(Context context) {
		FunctionManager funcMgr = FunctionManager.getInstance();
		Function func = funcMgr.getFunction(context, function, getParams());
		if (func != null) {
			return func.getParamTypes();
		} else {
			return null;
		}
	}

	public String getErrorMessage(Context context) {
		FunctionManager funcMgr = FunctionManager.getInstance();
		Function func = funcMgr.getFunction(context, function, getParams());
		return func.getErrorMessage();
	}

	public boolean run(Context context) {
		FunctionManager funcMgr = FunctionManager.getInstance();
		Function func = funcMgr.getFunction(context, function, getParams());
		if (func != null) {
			Thread thread = new Thread(func);
			thread.start();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString((this.name != null ? this.name : ""));
		dest.writeString((this.uid != null ? this.uid : ""));
		dest.writeString((this.function != null ? this.function : ""));
		dest.writeString((this.params != null ? this.params : ""));
		dest.writeLong((this.created != null ? created.getTime() : -1));
	}

	@Override
	public String toString() {
		return "Task{" +
				"id=" + id +
				", name='" + name + '\'' +
				", uid='" + uid + '\'' +
				", function='" + function + '\'' +
				", params='" + params + '\'' +
				", created=" + created +
				'}';
	}
}
