package com.hlandim.gituserssearch.fragment;

import android.support.v4.app.Fragment;

import com.hlandim.gituserssearch.inteface.NewFragmentStateListener;

/**
 * Created by hlandim on 5/7/16.
 */
public class BaseFragment extends Fragment {

    private NewFragmentStateListener mFragmentStateListener;

    public void setFragmentStateListener(NewFragmentStateListener listener) {
        mFragmentStateListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragmentStateListener != null)
            mFragmentStateListener.onFragmentRemoved(this);
    }

}
