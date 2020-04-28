package xyz.kingsword.course.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.CalendarMapper;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.DO.CalendarDataDO;
import xyz.kingsword.course.pojo.param.CalendarSelectParam;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;
import xyz.kingsword.course.service.CalendarService;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.service.calendarExport.TableRenderPolicy;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.Constant;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {
    @Autowired
    private CalendarMapper calendarMapper;
    @Autowired
    private SemesterMapper semesterMapper;
    @Autowired
    private CourseGroupMapper courseGroupMapper;

    @Override
    public Calendar selectOne(int id) {
        return calendarMapper.selectByPrimaryKey(id);
    }

    @Override
    public PageInfo<Calendar> search(CalendarSelectParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> calendarMapper.search(param));
    }

    @Override
    public int insert(Calendar calendar) {
        return calendarMapper.insert(calendar);
    }

    @Override
    public int update(Calendar calendar) {
        return calendarMapper.update(calendar);
    }

    @Override
    public void verify(List<Integer> ids, int roleId) {
        int status = 0;
        if (roleId == RoleEnum.OFFICE_MANAGER.getCode())
            status = 2;
        if (roleId == RoleEnum.COURSE_MANAGER.getCode())
            status = 1;
        calendarMapper.setStatus(ids, status);
    }

    @Override
    public XWPFTemplate export(int calendarId) {
        CalendarData calendarData = renderExportData(calendarId);
        Constant.threadLocal.set(calendarData);
        //读取模版
        ClassPathResource resource = new ClassPathResource("templates/calendar.docx");
        Configure config = Configure.newBuilder().customPolicy("tableData", new TableRenderPolicy()).build();
        return XWPFTemplate.compile(resource.getStream(), config).render(calendarData);
    }

    @Override
    @Transactional
    public void copy(int id, String teaId) {
        Calendar calendar = calendarMapper.selectByPrimaryKey(id);
        CourseGroupSelectParam param = CourseGroupSelectParam.builder()
                .teaId(teaId).courseId(calendar.getCourseId()).semesterId(calendar.getSemesterId())
                .build();
        List<CourseGroup> courseGroupList = courseGroupMapper.select(param);
        ConditionUtil.validateTrue(!courseGroupList.isEmpty()).orElseThrow(() -> new DataException(ErrorEnum.DATA_ERROR));
        CourseGroup courseGroup = courseGroupList.get(0);
        ConditionUtil.validateTrue(courseGroup.getCalendarId() != null).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_FORBIDDEN));
        calendar.setTeaId(teaId);
        calendarMapper.insert(calendar);
    }


    private CalendarData renderExportData(int calendarId) {
        CalendarDataDO calendarDataDO = calendarMapper.exportCalendar(calendarId);
        Optional.ofNullable(calendarDataDO).orElseThrow(() -> new DataException(ErrorEnum.ERROR));
        return new CalendarData(calendarDataDO);
    }
}
