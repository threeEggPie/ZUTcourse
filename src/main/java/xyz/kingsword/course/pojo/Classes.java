package xyz.kingsword.course.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classes {
    private String classname;

    private Integer studentNum;

    private int grade;
}