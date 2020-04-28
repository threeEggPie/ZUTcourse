package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.User;

@Mapper
public interface UserMapper {

    User login(String username);

    User loginOnUsername(@Param("username") String username);

    int resetPasswordTeacher(@Param("username") String username, @Param("password") String password);

    int resetPasswordStudent(@Param("username") String username, @Param("password") String password);
}