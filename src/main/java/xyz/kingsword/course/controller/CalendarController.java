package xyz.kingsword.course.controller;

import com.deepoove.poi.XWPFTemplate;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.CalendarSelectParam;
import xyz.kingsword.course.service.CalendarService;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.util.Constant;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/calendar")
@Api(tags = "教学日历接口")
public class CalendarController {
    @Resource
    private CalendarService calendarService;

    /**
     * 根据id返回教学日历
     *
     * @param id calendar id
     */
    @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
    @ApiOperation("根据id返回教学日历")
    public Result<Object> getCalendarById(int id) {
        Calendar calendar = calendarService.selectOne(id);
        return new Result<>(calendar);
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增")
    public void insert(@RequestBody Calendar calendar) {
        calendarService.insert(calendar);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("更新接口，仅更新teachingContent")
    public Result<Object> update(@RequestBody Calendar calendar) {
        calendarService.update(calendar);
        return Result.emptyResult();
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation("多条件查询接口")
    public Result<Object> search(@RequestBody CalendarSelectParam param) {
        PageInfo<Calendar> pageInfo = calendarService.search(param);
        return new Result<>(pageInfo);
    }


    /**
     * 教学日历导出接口
     *
     * @param id calendarId
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation("教学日历导出")
    public void export(int id, HttpServletResponse response) throws IOException {
        XWPFTemplate template = calendarService.export(id);
        CalendarData calendarData = Constant.threadLocal.get();
        Constant.threadLocal.remove();
        String fileName = calendarData.getCourseName() + "教学日历-" + calendarData.getTeaName() + "-" + calendarData.getSemesterId() + ".docx";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msword;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream out = response.getOutputStream();
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }

    /**
     * 教学日历审核接口
     *
     * @param calendarIdList 批量审核，传入id list
     */
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ApiOperation("教学日历审核接口")
    public void verify(@RequestBody List<Integer> calendarIdList, HttpSession session) {
        User user = (User) session.getAttribute("user");
        calendarService.verify(calendarIdList, user.getCurrentRole());
    }

    /**
     * 教学日历复制接口
     */
    @Role(RoleEnum.TEACHER)
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value = "被复制的教学日历id")
    public void copy(int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        calendarService.copy(id, user.getUsername());
    }
}
