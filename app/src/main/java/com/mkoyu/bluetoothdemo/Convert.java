package com.mkoyu.bluetoothdemo;

import com.ronds.eam.lib_sensor.BleClient;

import java.util.List;

class MeasureData{
    public double[] getMdAcc() {
        return mdAcc;
    }

    public void setMdAcc(double[] mdAcc) {
        this.mdAcc = mdAcc;
    }

    public double[] getMdSpeed() {
        return mdSpeed;
    }

    public void setMdSpeed(double[] mdSpeed) {
        this.mdSpeed = mdSpeed;
    }

    public double[] getMdDisp() {
        return mdDisp;
    }

    public void setMdDisp(double[] mdDisp) {
        this.mdDisp = mdDisp;
    }

    /*
        加速度数据
         */
    private double[] mdAcc;

    /*
    加速度频谱
     */
    private double[] accSpectrum;

    /*
    速度数据
     */
    private double[] mdSpeed;

    /*
       速度频谱
     */
    private double[] speedSpectrum;

    /*
    位移数据
     */
    private double[] mdDisp;

    public double[] getAccSpectrum() {
        return accSpectrum;
    }

    public void setAccSpectrum(double[] accSpectrum) {
        this.accSpectrum = accSpectrum;
    }

    public double[] getSpeedSpectrum() {
        return speedSpectrum;
    }

    public void setSpeedSpectrum(double[] speedSpectrum) {
        this.speedSpectrum = speedSpectrum;
    }

    public double[] getDispSpectrum() {
        return dispSpectrum;
    }

    public void setDispSpectrum(double[] dispSpectrum) {
        this.dispSpectrum = dispSpectrum;
    }

    /*
        位移频谱
         */
    private double[] dispSpectrum;

    /*
    加速度有效值
     */
    private double measValueAcc;
    /*
    速度有效值
     */
    private double measValueSpeed;
    /*
     位移有效值
       */
    private double measValueDisp;

    /*
    加速度峰值
     */
    private double peakValueAcc;

    /**
     * 速度峰值
     */
    private double peakValueSpeed;

    /*
    位移峰值
     */
    private double peakValueDisp;

    /*
    加速度峰峰值
     */
    private double peakPeakValueAcc;
    /*
    速度峰峰值
     */
    private double peakPeakValueSpeed;
    /**
     * 位移峰峰值
     */
    private double peakPeakValueDisp;


    /*
    加速度峭度值
     */
    private double kurtosisValueAcc;

    /*
    速度峭度值
     */
    private double kurtosisValueSpeed;

    /*
    位移峭度值
     */
    private double kurtosisValueDisp;


    public double getPeakValueAcc() {
        return peakValueAcc;
    }

    public void setPeakValueAcc(double peakValueAcc) {
        this.peakValueAcc = peakValueAcc;
    }

    public double getPeakValueSpeed() {
        return peakValueSpeed;
    }

    public void setPeakValueSpeed(double peakValueSpeed) {
        this.peakValueSpeed = peakValueSpeed;
    }

    public double getPeakValueDisp() {
        return peakValueDisp;
    }

    public void setPeakValueDisp(double peakValueDisp) {
        this.peakValueDisp = peakValueDisp;
    }

    public double getPeakPeakValueAcc() {
        return peakPeakValueAcc;
    }

    public void setPeakPeakValueAcc(double peakPeakValueAcc) {
        this.peakPeakValueAcc = peakPeakValueAcc;
    }

    public double getPeakPeakValueSpeed() {
        return peakPeakValueSpeed;
    }

    public void setPeakPeakValueSpeed(double peakPeakValueSpeed) {
        this.peakPeakValueSpeed = peakPeakValueSpeed;
    }

    public double getPeakPeakValueDisp() {
        return peakPeakValueDisp;
    }

    public void setPeakPeakValueDisp(double peakPeakValueDisp) {
        this.peakPeakValueDisp = peakPeakValueDisp;
    }

    public double getKurtosisValueAcc() {
        return kurtosisValueAcc;
    }

    public void setKurtosisValueAcc(double kurtosisValueAcc) {
        this.kurtosisValueAcc = kurtosisValueAcc;
    }

