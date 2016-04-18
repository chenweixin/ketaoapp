package com.android.ketaoapp.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/24 0024.
 */
public class Course implements Serializable{
    private String id;

    private String name;

    private String teacher_id;

    private String teacher_name;

    private String location;

    private Integer credit;

    /**
     * 0：人文科学
     * 1：人文科学核心
     * 2：社会科学
     * 3：社会科学核心
     * 4：科学技术
     * 5：科学技术核心
     */
    private Integer type;

    private String create_time;

    private Integer num_collect;

    private Integer num_evaluate;

    private Integer score;

    private String introduction;

    private double avg_score;

    private boolean iscollect;

    private boolean isevaluate;

    public boolean isevaluate() {
        return isevaluate;
    }

    public void setIsevaluate(boolean isevaluate) {
        this.isevaluate = isevaluate;
    }

    public boolean iscollect() {
        return iscollect;
    }

    public void setIscollect(boolean iscollect) {
        this.iscollect = iscollect;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public Integer getNum_collect() {
        return num_collect;
    }

    public void setNum_collect(Integer num_collect) {
        this.num_collect = num_collect;
    }

    public Integer getNum_evaluate() {
        return num_evaluate;
    }

    public void setNum_evaluate(Integer num_evaluate) {
        this.num_evaluate = num_evaluate;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public double getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(double avg_score) {
        this.avg_score = avg_score;
    }
}
