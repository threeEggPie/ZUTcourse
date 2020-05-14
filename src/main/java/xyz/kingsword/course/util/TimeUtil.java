package xyz.kingsword.course.util;

import cn.hutool.core.lang.Validator;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * 与学期，学年有关的日期util
 *
 * @author wzh
 **/
@Component
public class TimeUtil {

    @Resource
    private SemesterService semesterService;
    private static TimeUtil timeUtil;

    /**
     * 在静态方法里调用spring注入的方法
     */
    @PostConstruct
    public void init() {
        timeUtil = this;
    }


    /**
     * 从数据库查询当前学期及以后的学期，按学期id排序
     *
     * @return eg:[18191,18192,19201]
     */
    public static Semester getNextSemester() {
        List<Semester> semesterList = timeUtil.semesterService.getAllSemester(1, 0).getList();
        return semesterList.parallelStream().filter(v -> v.getStatus() > 0).findFirst().orElseThrow(() -> new DataException(ErrorEnum.NO_SEMESTER));
    }

    public static Semester getNowSemester() {
        List<Semester> semesterList = timeUtil.semesterService.getAllSemester(1, 0).getList();
        return semesterList.parallelStream().filter(v -> v.getStatus() == 0).findFirst().orElseThrow(() -> new DataException(ErrorEnum.NO_SEMESTER));
    }

    /**
     * 根据学期id获取学期名字
     *
     * @param semesterIdString eg:19201
     * @return 2019-2020学年第一学期
     */
    public static String getSemesterName(String semesterIdString) {
        String semesterName;
        try {
            int semesterId = Integer.parseInt(semesterIdString);
            Validator.validateFalse(semesterId <= 10000, "学期参数过大");
            String semester = semesterId % 2 == 1 ? "一" : "二";
            semesterId = semesterId / 10;
            int rear = semesterId % 100;
            int font = semesterId / 100;
            semesterName = "20" + font + "-20" + rear + "学年第" + semester + "学期";
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException("学期id异常：" + semesterIdString);
        }

        return semesterName;
    }

    /**
     * 返回年级名称
     * @param grade 年级
     * @param rb 是否为本科
     * @return 年级+本科/专科
     */

    public static String getGradeName(int grade,boolean rb){
        return grade+"级"+(rb==true?"本科":"专科");
    }
}
