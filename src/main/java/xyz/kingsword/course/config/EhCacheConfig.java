package xyz.kingsword.course.config;

import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.param.TeacherSelectParam;

import javax.annotation.Resource;
import java.util.List;

/**
 * 缓存初始化
 */
@Configuration
public class EhCacheConfig {
    @Resource
    private org.springframework.cache.CacheManager springCacheManager;
    @Resource
    private net.sf.ehcache.CacheManager ehcacheCacheManager;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private TeacherMapper teacherMapper;

    @Bean(name = "config")
    public Cache config() {
        return springCacheManager.getCache("config");
    }

    @Bean(name = "book")
    public Cache book() {
        Cache cache = springCacheManager.getCache("book");
//        net.sf.ehcache.Cache cache = ehcacheCacheManager.getCache("book");
//        springCacheManager.getCache("book")
        net.sf.ehcache.Cache book1 = ehcacheCacheManager.getCache("book");
        int size = (int) book1.getSize();
        if (size == 0) {
            List<Book> bookList = bookMapper.selectAll();
            for (Book book : bookList) {
                cache.put(book.getId(), book);
            }
        }
        return cache;
    }

    @Bean(name = "teacher")
    public Cache teacher() {
        Cache cache = springCacheManager.getCache("teacher");
        int size = ehcacheCacheManager.getCache("teacher").getSize();
        if (size == 0) {
            List<Teacher> teacherList = teacherMapper.select(TeacherSelectParam.builder().pageSize(0).build());
            for (Teacher teacher : teacherList) {
                cache.put(teacher.getId(), teacher);
            }
        }
        return cache;
    }
}
