package xyz.kingsword.course.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.dao.ClassesMapper;
import xyz.kingsword.course.dao.SpecialityMapper;
import xyz.kingsword.course.pojo.Speciality;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpecialityUtil {
    private static Map<Integer, Speciality> specialityMap;
    private static Map<String, Integer> classSpecialityMap;
    @Autowired
    private SpecialityMapper specialityMapper;
    @Autowired
    private ClassesMapper classesMapper;

    @PostConstruct
    public void init() {
        specialityMap = specialityMapper.selectSpeciality()
                .stream()
                .collect(Collectors.toMap(Speciality::getId, v -> v));

        classSpecialityMap = classesMapper.selectAll().stream().collect(Collectors.toMap(v -> v.getClassname(), v -> v.getSpeciality()));

    }

    public static Set<Integer> getAllId() {
        return specialityMap.keySet();
    }

    public static String getSpecialityName(int id) {
        return specialityMap.get(id).getName();
    }

    public static String getSpecialityName(String className) {
        return specialityMap.get(classSpecialityMap.get(className)).getName();
    }

    public static Speciality getSpeciality(int id) {
        return specialityMap.get(id);
    }

    public static Speciality getSpeciality(String className) {
        return specialityMap.get(classSpecialityMap.get(className));
    }
}
