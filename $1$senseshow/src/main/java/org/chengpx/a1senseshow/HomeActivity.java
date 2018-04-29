package org.chengpx.a1senseshow;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import org.chengpx.a1senseshow.fragment.HomeMenuFragment;

/**
 * create at 2018/4/28 15:49 by chengpx
 */
public class HomeActivity extends SlidingFragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initFragment();
    }

    private void initFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.senseshow_fl_homemenu, new HomeMenuFragment(), "");
        fragmentTransaction.commit();
    }

    private void initView() {
        setBehindContentView(R.layout.slidingmenu_home);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置全屏触摸滑出菜单
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        assert windowManager != null;
        int width = windowManager.getDefaultDisplay().getWidth();
        slidingMenu.setBehindOffset((int) (width * (400 * 1.0f / 500)));
    }

}
