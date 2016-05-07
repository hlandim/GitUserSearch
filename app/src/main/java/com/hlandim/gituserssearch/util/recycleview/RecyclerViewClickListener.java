package com.hlandim.gituserssearch.util.recycleview;

import android.view.View;

/**
 * Created by hlandim on 5/7/16.
 */
public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
