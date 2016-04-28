package com.android.ketaoapp.entity;

/**
 * Created by Administrator on 2016/4/27 0027.
 */
public class Search {

    private String search;
    private String user_id;
    private int timestamp;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
