package com.hlandim.gituserssearch.util;

import com.hlandim.gituserssearch.fragment.BaseFragment;
import com.hlandim.gituserssearch.fragment.SearchFragment;

/**
 * Created by hlandim on 5/7/16.
 */
public enum PageId {
    SEARCH {
        @Override
        BaseFragment getFragmentInstance() {
            return new SearchFragment();
        }
    };

    private BaseFragment fragment;

    abstract BaseFragment getFragmentInstance();

    public BaseFragment getFragment() {
        if (fragment == null) {
            fragment = getFragmentInstance();
        }
        return fragment;
    }

    public void setFragment(BaseFragment fragment) {
        this.fragment = fragment;
    }
}