package xyz.kingsword.course.pojo;

import lombok.Data;
import xyz.kingsword.course.VO.TeacherVo;

import java.util.List;

@Data
public class ResearchRoom {
    private String name;

    private String teacherInCharge;

    private List<TeacherVo> teacherVoList;
}