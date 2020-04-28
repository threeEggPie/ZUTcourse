package xyz.kingsword.course.pojo;

import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wzh
 */
@Data
public class User implements Serializable {
    /**
     * 对应student表或teacher表的id
     */
    @Size(max = 4)
    private String username;

    private String password;

    private String name;

    /**
     * 角色，存json
     * 0管理1教师2教学部3学生4专业负责人5教研室主任
     */
    private String role;

    private Date entryTime;

    private String phone;

    private String email;

    private Integer currentRole;

    private String currentRoleName;

    private static final long serialVersionUID = 1L;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {
    }
}