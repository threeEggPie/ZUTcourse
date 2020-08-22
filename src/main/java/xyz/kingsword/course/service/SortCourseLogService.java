package xyz.kingsword.course.service;

import xyz.kingsword.course.pojo.SortCourseLog;
import xyz.kingsword.course.pojo.param.SortCourseUpdateParam;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface SortCourseLogService {
    void addLog(SortCourseUpdateParam param, HttpSession session);

    List<SortCourseLog> getLogs(Integer sortCourseId);
}
