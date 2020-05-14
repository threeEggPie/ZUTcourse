package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.DO.BookExportCourseDo;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;

import java.util.List;

public interface BookOrderMapper {
    int insert(List<BookOrder> bookOrderList);

    int delete(int id);

    int selectByBookId(int id);

    List<BookOrderVo> select(BookOrderSelectParam param);

    List<BookOrderVo> courseGroupOrderInfo(String courseId, String semesterId);

    List<BookExportCourseDo> export(String semesterId);

    List<String> purchaseClass(String semesterId);

    int getClassBookCount(@Param("id") Integer id, @Param("className") String classname);
}
