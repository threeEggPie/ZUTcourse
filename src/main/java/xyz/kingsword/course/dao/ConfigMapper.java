package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ConfigMapper {
    @Update("update config set declare_status=#{flag}")
    void setDeclareStatus(boolean flag);

    @Update("update config set purchase_status=#{flag}")
    void setPurchaseStatus(boolean flag);

    @Select("select declare_status from config")
    boolean selectDeclareStatus();

    @Select("select purchase_status from config")
    boolean selectPurchaseStatus();
}
