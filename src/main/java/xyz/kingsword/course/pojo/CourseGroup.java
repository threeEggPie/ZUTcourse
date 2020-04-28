package xyz.kingsword.course.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseGroup {
    private Integer sortId;

    private String couId;

    private String semesterId;

    private String courseName;

    private String textBookStr;

    private List<Integer> textBook;

    private String teaId;

    private String teacherName;

    private String className;

    private Integer calendarId;

    private int courseNature;

    public void setTextBookStr(String textBookStr) {
        if (textBookStr != null) {
            this.textBookStr = textBookStr;
            this.textBook = textBookStr.length() > 2 ? JSON.parseArray(textBookStr, Integer.class) : new ArrayList<>();
        }
    }
}
