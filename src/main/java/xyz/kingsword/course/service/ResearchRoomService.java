package xyz.kingsword.course.service;

import xyz.kingsword.course.pojo.ResearchRoom;

import java.util.List;

public interface ResearchRoomService {
    void insert(ResearchRoom researchRoom);

    void delete(String name);

    void update(ResearchRoom researchRoom);

    List<ResearchRoom> select();
}
