package com.hlandim.gituserssearch.fragment;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchFragment extends BaseFragment implements UsersListAdapter.UserListListener {

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
        usersListAdapter.setListener(this);
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
            getUser(search);
        }
    }

    private void getUser(String search) {
        controller.getUsers(search, new SearchController.GetUsersCallback() {
            @Override
            public void onGotUsers(List<User> users) {
                if (users != null && users.size() > 0) {
                    final User user = users.get(0);
                    usersListAdapter.addUseTop(user);
                    rv_users.scrollToPosition(0);
                    getUrlHash(user);
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

    private void getUrlHash(final User user) {
        controller.getHashFromUrl(user.getUrl(), new SearchController.GetHashCallBack() {
            @Override
            public void onGetHash(String hash) {
                user.setUrl_hash(hash);
                int position = usersListAdapter.getList().indexOf(user);
                usersListAdapter.notifyItemChanged(position);
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
                .setImageUrl(Uri.parse(user.getAvatar_url()))
                .setContentTitle(user.getLogin())
                .setContentDescription(getActivity().getString(R.string.facebook_share_description))
                .build();

        ShareDialog shareDialog = new ShareDialog(getActivity());
        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
    }
}
