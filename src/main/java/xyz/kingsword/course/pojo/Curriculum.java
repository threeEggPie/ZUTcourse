package xyz.kingsword.course.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Curriculum {
    private String studentId;

    private String className;

    private String courseName;

    private List<Integer> idList;

    private List<Book> textBook;

    public void setIdList(String json) {
        if (json != null && json.length() > 2) {
            this.idList = JSON.parseArray(json, Integer.class);
        } else {
            this.idList = new ArrayList<>();
        }
    }
}
