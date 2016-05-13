package com.hlandim.gituserssearch.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.hlandim.gituserssearch.R;
import com.hlandim.gituserssearch.adapter.UsersListAdapter;
import com.hlandim.gituserssearch.controller.SearchController;
import com.hlandim.gituserssearch.model.User;

import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchFragment extends BaseFragment implements UsersListAdapter.UserListListener {

    private SearchController mController;
    private EditText mEdtSearch;
    private RecyclerView mRvUsers;
    private UsersListAdapter mUsersListAdapter;
    private ProgressBar mPbLoading;
    private Paint mPaint = new Paint();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mController = new SearchController(getContext());
        configureBtnFind(view);
        configureUserList(view);
        mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
        return view;
    }

    private void configureBtnFind(View view) {
        Button btn_find = (Button) view.findViewById(R.id.btn_find);
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsers();
            }
        });
    }

    private void configureUserList(final View view) {
        mEdtSearch = (EditText) view.findViewById(R.id.edt_search);
        mRvUsers = (RecyclerView) view.findViewById(R.id.rv_users);
        RecyclerView.LayoutManager gridLayoutManager = new LinearLayoutManager(getActivity());
        mRvUsers.setLayoutManager(gridLayoutManager);
        if (mUsersListAdapter == null) {
            List<User> users = mController.getLocalUsers();
            mUsersListAdapter = new UsersListAdapter(users);
        }
        mRvUsers.setAdapter(mUsersListAdapter);
        mUsersListAdapter.setmListener(this);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                User user = mUsersListAdapter.getUser(position);
                mController.removeUser(user);
                mUsersListAdapter.remover(position);


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        mPaint.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,mPaint);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,mPaint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        itemTouchHelper.attachToRecyclerView(mRvUsers);

    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }

    private void getUsers() {
        String search = mEdtSearch.getText().toString();
        if (TextUtils.isEmpty(search)) {
            mEdtSearch.setError("Digite um usuário válido!");
        } else {
            mEdtSearch.setError(null);
            mPbLoading.setVisibility(View.VISIBLE);
            getUser(search);
        }
    }

    private void getUser(String search) {
        mController.getGitHubUsers(search, new SearchController.GetUsersCallback() {
            @Override
            public void onGotUsers(final User user) {
                if (user != null) {
                    mUsersListAdapter.addUseTop(user);
                    mRvUsers.scrollToPosition(0);
                } else {
                    showToast(getString(R.string.no_users_found));
                }

                mPbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onGotError(String errorMessage) {
                showToast(errorMessage);
                mPbLoading.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUserSelected(User user) {
        showFacebookSharingDialog(user);
    }

    private void showFacebookSharingDialog(User user) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(user.getUrl()))
                .setImageUrl(Uri.parse(user.getAvatarUrl()))
                .setContentTitle(user.getLogin())
                .setContentDescription(getActivity().getString(R.string.facebook_share_description))
                .build();

        ShareDialog shareDialog = new ShareDialog(getActivity());
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }
}
