package xyz.kingsword.course.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.DO.CalendarDataDO;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.param.CalendarSelectParam;

import java.util.List;

@Mapper
public interface CalendarMapper {
    int insert(Calendar record);

    Calendar selectByPrimaryKey(Integer id);

    int update(Calendar record);

    List<Calendar> search(CalendarSelectParam param);

    /**
     * 查看课程组教学日历
     *
     * @param courseId   课程id
     * @param semesterId 学期id
     * @return calendarId，courseId封到calendar
     */
    List<Calendar> getCalendarByCourse(@Param("courseId") String courseId, @Param("semesterId") String semesterId);

    /**
     * 查看教师所有的教学日历
     *
     * @param teacherId  课程id
     * @param semesterId 学期id
     * @return calendarId，courseId封到calendar
     */
    List<Calendar> getCalendarByTeacher(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);


    /**
     * 设置教学日历审核状态，0未审核，1已审核
     *
     * @param ids    批量审核的教学日历id
     * @param status 状态
     */
    void setStatus(@Param("list") List<Integer> ids, @Param("status") int status);

    CalendarDataDO exportCalendar(int calendarId);

    List<TeacherGroup> getCourseGroupByResearch(@Param("researchRoomId") int researchRoomId, @Param("semesterId") String semesterId);

    List<Calendar> getVerifyStatus(String courseId, String semesterId);

}