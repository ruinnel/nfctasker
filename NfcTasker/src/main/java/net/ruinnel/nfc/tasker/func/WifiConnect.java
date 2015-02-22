package net.ruinnel.nfc.tasker.func;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import net.ruinnel.nfc.tasker.R;
import net.ruinnel.nfc.tasker.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WifiConnect extends Function {
	private static final String TAG = WifiConnect.class.getSimpleName();

	private boolean mIsNowConnecting = false;
	private final String[] params;
	public WifiConnect(Context context, String[] params) {
		super(context);
		this.params = params;
	}

	@Override
	public List<Param> getParamTypes() {
		List<Param> params = new ArrayList<Param>();
		String[] vals = context.getResources().getStringArray(R.array.param_on_off_toggle);
		params.add(new Param(context.getString(R.string.label_param_ssid), ParamType.STRING, vals));
		params.add(new Param(context.getString(R.string.label_param_password), ParamType.STRING, vals));
		return params;
	}

	@Override
	public String getErrorMessage() {
		if (params == null || params.length < 2) {
			return context.getString(R.string.msg_invalid_not_enough_params);
		}
		if (params[0] == null || params[0].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_need_value), context.getString(R.string.label_param_ssid));
		}
		if (params[1] == null || params[1].length() == 0) {
			return String.format(context.getString(R.string.msg_invalid_need_value), context.getString(R.string.label_param_password));
		}
		return null;
	}

	@Override
	public void run() {
		String ssid = (params != null && params.length > 0 ? params[0] : "");
		String password = (params != null && params.length >= 1 ? params[1] : "");
		connectWifi(ssid, password);
	}

	private void connectWifi(final String ssid, final String password) {
		final WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.i(TAG, "action = " + action);

				if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
					NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
					if (info != null && info.isConnected()) {
						context.getApplicationContext().unregisterReceiver(this); // unRegister
						// TODO connect complate
						Log.i(TAG, "connected!");
					}
				} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
					int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
					if (state == WifiManager.WIFI_STATE_ENABLED) {
						Log.v(TAG, "Wifi Status Now ON!!");
						if (wifiMgr.startScan()) {
							Log.i(TAG, "wifi scan start!!");
						}
					}
				} else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
					ScanResult scanResult = null;
					if (mIsNowConnecting) return; // 스캔 결과는 지속적으로 들어오므로, 원하는 SSID를 찾아서 접속이 진행중일 때는 SCAN_RESULT 인텐트를 무시합니다.

					List<ScanResult> mApList = wifiMgr.getScanResults();
					for (ScanResult result : mApList) {
						if (ssid.equals(result.SSID)) {
							scanResult = result;
						}
					}

					if (scanResult != null) {
						Log.v(TAG, "do Connect! - " + scanResult.SSID);
						doConnect(wifiMgr, scanResult, password);
					} else {
						Log.i(TAG, String.format(context.getString(R.string.msg_ap_out_of_range), ssid));
					}
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		context.getApplicationContext().registerReceiver(receiver, filter);
		Log.v(TAG, "register receiver");

		if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			Log.v(TAG, "Wifi State = enabled, do Start Scan!");
			if (wifiMgr.startScan()) {
				Log.v(TAG, "wifi scan start!!");
			}
		} else {
			Log.v(TAG, "Wifi State = disabled, try turn on!");
			wifiMgr.setWifiEnabled(true);
		}
	}

	public void doConnect(WifiManager wifiMgr, ScanResult result, String password) {
		WifiConfiguration conf = new WifiConfiguration();

		boolean isSaved = false;
		WifiConfiguration saved = null;
		List<WifiConfiguration> savedNetworks = wifiMgr.getConfiguredNetworks();
		if (savedNetworks != null) {
			Iterator<WifiConfiguration> itr = savedNetworks.iterator();
			while (itr.hasNext()) {
				WifiConfiguration cf = itr.next();
				if (cf.SSID.equals(result.SSID)) {
					isSaved = true;
					saved = cf;
				}
			}
		}

		// 이미 동일한 이름의 SSID가 저장되어 있을 경우 삭제하고 다시 설정을 저장한다.
		if (isSaved && saved != null) {
			wifiMgr.removeNetwork(saved.networkId);
		}

		//기본 설정사항
		conf.SSID = "\"" + result.SSID + "\"";
		conf.status = WifiConfiguration.Status.DISABLED;
		conf.priority = 40;

		Log.v(TAG, "result.capabilities = " + result.capabilities);

		if (result.capabilities.contains("WEP")) {
			//WEP방식
			Log.v(TAG, "WEP");

			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

			conf.wepKeys[0] = password;
			conf.wepTxKeyIndex = 0;

		} else if (result.capabilities.contains("WPA")) {
			//WPA, WPA2 방식
			Log.v(TAG, "WPA");
			conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			conf.preSharedKey = "\"" + password + "\"";

		} else { // (result.capabilities.contains("")){
			//OPEN방식
			Log.v(TAG, "OPEN");
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			conf.allowedAuthAlgorithms.clear();
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		}

		int netId = wifiMgr.addNetwork(conf);

		Log.v(TAG, "saved netWorkId = " + netId);
		Log.v(TAG, "config = " + conf.toString());
		if (netId != -1) {
			wifiMgr.enableNetwork(netId, true);
			mIsNowConnecting = true;
			Log.i(TAG, "wifi connect success!!!");
		} else {
			Log.w(TAG, "wifi connect failed!!!");

		}
	}
}
