package com.hlandim.gituserssearch.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlandim.gituserssearch.R;
import com.hlandim.gituserssearch.model.User;
import com.hlandim.gituserssearch.web.GitHubApi;

import java.util.List;

/**
 * Created by hlandim on 5/7/16.
 */
public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserHolder> {

    private List<User> list;

    public UsersListAdapter(List<User> list) {
        this.list = list;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        UserHolder userHolder = new UserHolder(view);
        return userHolder;
    }

    @Override
    public void onBindViewHolder(final UserHolder userHolder, int position) {
        User user = list.get(position);
        userHolder.tv_id.setText(user.getId());
        userHolder.tv_url.setText(user.getUrl());
        userHolder.tv_login.setText(user.getLogin());
        if (!TextUtils.isEmpty(user.getAvatar_url())) {
            GitHubApi.getInstance().getImage(user.getAvatar_url(), new GitHubApi.GitHubImageCallBack() {
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
        return list.size();
    }

    public void addUser(User user, int position) {
        list.add(position, user);
        notifyItemInserted(position);
    }

    public class UserHolder extends RecyclerView.ViewHolder {

        public TextView tv_id;
        public TextView tv_url;
        public TextView tv_login;
        public ImageView img_avatar;

        public UserHolder(View itemView) {
            super(itemView);
            tv_id = (TextView) itemView.findViewById(R.id.tv_id);
            tv_url = (TextView) itemView.findViewById(R.id.tv_url);
            tv_login = (TextView) itemView.findViewById(R.id.tv_login);
            img_avatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        }
    }

    public List<User> getList() {
        return list;
    }
}
