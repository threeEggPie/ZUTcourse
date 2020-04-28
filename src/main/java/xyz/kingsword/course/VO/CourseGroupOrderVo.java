package xyz.kingsword.course.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseGroupOrderVo {
    private int bookId;
    private String bookName;
    private final List<OrderInfo> orderInfo = new ArrayList<>();

    public void addOrderInfo(String teacherName, boolean orderFlag) {
        this.orderInfo.add(new OrderInfo(teacherName, orderFlag));
    }
}

@Data
@AllArgsConstructor
class OrderInfo {
    private String teacherName;
    private boolean orderFlag;
}
