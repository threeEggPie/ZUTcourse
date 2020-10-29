package xyz.kingsword.course.util;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;
import xyz.kingsword.course.service.impl.SemesterServiceImpl;

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
    private static List<Semester> semesterList;

    /**
     * 在静态方法里调用spring注入的方法
     */
    @PostConstruct
    public void init() {
        timeUtil = this;
        semesterList = SemesterServiceImpl.getSemesterList();
    }

    public static List<Semester> getAllSemester() {
        return semesterList;
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

    /**
     * 根据已知学期id获取下一个学期id
     *
     * @param semesterId semesterId
     * @return semesterId+1
     */
    public static String getNextSemester(String semesterId) {
        int semesterInt = Integer.parseInt(semesterId);
        if (semesterInt % 2 == 1) {
            return String.valueOf(semesterInt + 1);
        } else {
            return String.valueOf(semesterInt + 10 + 1000 - 1);
        }
    }

    public static int getNextSemester(int semesterId) {
        if (semesterId % 2 == 1) {
            return semesterId + 1;
        } else {
            return semesterId + 10 + 1000 - 1;
        }
    }

    /**
     * 获取当前学期
     * @return
     */
    public static Semester getNowSemester() {
        return semesterList.parallelStream().filter(v -> v.getStatus() == 0).findFirst().orElseThrow(() -> new DataException(ErrorEnum.NO_SEMESTER));
    }

    /**
     * 获取某年级所有的学期列表
     * 本科八个学期，专科刘六个学期
     *
     * @param grade  2017
     * @param degree 1本科2专科
     * @return list
     */
    public static List<Semester> getGradeSemesterList(int grade, int degree) {
        String semesterId = getFirstSemester(grade);
        Semester semester = semesterList.stream().filter(v -> v.getId().equals(semesterId)).findFirst().orElseThrow(DataException::new);
        int index = semesterList.indexOf(semester);
        return semesterList.subList(index, index + (degree == 0 ? 8 : 6));
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
     * 根据学期名获取学期id
     *
     * @param semesterName eg:2019-2020学年第一学期
     * @return 19201
     */
    public static String getSemesterId(@NonNull String semesterName) {
        char[] chars = semesterName.toCharArray();
        String semesterId;
        try {
            semesterId = StrUtil.builder(5).append(chars[2]).append(chars[3]).append(chars[7]).append(chars[8]).append(chars[12] == '一' ? '1' : '2').toString();
        } catch (Exception e) {
            throw new OperationException("学期名称错误,示例:2019-2020学年第一学期");
        }
        return semesterId;
    }

    /**
     * 返回年级名称
     *
     * @param grade  年级
     * @param degree 0全部，1本科 2专科
     * @return 年级+本科/专科
     */

    public static String getGradeName(int grade, int degree) {
        switch (degree) {
            case 1:
                return StrUtil.builder(7).append(grade).append("级本科").toString();
            case 2:
                return StrUtil.builder(7).append(grade).append("级专科").toString();
            default:
                return StrUtil.builder(7).append(grade).append("级").toString();
        }
    }

    /**
     * 根据年级获取该年级的第一个学期
     *
     * @param grade 2017
     * @return semesterId
     */
    public static String getFirstSemester(int grade) {
        return StrUtil.builder(5).append(grade % 100).append((grade + 1) % 100).append(1).toString();
    }

    /**
     * 获取某年级某学期是第几个学期
     *
     * @param grade      grade
     * @param semesterId semesterId
     * @return int
     */
    public static int getSemesterNum(int grade, String semesterId) {
        int semesterInt = Integer.parseInt(semesterId);
        int startSemester = grade % 100 * 1000 + (grade % 100 + 1) * 10 + 1;
        int count = 1;
        while (semesterInt != startSemester) {
            count++;
            startSemester = getNextSemester(startSemester);
        }
        return count;
    }



}
