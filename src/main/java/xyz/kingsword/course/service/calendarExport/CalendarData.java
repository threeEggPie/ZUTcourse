package xyz.kingsword.course.service.calendarExport;

import com.alibaba.fastjson.JSON;
import com.deepoove.poi.config.Name;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.Style;
import lombok.Data;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.pojo.DO.CalendarDataDO;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.TeachingContent;
import xyz.kingsword.course.util.SpringContextUtil;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出word从这个实体类拿数据，所有需要输出的数据均为String，数值类型为0或null均设置为空字符串
 */
@Data
public class CalendarData {
    private BookMapper bookMapper;
    /**
     * 对应教学日历id
     */
    private Integer id;

    /**
     * 教师名称
     */

    private String teaName;
    /**
     * 职称
     */
    private String title;
    /**
     * 教师id
     */
    private String teaId;

    /**
     * 所在学院
     */
    private String college;

    /**
     * 教研室
     */
    private String researchRoom;

    /**
     * 课程id
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 教授班级
     */
    private String className;

    /**
     * 排课id
     */
    private Integer sortId;

    /**
     * 理教周数
     */
    private String weekOfTheory;
    /**
     * 讲课时数
     */
    public String timeOfTheory;

    /**
     * 上机或实验学时
     */
    private String timeOfComputer;
    /**
     * 习题课学时
     */
    private String timeOfHomework;
    /**
     * 总学时
     */
    private String timeOfAll;
    /**
     * 学分
     */
    private String credit;
    /**
     * 学生人数
     */
    private String studentNum;

    /**
     * 课程属性 选修必修
     */
    private String courseProperty;

    /**
     * 考试类型 考试考查
     */
    private String testType;

    /**
     * 第一页的日期
     */
    private String year;

    private String month;

    private String day;

    /**
     * 期末考核方式 只有一个打勾，其他的空字符串
     */
    private String w1;//闭卷考试
    private String w2;//口试
    private String w3;//综合实验
    private String w4;//开卷考试
    private String w5;//论文
    private String w6;//其他

    private String examProportion;

    private String homeworkProportion;

    private String labProportion;

    private String testProportion;
    /**
     * 小论文或综合作业
     */
    private String paperProportion;

    /**
     * 出勤占比
     */
    private String attendanceProportion;

    /**
     * 课堂表现占比
     */
    private String performanceProportion;

    /**
     * 其他方式占比
     */
    private String otherProportion;

    /**
     * 平时表现占比总和
     */
    private String usualProportion;

    /**
     * 辅导答疑时间地点
     */
    private String coach;
    private String coachTeacher;

    /**
     * 授课内容
     */
    private String teachingContent;

    /**
     * 教材和参考书只显示两个
     * 教材2
     */
    private String textBookName1;
    private String textBookAuthor1;
    private String textBookPublish1;

    /**
     * 教材和参考书只显示两个
     * 教材1
     */
    private String textBookName2;
    private String textBookAuthor2;
    private String textBookPublish2;

    private String semesterId;
    /**
     * 教学内容数据
     */
    @Name("tableData")
    private List<RowRenderData> tableData;

    public CalendarData() {
    }


