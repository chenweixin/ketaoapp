package com.android.ketaoapp.entity;

/**
 * Created by Administrator on 2016/3/30 0030.
 */
public class Evaluation {
    private String id;

    private String course_id;

    private String student_id;

    private String create_time;

    private int score;

    private boolean isLike;

    private String evalike_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public String getEvalike_id() {
        return evalike_id;
    }

    public void setEvalike_id(String evalike_id) {
        this.evalike_id = evalike_id;
    }
}
