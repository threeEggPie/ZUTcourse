package xyz.kingsword.course.service.impl;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.dao.ResearchRoomMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.pojo.ResearchRoom;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.service.ResearchRoomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResearchRoomServiceImpl implements ResearchRoomService {

    @Autowired
    private ResearchRoomMapper researchroomMapper;
    @Autowired
    private TeacherMapper teacherMapper;


    @Override
    public void insert(ResearchRoom researchRoom) {
        researchroomMapper.insert(researchRoom);
    }

    @Override
    public void delete(String name) {
        researchroomMapper.delete(name);
    }

    @Override
    public void update(ResearchRoom researchRoom) {
        researchroomMapper.update(researchRoom);
    }

    @Override
    public List<ResearchRoom> select() {
        List<ResearchRoom> researchRoomList = researchroomMapper.select();
        List<String> nameList = researchRoomList.stream().map(ResearchRoom::getName).collect(Collectors.toList());
        List<Teacher> teacherList = teacherMapper.getByResearchRoom(nameList);
        List<TeacherVo> teacherVoList = new ArrayList<>(teacherList.size());
        teacherList.parallelStream().forEach(v -> {
            TeacherVo teacherVo = new TeacherVo();
            BeanUtils.copyProperties(v, teacherVo);
            teacherVoList.add(teacherVo);
        });

        Map<String, List<TeacherVo>> map =teacherVoList
                .parallelStream()
                .collect(Collectors.groupingBy(TeacherVo::getResearchRoom));
        researchRoomList.forEach(v -> v.setTeacherVoList(map.get(v.getName())));
        return researchRoomList;
    }
}
