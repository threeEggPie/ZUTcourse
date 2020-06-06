package xyz.kingsword.course;


import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.data.style.TableStyle;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.CourseNature;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.util.PinYinTool;
import xyz.kingsword.course.util.SpringContextUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CourseApplicationTests {
    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private SortCourseMapper sortCourseMapper;
    @Autowired
    private CourseMapper courseMapper;


    @Test
    public void contextLoads() throws Exception {
        System.out.println((int) 60.0);
    }

    private void importStudent() throws IOException {
        String filePath = "C:\\Users\\wang\\Desktop\\学生基本信息 (1).xls";
        InputStream inputStream = new FileInputStream(filePath);
        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        List<Student> studentList = new ArrayList<>(sheet.getLastRowNum() + 1);
        Set<Classes> classesSet = new HashSet<>();
        for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
            Row row = sheet.getRow(i);
            String username = row.getCell(0).getStringCellValue();
            String name = row.getCell(1).getStringCellValue();
            String className = row.getCell(2).getStringCellValue();
            int grade = ReUtil.getFirstNumber(className);
            grade = grade < 100 ? grade : grade / 10;
            grade += 2000;
            Student student = new Student();
            student.setId(username);
            student.setName(name);
            student.setClassName(className);
            student.setGrade(grade);
            studentList.add(student);

            Classes classes = new Classes();
            classes.setClassname(className);
            classes.setGrade(grade);
            classesSet.add(classes);
        }
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
//        studentMapper.insert(studentList);

        ClassesMapper classesMapper = SpringContextUtil.getBean(ClassesMapper.class);
        classesMapper.insertList(classesSet);
    }

    private static boolean isChineseByScript(String a) {
        Character.UnicodeScript sc = Character.UnicodeScript.of(a.charAt(0));
        return sc == Character.UnicodeScript.HAN;
    }

    private void sortCourseImport() throws Exception {
        PinYinTool tool = new PinYinTool();
        Map<String, String> collect = teacherMapper.select(TeacherSelectParam.builder().pageSize(0).build()).parallelStream()
                .collect(Collectors.toMap(Teacher::getName, Teacher::getId));
        Set<String> teaNameSet = collect.keySet();
        Workbook workbook = new HSSFWorkbook(new FileInputStream("C:\\Users\\wang\\Desktop\\19202学期教学计划.xls"));
        Sheet sheet = workbook.getSheetAt(0);
        String semesterId = "19202";
        List<SortCourse> sortCourseList = new ArrayList<>();
        List<Teacher> teacherList = new ArrayList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String id = row.getCell(1).getStringCellValue();
            int num = (int) row.getCell(5).getNumericCellValue();
            String className = ReUtil.delAll("\\([^)]*\\)", row.getCell(4).getStringCellValue());
            String teaName = row.getCell(12).getStringCellValue();
            String teaId = teaNameSet.parallelStream().filter(teaName::contains).map(collect::get).collect(Collectors.joining());
            if (teaId.isEmpty()) {
                Teacher teacher = new Teacher();
                teacher.setName(teaName);
                String pinyin = tool.toPinYin(teaName, "", PinYinTool.Type.LOWERCASE);
                teacher.setId(pinyin);
                teaId = pinyin;
                teacher.setRole("[" + RoleEnum.TEACHER.getCode() + "]");
                teacherList.add(teacher);
            }
            SortCourse course = new SortCourse();
            course.setCouId(id);
            course.setTeaId(teaId);
            course.setClassName(className);
            course.setSemesterId(semesterId);
            course.setStudentNum(num);
            sortCourseList.add(course);
        }
        teacherMapper.insert(teacherList);
        sortCourseMapper.insert(sortCourseList);