    public double getKurtosisValueSpeed() {
        return kurtosisValueSpeed;
    }

    public void setKurtosisValueSpeed(double kurtosisValueSpeed) {
        this.kurtosisValueSpeed = kurtosisValueSpeed;
    }

    public double getKurtosisValueDisp() {
        return kurtosisValueDisp;
    }

    public void setKurtosisValueDisp(double kurtosisValueDisp) {
        this.kurtosisValueDisp = kurtosisValueDisp;
    }



    public double getMeasValueAcc() {
        return measValueAcc;
    }

    public void setMeasValueAcc(double measValueAcc) {
        this.measValueAcc = measValueAcc;
    }

    public double getMeasValueSpeed() {
        return measValueSpeed;
    }

    public void setMeasValueSpeed(double measValueSpeed) {
        this.measValueSpeed = measValueSpeed;
    }

    public double getMeasValueDisp() {
        return measValueDisp;
    }

    public void setMeasValueDisp(double measValueDisp) {
        this.measValueDisp = measValueDisp;
    }


}


public class Convert{

    private double[] measureDataAcc;//0加速度数据存储
    private double[] measureDataSpeed;//1速度数据存储
    private double[] measureDataDisp;//2位移数据存储
    private int data_sampling;//采样频率;
    private int SignalType = 0;//测量定义具体的类型(0-加速度,1-速度,2-位移)
    private double UpperLimitingFreq;//上限频率
    private boolean needFFT = true;//是否转换为频谱
    //private float coe = 0.01496883375f;//灵敏度系数

    public MeasureData waveData(List<Short> arrayList, double freq, float coe){
        UpperLimitingFreq = freq;
        data_sampling = (int)(freq*2.56);
        measureDataAcc = new double[arrayList.size()];
        MeasureData md =new MeasureData();
        double sum = 0.0;
        if (measureDataAcc != null && measureDataAcc.length > 0) {
            for (int i = 0; i < measureDataAcc.length; i++) {
                measureDataAcc[i] = (double) (arrayList.get(i) * coe);
                sum += measureDataAcc[i];
            }
            //取平均值
            double avge = sum / measureDataAcc.length;
            //去除平均值
            for (int i = 0; i < measureDataAcc.length; i++) {
                measureDataAcc[i] = measureDataAcc[i] - avge;
            }
            //加速度数据
            md.setMeasValueAcc(value(measureDataAcc));
            md.setPeakValueAcc(fvalue(measureDataAcc));
            md.setPeakPeakValueAcc(ffvalue(measureDataAcc));
            md.setKurtosisValueAcc(qdvalue(measureDataAcc));
            if (needFFT) {
                ///SDK 中的方法 如果无法引用注意替换
                double[] accSpectrum = BleClient.getInstance().fft(measureDataAcc, data_sampling).getAmplitude();
                md.setAccSpectrum(accSpectrum);
            }

            //速度
            measureDataSpeed = BleClient.getInstance().accToVel(measureDataAcc, data_sampling, 10, UpperLimitingFreq);
            md.setMeasValueSpeed((value(measureDataSpeed)*1000.0));
            md.setPeakValueSpeed(fvalue(measureDataSpeed)*1000.0);
            md.setPeakPeakValueSpeed(ffvalue(measureDataSpeed)*1000.0);
            md.setKurtosisValueSpeed(qdvalue(measureDataSpeed)*1000.0);
            if (needFFT) {
                double[] speedSpectrum = BleClient.getInstance().fft(measureDataSpeed, data_sampling).getAmplitude();
                speedSpectrum = valueConvertByUnit(measureDataSpeed,1);
                speedSpectrum = valueConvertByUnit(speedSpectrum,1);
                md.setSpeedSpectrum(speedSpectrum);
            }
            measureDataSpeed = valueConvertByUnit(measureDataSpeed, 1);


            //位移
            //低频下进行扩充
            if (UpperLimitingFreq == 100 || UpperLimitingFreq == 200) {
                //低分析频谱100 200
                measureDataDisp = BleClient.getInstance().accToDist(measureDataAcc, data_sampling * 5, 10, UpperLimitingFreq * 5);
                double[] result = new double[measureDataDisp.length / 5];
                for (int i = 0; i < result.length; i++) {
                    result[i] = measureDataDisp[5 * i];
                }
                measureDataDisp = result;
                md.setMeasValueDisp(value(measureDataDisp)*100000);
                md.setPeakValueDisp(fvalue(measureDataDisp)*100000);
                md.setPeakPeakValueDisp(ffvalue(measureDataDisp)*100000);
                md.setKurtosisValueSpeed(qdvalue(measureDataDisp)*100000);
                if (needFFT) {
                    double[] dispSpectrum = BleClient.getInstance().fft(measureDataDisp, data_sampling).getAmplitude();
                    dispSpectrum = valueConvertByUnit(dispSpectrum,2);
                    md.setDispSpectrum(dispSpectrum);
                }
                measureDataDisp = valueConvertByUnit(measureDataDisp, 2);

            }
            else {
                measureDataDisp = BleClient.getInstance().accToDist(measureDataAcc, data_sampling, 10, UpperLimitingFreq);
                md.setMeasValueDisp(value(measureDataDisp)*100000);
                md.setPeakValueDisp(fvalue(measureDataDisp)*100000);
                md.setPeakPeakValueDisp(ffvalue(measureDataDisp)*100000);
                md.setKurtosisValueSpeed(qdvalue(measureDataDisp)*100000);
                if (needFFT) {
                    double[] dispSpectrum = BleClient.getInstance().fft(measureDataDisp, data_sampling).getAmplitude();
                    dispSpectrum = valueConvertByUnit(dispSpectrum,2);
                    md.setDispSpectrum(dispSpectrum);
                }
                measureDataDisp = valueConvertByUnit(measureDataDisp, 2);
            }
        }

        md.setMdAcc(measureDataAcc);
        md.setMdSpeed(measureDataSpeed);
        md.setMdDisp(measureDataDisp);

        return md;
    }

