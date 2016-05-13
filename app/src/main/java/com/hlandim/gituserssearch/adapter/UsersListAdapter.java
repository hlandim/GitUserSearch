package com.hlandim.gituserssearch.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hlandim.gituserssearch.R;
import com.hlandim.gituserssearch.model.User;
import com.hlandim.gituserssearch.web.GitHubApi;

import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserHolder> {


    private List<User> mList;
    private UserListListener mListener;

    public UsersListAdapter(List<User> list) {
        this.mList = list;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserHolder userHolder, int position) {
        final User user = mList.get(position);
        userHolder.tv_id.setText("ID: " + user.getIdGitHub());
        userHolder.tv_url.setText(user.getUrl());
        userHolder.tv_login.setText(user.getLogin());
        if (user.getUrlHash() != null) {
            userHolder.tv_hash_url.setText("HASH: " + user.getUrlHash());
            userHolder.pb_loading.setVisibility(View.GONE);
        } else {
            userHolder.pb_loading.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(user.getAvatarUrl())) {
            GitHubApi.getInstance().getImage(user.getAvatarUrl(), new GitHubApi.GitHubImageCallBack() {
                @Override
                public void onGetBitmap(Bitmap bitmap) {
                    userHolder.img_avatar.setImageBitmap(bitmap);
                    userHolder.img_avatar.animate().alpha(1.0f);
                }

                @Override
                public void onGetError(String msgError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addUseTop(User user) {
        mList.add(0, user);
        notifyItemInserted(0);
    }

    public void remover(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public User getUser(int position) {
        return mList.get(position);
    }

    public List<User> getmList() {
        return mList;
    }

    public void setmListener(UserListListener mListener) {
        this.mListener = mListener;
    }

    public interface UserListListener {
        void onUserSelected(User user);
    }

    public class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tv_id;
        public TextView tv_url;
        public TextView tv_login;
        public ImageView img_avatar;
        public TextView tv_hash_url;
        public ProgressBar pb_loading;

        public UserHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_url = (TextView) itemView.findViewById(R.id.tv_url);
            tv_login = (TextView) itemView.findViewById(R.id.tv_login);
            img_avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
            tv_hash_url = (TextView) itemView.findViewById(R.id.tv_hash_url);
            pb_loading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                Integer position = Integer.valueOf(getLayoutPosition());
                mListener.onUserSelected(mList.get(position));
            }
        }
    }

}
