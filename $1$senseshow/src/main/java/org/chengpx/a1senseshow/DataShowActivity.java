package org.chengpx.a1senseshow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.chengpx.a1senseshow.domain.RoadBean;
import org.chengpx.a1senseshow.domain.SenseBean;
import org.chengpx.mylib.AppException;
import org.chengpx.mylib.common.DataUtils;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * create at 2018/4/27 16:27 by chengpx
 */
public class DataShowActivity extends Activity {

    private static String sTag = "org.chengpx.a1senseshow.DataShowActivity";
    private static int sRomveEntryCount;

    private LineChart senseshow_linechart_data;
    private Timer mTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datashow);
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    private void initData() {
        String senseName = getIntent().getStringExtra("senseName");
        MyTimerTask myTimerTask = null;
        if (!TextUtils.isEmpty(senseName)) {// 传感器
            Log.d(sTag, senseName);
            SenseBean senseBean = new SenseBean();
            senseBean.setSenseName(senseName);
            myTimerTask = new MyTimerTask("http://192.168.2.19:9090/transportservice/type/jason/action/GetSenseByName.do",
                    new GetSenseByNameCallBack(Map.class, senseshow_linechart_data, senseName), senseBean);
        } else {// 道路状态
            RoadBean roadBean = new RoadBean();
            roadBean.setRoadId(1);
            myTimerTask = new MyTimerTask("http://192.168.2.19:9090/transportservice/type/jason/action/GetRoadStatus.do",
                    new GetRoadStatusCallBack(RoadBean.class, senseshow_linechart_data), roadBean);
        }
        mTimer = new Timer();
        mTimer.schedule(myTimerTask, 0, 5000);
    }

    private void initView() {
        senseshow_linechart_data = (LineChart) findViewById(R.id.senseshow_linechart_data);

        senseshow_linechart_data.getDescription().setEnabled(false);
        senseshow_linechart_data.animateXY(200, 500);
        senseshow_linechart_data.getLegend().setForm(Legend.LegendForm.LINE);
        senseshow_linechart_data.setMaxVisibleValueCount(10);// 在 lineData.setDrawValues(true) 是才生效

        XAxis xAxis = senseshow_linechart_data.getXAxis();
        xAxis.setLabelCount(10);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);// 不绘制网格线
        xAxis.setGranularity(5);// 设置粒度

        YAxis axisLeft = senseshow_linechart_data.getAxisLeft();
        int[] range = getIntent().getIntArrayExtra("range");
        axisLeft.setAxisMinimum(range[0]);
        axisLeft.setAxisMaximum(range[1]);
        axisLeft.setDrawGridLines(false);// 不绘制网格线
        senseshow_linechart_data.getAxisRight().setEnabled(false);// 不使用右侧 y 轴线
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sRomveEntryCount = 0;
        initData();
    }

    private static class GetSenseByNameCallBack extends HttpUtils.Callback<Map> {

        private final LineChart senseshow_linechart_data_inner;
        private final String mSenseName;

        /**
         * @param mapClass                 结果数据封装体类型字节码
         * @param senseshow_linechart_data
         * @param senseName
         */
        public GetSenseByNameCallBack(Class<Map> mapClass, LineChart senseshow_linechart_data, String senseName) {
            super(mapClass);
            senseshow_linechart_data_inner = senseshow_linechart_data;
            mSenseName = senseName;
        }

        @Override
        protected void onSuccess(Map map) {
            try {
                addEntry(DataUtils.obj2int(map.get(mSenseName)), senseshow_linechart_data_inner, mSenseName);
            } catch (AppException e) {
                e.printStackTrace();
            }
        }

    }

    private static class GetRoadStatusCallBack extends HttpUtils.Callback<RoadBean> {

        private final LineChart senseshow_linechart_data_inner;

        /**
         * @param roadBeanClass            结果数据封装体类型字节码
         * @param senseshow_linechart_data
         */
        public GetRoadStatusCallBack(Class<RoadBean> roadBeanClass, LineChart senseshow_linechart_data) {
            super(roadBeanClass);
            senseshow_linechart_data_inner = senseshow_linechart_data;
        }

        @Override
        protected void onSuccess(RoadBean roadBean) {
            addEntry(roadBean.getStatus(), senseshow_linechart_data_inner, "RoadStatus");
        }


    }

    private static void addEntry(int yVal, LineChart senseshow_linechart_data_inner, String desc) {
        LineData lineData = senseshow_linechart_data_inner.getData();
        if (lineData == null) {
            lineData = new LineData();
            lineData.setDrawValues(true);// 为所有 DataSet 启用 / 禁用绘图值（值 - 文本）这个数据对象包含。
            senseshow_linechart_data_inner.setData(lineData);
        }
        if (lineData.getDataSetCount() < 1) {
            LineDataSet lineDataSet = new LineDataSet(new ArrayList<Entry>(), desc);
            lineDataSet.setDrawValues(true);
            lineData.addDataSet(lineDataSet);
            // lineDataSet.setLineWidth(2.5f);
            // lineDataSet.setColor(Color.GRAY);
            // lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);// 设置这个 DataSet 应绘制的 Y 轴（LEFT 或 RIGHT）。 默认值：LEFT
            // lineDataSet.setValueTextSize(10f);
        }
        lineData.addEntry(new Entry((lineData.getDataSetByIndex(0).getEntryCount() + sRomveEntryCount) * 5, yVal), 0);
        if (lineData.getDataSetByIndex(0).getEntryCount() > 10) {
            lineData.removeEntry(lineData.getDataSetByIndex(0).getEntryForIndex(0), 0);
            sRomveEntryCount++;
            Log.d(sTag, "sRomveEntryCount = " + sRomveEntryCount);
        }
        senseshow_linechart_data_inner.setMaxVisibleValueCount(10);// 在 lineData.setDrawValues(true) 是才生效
        lineData.notifyDataChanged();
        senseshow_linechart_data_inner.notifyDataSetChanged();
        senseshow_linechart_data_inner.invalidate();
    }

    private class MyTimerTask extends TimerTask {

        private final String mUrl;
        private final HttpUtils.Callback mCallback;
        private final Object mParams;

        public MyTimerTask(String url, HttpUtils.Callback callback, Object params) {
            mUrl = url;
            mCallback = callback;
            mParams = params;
        }

        @Override
        public void run() {
            RequestPool.getInstance().add(mUrl,
                    mParams, mCallback);
        }

    }

}
