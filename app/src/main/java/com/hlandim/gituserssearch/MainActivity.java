package com.hlandim.gituserssearch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.hlandim.gituserssearch.util.PageId;
import com.hlandim.gituserssearch.web.GitHubApi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        goToPage(PageId.SEARCH);
    }

    private void goToPage(PageId pageId) {
        Fragment fragment = pageId.getFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GitHubApi.getInstance().close();
    }
}
