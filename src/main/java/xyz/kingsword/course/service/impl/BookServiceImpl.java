package xyz.kingsword.course.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.SpringContextUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private BookOrderService bookOrderService;

    @Resource(name = "book")
    private Cache<Integer, Book> bookCache;

    @Override
    public List<Book> getTextBook(String courseId) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(courseId);
        if (optional.isPresent()) {
            Course course = optional.get();
            String bookListJson = course.getTextBook();
            return getByIdList(bookListJson);
        }
        return new ArrayList<>();
    }

    private List<Integer> getTextBookId(String courseId) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(courseId);
        if (optional.isPresent()) {
            String bookListJson = optional.get().getTextBook();
            if (bookListJson != null && bookListJson.length() > 2) {
                return JSONArray.parseArray(bookListJson, Integer.class);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<Book> getReferenceBook(String courseId) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(courseId);
        if (optional.isPresent()) {
            Course course = optional.get();
            String bookListJson = course.getReferenceBook();
            return getByIdList(bookListJson);
        }
        return new ArrayList<>();
    }

    /**
     * 先读缓存，遇到没有的再去数据库拿
     *
     * @param idList idList
     * @return List<Book>
     */
    @Override
    public List<Book> getByIdList(Collection<Integer> idList) {
        List<Book> bookList = new ArrayList<>(idList.size());
        Iterator<Integer> iterator = idList.iterator();
        while (iterator.hasNext()) {
            Book book = bookCache.get(iterator.next());
            if (book != null) {
                bookList.add(book);
                iterator.remove();
            }
        }
        if (!idList.isEmpty()) {
            List<Book> bookListDb = bookMapper.selectBookList(idList);
            bookList.addAll(bookListDb);
        }
        return bookList;
    }

    @Override
    public Map<Integer, Book> getMap(Collection<Integer> idList) {
        Collection<Integer> collection = new ArrayList<>(idList);
        Map<Integer, Book> map = new HashMap<>(collection.size());
        Iterator<Integer> iterator = collection.iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            if (bookCache.containsKey(id)) {
                map.put(id, bookCache.get(id));
                iterator.remove();
            }
        }
        if (!collection.isEmpty()) {
            List<Book> bookListDb = bookMapper.selectBookList(collection);
            bookListDb.forEach(v -> map.put(v.getId(), v));
        }
        return map;
    }

    @Override
    public List<Book> getByIdList(String json) {
        return json != null && json.length() > 2 ? getByIdList(JSON.parseArray(json, Integer.class)) : new ArrayList<>();
    }

    @Override
    public Book getBook(int id) {
        return bookCache.get(id, () -> bookMapper.selectBookByPrimaryKey(id));
    }


    @Override
    public Book update(Book book) {
        bookMapper.update(book);
        bookCache.put(book.getId(), book);
        return book;
    }

    /**
     * 新增教材，默认会给每一位老师都订书
     *
     * @param book     book
     * @param courseId courseId
     */
    @Override
    @Transactional
    public Book insert(Book book, String courseId) {
        validateAuth(courseId);
//        避免老师申报书籍重复
        List<Book> bookList = getTextBook(courseId);
        bookList.parallelStream().map(Book::getIsbn).forEach(v -> {
            ConditionUtil.validateTrue(!StrUtil.equals(v, book.getIsbn())).orElseThrow(() -> new DataException(ErrorEnum.DATA_REPLICATION));
        });
//        查课程组所有老师
        CourseGroupMapper courseGroupMapper = SpringContextUtil.getBean(CourseGroupMapper.class);
        List<CourseGroup> courseGroupList = courseGroupMapper.getSemesterCourseGroup(courseId, TimeUtil.getNextSemester().getId());
        book.setForTeacher(courseGroupList.size());
        bookMapper.insert(book);
        int bookId = book.getId();
        bookCache.put(bookId, book);
        courseMapper.addCourseBook(bookId, courseId);

        List<BookOrder> bookOrderList = new ArrayList<>(courseGroupList.size());
        for (CourseGroup courseGroup : courseGroupList) {
            BookOrder bookOrder = new BookOrder();
            bookOrder.setUserId(courseGroup.getTeaId());
            bookOrder.setBookId(bookId);
            bookOrder.setCourseId(courseId);
            bookOrder.setSemesterId(courseGroup.getSemesterId());
            bookOrderList.add(bookOrder);
        }
        bookOrderService.insert(bookOrderList);
        return book;
    }

    @Override
    public void delete(@NonNull List<Integer> idList, @NonNull String courseId) {
        validateAuth(courseId);
        if (!idList.isEmpty()) {
            idList.forEach(v -> {
                int flag = bookOrderService.select(BookOrderSelectParam.builder().bookId(v).build()).size();
                ConditionUtil.validateTrue(flag == 0).orElseThrow(() -> new OperationException(ErrorEnum.ORDERED));
                bookCache.remove(v);
            });
            List<Integer> textBookIdList = getTextBookId(courseId);
            textBookIdList.removeAll(idList);
            courseMapper.setTextBook(JSON.toJSONString(textBookIdList), courseId);
        }
    }


    /**
     * 需要对教材管理进行权限控制，一个课程组只能一个人报教材，哪个老师先报就进行授权，其他人不能报，对教学部不做限制
     *
     * @param courseId 通过课程号查教材管理者是谁
     */
    private void validateAuth(String courseId) {
        User user = UserUtil.getUser();
        Integer roleId = user.getCurrentRole();
        if (roleId != null && roleId == RoleEnum.ACADEMIC_MANAGER.getCode()) {
            return;
        }
        Teacher teacher = courseMapper.getBookManager(courseId);
        if (teacher == null) {
            courseMapper.setBookManager(courseId, user.getUsername());
            return;
        }
        String message = "您没有权限，请咨询" + teacher.getName() + "老师";
        ConditionUtil.validateTrue(StrUtil.equals(teacher.getId(), user.getUsername())).orElseThrow(() -> new BaseException(message));
    }
}