    public CalendarData(CalendarDataDO calendarDataDO) {
        System.out.println(calendarDataDO);
        bookMapper = SpringContextUtil.getBean(BookMapper.class);
        LocalDate localDate = LocalDate.now();
        year = Integer.toString(localDate.getYear());
        month = Integer.toString(localDate.getMonthValue());
        day = Integer.toString(localDate.getDayOfMonth());

        id = calendarDataDO.getId();
        teaName = calendarDataDO.getTeaName();
        title = calendarDataDO.getTitle();
        teaId = calendarDataDO.getTeaId();
        college = calendarDataDO.getCollege();
        researchRoom = calendarDataDO.getCollege();
        courseId = calendarDataDO.getCourseId();
        courseName = calendarDataDO.getCourseName();
        className = calendarDataDO.getClassName();
        sortId = calendarDataDO.getSortId();
        weekOfTheory = convertToStr(calendarDataDO.getWeekOfTheory());
        timeOfTheory = convertToStr(calendarDataDO.getTimeOfTheory());
        timeOfComputer = convertToStr(calendarDataDO.getTimeOfComputer());
        timeOfHomework = convertToStr(calendarDataDO.getTimeOfHomework());
        timeOfAll = convertToStr(calendarDataDO.getTimeOfAll());
        credit = convertToStr(calendarDataDO.getCredit());
        studentNum = convertToStr(calendarDataDO.getStudentNum());
        studentNum = convertToStr(calendarDataDO.getStudentNum());
        courseProperty = calendarDataDO.getCourseProperty() == 1 ? "选修" : "必修";
        testType = calendarDataDO.getTestType();
        try {
            Field field = CalendarData.class.getDeclaredField("w1");
            field.setAccessible(true);
            field.set(this, "√");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        examProportion = convertToStr(calendarDataDO.getExamProportion());
        homeworkProportion = convertToStr(calendarDataDO.getHomeworkProportion());
        labProportion = convertToStr(calendarDataDO.getLabProportion());
        testProportion = convertToStr(calendarDataDO.getTestProportion());
        paperProportion = convertToStr(calendarDataDO.getPaperProportion());
        attendanceProportion = convertToStr(calendarDataDO.getAttendanceProportion());
        performanceProportion = convertToStr(calendarDataDO.getPerformanceProportion());
        otherProportion = convertToStr(calendarDataDO.getOtherProportion());
        usualProportion = convertToStr(100 - calendarDataDO.getExamProportion());
        coach = calendarDataDO.getCoach();
        tableData = getTeachingContent(JSON.parseArray(calendarDataDO.getTeachingContent(), TeachingContent.class));
        semesterId = calendarDataDO.getSemesterId();
        List<Book> bookList = renderBook(calendarDataDO.getTextBook());
        switch (bookList.size()) {
            case 1:
                textBookName1 = bookList.get(0).getName();
                textBookAuthor1 = bookList.get(0).getAuthor();
                textBookPublish1 = bookList.get(0).getPublish();
                break;
            case 2:
                textBookName1 = bookList.get(0).getName();
                textBookAuthor1 = bookList.get(0).getAuthor();
                textBookPublish1 = bookList.get(0).getPublish();
                textBookName2 = bookList.get(1).getName();
                textBookAuthor2 = bookList.get(1).getAuthor();
                textBookPublish2 = bookList.get(1).getPublish();
                break;
            default:
                textBookName1 = "";
                textBookAuthor1 = "";
                textBookPublish1 = "";
        }
    }

    /**
     * @param bookJSON 存有id的json数组
     * @return 前两本书
     */
    private List<Book> renderBook(String bookJSON) {
        List<Integer> integerList = JSON.parseArray(bookJSON, Integer.class).stream().limit(2).collect(Collectors.toList());
        return integerList.isEmpty() ? new ArrayList<>() : bookMapper.selectBookList(integerList);
    }

    private String convertToStr(Integer data) {
        return data == null || data == 0 ? "" : Integer.toString(data);
    }

    //构建表格体
    private List<RowRenderData> getTeachingContent(List<TeachingContent> teachingContentList) {
        Style cellStyle = new Style("宋体", 11);
        DateFormat simpleDateFormat = new SimpleDateFormat("MM dd");
        List<RowRenderData> rowRenderDataList = new ArrayList<>(teachingContentList.size());
        for (TeachingContent teachingContent : teachingContentList) {
            TextRenderData t1 = new TextRenderData(simpleDateFormat.format(teachingContent.getDate()), cellStyle);
            TextRenderData t2 = new TextRenderData(Integer.toString(teachingContent.getWeekNum()), cellStyle);
            TextRenderData t3 = new TextRenderData(Integer.toString(teachingContent.getIndex()), cellStyle);
            TextRenderData t4 = new TextRenderData(teachingContent.getTeachingContent(), cellStyle);
            TextRenderData t5 = new TextRenderData(Integer.toString(teachingContent.getStudyTime()), cellStyle);
            TextRenderData t6 = new TextRenderData(teachingContent.getHomeworkOrLab(), cellStyle);
            RowRenderData rowRenderData = RowRenderData.build(t1, t2, t3, t4, t5, t6);
            rowRenderDataList.add(rowRenderData);
        }
        return rowRenderDataList;
    }


}