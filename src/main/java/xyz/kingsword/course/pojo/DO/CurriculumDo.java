package xyz.kingsword.course.pojo.DO;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CurriculumDo {
    private String className;

    private String courseId;

    private List<Integer> textBook;

    private String courseName;

    public void setTextBook(String textBook) {
        this.textBook = textBook != null && textBook.length() > 2 ? JSON.parseArray(textBook, Integer.class) : new ArrayList<>();
    }
}
