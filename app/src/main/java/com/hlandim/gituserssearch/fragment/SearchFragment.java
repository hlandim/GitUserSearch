package com.hlandim.gituserssearch.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hlandim.gituserssearch.R;
import com.hlandim.gituserssearch.adapter.UsersListAdapter;
import com.hlandim.gituserssearch.controller.SearchController;
import com.hlandim.gituserssearch.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class SearchFragment extends BaseFragment {

    private Button btn_find;
    private SearchController controller;
    private EditText edt_search;
    private RecyclerView rv_users;
    private UsersListAdapter usersListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        configureBtnFind(view);
        configureUserList(view);
        controller = new SearchController(getContext());
        return view;
    }

    private void configureBtnFind(View view) {
        btn_find = (Button) view.findViewById(R.id.btn_find);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }

    private void getUsers() {
        String search = edt_search.getText().toString();
        controller.getUsers(search, new SearchController.GetUsersCallback() {
            @Override
            public void onGotUsers(List<User> users) {
//                showToast("Total: " + users.size());
                if (users != null && users.size() > 0) {
                    User user = users.get(0);
                    usersListAdapter.addUser(user, 0);
                    rv_users.scrollToPosition(0);
                } else {
                    showToast("Nenhum usuário encontrado!");
                }
            }

            @Override
            public void onGotError(String errorMessage) {
                showToast("Error : " + errorMessage);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}
