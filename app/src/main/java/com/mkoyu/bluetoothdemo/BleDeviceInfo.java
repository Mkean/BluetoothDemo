package com.mkoyu.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class BleDeviceInfo implements Parcelable {
    private BluetoothDevice mDevice;
    private byte[] mScanRecord;
    private int mRssi;
    private long mTimestampNanos;

    public static final Creator<BleDeviceInfo> CREATOR = new Creator<BleDeviceInfo>() {
        @Override
        public BleDeviceInfo createFromParcel(Parcel in) {
            return new BleDeviceInfo(in);
        }

        @Override
        public BleDeviceInfo[] newArray(int size) {
            return new BleDeviceInfo[size];
        }
    };

    public BleDeviceInfo(BluetoothDevice device) {
        this.mDevice = device;
    }

    public BleDeviceInfo(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
        this.mDevice = device;
        this.mScanRecord = scanRecord;
        this.mRssi = rssi;
        this.mTimestampNanos = timestampNanos;
    }

    protected BleDeviceInfo(Parcel in) {
        mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        mScanRecord = in.createByteArray();
        mRssi = in.readInt();
        mTimestampNanos = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mDevice, flags);
        dest.writeByteArray(this.mScanRecord);
        dest.writeInt(this.mRssi);
        dest.writeLong(this.mTimestampNanos);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getName() {
        return this.mDevice != null ? this.mDevice.getName() : null;
    }

    public String getMac() {
        return this.mDevice != null ? this.mDevice.getAddress() : null;
    }

    public String getKey() {
        return this.mDevice != null ? this.mDevice.getName() + this.mDevice.getAddress() : "";
    }

    public BluetoothDevice getDevice() {
        return this.mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }

    public byte[] getScanRecord() {
        return this.mScanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.mScanRecord = scanRecord;
    }

    public int getRssi() {
        return this.mRssi;
    }

    public void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    public long getTimestampNanos() {
        return this.mTimestampNanos;
    }

    public void setTimestampNanos(long timestampNanos) {
        this.mTimestampNanos = timestampNanos;
    }
}
