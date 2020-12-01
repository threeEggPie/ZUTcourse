package xyz.kingsword.course.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.CourseNature;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.DO.SemesterDiscountDo;
import xyz.kingsword.course.pojo.param.*;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.*;
import xyz.kingsword.course.vo.*;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDate;
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
    @Resource
    private ClassesMapper classesMapper;

    /**
     * @param bookOrderList 构建好的订单beenList
     * @return 订单id集合
     */
    @Override
    public List<Integer> insert(List<BookOrder> bookOrderList) {
        if (CollUtil.isNotEmpty(bookOrderList)) {
            ConditionUtil.validateTrue(purchaseStatusCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
            bookOrderMapper.insert(bookOrderList);
            return bookOrderList.parallelStream().map(BookOrder::getId).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 使forTeacher字段自增
     *
     * @param bookIdList bookIdList
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
     * 根据教材id删除订单信息
     *
     * @param bookIdList bookIdList
     */
    @Override
    public void deleteByBook(List<Integer> bookIdList) {
        bookMapper.delete(bookIdList);
    }

    @Override
    public int selectByBookIdSemester(List<Integer> bookIdList, String semesterId) {
        if (CollUtil.isNotEmpty(bookIdList)) {
            return bookOrderMapper.selectByBookIdSemester(bookIdList, semesterId);
        }
        return 0;
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
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
        Map<Integer, List<Classes>> classesMap = classesMapper.selectAll().parallelStream().collect(Collectors.groupingBy(Classes::getGrade));
        List<CourseGroup> courseGroupList = courseGroupMapper.select(CourseGroupSelectParam.builder().semesterId(semesterId).nature(CourseNature.REQUIRED.getCode()).build());
//      获取课程idList
        List<String> courseList = courseGroupList.stream().map(CourseGroup::getCouId).collect(Collectors.toList());
//      获取全部课程的教材id
        Map<String, List<Book>> courseBookMap = bookService.getStudentBookByCourseList(courseList).stream().collect(Collectors.groupingBy(Book::getCourseId));
        for (Integer grade : gradeList) {
            List<Classes> classesList = classesMap.get(grade);
            ConditionUtil.notEmpty(classesList).orElseThrow(() -> new DataException(ErrorEnum.NO_DATA));
            for (Classes classes : classesList) {
//              查出本班的课程信息
                List<CourseGroup> classCourseGroupList = courseGroupList.stream().filter(v -> v.getClassName().contains(classes.getClassname())).collect(Collectors.toList());
                List<StudentVo> studentVoList = studentMapper.select(StudentSelectParam.builder().className(classes.getClassname()).build());
                for (CourseGroup courseGroup : classCourseGroupList) {
                    String courseId = courseGroup.getCouId();
                    List<Book> bookList = courseBookMap.get(courseId);
                    if (CollUtil.isNotEmpty(bookList)) {
                        for (StudentVo studentVo : studentVoList) {
                            for (Book book : bookList) {
                                BookOrder bookOrder = new BookOrder();
                                bookOrder.setUserId(studentVo.getId());
                                bookOrder.setBookId(book.getId());
                                bookOrder.setSemesterId(semesterId);
                                bookOrder.setCourseId(courseId);
                                bookOrderList.add(bookOrder);
                            }
                        }
                    }
                }
            }
        }
        insert(bookOrderList);
    }

    @Override
    public List<BookOrderVo> select(BookOrderSelectParam param) {
        //pageSize设为0可查询全部
        PageHelper.startPage(param.getPageNum(), param.getPageSize(), true, null, true);
        return bookOrderMapper.select(param);
    }

    /**
     * 查询课程组订书情况
     *
     * @param courseId courseId
     */
    @Override
    public List<CourseGroupOrderVo> courseGroupOrder(String courseId, String semesterId) {
        List<CourseGroup> courseGroupList = courseGroupMapper.selectTeaIdDistinct(CourseGroupSelectParam.builder().semesterId(semesterId).courseId(courseId).build());
        if (courseGroupList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Book> bookList = bookService.getTextBook(courseId);
        List<Integer> bookIdList = bookList.stream().map(Book::getId).collect(Collectors.toList());
        Map<Integer, Book> bookMap = bookList.stream().collect(Collectors.toMap(Book::getId, v -> v));

        ConditionUtil.validateTrue(bookMap.size() == bookIdList.size()).orElseThrow(DataException::new);

        List<BookOrderVo> bookOrderVoList = bookOrderMapper.courseGroupOrderInfo(courseId, semesterId);
//        if (bookOrderVoList.isEmpty()) {
//            return Collections.emptyList();
//        }
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
            if(bookToOrderMap.get(bookId)==null){
                for (CourseGroup courseGroup : courseGroupList) {
                    String teaName = courseGroup.getTeacherName();
                    courseGroupOrderVo.addOrderInfo(teaName, false);
                }
            }
            courseGroupOrderVoList.add(courseGroupOrderVo);
        }
        return courseGroupOrderVoList;
    }


    /**
     * 订书开关验证，只针对学生
     *
     * @return true可订购，false不可订购
     */
    private boolean purchaseStatusCheck() {
        if (UserUtil.isStudent()) {
            return "true".equals(SpringContextUtil.getBean(Constant.class).getPurchaseStatus());
        }
        return true;
    }

    private Map<String, Integer> classIndex = new HashMap<>();
    private final int CLASS_START_INDEX = 15;

    @Override
    @SneakyThrows(IOException.class)
    public Workbook exportAllStudentRecord(DeclareBookExportParam param) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/orderDetail.xlsx");
        Workbook workbook = new XSSFWorkbook(inputStream);
        inputStream.close();
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
        Map<String, List<CourseGroup>> courseMap = courseGroupList.parallelStream().collect(Collectors.groupingBy(CourseGroup::getCouId));
        List<Book> allBookList = bookService.getTextBookByCourseList(courseIdSet);
        if (allBookList.isEmpty()) {
            return new String[0][0];
        }
        int length = CLASS_START_INDEX + this.classIndex.size();
        String[][] data = new String[courseMap.size()][];
        int i = 0;
        for (String courseId : courseMap.keySet()) {
            List<CourseGroup> courseGroupListItem = courseMap.get(courseId);
            String[] strings = new String[length];
//            数据初始化为空字符串，避免导出null
            Arrays.fill(strings, StrUtil.EMPTY);
            StrBuilder classStrBuilder = StrBuilder.create();
            StrBuilder teacherStrBuilder = StrBuilder.create();
            for (CourseGroup courseGroup : courseGroupListItem) {
                String className = courseGroup.getClassName().replace(" ", "\n");
                classStrBuilder.append(className).append("\n");
                teacherStrBuilder.append(courseGroup.getTeacherName()).append("\n");
            }
            CourseGroup courseGroup = courseGroupListItem.get(0);
            strings[0] = courseId;
            strings[1] = courseGroup.getCourseName();
            strings[2] = CourseNature.getContent(courseGroup.getCourseNature()).getContent();
            strings[3] = classStrBuilder.toStringAndReset();
            List<Book> bookList = allBookList.stream().filter(v -> courseId.equals(v.getCourseId())).collect(Collectors.toList());
            if (!bookList.isEmpty()) {
                StrBuilder isbn = new StrBuilder();
                StrBuilder name = new StrBuilder();
                StrBuilder price = new StrBuilder();
                StrBuilder author = new StrBuilder();
                StrBuilder publish = new StrBuilder();
                StrBuilder pubDate = new StrBuilder();
                StrBuilder edition = new StrBuilder();
                StrBuilder award = new StrBuilder();
                StrBuilder forTeacher = new StrBuilder();
                for (Book book : bookList) {
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
                        Map<String, Long> classToNum = bookIdToClass.get(book.getId());
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
            strings[14] = bookList.isEmpty() ? "否" : "是";
            data[i++] = strings;
        }
        return data;
    }


    private CellStyle getBaseCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);
        return cellStyle;
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
        String[][] data = new String[collect.size() + 1][7];
        Iterator<Map.Entry<Integer, List<BookOrderVo>>> entries = collect.entrySet().iterator();
        int i = 1;
        String[] head = {"序号", "课程号", "书名", "作者", "出版社", "ISBN号", "订购数量"};
        data[0] = head;
        while (entries.hasNext()) {
            Map.Entry<Integer, List<BookOrderVo>> entry = entries.next();
            String[] strings = new String[7];
            Book book = bookService.getBook(entry.getKey());
            strings[0] = String.valueOf(i);
            strings[1] = entry.getValue().get(0).getCourseId();
            strings[2] = book.getName();
            strings[3] = book.getAuthor();
            strings[4] = book.getPublish();
            strings[5] = book.getIsbn();
            strings[6] = String.valueOf(entry.getValue().size());
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

    @Override
    public Workbook exportOutBoundData(int grade, String semesterId, int degree) {
//      开始构建excel
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/OutboundDetails.xlsx");
        Workbook mould;
        try {
            mould = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            log.error("OutboundDetails is not fount");
            return null;
        }
        Sheet mouldSheet = mould.getSheetAt(0);
        Workbook workbook = new XSSFWorkbook();
        CellStyle cellStyle0 = workbook.createCellStyle();
        cellStyle0.cloneStyleFrom(mouldSheet.getRow(0).getCell(0).getCellStyle());

        CellStyle cellStyle1 = workbook.createCellStyle();
        cellStyle1.cloneStyleFrom(mouldSheet.getRow(1).getCell(0).getCellStyle());

        CellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.cloneStyleFrom(mouldSheet.getRow(2).getCell(0).getCellStyle());

        CellStyle cellStyle3 = workbook.createCellStyle();
        cellStyle3.cloneStyleFrom(mouldSheet.getRow(3).getCell(0).getCellStyle());

        CellStyle cellStyle4 = workbook.createCellStyle();
        cellStyle4.cloneStyleFrom(mouldSheet.getRow(3).getCell(1).getCellStyle());

        CellStyle cellStyle5 = workbook.createCellStyle();
        cellStyle5.cloneStyleFrom(mouldSheet.getRow(22).getCell(1).getCellStyle());

        Map<Integer, List<OutBoundDataVo>> map = renderData(grade, semesterId, degree);

        for (Integer specialityId : map.keySet()) {
            Sheet sheet = workbook.createSheet(SpecialityUtil.getSpeciality(specialityId).getName());
//      设置列宽
            for (int i = 0; i < 6; i++) {
                sheet.setColumnWidth(i, mouldSheet.getColumnWidth(i));
            }
//      设置页边距
            for (short i = 0; i < 4; i++) {
                sheet.setMargin(i, mouldSheet.getMargin(i));
            }

            String[] header = {"序号", "教材名称", "单价(元)", "单位", "数量", "总价"};
            String semesterName = TimeUtil.getSemesterName(semesterId);
            int semesterNum = TimeUtil.getSemesterNum(grade, semesterId);
            String date = LocalDate.now().toString();

            Map<String, List<OutBoundDataVo>> classOutBoundData = map.get(specialityId).stream()
                    .collect(Collectors.groupingBy(OutBoundDataVo::getClassName));
//      以班级为单位循环输出
            Set<String> classNameSet = new TreeSet<>(classOutBoundData.keySet());
            int i = -1;
            for (String className : classNameSet) {
                i++;
                Row row0 = sheet.createRow(i * 24);
                row0.setHeight(mouldSheet.getRow(0).getHeight());
                Cell cell00 = row0.createCell(0);
                cell00.setCellValue("中原工学院软件学院教材出库单");
                cell00.setCellStyle(cellStyle0);

                Row row1 = sheet.createRow(1 + i * 24);
                row1.setHeight(mouldSheet.getRow(1).getHeight());
                Cell cell10 = row1.createCell(0);
                cell10.setCellValue(StrUtil.format("{} {} (总第{}学期)      {}", className, semesterName, semesterNum, date));
                cell10.setCellStyle(cellStyle1);

                Row row2 = sheet.createRow(2 + i * 24);
                row2.setHeight(mouldSheet.getRow(2).getHeight());
                int startCell = 0, endCell = 5, startRow1 = 0, startRow2 = 1, startRow3 = 22, startRow4 = 23;
//          序号教材名称单价
                for (int j = 0; j < header.length; j++) {
                    Cell cell = row2.createCell(j);
                    cell.setCellValue(header[j]);
                    cell.setCellStyle(cellStyle2);
                }
                List<OutBoundDataVo> outBoundDataVoList = classOutBoundData.get(className);
                int k = 0;
                for (int j = 3 + i * 24; j < 21 + i * 24; j++) {
                    Row row = sheet.createRow(j);
                    row.setHeight(mouldSheet.getRow(j - i * 24).getHeight());
                    Cell cell0 = row.createCell(0);
                    cell0.setCellStyle(cellStyle3);

                    Cell cell1 = row.createCell(1);
                    cell1.setCellStyle(cellStyle4);

                    Cell cell2 = row.createCell(2);
                    cell2.setCellStyle(cellStyle3);

                    Cell cell3 = row.createCell(3);
                    cell3.setCellStyle(cellStyle3);

                    Cell cell4 = row.createCell(4);
                    cell4.setCellStyle(cellStyle3);

                    Cell cell5 = row.createCell(5);
                    cell5.setCellStyle(cellStyle3);

                    OutBoundDataVo outBoundDataVo;
                    if (k < outBoundDataVoList.size()) {
                        outBoundDataVo = outBoundDataVoList.get(k++);
                        cell0.setCellValue(j - 2 - i * 24);
                        cell1.setCellValue(outBoundDataVo.getBookName());
                        cell2.setCellValue(outBoundDataVo.getPrice());
                        cell3.setCellValue("本");
                        cell4.setCellValue(outBoundDataVo.getNumber());
                        cell5.setCellFormula(StrUtil.format("C{}*E{}", j + 1, j + 1));
                    }
                }

                Row row21 = sheet.createRow(21 + i * 24);
                row21.setHeight(mouldSheet.getRow(21).getHeight());
                row21.createCell(0).setCellStyle(cellStyle4);
                Cell cell211 = row21.createCell(1);
                cell211.setCellValue("合计：");
                cell211.setCellStyle(cellStyle4);

                Cell cell212 = row21.createCell(2);
                cell212.setCellFormula(StrUtil.format("SUM(C{}:C{})", 4 + i * 24, 21 + i * 24));
                cell212.setCellStyle(cellStyle3);

                Cell cell215 = row21.createCell(5);
                cell215.setCellFormula(StrUtil.format("SUM(F{}:F{})", 4 + i * 24, 21 + i * 24));
                cell215.setCellStyle(cellStyle3);

                Row row22 = sheet.createRow(22 + i * 24);
                row22.setHeight(mouldSheet.getRow(22).getHeight());
                Cell cell220 = row22.createCell(0);
                cell220.setCellValue("用途：学生用书");
                cell220.setCellStyle(cellStyle5);

                Row row23 = sheet.createRow(23 + i * 24);
                row23.setHeight((short) 1000);
                Cell cell230 = row23.createCell(0);
                cell230.setCellValue("执单人：                                 领书人：                                          验收人：");
                cell230.setCellStyle(cellStyle5);

                sheet.addMergedRegion(new CellRangeAddress(startRow1 + i * 24, startRow1 + i * 24, startCell, endCell));
                sheet.addMergedRegion(new CellRangeAddress(startRow2 + i * 24, startRow2 + i * 24, startCell, endCell));
                sheet.addMergedRegion(new CellRangeAddress(startRow3 + i * 24, startRow3 + i * 24, startCell, endCell));
                sheet.addMergedRegion(new CellRangeAddress(startRow4 + i * 24, startRow4 + i * 24, startCell, endCell));
            }
//        合并单元格添加边框
            sheet.getMergedRegions().forEach(v -> {
                RegionUtil.setBorderBottom(BorderStyle.THIN, v, sheet);
                RegionUtil.setBorderLeft(BorderStyle.THIN, v, sheet);
                RegionUtil.setBorderRight(BorderStyle.THIN, v, sheet);
                RegionUtil.setBorderTop(BorderStyle.THIN, v, sheet);
            });
        }
        return workbook;
    }

    /**
     * @return [专业id，出库数据]
     */
    private Map<Integer, List<OutBoundDataVo>> renderData(int grade, String semesterId, int degree) {
        Set<String> classNameSet = classesMapper
                .select(ClassesSelectParam.builder().grade(grade).degree(degree).build())
                .stream().map(Classes::getClassname)
                .collect(Collectors.toSet());

        List<BookOrderVo> allOrderList = bookOrderMapper.select(BookOrderSelectParam.builder().semesterId(semesterId).build())
                .stream()
                .filter(v -> classNameSet.contains(v.getClassName()))
                .collect(Collectors.toList());

//      [班级，个人订单]
        Map<String, List<BookOrderVo>> classMap = allOrderList.stream().collect(Collectors.groupingBy(BookOrderVo::getClassName));
//      [专业方向，出库数据]
        Map<Integer, List<OutBoundDataVo>> specialityOutBoundDataMap = new HashMap<>(6);
        Collection<Integer> specialityIdColl = SpecialityUtil.getAllId();
        specialityIdColl.forEach(v -> specialityOutBoundDataMap.put(v, new ArrayList<>(20)));


        for (String className : classMap.keySet()) {
            List<BookOrderVo> classBookOrderVoList = classMap.get(className);
//          [bookId，个人订单]
            Map<Integer, List<BookOrderVo>> bookMap = classBookOrderVoList.stream().collect(Collectors.groupingBy(BookOrderVo::getBookId));
//          一个班的信息
            List<OutBoundDataVo> outBoundDataVoList = new ArrayList<>(bookMap.size());
            bookMap.keySet().forEach(v -> {
                List<BookOrderVo> item = bookMap.get(v);
                if (!item.isEmpty()) {
                    outBoundDataVoList.add(
                            OutBoundDataVo.builder()
                                    .bookName(item.get(0).getName())
                                    .price(item.get(0).getPrice())
                                    .className(className)
                                    .number(item.size())
                                    .build());
                }
            });
            int specialityId = SpecialityUtil.getSpeciality(className).getId();
            specialityOutBoundDataMap.get(specialityId).addAll(outBoundDataVoList);
        }
        return specialityOutBoundDataMap;
    }

    /**
     * 导出年级订书结算表
     *
     * @param grade  年级
     * @param degree 0全部，1本科 2专科
     */
    @Override
    public Workbook exportBookSettlement(int grade, int degree) {
//      获取折扣信息
        List<Semester> semesterList = TimeUtil.getGradeSemesterList(grade, degree);
        Map<String, Double> semesterDiscountMap = bookOrderMapper.getDiscountList()
                .stream()
                .collect(Collectors.toMap(SemesterDiscountDo::getSemesterId, SemesterDiscountDo::getDiscount));
        semesterList.forEach(v -> {
            if (!semesterDiscountMap.containsKey(v.getId())) {
                semesterDiscountMap.put(v.getId(), 0.0);
            }
        });

        int colNum = 5 + semesterList.size() * 2;
        String title = StrUtil.format("软件学院{}学生书费单", TimeUtil.getGradeName(grade, degree));
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("教材结算");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colNum - 1));
//      设置列宽
        sheet.setColumnWidth(0, 256 * 4 + 184);
        for (int i = 1; i < colNum; i++) {
            sheet.setColumnWidth(i, 256 * 12 + 184);
        }

        Cell cell00 = sheet.createRow(0).createCell(0);
        cell00.setCellValue(title);
        cell00.setCellStyle(getBaseCellStyle(workbook));

        String[] head = new String[colNum];
        head[0] = "序号";
        head[1] = "班级";
        head[2] = "学号";
        head[3] = "姓名";
        int i = 3;
        for (Semester semester : semesterList) {
            String semesterId = semester.getId();
            Double discount = semesterDiscountMap.get(semesterId);
            head[i + 1] = StrUtil.builder(7).append(semesterId).append("学期").toString();
            head[i + 2] = StrUtil.builder(6).append(discount == null ? StrUtil.EMPTY : discount).append("折后").toString();
            i = i + 2;
        }
        head[head.length - 1] = "应交书费";

        Row row1 = sheet.createRow(1);
        row1.setHeight((short) 400);
        for (int j = 0; j < head.length; j++) {
            Cell cell = row1.createCell(j);
            cell.setCellStyle(getBaseCellStyle(workbook));
            cell.setCellValue(head[j]);
        }

        List<BookSettlementVo> bookSettlementVoList = renderData(grade, degree);
        for (int j = 2; j < bookSettlementVoList.size() + 2; j++) {
            BookSettlementVo bookSettlementVo = bookSettlementVoList.get(j - 2);
            Row row = sheet.createRow(j);
            row.setHeight((short) 400);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(j - 1);
            cell0.setCellStyle(getBaseCellStyle(workbook));

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(bookSettlementVo.getClassName());
            cell1.setCellStyle(getBaseCellStyle(workbook));

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(bookSettlementVo.getUserId());
            cell2.setCellStyle(getBaseCellStyle(workbook));

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(bookSettlementVo.getName());
            cell3.setCellStyle(getBaseCellStyle(workbook));

            Map<String, Double> semesterBill = bookSettlementVo.getSemesterBill();
            Set<String> semesterSet = new TreeSet<>(semesterBill.keySet());
            int k = 4;
            for (String semesterId : semesterSet) {
                Cell cell = row.createCell(k);
                cell.setCellValue(semesterBill.get(semesterId));
                cell.setCellStyle(getBaseCellStyle(workbook));

                Cell cellDiscount = row.createCell(k + 1);
//              账单*折扣保留两位小数
                cellDiscount.setCellValue(NumberUtil.round(semesterBill.get(semesterId) * semesterDiscountMap.get(semesterId), 2).doubleValue());
                cellDiscount.setCellStyle(getBaseCellStyle(workbook));
                k = k + 2;
            }
            double sum = 0;
            for (String semesterId : semesterSet) {
                sum += semesterBill.get(semesterId) * semesterDiscountMap.get(semesterId);
            }
            Cell sumCell = row.createCell(k);
            sumCell.setCellValue(NumberUtil.round(sum, 2).doubleValue());
            sumCell.setCellStyle(getBaseCellStyle(workbook));
        }


        return workbook;
    }

    /**
     * 计算每学期的教材计算费用
     *
     * @param grade  年级
     * @param degree 0全部，1本科 2专科
     */
    public List<BookSettlementVo> renderData(int grade, int degree) {
        List<Semester> semesterList = TimeUtil.getGradeSemesterList(grade, degree);
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
        List<StudentVo> studentVoList = studentMapper.select(StudentSelectParam.builder().grade(grade).degree(degree).build());
        List<BookSettlementVo> bookSettlementVoList = new ArrayList<>(studentVoList.size());
//      初始化每个学生的账单数据
        studentVoList.forEach(student -> {
            BookSettlementVo bookSettlementVo = new BookSettlementVo();
            bookSettlementVo.setName(student.getName());
            bookSettlementVo.setClassName(student.getClassName());
            bookSettlementVo.setUserId(student.getId());

            Map<String, Double> bill = new HashMap<>(semesterList.size());
            semesterList.forEach(semester -> bill.put(semester.getId(), 0.0));
            bookSettlementVo.setSemesterBill(bill);
            bookSettlementVoList.add(bookSettlementVo);
        });
//      [userId，[学期id，金额]]
        Map<String, Map<String, DoubleSummaryStatistics>> collect =
                bookOrderMapper.select(BookOrderSelectParam.builder().grade(grade).degree(degree).build())
                        .stream()
                        .collect(Collectors.groupingBy(BookOrderVo::getUserId,
                                Collectors.groupingBy(BookOrderVo::getSemesterId,
                                        Collectors.summarizingDouble(BookOrderVo::getPrice))));

        for (BookSettlementVo bookSettlementVo : bookSettlementVoList) {
            String userId = bookSettlementVo.getUserId();
            Map<String, Double> semesterBill = bookSettlementVo.getSemesterBill();
            Map<String, DoubleSummaryStatistics> map = collect.get(userId);
            if (map != null) {
                map.forEach((k, v) -> semesterBill.put(k, v.getSum()));
            }

        }
        bookSettlementVoList.sort(Comparator.comparing(BookSettlementVo::getUserId));


        return bookSettlementVoList;
    }


    @Override
    public void setSemesterDiscount(String semester, Double discount) {
        bookOrderMapper.setSemesterDiscount(semester, discount);
    }

    @Override
    public Double getDiscountBySemester(String semester) {
        return bookOrderMapper.selectDiscountBySemester(semester);
    }

    /**
     * 获取每学期折扣列表
     */
    @Override
    public List<SemesterDiscountVo> getDiscountBySemesterList() {
        Map<String, Double> map = bookOrderMapper.getDiscountList()
                .stream()
                .collect(Collectors.toMap(SemesterDiscountDo::getSemesterId, SemesterDiscountDo::getDiscount));

        List<Semester> semesterList = TimeUtil.getAllSemester();
        List<SemesterDiscountVo> semesterDiscountVoList = new ArrayList<>(semesterList.size());
        for (Semester semester : semesterList) {
            String semesterId = semester.getId();
            String semesterName = semester.getName();
            SemesterDiscountVo semesterDiscountVo = new SemesterDiscountVo(semesterId, String.valueOf(map.get(semesterId)), semesterName);
            semesterDiscountVoList.add(semesterDiscountVo);
        }
        return semesterDiscountVoList;
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
        return data;
    }
}
