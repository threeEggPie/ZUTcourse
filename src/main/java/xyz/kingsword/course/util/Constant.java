package xyz.kingsword.course.util;

import xyz.kingsword.course.service.calendarExport.CalendarData;

public class Constant {
    public final static ThreadLocal<CalendarData> threadLocal = new ThreadLocal<>();

    public static final String DEFAULT_PASSWORD = "123456";

    public static final String SESSION_USERINFO = "user";

    public static final String SESSION_STUDENT_USERINFO = "student";
}
