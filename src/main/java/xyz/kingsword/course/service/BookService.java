package xyz.kingsword.course.service;


import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.param.SelectBookDeclareParam;
import xyz.kingsword.course.vo.BookDeclareVo;

import java.util.Collection;
import java.util.List;

public interface BookService {

    Book insert(Book book, String courseId);

    void delete(List<Integer> bookIdList, String courseId);

    Book getBook(int id);

    Book update(Book book);

    List<Book> getTextBook(String courseId);

    List<Book> getTextBookByCourseList(Collection<String> courseIdCollection);

    List<Book> getReferenceBook(String courseId);

    List<Book> getByBookIdList(Collection<Integer> idList);

    PageInfo<BookDeclareVo> selectBookDeclare(SelectBookDeclareParam param);

}