package com.hlandim.gituserssearch;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.hlandim.gituserssearch.fragment.BaseFragment;
import com.hlandim.gituserssearch.inteface.NewFragmentStateListener;
import com.hlandim.gituserssearch.util.PageId;
import com.hlandim.gituserssearch.web.GitHubApi;


public class MainActivity extends AppCompatActivity implements NewFragmentStateListener {

    private static PageId currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            goToPage(PageId.SEARCH);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentPage != null) {
            currentPage.setFragment(null);
        }
    }

    private void goToPage(PageId pageId) {
        BaseFragment fragment = pageId.getFragment();
        fragment.setFragmentStateListener(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, pageId.name()).commit();
        currentPage = pageId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentPage = null;
        GitHubApi.getInstance().close();
    }


    @Override
    public void onFragmentRemoved(BaseFragment frag) {
        if (frag != null) {
            PageId pageId = PageId.getPageId(frag);
            if (pageId != null) {
                pageId.setFragment(null);
            }
        }
    }
}
