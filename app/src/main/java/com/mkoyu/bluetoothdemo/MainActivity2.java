package com.mkoyu.bluetoothdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity2 extends AppCompatActivity {


    private final String TAG = "ZC_BLUETOOTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        initBluetooth();

        findViewById(R.id.open_bluetooth).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 1,\n" +
                        "  \"mac\": \"\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.connect).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 3,\n" +
                        "  \"mac\": \"BA:03:68:45:2B:09\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.unconnect).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 4,\n" +
                        "  \"mac\": \"BA:03:68:45:2B:09\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.temp).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 5,\n" +
                        "  \"mac\": \"BA:03:68:45:2B:09\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.vib).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 6,\n" +
                        "  \"mac\": \"BA:03:68:45:2B:09\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        findViewById(R.id.state).setOnClickListener(view -> {
            try {
                action(new JSONArray().put(0, "1").put(1, "measureAction").put(2, "{\n" +
                        "  \"code\": 7,\n" +
                        "  \"mac\": \"BA:03:68:45:2B:09\"\n" +
                        "}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.route).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    private final int REQUEST_PERMISSION_CODE = 1001;
    private final int REQUEST_BLUETOOTH_ENABLED = 1002;

    private static final Executor threadExecutor = new ThreadPoolExecutor(2, 4, 30,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(10), runnable -> {
        Thread var10000 = new Thread(runnable, "ZC_BLUETOOTH");
        var10000.setDaemon(true);
        return var10000;
    }
    );


    private boolean mScanning = false;

    private final List<BleDeviceInfo> mBleDeviceList = new ArrayList<>();

    private String mCallbackId = "";
    // ???????????????
    private String mType = "";
    // ???????????????
    private String mContent = "";
    private ActionInfo mActionInfo;

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Handler mHandler;
    private boolean mHandling;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeService mBluetoothLeService;

    private boolean initConn = false;
    private boolean mConnected = false;

    private String mDeviceName;
    private String mDeviceAddress;

    private String sendCMD = "";

    /**
     * ????????????????????? ?????? 10s
     */
    private static final long SCAN_PERIOD = 10000;


    private void handleResult(final BleDeviceInfo bleDevice) {
        AtomicBoolean hasFound = new AtomicBoolean(false);
        for (BleDeviceInfo result : mBleDeviceList) {
            if (result.getDevice().equals(bleDevice.getDevice())) {
                hasFound.set(true);
            }
        }

        if (!hasFound.get()) {
//            Log.i("ZC_BLUETOOTH", "device detected  ------  name: " + bleDevice.getName() + "  mac: " + bleDevice.getMac() + "  Rssi: " + bleDevice.getRssi() + "  scanRecord: " + HexUtil.formatHexString(bleDevice.getScanRecord(), true));
            mBleDeviceList.add(bleDevice);
            // ??????????????????????????????????????????
            scanLeDevice(false);

        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    public void initBluetooth() {

        if (!isSupportBle(this)) {
//                ActionResult actionResult = new ActionResult();
//                actionResult.setCode(3);
//                actionResult.setMsg("??????????????????BLE");
//                JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
            Log.e(TAG, "??????????????????BLE");
            return;
        }

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mHandler = new ScanHandler(mMainHandler.getLooper(), this);
        mHandling = true;

        if (!isOpenBluetooth()) {
//                ActionResult actionResult = new ActionResult();
//                actionResult.setCode(2);
//                actionResult.setMsg("???????????????");
//                JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
            Log.e(TAG, "???????????????");
        }
    }


    public void action(JSONArray array) {
        initBluetooth();
        Log.e(TAG, array.toString());
        if (array != null && array.length() > 2) {
            mCallbackId = array.optString(0);
            mType = array.optString(1);
            mContent = array.optString(2);
        }

        if (mType.equals("measureAction")) {
            ActionInfo x = new ActionInfo(1, "");
            System.out.println(JSON.toJSONString(x));
            mActionInfo = JSON.parseObject(mContent, ActionInfo.class);
            if (mActionInfo == null) {
                ActionResult actionResult = new ActionResult();
                actionResult.setCode(1);
                actionResult.setMsg("??????????????????");
//                JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
                Log.e(TAG, "??????????????????");
                return;
            }

            // ????????????
            if (mActionInfo.getCode() == 1) {
                startScan();
            }

            // ????????????
            if (mActionInfo.getCode() == 2) {
                scanLeDevice(false);
            }
            // ??????
            if (mActionInfo.getCode() == 3) {
                if (TextUtils.isEmpty(mActionInfo.getMac())) {
                    ActionResult actionResult = new ActionResult();
                    actionResult.setCode(4);
                    actionResult.setMsg("?????????mac??????,????????????");
//                    JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
                    Log.e(TAG, "?????????mac??????,????????????");
                } else {
                    BleDeviceInfo targetDevice = null;
                    if (!mBleDeviceList.isEmpty()) {
                        for (BleDeviceInfo device : mBleDeviceList) {
                            if (TextUtils.equals(device.getMac(), mActionInfo.getMac())) {
                                targetDevice = device;
                                break;
                            }
                        }
                    }
                    if (targetDevice == null) {
                        ActionResult actionResult = new ActionResult();
                        actionResult.setCode(5);
                        actionResult.setMsg("????????????????????????,???????????????");
//                        JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
                        Log.e(TAG, "????????????????????????,???????????????");
                        return;
                    } else {
                        if (mBluetoothLeService != null) {
                            // ?????????
                            mConnected = false;
                            mBluetoothLeService.disconnect();
                        }

                        mDeviceName = targetDevice.getName();
                        mDeviceAddress = targetDevice.getMac();
                        threadExecutor.execute(() -> {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (mScanning) {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                mScanning = false;
                            }

                            if (initConn) {
                                if (!mConnected) {
                                    mBluetoothLeService.connect(mDeviceAddress);
                                }
                            } else {
                                Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
                                bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                                registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
                                initConn = true;
                            }

                        });

                    }

                }
            }

            if (mActionInfo.getCode() == 4) {
                sendCMD = "";
                mConnected = false;
                if (mBluetoothLeService != null) {
                    mBluetoothLeService.disconnect();
                }
//                mBluetoothLeService.close();
            }

            if (mActionInfo.getCode() == 5) {
//                    writeCMD();
                // 0B0B?????????  63(?????????1-100???????????????) 00(??????????????????00??? 01???)
                sendCMD = "0B0B6300";
                writeCMD(sendCMD, true);
            }

            if (mActionInfo.getCode() == 6) {
                measureCount = 0;
                sendCMD = "1" + "F11";
                writeCMD(sendCMD, true);
            }

            if (mActionInfo.getCode() == 7) {
                BleDeviceInfo targetDevice = null;

                if (!mBleDeviceList.isEmpty()) {
                    for (BleDeviceInfo device : mBleDeviceList) {
                        if (TextUtils.equals(device.getMac(), mActionInfo.getMac())) {
                            targetDevice = device;
                            break;
                        }
                    }
                }
                boolean con = isConnected(targetDevice);
                Log.e("onScanResult", con + "");
                ActionResult actionResult = new ActionResult();
                actionResult.setCode(0);
                actionResult.setResult(con + "");
//                JSUtil.execCallback(pWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, true);
            }
        }
    }


    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                ActionResult actionResult = new ActionResult();
                actionResult.setCode(6);
                actionResult.setMsg("????????????");
//                JSUtil.execCallback(mWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, false);
                Log.e(TAG, "????????????");
                return;
            }
            Log.e(TAG, "??????");
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.e(TAG, "onServiceDisconnected");
        }
    };
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.e(TAG, "?????????");
                //mBluetoothLeService.connect(mDeviceAddress);//??????????????????????????????????????????????????????
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.e(TAG, "??????????????????????????????");
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //Toast.makeText(DeviceScanActivity.this,"????????????" , Toast.LENGTH_SHORT).show();
                showData(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /**
     * ????????????
     */
    public void startScan() {

        if (hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            scanLeDevice(true);
        } else {
            requestPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        scanLeDevice(false);

        Log.e(TAG, "bluetoothService: " + (mBluetoothLeService != null));
        if (mBluetoothLeService != null) {
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
        }

    }

    /**
     * ??????
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        if (mBluetoothAdapter != null) {
            if (enable) {
                mMainHandler.postDelayed(
                        () -> {
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            notifyScanStopped();
                        },
                        SCAN_PERIOD
                );
                Log.e(TAG, "????????????");
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);

            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                notifyScanStopped();

            }
        }
    }

    /**
     * ??????????????????????????????
     */
    private void notifyScanStopped() {
        this.mHandling = false;
        removeHandlerMsg();
        this.mMainHandler.post(this::postData);
    }

    /**
     * ????????????
     */
    private void postData() {
        if (mBleDeviceList.isEmpty()) {
            Log.e("onScanResult", "list??????");
        } else {
            Log.e("onScanResult", "list???size???" + mBleDeviceList.size());
            StringBuilder macList = new StringBuilder();
            List<String> mBleMacList = new ArrayList<>();
            for (int i = 0; mBleDeviceList.size() > i; i++) {
                if (!mBleMacList.contains(mBleDeviceList.get(i).getMac())) {
                    mBleMacList.add(mBleDeviceList.get(i).getMac());
                    if (i == mBleDeviceList.size() - 1) {
                        macList.append(mBleDeviceList.get(i).getMac());
                    } else {
                        macList.append(mBleDeviceList.get(i).getMac()).append(",");
                    }
                }
            }
            ActionResult actionResult = new ActionResult();
            actionResult.setCode(0);
            actionResult.setResult(macList.toString());
            try {
                Log.e("XTEST", JSON.toJSONString(actionResult));
//                JSUtil.execCallback(mWebView, mCallbackId, JSON.toJSONString(actionResult), JSUtil.OK, true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * ??????
     *
     * @param cmd
     * @param ishex
     */
    public void writeCMD(String cmd, Boolean ishex) {
        if (mBluetoothLeService == null) {
            return;
        }
        mBluetoothLeService.writeCharacteristicZCBLE(cmd, ishex);
    }

    private void removeHandlerMsg() {
        this.mMainHandler.removeCallbacksAndMessages((Object) null);
        this.mHandler.removeCallbacksAndMessages((Object) null);
    }

    int receive_len = 0;
    byte[] receive_buff;

    private int measureCount = -1;

    //????????????
    private void showData(byte[] data) {
        if (sendCMD.contains("0e")) {
            //*****????????????????????????????????????????????????????????????????????????????????????0xFF 0xFF?????????????????????????????????????????????,??????2???????????????-1???????????????????????????*******
            if (data[data.length - 2] == -1 && data[data.length - 1] == -1) {
                System.out.println("????????????????????????????????????");
            }

            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2?????????????????????????????????????????????????????????
            short sydata1 = (short) ((char) (receive_buff[0] & 0x00FF) + (char) ((receive_buff[1] << 8) & 0xff00));
            short sydata2 = (short) ((char) (receive_buff[2] & 0x00FF) + (char) ((receive_buff[3] << 8) & 0xff00));
            //?????????
            //?????????
            //??????????????????,?????????????????????????????????????????????????????????

            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                /*long fSumTemp=0l;
                fSumTemp=sydata1+sydata2+??????????????????;//????????????????????????
                short AVG=(short)(fSumTemp/sydataLen);//???????????????
                sydata1=sydata1-AVG;//??????????????????????????? ?????????????????????
                sydata2=sydata2-AVG;//??????????????????????????? ?????????????????????
                //????????????
                //????????????
                */
            double VolCoef = (getRK() * 5 * 1000) / 65536.0f;//???????????????????????????????????????????????????????????????*1000???????????????double???????????????????????????
        } else {
            //******????????????????????????????????????\r\n????????????????????????????????????????????????????????????******
            String str = new String(data);
            //?????????????????????????????????????????????????????????????????????
            if (mActionInfo != null) {

                if (!TextUtils.isEmpty(str) && str.contains("\r\n") && str.contains(":")) {
                    String trim = str.split("\r\n")[0].trim().split(":")[1];
                    if (mActionInfo.getCode() == 5) {

                        ActionResult actionResult = new ActionResult();
                        actionResult.setCode(0);
                        actionResult.setResult(trim);

                    } else if (mActionInfo.getCode() == 6) {
                        measureVib(str);
                    }

                } else {

                    if (mActionInfo.getCode() == 5) {
                        ActionResult actionResult = new ActionResult();
                        actionResult.setCode(7);
                        actionResult.setMsg("????????????");

                    } else if (mActionInfo.getCode() == 6) {
                        ActionResult actionResult = new ActionResult();
                        actionResult.setCode(7);
                        actionResult.setMsg("????????????");
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private void measureVib(String vib) {
        VirbrationInfo virbrationInfo = new VirbrationInfo();

        if (measureCount == 0) {
            sendCMD = "1" + "F11";
            writeCMD(sendCMD, true);
            measureCount++;
            virbrationInfo.setAcc(Double.parseDouble(vib));
        } else if (measureCount == 1) {
            sendCMD = "1" + "F12";
            writeCMD(sendCMD, true);
            measureCount++;
            virbrationInfo.setSpeed(Double.parseDouble(vib));
        } else if (measureCount == 2) {
            sendCMD = "1" + "F13";
            writeCMD(sendCMD, true);
            virbrationInfo.setDistance(Double.parseDouble(vib));
            measureCount = -1;
        } else {
            ActionResult actionResult = new ActionResult();
            actionResult.setCode(0);
            actionResult.setResult(virbrationInfo.toString());
        }
    }

    private double getRK() {
        double res = 1.0;
        //??????????????????RK??????????????????????????????????????????
        /*writeCMD("RK",false); ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        ????????????0.111,0.222,0.333,0.444,0.555,0.666 ??????????????????6????????????
        ??????????????????????????????,????????????,????????????,???????????????,????????????,??????????????????????????????
        ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????*/
        return res;
    }

    /**
     * ????????????????????????
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = (device, rssi, scanRecord) -> {
        if (device != null) {
            String dName = device.getName();
            if (dName == null || dName.length() < 4) {
                return;
            }
            Log.e(TAG, "name: " + dName + ", mac: " + device.getAddress());
            if ("ZC".equals(dName.substring(0, 2))) {
                if (this.mHandling) {
                    Message message = this.mHandler.obtainMessage();
                    message.what = 0;
                    message.obj = new BleDeviceInfo(device, rssi, scanRecord, System.currentTimeMillis());
                    this.mHandler.sendMessage(message);

                }
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanLeDevice(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_BLUETOOTH_ENABLED && resultCode == RESULT_OK) {
            startScan();
        } else {
            Log.e("ZC_BLUETOOTH", "???????????????");
        }

    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    private boolean isSupportBle(Context context) {
        return Build.VERSION.SDK_INT >= 18 && context.getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }

    /**
     * ??????????????????
     *
     * @return
     */
    private boolean isOpenBluetooth() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * ????????????
     */
    private void openBluetooth(Context context) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(intent, REQUEST_BLUETOOTH_ENABLED);
    }

    /**
     * ?????????????????????
     *
     * @param context
     * @param permissions
     * @return
     */
    private boolean hasPermission(Context context, String... permissions) {
        if (permissions.length > 0) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public boolean isConnected(BleDeviceInfo bleDevice) {
        return this.getConnectState(bleDevice) == 2;
    }

    public int getConnectState(BleDeviceInfo bleDevice) {
        return bleDevice != null ? this.mBluetoothManager.getConnectionState(bleDevice.getDevice(), 7) : 0;
    }

    private void requestPermissions(Context context, String... permissions) {
        ActivityCompat.requestPermissions((Activity) context, permissions, REQUEST_PERMISSION_CODE);
    }

    private static final class ScanHandler extends Handler {
        private final WeakReference<MainActivity2> ZCBluetoothTool;

        ScanHandler(Looper looper, MainActivity2 bleScanPresenter) {
            super(looper);
            this.ZCBluetoothTool = new WeakReference(bleScanPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity2 bleScanPresenter = this.ZCBluetoothTool.get();
            if (bleScanPresenter != null && msg.what == 0) {
                BleDeviceInfo bleDevice = (BleDeviceInfo) msg.obj;
                if (bleDevice != null) {
                    bleScanPresenter.handleResult(bleDevice);
                }
            }

        }
    }
}