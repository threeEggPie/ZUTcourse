package xyz.kingsword.course.enmu;

import lombok.Data;
import lombok.Getter;

@Getter
public enum SpecialityEnum {
    SOFTWARE_ENGINEERING(1,"软件工程",0),
    DATA_SCIENCE(2,"数据与科学",0),
    JUNIOR_COLLEGE(11,"专科",0),
    OUTSTANDING_CLASS(3,"软件工程卓越班",1),
    Financial_CLASS(4,"金融软件开发",1),
    NETWORK_CLASS(5,"网络软件开发",1),
    BIG_DATA_CLASS(6,"大数据软件开发",1),
    MOBILE_INTERNET_CLASS(7,"互联网软件开发",1),
    INTERNET_CLASS(8,"互联网软件开发",1),
    SOFTWARE_TEST_CLASS(9,"软件测试",11),
    SOFTWARE_TECHNOLOGY_CLASS(10,"软件技术",11);

    private int code;
    private String content;
    private int parent;

    SpecialityEnum(int code, String content, int parent) {
        this.code = code;
        this.content = content;
        this.parent = parent;
    }
}
