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

    private BaseFragment mFragment;

    public static PageId getPageId(BaseFragment baseFragment) {
        for (PageId pageId : PageId.values()) {
            if (baseFragment == pageId.getFragment()) {
                return pageId;
            }
        }
        return null;
    }

    abstract BaseFragment getFragmentInstance();

    public BaseFragment getFragment() {
        if (mFragment == null) {
            mFragment = getFragmentInstance();
        }
        return mFragment;
    }

    public void setmFragment(BaseFragment mFragment) {
        this.mFragment = mFragment;
    }
}
