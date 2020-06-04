package xyz.kingsword.course.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.kingsword.course.vo.StudentVo;
import xyz.kingsword.course.vo.TeacherVo;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * 采用aes对称加密 密钥写死在代码中不要动即可
 */
public class UserUtil {
    private static final byte[] key = new byte[]{49, 50, 25, 52, 63, 54, 55, 32, 57, 48, 49, 75, 51, 52, 20, 2};

    private static final SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);

    /**
     * 加密获得密文
     */
    public static String encrypt(String content) {
        return aes.encryptHex(content);
    }

//    /**
//     * 解密获得明文
//     */
//    private static String decrypt(String content) {
//        return aes.decryptStr(content);
//    }

    /**
     * 明文加密之后与数据库数据进行对比
     */
    public static boolean validPassword(String password, String passwordDb) {
        return StrUtil.equals(encrypt(password), passwordDb);
    }

    public static User getUser() {
        User user = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            HttpSession session = request.getSession();
            user = (User) session.getAttribute("user");
            Optional.ofNullable(user).orElseThrow(AuthException::new);
        }
        return user;
    }

    public static StudentVo getStudent() {
        HttpSession session = getSession();
        StudentVo studentVo = (StudentVo) session.getAttribute("student");
        Optional.ofNullable(studentVo).orElseThrow(AuthException::new);
        return studentVo;
    }

    public static TeacherVo getTeacher() {
        HttpSession session = getSession();
        TeacherVo teacherVo = (TeacherVo) session.getAttribute("teacher");
        Optional.ofNullable(teacherVo).orElseThrow(AuthException::new);
        return teacherVo;
    }

    public static boolean isStudent() {
        HttpSession session = getSession();
        return session.getAttribute("student") != null;
    }

    private static HttpSession getSession() {
        HttpSession session = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            session = request.getSession();
        }
        Optional.ofNullable(session).orElseThrow(() -> new AuthException(ErrorEnum.UN_LOGIN));
        return session;
    }
}
