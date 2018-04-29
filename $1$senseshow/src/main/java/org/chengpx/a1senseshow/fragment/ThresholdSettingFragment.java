package org.chengpx.a1senseshow.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.chengpx.a1senseshow.R;
import org.chengpx.mylib.BaseFragment;
import org.chengpx.mylib.common.SpUtils;

import java.util.Arrays;

/**
 * 第七题阈值设置功能
 * create at 2018/4/29 10:28 by chengpx
 */
public class ThresholdSettingFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    private static String sTag = "org.chengpx.a1senseshow.fragment.ThresholdSettingFragment";

    private Switch thresholdsetting_switch_isautocallpolice;
    private EditText thresholdsetting_et_temperature;
    private EditText thresholdsetting_et_humidity;
    private EditText thresholdsetting_et_LightIntensity;
    private EditText thresholdsetting_et_co2;
    private EditText thresholdsetting_et_pm25;
    private EditText thresholdsetting_et_RoadStatus;
    private Button thresholdsetting_btn_save;

    private EditText[] mEditTextArr;
    private int[][] mRangeArr = {
            new int[]{0, 37}, new int[]{20, 80}, new int[]{1, 7000},
            new int[]{350, 5000}, new int[]{0, 300}, new int[]{1, 5}
    };
    private String[] mSystemEnvNameArr = {
            "temperature", "humidity", "LightIntensity", "co2", "pm2.5", "RoadStatus"
    };

    @Override
    protected void initListener() {
        thresholdsetting_btn_save.setOnClickListener(this);
        thresholdsetting_switch_isautocallpolice.setOnCheckedChangeListener(this);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thresholdsetting, container, false);
        thresholdsetting_switch_isautocallpolice = (Switch) view.findViewById(R.id.thresholdsetting_switch_isautocallpolice);
        thresholdsetting_et_temperature = (EditText) view.findViewById(R.id.thresholdsetting_et_temperature);
        thresholdsetting_et_humidity = (EditText) view.findViewById(R.id.thresholdsetting_et_humidity);
        thresholdsetting_et_LightIntensity = (EditText) view.findViewById(R.id.thresholdsetting_et_LightIntensity);
        thresholdsetting_et_co2 = (EditText) view.findViewById(R.id.thresholdsetting_et_co2);
        thresholdsetting_et_pm25 = (EditText) view.findViewById(R.id.thresholdsetting_et_pm25);
        thresholdsetting_et_RoadStatus = (EditText) view.findViewById(R.id.thresholdsetting_et_RoadStatus);
        thresholdsetting_btn_save = (Button) view.findViewById(R.id.thresholdsetting_btn_save);
        return view;
    }

    @Override
    protected void onDie() {

    }

    @Override
    protected void main() {
        SpUtils spUtils = SpUtils.getInstance(mFragmentActivity);
        boolean isautocallpolice = spUtils.getBoolean("isautocallpolice", true);
        thresholdsetting_switch_isautocallpolice.setChecked(isautocallpolice);
        for (int index = 0; index < mEditTextArr.length; index++) {
            int val = spUtils.getInt(mSystemEnvNameArr[index], -999);
            EditText editText = mEditTextArr[index];
            editText.setEnabled(!isautocallpolice);
            if (val != -999) {
                editText.setText(val + "");
            }
        }
    }

    @Override
    protected void initData() {
        mEditTextArr = new EditText[]{
                thresholdsetting_et_temperature, thresholdsetting_et_humidity,
                thresholdsetting_et_LightIntensity, thresholdsetting_et_co2,
                thresholdsetting_et_pm25, thresholdsetting_et_RoadStatus,
        };
    }

    @Override
    protected void onDims() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thresholdsetting_btn_save:
                save();
                break;
        }
    }

    private void save() {
        if (thresholdsetting_switch_isautocallpolice.isChecked()) {
            Toast.makeText(mFragmentActivity, "请先关闭自动报警", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int index = 0; index < mEditTextArr.length; index++) {
            String str = mEditTextArr[index].getText().toString();
            if (TextUtils.isEmpty(str)) {
                continue;
            }
            if (!str.matches("^[1-9]\\d*$")) {
                Toast.makeText(mFragmentActivity, mSystemEnvNameArr[index] + " 阈值非法", Toast.LENGTH_SHORT).show();
                return;
            }
            int val = Integer.parseInt(str);
            int[] range = mRangeArr[index];
            if (range[0] > val || val > range[1]) {
                Toast.makeText(mFragmentActivity, mSystemEnvNameArr[index] + " 阈值非法, 阈值范围: " + Arrays.toString(range), Toast.LENGTH_SHORT).show();
                return;
            }
            SpUtils.getInstance(mFragmentActivity).putInt(mSystemEnvNameArr[index], val);
        }
        Toast.makeText(mFragmentActivity, "ok", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(sTag, "ischecked = " + isChecked + ", " + buttonView.toString());
        for (EditText editText : mEditTextArr) {
            editText.setEnabled(!isChecked);
        }
        SpUtils.getInstance(mFragmentActivity).putBoolean("isautocallpolice", isChecked);
    }

}
