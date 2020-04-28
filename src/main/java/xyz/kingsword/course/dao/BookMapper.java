package xyz.kingsword.course.dao;


import org.apache.ibatis.annotations.Mapper;
import xyz.kingsword.course.pojo.Book;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BookMapper {
    void insert(Book record);

    int update(Book record);

    int delete(List<Integer> idList);

    List<Book> selectBookList(Collection<Integer> idList);

    int forTeacherIncrease(Collection<Integer> idList);

    int cancelTeacherPurchase(int orderId);

    Book selectBookByPrimaryKey(int id);

    List<Book> selectAll();

}