    /**
     * 根据单位进行值换算 1是速度 2是位移
     */
    private double[] valueConvertByUnit(double[] values, int type) {
        for (int i = 0; i < values.length; i++) {
            if (type == 1) {
                values[i] = values[i] * 1000;
            }
            else {
                values[i] = values[i] * 1000000;
            }
        }
        return values;
    }

    /*
    平方和
     */
    private double sumOfSquare(double[] data){
        double sum = 0;
        for(double v : data){
            sum = Math.pow(v,2)+sum;
        }
        return sum;
    }

    /*
    有效值
     */
    private double value(double[] data){
        double ret = 0;
        ret = Math.sqrt(sumOfSquare(data)/(data.length*1.0));
        return ret;
    }

    /**
     * 峰值
     */
    private static double fvalue(double[] data) {
        try {
            return Math.max(Math.abs(max(data) - mean(data)), Math.abs(min(data) - mean(data)));
        }
        catch (Exception ex) {
            String str = ex.toString();
            return 0.0;
        }
    }


    /**
     * 峰峰值
     */
    public static double ffvalue(double[] data) {
        try {
            return max(data) - min(data);
        }
        catch (Exception ex) {
            String str = ex.toString();
            return 0.0;
        }
    }

    /**
     * 峭度值
     */
    public static double qdvalue(double[] data) {
        try {
            double[] newData = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                newData[i] = Math.pow(data[i], 4);
            }
            return Math.sqrt(Math.pow(mean(newData), 4));
        }
        catch (Exception ex) {
            String str = ex.toString();
            return 0.0;
        }
    }


    /**
     * 最大值
     */
    public static double max(double[] data) {
        double max = data[0];
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    /**
     * 最小值
     */
    public static double min(double[] data) {
        double min = data[0];
        for (int i = 0; i < data.length; i++) {
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    /**
     * 平均值
     */
    private static double mean(double[] data) {
        double sum = 0;
        double avg = 0;
        for (int i = 0; i < data.length; i++) {
            sum = sum+data[i];
        }
        avg = sum/data.length;
        return avg;
    }


    /*
        画图形-
     */
    private void drawMap(double[] datas){
        //根据这个画出趋势-x轴为时间，y轴为值，x轴两点之间间隔：时域波形为df，频谱为dms，单位均为ms


    }
}


