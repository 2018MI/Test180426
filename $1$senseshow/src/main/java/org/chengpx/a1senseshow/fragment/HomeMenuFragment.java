package org.chengpx.a1senseshow.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.chengpx.a1senseshow.R;

/**
 * create at 2018/4/28 16:00 by chengpx
 */
public class HomeMenuFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView senseshow_lv_homemenu;
    private FragmentActivity mFragmentActivity;
    private String[] mItemStrArr = {
            "系统环境实时显示", "传感器数据历史记录功能", "第七题阈值设置功能"
    };
    private Fragment[] mFragmentArr = {
            new SystemEnvShowFragment(),
            new SenseHistoryShowFragment(),
            new ThresholdSettingFragment()
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homemenu, container, false);
        mFragmentActivity = getActivity();
        initView(view);
        initData();
        main();
        initListener();
        return view;
    }

    private void initListener() {
        senseshow_lv_homemenu.setOnItemClickListener(this);
    }

    private void main() {
        senseshow_lv_homemenu.setAdapter(new ArrayAdapter<String>(
                mFragmentActivity, android.R.layout.simple_list_item_1, mItemStrArr
        ));
    }

    private void initData() {
    }

    private void initView(View view) {
        senseshow_lv_homemenu = (ListView) view.findViewById(R.id.senseshow_lv_homemenu);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        assert getFragmentManager() != null;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.senseshow_fl_content, mFragmentArr[position], "");
        fragmentTransaction.commit();
    }

}
