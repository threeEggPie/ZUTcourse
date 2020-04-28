package xyz.kingsword.course;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import xyz.kingsword.course.config.CorsFilter;

@ServletComponentScan(basePackageClasses = {CorsFilter.class})
@EnableCaching
@EnableScheduling
@EnableTransactionManagement//启用事务
@MapperScan("xyz.kingsword.course.dao")
@SpringBootApplication
public class CourseApplication {
    public static void main(String[] args) {
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
        SpringApplication application = new SpringApplication(CourseApplication.class);
        application.run(args);
    }
}
