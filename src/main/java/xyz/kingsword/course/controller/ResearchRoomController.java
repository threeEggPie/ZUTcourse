package xyz.kingsword.course.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.pojo.ResearchRoom;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.service.ResearchRoomService;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/researchRoom")
@RestController
@Api(tags = "教研室操作接口")
public class ResearchRoomController {
    @Resource
    private ResearchRoomService researchroomService;


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("增")
    public Result insert(@RequestBody ResearchRoom researchroom) {
        researchroomService.insert(researchroom);
        return new Result();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("改")
    public Result update(@RequestBody ResearchRoom researchRoom) {
        researchroomService.update(researchRoom);
        return new Result<>();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation("删")
    public Result delete(String name) {
        researchroomService.delete(name);
        return new Result<>();
    }

    @GetMapping("/select")
    @ApiOperation("查")
    public Result select() {
        List<ResearchRoom> researchRoomList = researchroomService.select();
        return new Result<>(researchRoomList);
    }
}
