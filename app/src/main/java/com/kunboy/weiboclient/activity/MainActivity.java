package com.kunboy.weiboclient.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.kunboy.weiboclient.R;
import com.kunboy.weiboclient.fragment.MainFragment;


/**
 * Created by sunhongkun on 2015/2/8.
 */
public class MainActivity extends FragmentActivity {

    private final String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName()) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, MainFragment.getNewInstance(), MainFragment.class.getName());
            ft.commit();
        }

    }


}
