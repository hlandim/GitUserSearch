package com.hlandim.gituserssearch.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.hlandim.gituserssearch.util.recycleview.RecyclerViewClickListener;
import com.hlandim.gituserssearch.util.recycleview.RecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchFragment extends BaseFragment {

    private SearchController controller;
    private EditText edt_search;
    private RecyclerView rv_users;
    private UsersListAdapter usersListAdapter;
    private ProgressBar pb_loading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        configureBtnFind(view);
        configureUserList(view);
        controller = new SearchController(getContext());
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
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

    private void configureUserList(View view) {
        edt_search = (EditText) view.findViewById(R.id.edt_search);
        rv_users = (RecyclerView) view.findViewById(R.id.rv_users);
        RecyclerView.LayoutManager gridLayoutManager = new LinearLayoutManager(getActivity());
        rv_users.setLayoutManager(gridLayoutManager);
        if (usersListAdapter == null) {
            usersListAdapter = new UsersListAdapter(new ArrayList<User>());
        }
        rv_users.setAdapter(usersListAdapter);
        final Context applicationContext = getActivity().getApplicationContext();
        rv_users.addOnItemTouchListener(new RecyclerViewTouchListener(applicationContext, rv_users, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Toast.makeText(applicationContext, usersListAdapter.getList().get(position).getLogin() + " is clicked!", Toast.LENGTH_SHORT).show();
                User user = usersListAdapter.getList().get(position);
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(user.getUrl()))
                        .setImageUrl(Uri.parse(user.getAvatar_url()))
                        .setContentTitle(user.getLogin())
                        .setContentDescription(getActivity().getString(R.string.facebook_share_description))
                        .build();

                ShareDialog shareDialog = new ShareDialog(getActivity());
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

            }

            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(applicationContext, usersListAdapter.getList().get(position).getLogin() +" is long pressed!", Toast.LENGTH_SHORT).show();

            }
        }));
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }

    private void getUsers() {
        String search = edt_search.getText().toString();
        if (TextUtils.isEmpty(search)) {
            edt_search.setError("Digite um usuário válido!");
        } else {
            edt_search.setError(null);
            pb_loading.setVisibility(View.VISIBLE);
            controller.getUsers(search, new SearchController.GetUsersCallback() {
                @Override
                public void onGotUsers(List<User> users) {
//                showToast("Total: " + users.size());
                    if (users != null && users.size() > 0) {
                        User user = users.get(0);
                        usersListAdapter.addUseTop(user);
                        rv_users.scrollToPosition(0);
                    } else {
                        showToast("Nenhum usuário encontrado!");
                    }

                    pb_loading.setVisibility(View.GONE);
                }

                @Override
                public void onGotError(String errorMessage) {
                    showToast("Error : " + errorMessage);
                    pb_loading.setVisibility(View.GONE);
                }
            });
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
