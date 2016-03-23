package com.android.ketaoapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.android.ketaoapp.config.Define;
import com.android.ketaoapp.fragment.HomeFragment;
import com.android.ketaoapp.fragment.MessageFragment;
import com.android.ketaoapp.fragment.ProfileFragment;
import com.android.ketaoapp.util.HTTPRequestUtil;
import com.android.ketaoapp.view.MyTabView;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity implements MyTabView.onTabSelectListener {

    private int current = 0;

    private Fragment homeFragment, messageFragment, profileFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        showFragment(0);

    }

    @Override
    public void onTabSelect(int position) {
        if(current == position) return;
        current = position;
        showFragment(position);
    }

    private void showFragment(int index){
        FragmentTransaction ft = fm.beginTransaction();
        hideFragment(ft);

        switch (index){
            case 0:
                if(homeFragment != null){
                    ft.show(homeFragment);
                }
                else{
                    homeFragment = new HomeFragment();
                    ft.add(R.id.mypage, homeFragment);
                }
                break;
            case 1:
                if(messageFragment != null){
                    ft.show(messageFragment);
                }
                else{
                    messageFragment = new MessageFragment();
                    ft.add(R.id.mypage, messageFragment);
                }
                break;
            case 2:
                if(profileFragment != null){
                    ft.show(profileFragment);
                }
                else{
                    profileFragment = new ProfileFragment();
                    ft.add(R.id.mypage, profileFragment);
                }
                break;
        }
        ft.commit();
    }

    private void hideFragment(FragmentTransaction ft){
        if(homeFragment != null){
            ft.hide(homeFragment);
        }
        if(messageFragment != null){
            ft.hide(messageFragment);
        }
        if(profileFragment != null){
            ft.hide(profileFragment);
        }
    }
}
