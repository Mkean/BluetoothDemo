package com.mkoyu.bluetoothdemo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.clj.fastble.data.BleDevice
import com.google.android.material.button.MaterialButton
import com.ronds.eam.lib_sensor.BleClient
import com.ronds.eam.lib_sensor.BleInterfaces.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mContext = this

//        initBluetooth();
        findViewById<MaterialButton>(R.id.open_bluetooth).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 1,
  "mac": ""
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        findViewById<MaterialButton>(R.id.connect).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 3,
  "mac": "84:2E:14:4E:34:E6"
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        findViewById<MaterialButton>(R.id.unconnect).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 4,
  "mac": "84:2E:14:4E:34:E6"
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        findViewById<MaterialButton>(R.id.temp).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 5,
  "mac": "84:2E:14:4E:34:E6"
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        findViewById<MaterialButton>(R.id.vib).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 6,
  "mac": "84:2E:14:4E:34:E6"
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        findViewById<MaterialButton>(R.id.state).setOnClickListener { view: View? ->
            try {
                action(
                    JSONArray().put(0, "1").put(1, "measureAction").put(
                        2, """{
  "code": 7,
  "mac": "84:2E:14:4E:34:E6"
}"""
                    )
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    private var mContext: Context? = null
    private var mCallbackId = ""
    private var mType = "" //对应的类型

    private var mContent = "" //对应的内容

    // 测温发射率
    private val EMI = 0.97f

    // 采集长度, 4 字节整型, 单位 K, 不要超过 256K
    private val LEN = 4

    // 分析频率, 4 字节整型, 单位 Hz, 不要超过 40000Hz
    private val FREQ = 2000

    private var mBleDeviceList: List<BleDevice>? = null

    fun action(array: JSONArray?) {
        BleClient.getInstance().initOptions().init((mContext as Activity?)!!.application)
        if (array != null && array.length() > 2) {
            mCallbackId = array.optString(0)
            mType = array.optString(1)
            mContent = array.optString(2)
        }
        if (TextUtils.equals(mType, "measureAction")) {
            val x = ActionInfo(1, "")
            println(JSON.toJSONString(x))
            val actionInfo = JSON.parseObject(mContent, ActionInfo::class.java)
            //            ActionResult actionResult = new ActionResult();
            if (actionInfo == null) {
                val actionResult = ActionResult()
                actionResult.code = 1
                actionResult.msg = "参数传递错误"
                Log.e(TAG, "参数传递错误")
                return
            }
            if (!BleClient.getInstance().isBluetoothEnabled) {
                val actionResult = ActionResult()
                actionResult.code = 2
                actionResult.msg = "蓝牙未打开"
                Log.e(TAG, "蓝牙未打开")
                return
            }
            if (!BleClient.getInstance().isSupportBle) {
                val actionResult = ActionResult()
                actionResult.code = 3
                actionResult.msg = "该设备不支持BLE"
                Log.e(TAG, "该设备不支持BLE")
                return
            }
            if (actionInfo.code === 1) {
                BleClient.getInstance().stopScan()
                BleClient.getInstance().scan(mScanCallback)
            }
            if (actionInfo.code === 2) {
                BleClient.getInstance().stopScan()
            }
            if (actionInfo.code === 3) {
                if (TextUtils.isEmpty(actionInfo.mac)) {
                    val actionResult = ActionResult()
                    actionResult.code = 4
                    actionResult.msg = "未指定mac地址,连接失败"
                    Log.e(TAG, "未指定mac地址，连接失败")
                } else {
                    var targetDevice: BleDevice? = null
                    if (mBleDeviceList != null) {
                        for (device in mBleDeviceList!!) {
                            if (TextUtils.equals(device.mac, actionInfo.mac)) {
                                targetDevice = device
                                break
                            }
                        }
                    }
                    if (targetDevice == null) {
                        val actionResult = ActionResult()
                        actionResult.code = 5
                        actionResult.msg = "没有找到指定设备,请重新扫描"
                        Log.e(TAG, "没有找到指定设备,请重新扫描")
                        return
                    } else {
                        BleClient.getInstance().connect(targetDevice, mConnectCallback)
                    }
                }
            }
            if (actionInfo.code === 4) {
                BleClient.getInstance().disconnectAllDevices(object : DisconnectCallback {
                    override fun onDisconnectStart() {}
                    override fun onDisconnectEnd() {}
                })
            }
            if (actionInfo.code === 5) {
                BleClient.getInstance().sampleTemp(EMI, mTempCallback)
            }
            if (actionInfo.code === 6) {
                BleClient.getInstance().sampleVib(LEN, FREQ, mVibCallback)
            }
            if (actionInfo.code === 7) {
                val con = BleClient.getInstance().isConnected(actionInfo.mac)
                Log.e("onScanResult", con.toString() + "")
                val actionResult = ActionResult()
                actionResult.code = 0
                actionResult.result = con.toString() + ""
            }
        } else {
//            Intent intent = new Intent(mContext, BridgeActivity.class);
//            mContext.startActivity(intent);
        }
    }

    fun shortArrayToList(shorts: ShortArray): List<Short>? {
        val shortList: MutableList<Short> = ArrayList(shorts.size)
        for (anInt in shorts) {
            shortList.add(anInt)
        }
        return shortList
    }

    private val mScanCallback: ScanCallback = object : ScanCallback {
        override fun onScanStart() {}
        override fun onScanEnd() {}
        override fun onScanResult(list: List<BleDevice>) {
            Log.e("onScanResult", "list的size为" + list.size)
            mBleDeviceList = list
            var macList: String? = ""
            val mBleMacList: MutableList<String> = ArrayList()
            var i = 0
            while (mBleDeviceList!!.size > i) {
                if (!mBleMacList.contains(mBleDeviceList!![i].mac)) {
                    mBleMacList.add(mBleDeviceList!![i].mac)
                    macList += if (i == mBleDeviceList!!.size - 1) {
                        mBleDeviceList!![i].mac
                    } else {
                        mBleDeviceList!![i].mac + ","
                    }
                }
                i++
            }
            val actionResult = ActionResult()
            actionResult.code = 0
            actionResult.result = macList
            Log.d(TAG, "mac: $macList")
        }
    }

    private val mConnectCallback: ConnectStatusCallback = object : ConnectStatusCallback {
        override fun onConnectStart() {}
        override fun onDisconnected(bleDevice: BleDevice) {}
        override fun onConnectFail(bleDevice: BleDevice, s: String) {
            val actionResult = ActionResult()
            actionResult.code = 6
            actionResult.msg = "连接失败"
            Log.e(TAG, "连接失败")
        }

        override fun onConnectSuccess(bleDevice: BleDevice) {
            val actionResult = ActionResult()
            actionResult.code = 0
            Log.d(TAG, "连接成功")
        }
    }

    private val mTempCallback: SampleTempCallback = object : SampleTempCallback {
        override fun onReceiveTemp(v: Float) {
            val actionResult = ActionResult()
            actionResult.code = 0
            actionResult.result = v.toString()
            BleClient.getInstance().stopSampleTemp(object : ActionCallback {
                override fun onSuccess() {}
                override fun onFail(s: String) {}
            })
            Log.d(TAG, "temp: ${v.toString()}")
        }

        override fun onFail(s: String) {
            val actionResult = ActionResult()
            actionResult.code = 7
            actionResult.msg = "测温失败"
            Log.e(TAG, "测温失败")
        }
    }

    private val mVibCallback: SampleVibCallback = object : SampleVibCallback {
        override fun onReceiveVibData(vData: ShortArray, coe: Float) {
            val c = Convert()
            val md = c.waveData(shortArrayToList(vData), FREQ.toDouble(), coe)
            val virbrationInfo = VirbrationInfo()
            virbrationInfo.acc = md.measValueAcc
            virbrationInfo.speed = md.measValueSpeed
            virbrationInfo.distance = md.measValueDisp
            val actionResult = ActionResult()
            actionResult.code = 0
            actionResult.result = virbrationInfo.toString()
            Log.d(TAG, "actionResult: $virbrationInfo")
        }

        override fun onFail(s: String) {
            val actionResult = ActionResult()
            actionResult.code = 7
            actionResult.msg = "测振失败"
            Log.e(TAG, "测振失败")
        }
    }


}