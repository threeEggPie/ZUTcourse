package xyz.kingsword.course.config;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
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
public class CacheConfig {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private TeacherMapper teacherMapper;

    @Bean(name = "book")
    public Cache<Integer, Book> book() {
        List<Book> bookList = bookMapper.selectAll();
        LFUCache<Integer, Book> cache = CacheUtil.newLFUCache(bookList.size(), 24 * 60 * 60 * 1000L);
        bookList.forEach(v -> cache.put(v.getId(), v));
        return cache;
    }

    @Bean(name = "teacher")
    public Cache<String, Teacher> teacher() {
        List<Teacher> teacherList = teacherMapper.select(TeacherSelectParam.builder().pageSize(0).build());
        LFUCache<String, Teacher> cache = CacheUtil.newLFUCache(teacherList.size(), 24 * 60 * 60 * 1000L);
        teacherList.forEach(v -> cache.put(v.getId(), v));
        return cache;
    }
}
