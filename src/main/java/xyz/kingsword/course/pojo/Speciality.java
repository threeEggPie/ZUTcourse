package xyz.kingsword.course.pojo;

import lombok.Data;

@Data
public class Speciality {
    private int id;
    private String name;
    private int parentId;
    private String teacherInCharge;
}
