package xyz.kingsword.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.exceptions.POIException;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.dao.SortCourseLogMapper;
import xyz.kingsword.course.dao.SortCourseMapper;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.SortCourseSearchParam;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;
import xyz.kingsword.course.service.*;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.PinYinTool;
import xyz.kingsword.course.util.SpringContextUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.vo.SortCourseVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SortServiceImpl implements SortCourseService {

    @Resource
    private SortCourseMapper sortcourseMapper;

    @Resource
    private TeacherService teacherService;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private ClassesService classesService;

    @Resource
    private BookService bookService;

    @Resource
    private SortCourseLogService logService;



    @Override
    public void insertSortCourseList(List<SortCourse> sortCourseList) {
        sortcourseMapper.insert(sortCourseList);
    }


    @Override
    public void setSortCourse(SortCourseUpdateParam sortCourseUpdateParam, HttpSession session) {
        int count = sortcourseMapper.setSortCourse(sortCourseUpdateParam);
        if (count!=0){
            logService.addLog(sortCourseUpdateParam,session);
        }


    }

    @Override
    public void setClasses(List<String> classNameList, int sortId) {
        String className = String.join(",", classNameList);
        sortcourseMapper.setClasses(className, sortId);
    }


    @Override
    public void deleteSortCourseRecord(List<Integer> id) {
        sortcourseMapper.deleteSortCourseRecord(id);
    }

    @Override
    public List<SortCourseVo> getCourseHistory(String courseId) {
        return sortcourseMapper.getCourseHistory(courseId);
    }

    @Override
    public List<SortCourseVo> getTeacherHistory(String teacherId) {
        List<SortCourseVo> sortCourseVoList = sortcourseMapper.getTeacherHistory(teacherId);
        if (!sortCourseVoList.isEmpty()) {
            Map<String, Course> courseMap = courseMapper.getByIdList(sortCourseVoList.stream().map(SortCourseVo::getCourseId).collect(Collectors.toList())).stream().collect(Collectors.toMap(Course::getId, v -> v));
            sortCourseVoList.forEach(v -> v.setTimeAll(courseMap.get(v.getCourseId()).getTimeAll()));
        }
        return sortCourseVoList;
    }

    @Override
    public PageInfo<SortCourseVo> search(SortCourseSearchParam param) {
        PageInfo<SortCourseVo> pageInfo = PageMethod.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> sortcourseMapper.search(param));
        List<SortCourseVo> sortCourseVoList = pageInfo.getList();
        renderSortCourseVo(sortCourseVoList);

//      如果为true就删掉bookList为空的数据
        if (param.getDeclareStatus() != null) {
            sortCourseVoList.removeIf(v -> v.getBookList().isEmpty() == param.getDeclareStatus());
        }
        return PageInfo.of(sortCourseVoList, pageInfo.getNavigatePages());
    }


    /**
     * @param idList 待合并课头id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeCourseHead(List<Integer> idList) {
        List<SortCourse> sortCourseList = sortcourseMapper.getById(idList);
        mergeCheck(sortCourseList);
        SortCourse mainSortCourse = new SortCourse();
        BeanUtil.copyProperties(sortCourseList.get(0), mainSortCourse);
        String className = sortCourseList.parallelStream()
                .map(SortCourse::getClassName)
                .distinct()
                .sorted((a, b) -> StrUtil.compare(a, b, true))
                .collect(Collectors.joining(" "));
        int studentNum = sortCourseList.parallelStream().mapToInt(SortCourse::getStudentNum).sum();
        mainSortCourse.setClassName(className);
        mainSortCourse.setStudentNum(studentNum);
        mainSortCourse.setMergedId(JSON.toJSONString(idList));

        sortcourseMapper.mergeCourseHead(idList);
        sortcourseMapper.insert(Collections.singletonList(mainSortCourse));
    }

    /**
     * 重置课头，先把合并的课头删除，再把被合并进去的课头状态改为正常显示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreCourseHead(List<Integer> idList) {
        sortcourseMapper.deleteSortCourseRecord(idList);
        List<SortCourse> sortCourseList = sortcourseMapper.getById(idList);
        List<Integer> mergedIdList = new ArrayList<>(sortCourseList.size() * 2);
        for (SortCourse sortCourse : sortCourseList) {
            mergedIdList.addAll(JSON.parseArray(sortCourse.getMergedId()).toJavaList(Integer.class));
        }
        mergedIdList = mergedIdList.stream().distinct().collect(Collectors.toList());
        sortcourseMapper.restoreCourseHead(mergedIdList);
    }

    /**
     * 合并前检查courseId和teacherId
     */
    private void mergeCheck(List<SortCourse> sortCourseList) {
        ConditionUtil.validateTrue(sortCourseList.size() > 1).orElseThrow(() -> new OperationException(ErrorEnum.SINGLE_DATA));
//        验证，同一课程，同一老师才能合并
        Set<String> set = sortCourseList.parallelStream().map(v -> v.getCouId() + v.getTeaId()).collect(Collectors.toSet());
        ConditionUtil.validateTrue(set.size() == 1).orElseThrow(() -> new OperationException(ErrorEnum.DIFFERENT_COURSE));
    }

    /**
     * 为sortCourseVo添加教材
     */
    private void renderSortCourseVo(List<SortCourseVo> sortCourseVoList) {
        if (CollUtil.isNotEmpty(sortCourseVoList)) {
            String semesterId = sortCourseVoList.get(0).getSemesterId();
            for (SortCourseVo sortCourseVo : sortCourseVoList) {
                CourseGroupMapper courseGroupMapper = SpringContextUtil.getBean(CourseGroupMapper.class);
                List<String> courseGroup = courseGroupMapper.getSemesterCourseGroup(sortCourseVo.getCourseId(), semesterId).parallelStream().map(CourseGroup::getTeacherName).collect(Collectors.toList());
                sortCourseVo.setBookList(bookService.getTextBook(sortCourseVo.getCourseId()));
                sortCourseVo.setTextBookNum(sortCourseVo.getBookList().size());
                sortCourseVo.setCourseGroup(courseGroup);
                if (sortCourseVo.getBookManager() != null) {
                    Teacher bookManager = Optional.ofNullable(teacherService.getTeacherById(sortCourseVo.getBookManager())).orElseThrow(DataException::new);
                    sortCourseVo.setBookManager(bookManager.getName());
                }
            }
//            CollUtil.sort(sortCourseVoList, (a, b) -> StrUtil.compare(a.getCourseId(), b.getCourseId(), false));
        }
    }


    /**
     * 软件学院排课数据导入
     * 模板：preSortCourse.xls
     *
     * @param inputStream excelInputStream
     * @return List<SortCourse>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excelImport(InputStream inputStream) {
        Workbook workbook;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.error("排课信息导入失败，excel无法打开");
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new POIException("课程信息导入失败，excel无法打开");
        }
        Sheet sheet = workbook.getSheetAt(0);
        restoreCellRange(sheet);
        String semesterId = TimeUtil.getSemesterId(sheet.getRow(2).getCell(0).getStringCellValue());
        Map<String, String> teacherMap = teacherService.select(TeacherSelectParam.builder().pageSize(0).build())
                .getList()
                .stream()
                .collect(Collectors.toMap(Teacher::getName, Teacher::getId));
//      添加系统中不存在的教师
        List<Teacher> teacherList = new ArrayList<>(20);
        for (int i = 6; i < sheet.getLastRowNum() - 1; i++) {
            Row row = sheet.getRow(i);
            String name = row.getCell(12).getStringCellValue();
            if (!teacherMap.containsKey(name)) {
                Teacher teacher = new Teacher();
                String id = PinYinTool.getInstance().toPinYin(name);
                teacher.setId(id);
                teacherMap.put(name, id);
            }
        }
        teacherService.insert(teacherList);

        List<SortCourse> sortCourseList = new ArrayList<>(sheet.getLastRowNum());
        for (int i = 6; i < sheet.getLastRowNum() - 1; i++) {
            Row row = sheet.getRow(i);
            SortCourse sortCourse = new SortCourse();
            sortCourse.setCouId(row.getCell(1).getStringCellValue());
            sortCourse.setClassName(row.getCell(3).getStringCellValue().trim());
            int studentNum = row.getCell(4).getStringCellValue().isEmpty() ? 0 : Integer.parseInt(row.getCell(4).getStringCellValue());
            sortCourse.setStudentNum(studentNum);
            sortCourse.setSemesterId(semesterId);
            String teacherName = row.getCell(12).getStringCellValue();
            if (teacherName == null || teacherName.isEmpty()) {
                sortCourse.setTeaId("0");
            } else {
                sortCourse.setTeaId(teacherMap.get(teacherName));
            }
            sortCourseList.add(sortCourse);
        }
        sortcourseMapper.insert(sortCourseList);
        autoMerge(sheet, sortCourseList);
    }

    /**
     * 导入外院系排课表
     *
     * @param inputStream inputStream
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void otherCourseImport(InputStream inputStream) {
        Workbook workbook;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            log.error("排课信息导入失败，excel无法打开");
            log.error(ExceptionUtil.stacktraceToString(e));
            throw new POIException("课程信息导入失败，excel无法打开");
        }
        Sheet sheet = workbook.getSheetAt(0);
        String semesterId = TimeUtil.getSemesterId(sheet.getRow(2).getCell(0).getStringCellValue());
        Map<String, String> teacherMap = teacherService.select(TeacherSelectParam.builder().pageSize(0).build())
                .getList()
                .stream()
                .collect(Collectors.toMap(Teacher::getName, Teacher::getId));
//      添加系统中不存在的教师
        List<Teacher> teacherList = new ArrayList<>(20);
        for (int i = 6; i < sheet.getLastRowNum() - 1; i++) {
            Row row = sheet.getRow(i);
            String name = row.getCell(13).getStringCellValue();
            if (!teacherMap.containsKey(name)) {
                Teacher teacher = new Teacher();
                String id = PinYinTool.getInstance().toPinYin(name);
                teacher.setId(id);
                teacherMap.put(name, id);
            }
        }
        teacherService.insert(teacherList);

        List<SortCourse> sortCourseList = new ArrayList<>(sheet.getLastRowNum());
        for (int i = 6; i < sheet.getLastRowNum() - 1; i++) {
            Row row = sheet.getRow(i);
            SortCourse sortCourse = new SortCourse();
            sortCourse.setCouId(row.getCell(1).getStringCellValue());
            String className = row.getCell(4).getStringCellValue().trim();
            String patternBracket = "\\(.*?\\)";
            className = Pattern.compile(patternBracket).matcher(className).replaceAll(StrUtil.EMPTY);
            sortCourse.setClassName(className);

            int studentNum = (int) row.getCell(5).getNumericCellValue();
            sortCourse.setStudentNum(studentNum);
            sortCourse.setSemesterId(semesterId);
            String teacherName = row.getCell(13).getStringCellValue();
            if (teacherName == null || teacherName.isEmpty()) {
                sortCourse.setTeaId("0");
            } else {
                sortCourse.setTeaId(teacherMap.get(teacherName));
            }
            sortCourseList.add(sortCourse);
        }
        sortcourseMapper.insert(sortCourseList);
    }

    /**
     * 依赖合并单元格确定合班
     *
     * @param sheet          sheet
     * @param sortCourseList 须借助排课id
     */
    private void autoMerge(Sheet sheet, List<SortCourse> sortCourseList) {
        List<CellRangeAddress> cellRangeAddressList = sheet.getMergedRegions().stream().filter(v -> v.getFirstRow() > 5).collect(Collectors.toList());
        List<SortCourse> result = new ArrayList<>(sortCourseList.size() / 2);
        List<Integer> deletedIdList = new ArrayList<>(sortCourseList.size());
//      逐个合并单元格查询，获取最左侧的序号
        for (int j = 0; j < cellRangeAddressList.size() - 4; j++) {
            CellRangeAddress cellRangeAddress = cellRangeAddressList.get(j);
            int firstRow = cellRangeAddress.getFirstRow();
            int lastRow = cellRangeAddress.getLastRow();
            StringBuilder className = StrUtil.builder();
            int sum = 0;
            int index = firstRow - 6;
            SortCourse sortCourse = sortCourseList.get(index);
            for (; index <= lastRow - 6; index++) {
                if (sortCourse.getClassName() != null && !sortCourse.getClassName().isEmpty()) {
                    className.append(",").append(sortCourseList.get(index).getClassName());
                }
                sum += sortCourseList.get(index).getStudentNum();
            }

            List<Integer> mergedList = CollUtil.sub(sortCourseList, firstRow - 6, lastRow - 6 + 1)
                    .stream().map(SortCourse::getId).collect(Collectors.toList());
            sortCourse.setClassName(className.toString().substring(1));
            sortCourse.setStudentNum(sum);
            sortCourse.setMergedId(JSON.toJSONString(mergedList));
            result.add(sortCourse);
            deletedIdList.addAll(mergedList);
        }
        sortcourseMapper.insert(result);
        sortcourseMapper.mergeCourseHead(deletedIdList);
    }

    @Override
    @SneakyThrows(Exception.class)
    public Workbook excelExport(String semesterId) {
        InputStream inputStream = SortServiceImpl.class.getClassLoader().getResourceAsStream("templates/sortCourse.xls");
        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderExportData(semesterId);
        int startRow = 6;
        Row row = sheet.getRow(2);
        row.getCell(0).setCellValue(TimeUtil.getSemesterName(semesterId));
        for (int i = 0; i < data.length; i++) {
            row = sheet.createRow(i + startRow);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data[i][j]);
            }
        }
        Row lastRow = sheet.createRow(sheet.getLastRowNum());

        CellStyle singleStyle = workbook.createCellStyle();
        singleStyle.cloneStyleFrom(cellStyle);
        singleStyle.setAlignment(HorizontalAlignment.LEFT);

        for (int i = 0; i < 21; i++) {
            Cell cell1 = lastRow.createCell(i);
            cell1.setCellStyle(singleStyle);
        }

        sheet.addMergedRegion(new CellRangeAddress(lastRow.getRowNum(), lastRow.getRowNum(), 0, 20));
        lastRow.getCell(0).setCellValue("学院（部）院长签字：                                                                          系（教研室）主任签字：                                                                                          打印时间:" + LocalDate.now().toString());
        lastRow.setHeight((short) 700);

        return workbook;
    }

    /**
     * 构建导出排课表数据
     */
    private String[][] renderExportData(String semesterId) {
        List<SortCourseVo> sortCourseList = sortcourseMapper.search(SortCourseSearchParam.builder().semesterId(semesterId).build());
        String[][] data = new String[sortCourseList.size()][21];
        int courseHeadNum = 1;
        String lastCourseId = sortCourseList.get(0).getCourseId();
        Map<String, Integer> classStudentNum = classesService.getClassStudentNum();
        Map<String, Course> courseMap = courseMapper.getByIdList(sortCourseList.stream().map(SortCourseVo::getCourseId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(Course::getId, v -> v));
        for (int i = 0; i < sortCourseList.size(); i++) {
            SortCourseVo sortCourseVo = sortCourseList.get(i);
            String[] strings = new String[21];
            Arrays.fill(strings, StrUtil.EMPTY);
            String courseId = sortCourseVo.getCourseId();
            Course course = courseMap.get(courseId);
            strings[0] = String.valueOf(i + 1);
            strings[1] = courseId;
            if (!lastCourseId.equals(courseId)) {
                courseHeadNum = 1;
            }
            lastCourseId = courseId;
            strings[2] = String.valueOf(0) + courseHeadNum;
            courseHeadNum++;
            strings[3] = course.getName();
            String[] classesArray = sortCourseVo.getClassName().split(",");
            if (classesArray[0].length() < 5) {
                strings[5] = String.valueOf(0);
            } else {
                int allStudentNum = 0;
                StrBuilder classInfo = StrBuilder.create();
                for (String className : classesArray) {
                    allStudentNum += classStudentNum.get(className);
                    classInfo.append(className).append("(").append(classStudentNum.get(className)).append(")").append(",");
                }
//            去除最后一个逗号
                strings[4] = classInfo.subString(0, classInfo.length() - 1);
                strings[5] = String.valueOf(allStudentNum);
            }
            strings[6] = StrBuilder.create().append(course.getWeekNum()).append("/").append(course.getTimeWeek()).toStringAndReset();
            strings[7] = String.valueOf(course.getTimeAll());
            strings[8] = String.valueOf(course.getCredit());
            strings[9] = CourseTypeEnum.get(course.getType()).getContent();
            strings[10] = course.getNature() == 1 ? "选修课" : "必修课";
            strings[11] = course.getExaminationWay();
            strings[12] = "0".equals(sortCourseVo.getTeacherId()) ? StrUtil.EMPTY : sortCourseVo.getTeacherName();
            strings[13] = String.valueOf(course.getTimeTheory());
            strings[16] = String.valueOf(course.getTimeLab());
            data[i] = strings;

        }
        return data;
    }


    private CellStyle getBaseCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("SimSun");
        font.setFontHeightInPoints((short) 9);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(font);

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setTopBorderColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        cellStyle.setBottomBorderColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        cellStyle.setLeftBorderColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        cellStyle.setRightBorderColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        return cellStyle;
    }

    /**
     * 重置合并单元格，便于读取内容
     */
    private void restoreCellRange(Sheet sheet) {
        List<CellRangeAddress> list = sheet.getMergedRegions();
        for (CellRangeAddress cellRangeAddress : list) {
            String value = sheet.getRow(cellRangeAddress.getFirstRow()).getCell(cellRangeAddress.getFirstColumn()).getStringCellValue();
            for (int i = cellRangeAddress.getFirstRow(); i <= cellRangeAddress.getLastRow(); i++) {
                Row row = sheet.getRow(i);
                for (int j = cellRangeAddress.getFirstColumn(); j <= cellRangeAddress.getLastColumn(); j++) {
                    row.getCell(j).setCellValue(value);
                }
            }
        }
    }

    @Deprecated
    private <T> PageInfo<T> manualPaging(List<T> list, int pageNum, int pageSize) {
        Page<T> page = new Page<T>(pageNum, pageSize);
        int total = list.size();
        page.setTotal(total);
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, total);
        page.addAll(list.subList(startIndex, endIndex));
        return new PageInfo<>(page);
    }

}
