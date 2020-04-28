package xyz.kingsword.course.service;


import xyz.kingsword.course.pojo.Book;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface BookService {

    Book insert(Book book, String courseId);

    void delete(List<Integer> idList,String courseId);

    Book getBook(int id);

    Book update(Book book);

    List<Book> getTextBook(String courseId);

    List<Book> getReferenceBook(String courseId);

    List<Book> getByIdList(Collection<Integer> idList);

    Map<Integer,Book> getMap(Collection<Integer> idList);

    List<Book> getByIdList(String json);

    void setDeclareStatus(boolean flag);

    boolean getDeclareStatus();

    void setPurchaseStatus(boolean flag);

    boolean getPurchaseStatus();
}
