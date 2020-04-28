package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.ExecutionPlanMapper;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.param.ExecutionPlanSearchParam;
import xyz.kingsword.course.service.ExecutionPlanService;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ExecutionPlanServiceImpl implements ExecutionPlanService {
    @Resource
    private ExecutionPlanMapper executionPlanMapper;


    @Override
    @Transactional
    public void insert(List<ExecutionPlan> executionPlanList) {
        int flag = executionPlanMapper.insert(executionPlanList);
        log.debug("ExecutionPlan insert ,{}", flag);
    }

    @Override
    public void insert(ExecutionPlan executionPlan) {
        executionPlanMapper.insert(Collections.singletonList(executionPlan));
    }

    @Override
    @Transactional
    public void update(ExecutionPlan executionPlan) {
        executionPlanMapper.updateByPrimaryKey(executionPlan);
    }

    @Override
    public void delete(List<Integer> idList) {
        executionPlanMapper.delete(idList);
    }

    @Override
    public PageInfo<ExecutionPlan> select(ExecutionPlanSearchParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> executionPlanMapper.select(param));
    }

    @Override
    public List<ExecutionPlan> importData(InputStream inputStream) {
        final int startRow = 3;
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OperationException(ErrorEnum.ERROR_FILE);
        }
        Sheet sheet = workbook.getSheetAt(0);

        int len = sheet.getLastRowNum();
        List<ExecutionPlan> executionPlanList = new ArrayList<>(len);
        for (int i = startRow; i < len; i++) {
            ExecutionPlan executionPlan = renderExecutePlan(sheet.getRow(i));
            executionPlanList.add(executionPlan);
        }
        return executionPlanList;
    }

    private ExecutionPlan renderExecutePlan(Row row) {
        final String semesterId = TimeUtil.getNowSemester().getId();
        return ExecutionPlan.builder()
                .nature(row.getCell(2).getStringCellValue().substring(0, 2))
                .courseId(row.getCell(3).getStringCellValue())
                .courseName(row.getCell(4).getStringCellValue())
                .credit((float) row.getCell(5).getNumericCellValue())
                .timeTheory((float) row.getCell(7).getNumericCellValue())
                .timeLab((float) row.getCell(8).getNumericCellValue())
                .timeComputer((float) row.getCell(9).getNumericCellValue())
                .timeOther((float) row.getCell(10).getNumericCellValue())
                .timeWeek((float) row.getCell(11).getNumericCellValue())
                .startSemester(startSemesterConvert(row.getCell(12).getStringCellValue()))
                .examinationWay(row.getCell(13).getStringCellValue())
                .semesterId(semesterId)
                .status(0)
                .build();

    }

    private int startSemesterConvert(String item) {
        switch (item.charAt(0)) {
            case '一':
                return 1;
            case '二':
                return 2;
            case '三':
                return 3;
            case '四':
                return 4;
            case '五':
                return 5;
            case '六':
                return 6;
            case '七':
                return 7;
            case '八':
                return 8;
            default:
                return 0;
        }
    }

}
