package org.chengpx.a1senseshow.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.chengpx.a1senseshow.R;
import org.chengpx.a1senseshow.dao.SenseDao;
import org.chengpx.a1senseshow.domain.SenseBean;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * create at 2018/4/28 16:08 by chengpx
 */
public class SenseHistoryShowFragment extends Fragment implements View.OnClickListener {

    private static String sTag = "org.chengpx.a1senseshow.fragment.SenseHistoryShowFragment";

    private Spinner senseshow_spinner_sensetype;
    private Spinner senseshow_spinner_searchcycle;
    private Button senseshow_btn_search;
    private ListView senseshow_lv_sensedata;
    private FragmentActivity mFragmentActivity;
    private String[] mSenseTypeStrArr = {
            "空气温度", "空气湿度", "光照",
            "CO2", "PM2.5"
    };
    private List<SenseBean> mSenseBeanList;
    private String[] mSenseNameArr = {
            "temperature", "humidity", "LightIntensity", "co2", "pm2.5"
    };
    private String[] mSearchCycleStrArr = {
            "5/m", "3/m"
    };
    private MyAdapter mMyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensehistoryshow, container, false);
        mFragmentActivity = getActivity();
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        main();
        initListener();
    }

    private void initListener() {
        senseshow_btn_search.setOnClickListener(this);
    }

    private void main() {
        senseshow_spinner_sensetype.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mSenseTypeStrArr
        ));
        senseshow_spinner_searchcycle.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mSearchCycleStrArr
        ));
        if (mSenseBeanList != null) {
            mMyAdapter = new MyAdapter();
            senseshow_lv_sensedata.setAdapter(mMyAdapter);
            mMyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initData() {
        loadData();
    }

    private void initView(View view) {
        senseshow_spinner_sensetype = (Spinner) view.findViewById(R.id.senseshow_spinner_sensetype);
        senseshow_spinner_searchcycle = (Spinner) view.findViewById(R.id.senseshow_spinner_searchcycle);
        senseshow_btn_search = (Button) view.findViewById(R.id.senseshow_btn_search);
        senseshow_lv_sensedata = (ListView) view.findViewById(R.id.senseshow_lv_sensedata);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.senseshow_btn_search:
                loadData();
                if (mMyAdapter != null) {
                    mMyAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void loadData() {
        int selectedItemPosition_sensetype = senseshow_spinner_sensetype.getSelectedItemPosition();
        if (selectedItemPosition_sensetype < 0) {
            selectedItemPosition_sensetype = 0;
        }
        mSenseBeanList = SenseDao.getInstance(mFragmentActivity).select(mSenseNameArr[selectedItemPosition_sensetype]);
        int selectedItemPosition_sensecycle = senseshow_spinner_searchcycle.getSelectedItemPosition();
        if (selectedItemPosition_sensecycle < 0) {
            selectedItemPosition_sensecycle = 0;
        }
        if (selectedItemPosition_sensecycle == 1) {
            int recordCount = (int) (1000 * 60 * 1.0f / 5000);
            for (int index = recordCount + 1; index < mSenseBeanList.size(); index++) {
                SenseBean remove = mSenseBeanList.remove(index);
                Log.d(sTag, "remove: " + remove.toString());
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        private final SimpleDateFormat mSimpleDateFormat;

        public MyAdapter() {
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        @Override
        public int getCount() {
            return mSenseBeanList.size();
        }

        @Override
        public SenseBean getItem(int position) {
            return mSenseBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mFragmentActivity).inflate(R.layout.lv_senseshow_lv_sensedata,
                        senseshow_lv_sensedata, false);
                viewHolder = ViewHolder.get(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SenseBean senseBean = getItem(position);
            viewHolder.getSenseshow_tv_sensedesc().setText(senseBean.getDesc());
            viewHolder.getSenseshow_tv_data().setText(senseBean.getVal() + "");
            if (senseBean.getVal() <= (senseBean.getStartRange() + senseBean.getEndRange()) * 1.0f * 0.6f) {
                viewHolder.getSenseshow_tv_isNormal().setText("正常");
            } else {
                viewHolder.getSenseshow_tv_isNormal().setText("超标");
            }
            viewHolder.getSenseshow_tv_insertDate().setText(mSimpleDateFormat.format(senseBean.getInsertDate()));
            return convertView;
        }

    }

    private static class ViewHolder {

        private final TextView senseshow_tv_sensedesc;
        private final TextView senseshow_tv_data;
        private final TextView senseshow_tv_isNormal;
        private final TextView senseshow_tv_insertDate;

        private ViewHolder(View view) {
            senseshow_tv_sensedesc = (TextView) view.findViewById(R.id.senseshow_tv_sensedesc);
            senseshow_tv_data = (TextView) view.findViewById(R.id.senseshow_tv_data);
            senseshow_tv_isNormal = (TextView) view.findViewById(R.id.senseshow_tv_isNormal);
            senseshow_tv_insertDate = (TextView) view.findViewById(R.id.senseshow_tv_insertDate);
        }

        public static ViewHolder get(View view) {
            Object tag = view.getTag();
            if (tag == null) {
                tag = new ViewHolder(view);
                view.setTag(tag);
            }
            return (ViewHolder) tag;
        }

        public TextView getSenseshow_tv_sensedesc() {
            return senseshow_tv_sensedesc;
        }

        public TextView getSenseshow_tv_data() {
            return senseshow_tv_data;
        }

        public TextView getSenseshow_tv_isNormal() {
            return senseshow_tv_isNormal;
        }

        public TextView getSenseshow_tv_insertDate() {
            return senseshow_tv_insertDate;
        }

    }

}
