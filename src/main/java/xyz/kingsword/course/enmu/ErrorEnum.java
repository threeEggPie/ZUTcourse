package xyz.kingsword.course.enmu;

import lombok.Getter;

/*
 * 错误信息，命名规范：
 * error单词写后面，用注释对错误进行分类，每类留10个空间
 * */
@Getter
public enum ErrorEnum {

    /**
     * 其他
     */
    ERROR_LOGIN(400, "账号或密码不正确"),
    UN_LOGIN(401, "尚未登陆"),
    NO_AUTH(402, "没有权限执行此操作"),
    ERROR_PARAMETER(403, "参数异常"),
    ERROR_FILE(405, "文件上传异常"),
    OPERATION_FORBIDDEN(406, "操作禁止"),
    OPERATION_TIME_FORBIDDEN(407, "操作未开放"),
    DATA_REPLICATION(408, "数据重复"),

    /**
     * 排课
     */
    DIFFERENT_COURSE(411, "不同课程无法合并"),
    DIFFERENT_TEACHER(412, "不同教师无法合并"),
    SINGLE_DATA(413, "单条数据无法合并"),
    NO_DATA(414, "没有数据"),
    /**
     * 培养方案执行计划
     */
    TRAINING_PROGRAM_ERROR(421, "培养方案未上传"),
    EXECUTION_PLAN_ERROR(422, "执行计划未上传"),
    //    专业，年级，课程，执行学期不同导致
    VERIFY_ERROR(423, "系统内部错误,无法验证"),

    /**
     * 学生、教师、班级管理
     */
    CLASS_NOT_EXISTS(431, "班级不存在"),
    NO_SEMESTER(432, "当前不在学期，或学期未设置"),
    /**
     * 教材相关
     */
    ORDER_REPLICATION(440, "重复订书"),
    ORDERED(441, "已被订购"),


    /**
     * 编码可能导致的问题
     */

    ERROR(500, "系统内部异常"),
    DATA_ERROR(501, "数据库数据异常");

    private Integer code;

    private String msg;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}