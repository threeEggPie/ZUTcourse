package xyz.kingsword.course.vo;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeacherVo {
    private String id;

    private String name;

    private String researchRoom;

    private String phone;

    private String email;

    private String departmentSchool;

    private String teachingTitle;

    private String education;

    private String gender;

    private String semesterId;

    private String role;

    private int currentRole;

    //    下一学期需要的书
    private List<CourseBookOrderVo> courseList;

    /**
     * 管理的课程id，存json
     */
    private List<String> courseInCharge;

    @JsonIgnore
    private String courseInChargeStr;

    /**
     * 管理的专业，存json
     */
    private List<String> specialtyInCharge;
    @JsonIgnore
    private String specialtyInChargeStr;

//    public void setRole(String role) {
//        this.role = JSONArray.parseArray(role, Integer.class);
//    }

    public void setCourseInChargeStr(String courseInChargeStr) {
        if (courseInChargeStr == null || courseInChargeStr.length() < 2) {
            this.courseInCharge = new ArrayList<>();
        } else {
            this.courseInCharge = JSONArray.parseArray(courseInChargeStr, String.class);
        }
        this.courseInChargeStr = courseInChargeStr;
    }

    public void setSpecialtyInChargeStr(String specialtyInChargeStr) {
        if (specialtyInChargeStr == null || specialtyInChargeStr.length() < 2) {
            this.specialtyInCharge = new ArrayList<>();
        } else {
            this.specialtyInCharge = JSONArray.parseArray(specialtyInChargeStr, String.class);
        }
        this.specialtyInChargeStr = specialtyInChargeStr;
    }

    public List<String> getCourseInCharge() {
        return courseInCharge == null ? new ArrayList<>() : courseInCharge;
    }

    public List<String> getSpecialtyInCharge() {
        return specialtyInCharge == null ? new ArrayList<>() : specialtyInCharge;
    }
}
