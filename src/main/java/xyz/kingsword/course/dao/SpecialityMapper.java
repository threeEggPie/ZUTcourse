package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.Speciality;

import java.util.List;

/**
 * @author 余建青
 */
public interface SpecialityMapper {
    List<Speciality> findClassBySpeciality(int pareSpeciality);

    List<Speciality> selectAll();

    /**
     * 只有方向不含专业
     *
     * @return
     */
    List<Speciality> selectSpeciality();

}
