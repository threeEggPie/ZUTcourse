package xyz.kingsword.course.service.impl;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.VO.CourseGroupOrderVo;
import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.CourseNature;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.BookOrderSelectParam;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;
import xyz.kingsword.course.pojo.param.DeclareBookExportParam;
import xyz.kingsword.course.pojo.param.StudentSelectParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.*;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookOrderServiceImpl implements BookOrderService {
    @Resource
    private BookOrderMapper bookOrderMapper;
    @Resource
    private CourseGroupMapper courseGroupMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private BookService bookService;
    @Resource(name = "config")
    private Cache cache;

    /**
     * @param bookOrderList
     * @return
     */
    @Override
    public List<Integer> insert(List<BookOrder> bookOrderList) {
        ConditionUtil.validateTrue(purchaseStatusCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
        bookOrderMapper.insert(bookOrderList);
        List<Integer> orderIdList = new ArrayList<>(bookOrderList.size());
        for (BookOrder bookOrder : bookOrderList) {
            orderIdList.add(bookOrder.getId());
        }
        return orderIdList;
    }

    /**
     * 使forTeacher字段自增
     *
     * @param bookIdList
     */
    @Override
    public void forTeacherIncrease(Collection<Integer> bookIdList) {
        bookMapper.forTeacherIncrease(bookIdList);
    }

    /**
     * 取消订购
     *
     * @param orderId 订购记录id
     */
    @Override
    public void cancelPurchase(int orderId) {
        ConditionUtil.validateTrue(purchaseStatusCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
        int flag = bookOrderMapper.delete(orderId);
        if (flag == 1 && !UserUtil.isStudent()) {
            bookMapper.cancelTeacherPurchase(orderId);
        }
    }

    /**
     * 根据年级，默认订购必修教材
     *
     * @param gradeList  年级列表
     * @param semesterId 学期id
     */
    @Override
    public void insertByGrade(Collection<Integer> gradeList, String semesterId) {
        List<BookOrder> bookOrderList = new ArrayList<>(1000);
        ClassesMapper classesMapper = SpringContextUtil.getBean(ClassesMapper.class);
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
        Map<Integer, List<Classes>> classesMap = classesMapper.selectAll().parallelStream().collect(Collectors.groupingBy(Classes::getGrade));
        for (Integer grade : gradeList) {
            List<Classes> classesList = classesMap.get(grade);
            ConditionUtil.notEmpty(classesList).orElseThrow(() -> new DataException(ErrorEnum.NO_DATA));
            for (Classes classes : classesList) {
                List<CourseGroup> courseGroupList = courseGroupMapper.geyByClasses(classes.getClassname());
                List<StudentVo> studentVoList = studentMapper.select(StudentSelectParam.builder().className(classes.getClassname()).build());
                for (CourseGroup courseGroup : courseGroupList) {
                    String courseId = courseGroup.getCouId();
                    List<Integer> idList = courseGroup.getTextBook();
                    for (StudentVo studentVo : studentVoList) {
                        for (Integer bookId : idList) {
                            BookOrder bookOrder = new BookOrder();
                            bookOrder.setUserId(studentVo.getId());
                            bookOrder.setBookId(bookId);
                            bookOrder.setSemesterId(semesterId);
                            bookOrder.setCourseId(courseId);
                            bookOrderList.add(bookOrder);
                        }
//                }
                    }
                }
            }
        }
        this.insert(bookOrderList);
    }

    @Override
    public List<BookOrderVo> select(BookOrderSelectParam param) {
        return bookOrderMapper.select(param);
    }

    /**
     * 查询课程组订书情况
     *
     * @param courseId courseId
     */
    @Override
    public List<CourseGroupOrderVo> courseGroupOrder(String courseId, String semesterId) {
        List<CourseGroup> courseGroupList = courseGroupMapper.selectDistinct(CourseGroupSelectParam.builder().semesterId(semesterId).courseId(courseId).build());
        if (courseGroupList.isEmpty())
            return new ArrayList<>();
        List<Integer> bookIdList = courseGroupList.get(0).getTextBook();
        Map<Integer, Book> bookMap = bookService.getMap(bookIdList);
        ConditionUtil.validateTrue(bookMap.size() == bookIdList.size()).orElseThrow(DataException::new);

        List<BookOrderVo> bookOrderVoList = bookOrderMapper.courseGroupOrderInfo(courseId, semesterId);
        if (bookOrderVoList.isEmpty())
            return new ArrayList<>();
        Map<Integer, List<BookOrderVo>> bookToOrderMap = bookOrderVoList.parallelStream().collect(Collectors.groupingBy(BookOrderVo::getBookId));
        List<CourseGroupOrderVo> courseGroupOrderVoList = new ArrayList<>(bookIdList.size());
        for (Integer bookId : bookIdList) {
            Book book = Optional.ofNullable(bookMap.get(bookId)).orElseThrow(DataException::new);
            CourseGroupOrderVo courseGroupOrderVo = new CourseGroupOrderVo();
            courseGroupOrderVo.setBookId(bookId);
            courseGroupOrderVo.setBookName(book.getName());
            Optional.ofNullable(bookToOrderMap.get(bookId)).ifPresent(v -> {
                Set<String> orderedTeacher = v.parallelStream()
                        .map(BookOrderVo::getUserId)
                        .collect(Collectors.toSet());
                for (CourseGroup courseGroup : courseGroupList) {
                    String teaName = courseGroup.getTeacherName();
                    boolean flag = orderedTeacher.contains(courseGroup.getTeaId());
                    courseGroupOrderVo.addOrderInfo(teaName, flag);
                }
            });
            courseGroupOrderVoList.add(courseGroupOrderVo);
        }
        return courseGroupOrderVoList;
    }

    /**
     * 订书开关验证，只针对学生
     */
    private boolean purchaseStatusCheck() {
        User user = UserUtil.getUser();
        Integer roleId = user.getCurrentRole();
        if (roleId != null && roleId == RoleEnum.STUDENT.getCode()) {
            return Optional.ofNullable(cache.get("purchaseStatus", Boolean.class)).orElse(true);
        }
        return true;
    }

    private Map<String, Integer> classIndex = new HashMap<>();
    private final int CLASS_START_INDEX = 15;

    @Override
    public Workbook exportAllStudentRecord(DeclareBookExportParam param) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/orderDetail.xlsx");
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        sheet.getRow(0).getCell(0).setCellValue(TimeUtil.getSemesterName(param.getSemesterId()) + "教材订购详情");
        List<String> classNameList = bookOrderMapper.purchaseClass(param.getSemesterId());
        Row rowHead = sheet.getRow(1);
        for (int i = 0; i < classNameList.size(); i++) {
            int index = CLASS_START_INDEX + i;
            Cell cell = rowHead.createCell(index);
            cell.setCellStyle(getBaseCellStyle(workbook));
            cell.setCellValue(classNameList.get(i));
            classIndex.put(classNameList.get(i), index);
        }

        String[][] data = renderData(param);
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 2);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data[i][j]);
            }
        }
        return workbook;
    }

    /**
     * 构建导出年级订书信息excel所需数据
     *
     * @return String[][]
     */
    private String[][] renderData(DeclareBookExportParam declareBookExportParam) {
        String semesterId = declareBookExportParam.getSemesterId();
        CourseGroupSelectParam courseGroupSelectParam = new CourseGroupSelectParam();
        BeanUtils.copyProperties(declareBookExportParam, courseGroupSelectParam);
        List<CourseGroup> courseGroupList = courseGroupMapper.select(courseGroupSelectParam);
        Set<String> courseIdSet = courseGroupList.parallelStream().map(CourseGroup::getCouId).collect(Collectors.toSet());

        List<BookOrderVo> bookOrderVoList = bookOrderMapper.select(BookOrderSelectParam.builder().semesterId(semesterId).build())
                .parallelStream()
                .filter(v -> courseIdSet.contains(v.getCourseId()))
                .collect(Collectors.toList());
        Map<Integer, Map<String, Long>> bookIdToClass = bookOrderVoList
                .parallelStream()
                .filter(v -> v.getClassName() != null)
                .collect(Collectors.groupingBy(BookOrderVo::getBookId, Collectors.groupingBy(BookOrderVo::getClassName, Collectors.counting())));
        for (Integer integer : bookIdToClass.keySet()) {
            System.out.println("bookId: " + integer);
            bookIdToClass.get(integer).forEach((k, v) -> System.out.println(k + " " + v));
        }
        Map<String, List<CourseGroup>> courseMap = courseGroupList.parallelStream().collect(Collectors.groupingBy(CourseGroup::getCouId));
        List<Integer> idList = courseGroupList.parallelStream().flatMap(v -> v.getTextBook().stream()).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return new String[0][0];
        }
        ConditionUtil.notEmpty(idList).orElseThrow(() -> new OperationException(ErrorEnum.NO_DATA));
