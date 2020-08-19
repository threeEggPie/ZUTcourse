package xyz.kingsword.course.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import xyz.kingsword.course.service.calendarExport.CalendarData;

@Data
@Configuration
@ConfigurationProperties(prefix = "config")
public class Constant {
    public final static ThreadLocal<CalendarData> threadLocal = new ThreadLocal<>();

    public static final String DEFAULT_PASSWORD = "e10adc3949ba59abbe56e057f20f883e";

    @Value("purchaseStatus")
    private String purchaseStatus;
}
