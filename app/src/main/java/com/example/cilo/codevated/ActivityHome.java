package com.example.cilo.codevated;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by cilo on 4/20/17.
 */

public class ActivityHome extends AppCompatActivity implements View.OnClickListener{
    Toolbar toolbar;
    ImageView menuImg, postConceptImg,logoImg,notifyImg,userImg;
    TextView notifyCount;
    TabLayout tabLayout;
    ViewPager viewPager;
    Intent intent;
    Common common;

    LocalUserStorage localUserStorage;
    User user;

    HashMap<String,String> requestFromServerHashmap;
    HandleJsonDataFromServer handleJsonDataFromServer;
    ArrayList<HashMap<String,String>> dataFromServerArraylist;
    int dataFromServerState;
    String url;

    BroadcastReceiver broadcastReceiver;
    int rand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(0, 0);

        common = new Common(this);
        localUserStorage = new LocalUserStorage(this);
        user = localUserStorage.getSignedinUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        menuImg = (ImageView) toolbar.findViewById(R.id.menu_icon);
        postConceptImg = (ImageView) toolbar.findViewById(R.id.post_concept_icon);
        logoImg = (ImageView) toolbar.findViewById(R.id.logo_icon);
        notifyImg = (ImageView) toolbar.findViewById(R.id.notify_icon);
        userImg = (ImageView) toolbar.findViewById(R.id.user_icon);
        notifyCount = (TextView) toolbar.findViewById(R.id.notify_count);

        if(common.isNetworkAvailable() == true) {
            startService(new Intent(this, ServiceNotificationStatus.class));
        }

        menuImg.setOnClickListener(this);
        postConceptImg.setOnClickListener(this);
        logoImg.setOnClickListener(this);
        notifyImg.setOnClickListener(this);
        userImg.setOnClickListener(this);

        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pager);

        tabLayout.addTab(tabLayout.newTab().setText("Latest Posts"));
        tabLayout.addTab(tabLayout.newTab().setText("Concepts"));
        tabLayout.addTab(tabLayout.newTab().setText("Circles"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount(),"Home");

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        rand = (int) (System.currentTimeMillis() * dataFromServerState);

        if(common.isNetworkAvailable() == true) {

            if (broadcastReceiver == null) {
                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String item = intent.getStringExtra("data");
                        Log.d("cilo11111", item);
                        handleJsonDataFromServer = new HandleJsonDataFromServer(item);
                        dataFromServerState = handleJsonDataFromServer.getTotalUnreadNotifications();
                        if (dataFromServerState == -1) {

                        } else {
                            if (dataFromServerState > 0) {
                                notifyCount.setText("" + dataFromServerState);
                                notifyCount.setVisibility(View.VISIBLE);

                                common.notification(rand,dataFromServerState+" new notifications");
                            }
                        }
                    }
                };
            }
            registerReceiver(broadcastReceiver, new IntentFilter("notifications"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.menu_icon:
                    common.moreMenuPopUp();
                break;
            case R.id.post_concept_icon:
                intent = new Intent(ActivityHome.this,ActivityPostConcept.class);
                startActivity(intent);
                break;
            case R.id.notify_icon:
                intent = new Intent(ActivityHome.this,ActivityNotification.class);
                startActivity(intent);
                break;
            case R.id.user_icon:
                intent = new Intent(ActivityHome.this,ActivityUserProfile.class);
                startActivity(intent);
                break;
        }
    }
}
