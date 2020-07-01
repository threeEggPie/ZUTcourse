package xyz.kingsword.course.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.SelectBookDeclareParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.SpringContextUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;
import xyz.kingsword.course.vo.BookDeclareVo;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
        return bookMapper.selectBookListByCourse(courseId);
    }

    @Override
    public List<Book> getTextBookByCourseList(Collection<String> courseIdCollection) {
        if (CollUtil.isNotEmpty(courseIdCollection)) {
            return bookMapper.getTextBookByCourseList(courseIdCollection);
        }
        return Collections.emptyList();
    }


    @Override
    public List<Book> getReferenceBook(String courseId) {
        return Collections.emptyList();
    }

    /**
     * 先读缓存，遇到没有的再去数据库拿
     *
     * @param idList idList
     * @return List<Book>
     */
    @Override
    public List<Book> getByBookIdList(Collection<Integer> idList) {
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

    /**
     * 筛选教材申报情况
     */
    @Override
    public PageInfo<BookDeclareVo> selectBookDeclare(SelectBookDeclareParam param) {
        CourseGroupMapper courseGroupMapper = SpringContextUtil.getBean(CourseGroupMapper.class);
        PageInfo pageInfo = PageMethod.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> courseGroupMapper.selectBookDeclareStatus(param));
        List<CourseGroup> courseGroupList = pageInfo.getList();
        List<String> courseIdList = courseGroupList.stream().map(CourseGroup::getCouId).collect(Collectors.toList());

        Map<String, List<Book>> bookMap = getBookMap(courseIdList);
        Map<String, List<CourseGroup>> courseGroupTeacherMap = courseGroupMapper.getCourseGroupTeacher(courseIdList, param.getSemesterId())
                .stream().collect(Collectors.groupingBy(CourseGroup::getCouId));

        String semesterId = param.getSemesterId();
        String semesterName = TimeUtil.getSemesterName(semesterId);
        List<BookDeclareVo> bookDeclareVoLit = new ArrayList<>(courseGroupList.size());
        for (CourseGroup courseGroup : courseGroupList) {
            String courseId = courseGroup.getCouId();
            BookDeclareVo bookDeclareVo = new BookDeclareVo();
            bookDeclareVo.setCourseId(courseGroup.getCouId());
            bookDeclareVo.setCourseName(courseGroup.getCourseName());
            bookDeclareVo.setNature(courseGroup.getCourseNature());
            bookDeclareVo.setSemesterId(semesterId);
            bookDeclareVo.setSemesterName(semesterName);
            bookDeclareVo.setBookList(bookMap.getOrDefault(courseId, Collections.emptyList()));
            List<String> teacherList = courseGroupTeacherMap.containsKey(courseId) ? courseGroupTeacherMap.get(courseId).stream().map(CourseGroup::getTeacherName).distinct().collect(Collectors.toList()) : Collections.emptyList();
            bookDeclareVo.setCourseGroup(teacherList);

            bookDeclareVoLit.add(bookDeclareVo);
        }
        pageInfo.setList(bookDeclareVoLit);
        return pageInfo;
    }


    @Override
    public Book getBook(int id) {
        return bookCache.get(id, () -> bookMapper.selectBookByPrimaryKey(id));
    }


    @Override
    public Book update(Book book) {
        validateAuth(getBook(book.getId()).getCourseId());
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
        book.setCourseId(courseId);
        bookMapper.insert(book);
        int bookId = book.getId();
        bookCache.put(bookId, book);

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

    /**
     * 删除教材时，如果教材已被订购，则一并删除订单信息
     *
     * @param bookIdList bookIdList
     * @param courseId   courseId
     */
    @Override
    public void delete(@NonNull List<Integer> bookIdList, @NonNull String courseId) {
        validateAuth(courseId);
        String semesterId = TimeUtil.getNextSemester().getId();
        int count = bookOrderService.selectByBookIdSemester(bookIdList, semesterId);
        ConditionUtil.validateTrue(count == 0).orElseThrow(() -> new BaseException("教材已被学生订购，不能删除"));
        bookMapper.delete(bookIdList);
        bookIdList.forEach(v -> bookCache.remove(v));
    }

    private Map<String, List<Book>> getBookMap(List<String> courseIdList) {
        if (courseIdList == null || courseIdList.isEmpty()) {
            return Collections.emptyMap();
        }

        return bookMapper.getTextBookByCourseList(courseIdList).stream().collect(Collectors.groupingBy(Book::getCourseId));
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
