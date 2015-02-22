package net.ruinnel.nfc.tasker.func;

import android.content.Context;

import java.util.List;

abstract public class Function implements Runnable {
	private static final String TAG = Function.class.getSimpleName();

	public enum ParamType {
		SPINNER, STRING, INT
	}

	public static class Param {
		public String name;
		public ParamType type;
		public String[] showValues;
		public String[] values;
		public String hint;

		public Param(String name, ParamType type) {
			this.name = name;
			this.type = type;
			this.values = null;
			this.hint = null;
			this.showValues = null;
		}

		public Param(String name, ParamType type, String hint) {
			this.name = name;
			this.type = type;
			this.values = null;
			this.hint = hint;
			this.showValues = null;
		}

		public Param(String name, ParamType type, String[] values) {
			this.name = name;
			this.type = type;
			this.values = values;
			this.hint = null;
			this.showValues = null;
		}

		public Param(String name, ParamType type, String[] values, String hint) {
			this.name = name;
			this.type = type;
			this.values = values;
			this.hint = hint;
			this.showValues = null;
		}

		public Param(String name, ParamType type, String[] values, String[] showValues) {
			this.name = name;
			this.type = type;
			this.values = values;
			this.hint = null;
			this.showValues = showValues;
		}
	}

	protected Context context;
	public Function(Context context) {
		this.context = context;
	}
	abstract public List<Param> getParamTypes();
	abstract public String getErrorMessage();
}