//        主键为书籍id，便于搜索
        Map<Integer, Book> bookMap = bookMapper.selectBookList(idList)
                .parallelStream().collect(Collectors.toMap(Book::getId, v -> v));

        int length = CLASS_START_INDEX + this.classIndex.size();
        String[][] data = new String[courseMap.size()][];
        int i = 0;
        for (String courseId : courseMap.keySet()) {
            List<CourseGroup> courseGroupListItem = courseMap.get(courseId);
            String[] strings = new String[length];
//            数据初始化为空字符串，避免导出null
            Arrays.fill(strings, "");
            StrBuilder classStrBuilder = StrBuilder.create();
            StrBuilder teacherStrBuilder = StrBuilder.create();
            for (CourseGroup courseGroup : courseGroupListItem) {
                System.out.println();
                String className = courseGroup.getClassName().replace(" ", "\n");
                classStrBuilder.append(className).append("\n");
                teacherStrBuilder.append(courseGroup.getTeacherName()).append("\n");
            }
            CourseGroup courseGroup = courseGroupListItem.get(0);
            strings[0] = courseId;
            strings[1] = courseGroup.getCourseName();
            strings[2] = CourseNature.getContent(courseGroup.getCourseNature()).getContent();
            strings[3] = classStrBuilder.toStringAndReset();
            List<Integer> bookIdList = courseGroupListItem.get(0).getTextBook();
            if (!bookIdList.isEmpty()) {
                StrBuilder isbn = new StrBuilder();
                StrBuilder name = new StrBuilder();
                StrBuilder price = new StrBuilder();
                StrBuilder author = new StrBuilder();
                StrBuilder publish = new StrBuilder();
                StrBuilder pubDate = new StrBuilder();
                StrBuilder edition = new StrBuilder();
                StrBuilder award = new StrBuilder();
                StrBuilder forTeacher = new StrBuilder();
                for (int bookId : bookIdList) {
                    Book book = bookMap.get(bookId);
                    isbn.append(book.getIsbn()).append("\n");
                    name.append(book.getName()).append("\n");
                    price.append(book.getPrice()).append("\n");
                    author.append(book.getAuthor()).append("\n");
                    publish.append(book.getPublish()).append("\n");
                    pubDate.append(book.getPubDate()).append("\n");
                    edition.append(book.getEdition()).append("\n");
                    award.append(book.getAward()).append("\n");
                    forTeacher.append(book.getForTeacher()).append("\n");
                    if (!classIndex.isEmpty()) {
                        Map<String, Long> classToNum = bookIdToClass.get(bookId);
                        if (classToNum != null && !classToNum.isEmpty()) {
                            classToNum.forEach((className, num) -> strings[classIndex.get(className)] = StrBuilder.create(strings[classIndex.get(className)]).append(num).append("\n").toStringAndReset());
                        }
                    }
                }
                strings[4] = isbn.toStringAndReset();
                strings[5] = name.toStringAndReset();
                strings[6] = price.toStringAndReset();
                strings[7] = author.toStringAndReset();
                strings[8] = publish.toStringAndReset();
                strings[9] = pubDate.toStringAndReset();
                strings[10] = edition.toStringAndReset();
                strings[11] = award.toStringAndReset();
                strings[12] = teacherStrBuilder.toStringAndReset();
                strings[13] = forTeacher.toStringAndReset();
            }
            strings[14] = bookIdList.size() > 0 ? "是" : "否";
            data[i++] = strings;
        }
        return data;
    }


    private CellStyle getBaseCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);//自动换行
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public Workbook exportSingleRecord(String studentId) {
        return null;
    }

    @Override
    public Workbook exportClassRecord(String className, String semesterId) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderData(className, semesterId);
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
                cell.setCellStyle(cellStyle);
            }
        }
        return workbook;
    }

    @Override
    public byte[] exportPluralClassBookInfo(List<String> classNameList, String semesterId) {
        ApplicationHome h = new ApplicationHome(getClass());
        File jarFile = h.getDir();
        File dir = new File(jarFile.getPath() + File.separator + "temp");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            for (String className : classNameList) {
                Workbook workbook = exportClassRecord(className, semesterId);
                OutputStream outputStream = new FileOutputStream(dir.getPath() + File.separator + className + ".xlsx");
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File targetFile = new File(dir.getParent() + File.separator + "temp.zip");
        ZipUtil.zip(dir, targetFile);
        byte[] bytes = FileUtil.readBytes(targetFile);
        targetFile.delete();
        return bytes;
    }

    @Override
    public Workbook exportBookOrderStatistics(String semesterId) {
        List<BookOrderVo> bookOrderVoList = bookOrderMapper.select(BookOrderSelectParam.builder().semesterId(semesterId).build());
        Map<Integer, List<BookOrderVo>> collect = bookOrderVoList.stream().filter(v -> !v.getClassName().equals("教师组")).collect(Collectors.groupingBy(BookOrderVo::getBookId));
        String[][] data = new String[collect.size()+1][7];
        Iterator<Map.Entry<Integer, List<BookOrderVo>>> entries = collect.entrySet().iterator();
        int i = 1;
        String[] head = new String[7];
        head[0] = "序号";
        head[1] = "课程号";
        head[2] = "书名";
        head[3] = "作者";
        head[4] = "出版社";
        head[5] = "ISBN号";
        head[6] = "订购数量";
        data[0] = head;
        while (entries.hasNext()) {
            Map.Entry<Integer, List<BookOrderVo>> entry = entries.next();
            String[] strings = new String[7];
            Book book = bookService.getBook(entry.getKey());
            strings[0] = i + "";
            strings[1] = entry.getValue().get(0).getCourseId();
            strings[2] = book.getName();
            strings[3] = book.getAuthor();
            strings[4] = book.getPublish();
            strings[5] = book.getIsbn();
            strings[6] = entry.getValue().size() + "";
            data[i++] = strings;
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = getBaseCellStyle(workbook);
        for (i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
                cell.setCellStyle(cellStyle);
            }
        }
        return workbook;
    }

    /**
     * 构建导出班级课程表数据
     *
     * @param className
     * @param semesterId
     * @return
     */
    private String[][] renderData(String className, String semesterId) {
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
        List<StudentVo> studentList = studentMapper.select(StudentSelectParam.builder().className(className).pageSize(0).build())
                .parallelStream()
                .sorted((a, b) -> StrUtil.compare(a.getId(), b.getId(), false))
                .collect(Collectors.toList());
        List<BookOrderVo> bookOrderVoList = this.select(BookOrderSelectParam.builder().semesterId(semesterId).className(className).build());
        if (studentList.isEmpty()) {
            return new String[0][0];
        }
        List<String> bookNameList = bookOrderVoList.parallelStream().map(BookOrderVo::getName).distinct().collect(Collectors.toList());
        int columnCount = bookNameList.size() + 2;
        int rowCount = studentList.size() + 2;
        String[][] data = new String[rowCount][columnCount];
//        数据初始化
        for (int i = 0; i < data.length; i++) {
            data[i] = new String[columnCount];
            Arrays.fill(data[i], "");
        }
        data[0][0] = "";
//         第一行书名
        Map<String, Integer> bookNameToIndex = new HashMap<>(bookNameList.size());
        for (int i = 0; i < bookNameList.size(); i++) {
            bookNameToIndex.put(bookNameList.get(i), i + 1);
            data[0][i + 1] = bookNameList.get(i);
        }
        data[0][bookNameList.size() + 1] = "确认签字";
//      第一列学生名字
        for (int i = 1; i < studentList.size() + 1; i++) {
            data[i][0] = studentList.get(i - 1).getName();
        }

        Map<String, List<BookOrderVo>> studentToOrder = bookOrderVoList.parallelStream().collect(Collectors.groupingBy(BookOrderVo::getUserId));
        for (StudentVo studentVo : studentList) {
            List<BookOrderVo> orderList = studentToOrder.get(studentVo.getId());
            if (orderList != null && !orderList.isEmpty()) {
                for (BookOrderVo bookOrderVo : orderList) {
                    int bookIndex = bookNameToIndex.get(bookOrderVo.getName());
                    int studentIndex = studentList.indexOf(studentVo) + 1;
                    data[studentIndex][bookIndex] = "1";
                }
            }
        }
        data[rowCount - 1] = new String[columnCount];
        data[rowCount - 1][0] = "合计";

        Map<String, Long> map = bookOrderVoList.parallelStream().collect(Collectors.groupingBy(BookOrderVo::getName, Collectors.counting()));
        map.forEach((k, v) -> data[rowCount - 1][bookNameToIndex.get(k)] = v.toString());
//        map.forEach((k, v) -> System.out.println(k + " " + v));
        return data;
    }
}
