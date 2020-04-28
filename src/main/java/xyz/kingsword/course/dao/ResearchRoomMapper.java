package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.ResearchRoom;

import java.util.List;

public interface ResearchRoomMapper {
    int insert(ResearchRoom record);

    int delete(String name);

    int update(ResearchRoom researchRoom);

    List<ResearchRoom> select();

}