package net.ruinnel.nfc.tasker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Category implements Parcelable {
	private static final String TAG = Category.class.getSimpleName();

	public int icon;
	public String name;

	public List<Category> sub;
	public String function;
	public String params;

	public static final Creator<Category> CREATOR = new Creator<Category>()  {
		public Category createFromParcel(Parcel src) {
			return new Category(src);
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

	public Category() {}

	public Category(Parcel src) {
		this.name = src.readString();
		this.icon = src.readInt();
		this.function = src.readString();
		this.params = src.readString();

		// sub
		int temp = src.readInt();
		if (temp > 0) {
			List<Parcelable> list = Arrays.asList(src.readParcelableArray(Category.class.getClassLoader()));
			this.sub = new ArrayList<Category>();
			for (Parcelable parcel : list) {
				this.sub.add((Category) parcel);
			}
		}
	}

	public Category(String name, int icon, String func) {
		this.name = name;
		this.icon = icon;
		this.sub = null;
		this.function = func;
		this.params = null;
	}

	public Category(String name, int icon, String func, String params) {
		this.name = name;
		this.icon = icon;
		this.sub = null;
		this.function = func;
		this.params = params;
	}

	public Category(String name, int icon, List<Category> sub) {
		this.name = name;
		this.icon = icon;
		this.sub = sub;
		this.function = null;
		this.params = null;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString((this.name != null ? this.name : ""));
		dest.writeInt(this.icon);
		dest.writeString((this.function != null ? this.function : ""));
		dest.writeString((this.params != null ? this.params : ""));

		dest.writeInt((this.sub != null ? this.sub.size() : 0));
		if (this.sub != null && this.sub.size() > 0) {
			dest.writeParcelableArray(this.sub.toArray(new Category[0]), 0);
		}
	}

	public String[] getParams() {
		if (params == null || params.length() == 0) {
			return null;
		} else {
			return params.split(Task.DELIMITER);
		}
	}

	public void setParams(String[] params) {
		if (params == null || params.length == 0) {
			this.params = null;
		} else {
			StringBuffer buf = new StringBuffer();
			for (String param :params) {
				if (buf.length() > 0) {
					buf.append(Task.DELIMITER);
				}
				buf.append(param);
			}
			this.params = buf.toString();
		}
	}

	@Override
	public String toString() {
		return "Category{" +
				"name='" + name + '\'' +
				'}';
	}
}
