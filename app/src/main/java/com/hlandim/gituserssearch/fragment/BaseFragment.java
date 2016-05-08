package com.hlandim.gituserssearch.fragment;

import android.support.v4.app.Fragment;

import com.hlandim.gituserssearch.inteface.NewFragmentStateListener;

/**
 * Created by hlandim on 5/7/16.
 */
public class BaseFragment extends Fragment {

    private NewFragmentStateListener _fragmentStateListener;

    public void setFragmentStateListener(NewFragmentStateListener listener){
        _fragmentStateListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (_fragmentStateListener != null)
            _fragmentStateListener.onFragmentRemoved(this);
    }

}
