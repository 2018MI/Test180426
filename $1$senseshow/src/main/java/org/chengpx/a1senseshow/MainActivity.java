package org.chengpx.a1senseshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import org.chengpx.a1senseshow.dao.RoadDao;
import org.chengpx.a1senseshow.dao.SenseDao;
import org.chengpx.a1senseshow.domain.SenseBean;
import org.chengpx.mylib.AppException;
import org.chengpx.mylib.common.DataUtils;
import org.chengpx.mylib.http.HttpUtils;
import org.chengpx.mylib.http.RequestPool;
import org.chengpx.a1senseshow.domain.RoadBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static String sTag = "org.chengpx.a1senseshow.MainActivity";
    private static List<Map<String, Object>> sMapList;
    private static SenseAdapter sSenseAdapter;
    private static Context sContext;

    private GridView senseshow_gridview_data;
    private String[] mSenseDescArr = {
            "空气温度", "空气湿度", "光照",
            "CO2", "PM2.5", "道路状态"
    };
    private String[] mSenseNameArr = {
            "temperature", "humidity", "LightIntensity", "co2", "pm2.5", "RoadStatus"
    };
    private int[][] mRangeArr = {
            new int[]{0, 37}, new int[]{20, 80}, new int[]{1, 7000},
            new int[]{350, 5000}, new int[]{0, 300}, new int[]{1, 5}
    };
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sContext = this;
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        main();
        initListener();
    }

    private void initListener() {
        senseshow_gridview_data.setOnItemClickListener(this);
    }

    private void main() {
        sSenseAdapter = new SenseAdapter();
        senseshow_gridview_data.setAdapter(sSenseAdapter);
    }

    private void initData() {
        sMapList = new ArrayList<>();
        for (String aMSenseNameArr : mSenseNameArr) {
            sMapList.add(new HashMap<String, Object>());
        }
        List<String> senseNameList = Arrays.asList(mSenseNameArr);
        GetAllSenseCallBack getAllSenseCallBack = new GetAllSenseCallBack(Map.class,
                mSenseDescArr, senseNameList, mRangeArr);
        GetRoadStatusCallBack getRoadStatusCallBack = new GetRoadStatusCallBack(
                RoadBean.class, mSenseDescArr, senseNameList, mRangeArr);
        RoadBean roadBean = new RoadBean();
        roadBean.setRoadId(1);
        mTimer = new Timer();
        mTimer.schedule(new MyTimerTask(getAllSenseCallBack, getRoadStatusCallBack, roadBean), 0, 5000);
    }

    private void initView() {
        senseshow_gridview_data = (GridView) findViewById(R.id.senseshow_gridview_data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DataShowActivity.class);
        Map<String, Object> item = sSenseAdapter.getItem(position);
        intent.putExtra("senseName", (CharSequence) item.get("senseName"));
        intent.putExtra("range", (int[]) item.get("range"));
        startActivity(intent);
    }

    private static class GetAllSenseCallBack extends HttpUtils.Callback<Map> {


        private final String[] mSenseDescArr_inner;
        private final List<String> mSenseNameList_innner;
        private final int[][] mRangeArr_inner;
        /**
         * 数据库持久化计数器
         */
        private int mSaveCount;

        /**
         * @param mapClass      结果数据封装体类型字节码
         * @param senseDescArr
         * @param senseNameList
         * @param rangeArr
         */
        public GetAllSenseCallBack(Class<Map> mapClass, String[] senseDescArr,
                                   List<String> senseNameList, int[][] rangeArr) {
            super(mapClass);
            mSenseDescArr_inner = senseDescArr;
            mSenseNameList_innner = senseNameList;
            mRangeArr_inner = rangeArr;
        }

        @Override
        protected void onSuccess(Map map) {
            Log.d(sTag, map.toString());
            List<Object> keyList = new ArrayList<>();
            List<SenseBean> senseBeanList = new ArrayList<>();
            if (keyList.addAll(map.keySet())) {
                for (Object key : keyList) {
                    Object val = map.get(key);
                    if (!(key instanceof String)) {
                        continue;
                    }
                    int index = mSenseNameList_innner.indexOf((String) key);
                    if (index == -1) {
                        continue;
                    }
                    if (index < mSenseNameList_innner.size()) {
                        Map<String, Object> objMap = sMapList.get(index);
                        objMap.put("val", val);
                        objMap.put("desc", mSenseDescArr_inner[index]);
                        objMap.put("senseName", key);
                        objMap.put("range", mRangeArr_inner[index]);
                        // 数据库实体类准备
                        SenseBean senseBean = new SenseBean();
                        senseBean.setSenseName((String) key);
                        senseBean.setStartRange(mRangeArr_inner[index][0]);
                        senseBean.setEndRange(mRangeArr_inner[index][1]);
                        senseBean.setDesc(mSenseDescArr_inner[index]);
                        senseBean.setInsertDate(new Date());
                        try {
                            senseBean.setVal(DataUtils.obj2int(val));
                        } catch (AppException e) {
                            e.printStackTrace();
                        }
                        senseBeanList.add(senseBean);
                    }
                }
            }
            if (sSenseAdapter != null) {
                sSenseAdapter.notifyDataSetChanged();
            }
            // 数据持久化
            SenseDao senseDao = SenseDao.getInstance(sContext);
            if (mSaveCount > (1000 * 60 * 3) / 5000) {
                int delete = senseDao.delete();
                Log.d(sTag, "SenseDao delete: " + delete);
                mSaveCount = 0;
            }
            for (SenseBean senseBean : senseBeanList) {
                int insert = senseDao.insert(senseBean);
                Log.d(sTag, "SenseDao insert: " + insert);
            }
            mSaveCount++;
        }

    }

    private class MyTimerTask extends TimerTask {

        private final GetAllSenseCallBack mGetAllSenseCallBack;
        private final GetRoadStatusCallBack mGetRoadStatusCallBack;
        private final RoadBean mRoadBean;

        public MyTimerTask(GetAllSenseCallBack getAllSenseCallBack, GetRoadStatusCallBack getRoadStatusCallBack, RoadBean roadBean) {
            mGetAllSenseCallBack = getAllSenseCallBack;
            mGetRoadStatusCallBack = getRoadStatusCallBack;
            mRoadBean = roadBean;
        }

        @Override
        public void run() {
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetAllSense.do",
                    null, mGetAllSenseCallBack);
            RequestPool.getInstance().add("http://192.168.2.19:9090/transportservice/type/jason/action/GetRoadStatus.do",
                    mRoadBean, mGetRoadStatusCallBack);
            /*RequestPool.getInstance().add("http://192.168.2.17:8080/transportservice/action/GetAllSense.do",
                    null, mGetAllSenseCallBack);
            RequestPool.getInstance().add("http://192.168.2.17:8080/transportservice/action/GetRoadStatus.do",
                    mRoadBean, mGetRoadStatusCallBack);*/
        }

    }

    private class SenseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return sMapList.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return sMapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.gradview_senseshow_gridview_data,
                        senseshow_gridview_data, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> item = getItem(position);
            viewHolder.getSenseshow_tv_desc().setText(item.get("desc") + "");
            TextView senseshow_tv_val = viewHolder.getSenseshow_tv_val();
            Object oVal = item.get("val");
            senseshow_tv_val.setText(oVal + "");
            if (oVal != null) {
                int iVal = 0;
                try {
                    iVal = DataUtils.obj2int(oVal);
                } catch (AppException e) {
                    e.printStackTrace();
                }
                int[] range = (int[]) item.get("range");
                if (range.length == 2) {
                    float f06 = (range[0] + range[1]) * 1.0f * 0.6f;
                    if (iVal < f06) {
                        senseshow_tv_val.setBackgroundResource(R.drawable.shape_oval_green);
                    } else {
                        senseshow_tv_val.setBackgroundResource(R.drawable.shape_oval_red);
                    }
                }
            }
            return convertView;
        }

    }

    private static class GetRoadStatusCallBack extends HttpUtils.Callback<RoadBean> {

        private final String[] mSenseDescArr;
        private final List<String> mSenseNameList;
        private final int[][] mRangeArr_inner;
        /**
         * 数据库持久化计数器
         */
        private int mSaveCount;

        /**
         * @param roadBeanClass 结果数据封装体类型字节码
         * @param senseDescArr
         * @param senseNameList
         * @param rangeArr
         */
        public GetRoadStatusCallBack(Class<RoadBean> roadBeanClass, String[] senseDescArr, List<String> senseNameList, int[][] rangeArr) {
            super(roadBeanClass);
            mSenseDescArr = senseDescArr;
            mSenseNameList = senseNameList;
            mRangeArr_inner = rangeArr;
        }

        @Override
        protected void onSuccess(RoadBean roadBean) {
            Log.d(sTag, roadBean.toString());
            int index = mSenseNameList.indexOf("RoadStatus");
            Map<String, Object> objMap = sMapList.get(index);
            objMap.put("val", roadBean.getStatus());
            objMap.put("desc", mSenseDescArr[index]);
            objMap.put("range", mRangeArr_inner[index]);
            if (sSenseAdapter != null) {
                sSenseAdapter.notifyDataSetChanged();
            }
            roadBean.setDesc(mSenseDescArr[index]);
            roadBean.setInsertDate(new Date());
            // 数据持久化
            RoadDao roadDao = RoadDao.getInstance(sContext);
            if (mSaveCount > (1000 * 60 * 3) / 5000) {
                int delete = roadDao.delete();
                Log.d(sTag, "RoadDao delete: " + delete);
                mSaveCount = 0;
            }
            int insert = roadDao.insert(roadBean);
            Log.d(sTag, "RoadDao insert: " + insert);
            mSaveCount++;
        }

    }

    private static class ViewHolder {

        private final TextView senseshow_tv_desc;
        private final TextView senseshow_tv_val;

        private ViewHolder(View view) {
            senseshow_tv_desc = view.findViewById(R.id.senseshow_tv_desc);
            senseshow_tv_val = view.findViewById(R.id.senseshow_tv_val);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getSenseshow_tv_desc() {
            return senseshow_tv_desc;
        }

        public TextView getSenseshow_tv_val() {
            return senseshow_tv_val;
        }

    }

}
