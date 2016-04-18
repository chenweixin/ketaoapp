package com.android.ketaoapp.entity;

/**
 * Created by Administrator on 2016/3/31 0031.
 */
public class Comment {

    private String id;

    private String student_id;

    private String student_avatar;

    private String student_name;

    private String create_time;

    private String comment;

    private int score;

    private int num_like;

    private boolean isLike;

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public int getNum_like() {
        return num_like;
    }

    public void setNum_like(int num_like) {
        this.num_like = num_like;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudent_avatar() {
        return student_avatar;
    }

    public void setStudent_avatar(String student_avatar) {
        this.student_avatar = student_avatar;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }
}