//        System.out.println(sortCourseList.size());
    }

    private void courseImport() throws IOException {
        Workbook workbook = new HSSFWorkbook(new FileInputStream("C:\\Users\\wang\\Desktop\\19202学期教学计划.xls"));
        Sheet sheet = workbook.getSheetAt(0);
        List<Course> courseList = new ArrayList<>();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String id = row.getCell(1).getStringCellValue();
            String name = row.getCell(3).getStringCellValue();
            String week = row.getCell(6).getStringCellValue();
            week = Optional.ofNullable(week).orElse("0/0");
            String[] data = week.split("/");
            int timeWeek = 0;
            if (data.length == 2) {
                timeWeek = Integer.parseInt(data[0]);
            }
            double credit = row.getCell(8).getNumericCellValue();
            String type = row.getCell(9).getStringCellValue();
            String nature = row.getCell(10).getStringCellValue();
            String way = row.getCell(11).getStringCellValue();
            int theoryTime = (int) row.getCell(13).getNumericCellValue();
            int labTime = (int) row.getCell(16).getNumericCellValue();
            int studyingTime = (int) row.getCell(7).getNumericCellValue();
            Course course = new Course();
            course.setId(id);
            course.setName(name);
            course.setCredit(credit);
            course.setType(CourseTypeEnum.get(type).getCode());
            course.setNature(nature.equals("必修") ? CourseNature.NOT_REQUIRED.getCode() : CourseNature.REQUIRED.getCode());
            course.setExaminationWay(way);
            course.setTimeAll(studyingTime);
            course.setTimeTheory(theoryTime);
            course.setTimeLab(labTime);
            course.setTimeWeek(timeWeek);
            courseList.add(course);
        }
        courseList.forEach((a) -> courseMapper.insert(a));
    }


    public CalendarData getData() {
        CalendarData calendarData = new CalendarData();
        calendarData.setCollege("软件学院");
        calendarData.setTableData(getTeachingContent());
        return calendarData;
    }

    //构建表格体
    public List<RowRenderData> getTeachingContent() {
        ClassPathResource jsonData = new ClassPathResource("json/calendar.json");
        FileReader fileReader = new FileReader(jsonData.getFile());
        Style cellStyle = new Style("宋体", 11);
        TableStyle tableStyle = new TableStyle();
//        tableStyle.setAlign(STJc.CENTER);
        DateFormat simpleDateFormat = new SimpleDateFormat("MM dd");
        List<TeachingContent> teachingContentList = JSON.parseArray(fileReader.readString(), TeachingContent.class);
        List<RowRenderData> rowRenderDataList = new ArrayList<>(teachingContentList.size());
        for (TeachingContent teachingContent : teachingContentList) {
            TextRenderData t1 = new TextRenderData(simpleDateFormat.format(teachingContent.getDate()), cellStyle);
            TextRenderData t2 = new TextRenderData(String.valueOf(teachingContent.getWeekNum()), cellStyle);
            TextRenderData t3 = new TextRenderData(String.valueOf(teachingContent.getIndex()), cellStyle);
            TextRenderData t4 = new TextRenderData(teachingContent.getTeachingContent(), cellStyle);
            TextRenderData t5 = new TextRenderData(String.valueOf(teachingContent.getStudyTime()), cellStyle);
            TextRenderData t6 = new TextRenderData(teachingContent.getHomeworkOrLab(), cellStyle);
            RowRenderData rowRenderData = RowRenderData.build(t1, t2, t3, t4, t5, t6);
            rowRenderData.setRowStyle(tableStyle);
            rowRenderDataList.add(rowRenderData);
        }

        return rowRenderDataList;
    }

    @Test
    public void excelImportTest() throws Exception {
//        String filePath = "E:\\教学任务管理平台\\2015-软件工程-培养方案.xlsx";
//        InputStream inputStream = new FileInputStream(filePath);
//        List<TrainingProgram> trainingProgramList = trainingProgramService.excelImport(inputStream);
//        trainingProgramList.stream().filter(v -> v.getCollegesOrDepartments().equals("院考")).forEach(System.out::println);
    }

    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (char c : ch) {
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }

}
