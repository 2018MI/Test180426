package org.chengpx.mylib;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * create at 2018/4/29 10:28 by chengpx
 */
public abstract class BaseFragment extends Fragment {

    protected FragmentActivity mFragmentActivity;

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentActivity = getActivity();
        View view = initView(inflater, container, savedInstanceState);
        initListener();
        return view;
    }

    protected abstract void initListener();

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDie();
    }

    protected abstract void onDie();

    @Override
    public void onResume() {
        super.onResume();
        initData();
        main();
    }

    protected abstract void main();

    protected abstract void initData();

    @Override
    public void onPause() {
        super.onPause();
        onDims();
    }

    protected abstract void onDims();

}
