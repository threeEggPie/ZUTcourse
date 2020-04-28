package xyz.kingsword.course.service;

import com.deepoove.poi.XWPFTemplate;
import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.param.CalendarSelectParam;

import java.util.List;

public interface CalendarService {
    /**
     * 查单个教学日历
     *
     * @param id 教学日历id
     * @return Calendar
     */
    Calendar selectOne(int id);

    PageInfo<Calendar> search(CalendarSelectParam param);


    int insert(Calendar calendar);

    int update(Calendar calendar);

    /**
     * 教学日历审核，由教研室主任进行审核，考虑课程负责人进行一级审核所以要传角色id
     *
     * @param ids    教学日历id
     * @param roleId 角色id
     */
    void verify(List<Integer> ids, int roleId);


    XWPFTemplate export(int calendarId);

    /**
     * 复制教学日历，仅本课程组
     *
     * @param id    被复制教学日历id
     * @param teaId 执行复制操作的教师id
     */
    void copy(int id, String teaId);
}
