package xyz.kingsword.course.service;


import xyz.kingsword.course.pojo.Book;

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

}