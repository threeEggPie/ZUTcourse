package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.TrainingProgramMapper;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.pojo.param.TrainingProgramSearchParam;
import xyz.kingsword.course.service.TrainingProgramService;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TrainingProgramServiceImpl implements TrainingProgramService {
    @Resource
    private TrainingProgramMapper trainingProgramMapper;

    @Override
    @Transactional
    public void insert(TrainingProgram record) {
        trainingProgramMapper.insert(record);
    }

    @Override
    @Transactional
    public void insert(List<TrainingProgram> record) {
        trainingProgramMapper.insertList(record);
    }

    @Override
    public void update(TrainingProgram record) {
        trainingProgramMapper.update(record);
    }

    @Override
    public void delete(List<Integer> idList) {
        trainingProgramMapper.delete(idList);
    }

    @Override
    public PageInfo<TrainingProgram> select(TrainingProgramSearchParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> trainingProgramMapper.select(param));
    }


    /**
     * @param inputStream excel输入流
     */
    @Override
    public List<TrainingProgram> importData(InputStream inputStream) {
        final int startRow = 2;
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new OperationException(ErrorEnum.ERROR_FILE);
        }
        Sheet sheet = workbook.getSheetAt(0);
        resetMergeValue(sheet);

        int len = sheet.getLastRowNum();
        List<TrainingProgram> trainingProgramList = new ArrayList<>(len);
        for (int i = startRow; i < len; i++) {
            TrainingProgram trainingProgram = renderTrainingProgram(sheet.getRow(i));
            trainingProgramList.add(trainingProgram);
//            System.out.println(sheet.getRow(i).getCell(0).getStringCellValue());
        }
        return trainingProgramList;
    }

    /**
     * 培养方案导入取值
     *
     * @param row 行数据
     * @return 每行封装成的培养方案实体
     */
    private TrainingProgram renderTrainingProgram(Row row) {
        System.out.println(row.getCell(0).getStringCellValue());
        return TrainingProgram.builder()
                .type(CourseTypeEnum.get(row.getCell(0).getStringCellValue()).getCode())
                .courseId(row.getCell(2).getStringCellValue())
                .credit((float) row.getCell(4).getNumericCellValue())
                .core(row.getCell(5).getStringCellValue() != null)
                .collegesOrDepartments(row.getCell(6).getNumericCellValue() > 0 ? "院考" : "系考")
                .timeAll((int) row.getCell(7).getNumericCellValue())
                .timeTheory((int) row.getCell(8).getNumericCellValue())
                .timeLab((int) row.getCell(9).getNumericCellValue())
                .timeOther((int) row.getCell(10).getNumericCellValue())
                .timeComputer((int) row.getCell(11).getNumericCellValue())
                .startSemester((int) row.getCell(12).getNumericCellValue())
                .build();
    }

    /**
     * 取合并单元格的值并给每个被合并的cell赋值
     *
     * @param sheet 工作簿
     */
    private void resetMergeValue(Sheet sheet) {
        List<CellRangeAddress> range = sheet.getMergedRegions();
        for (CellRangeAddress cellRangeAddress : range) {
            int rowIndex = cellRangeAddress.getFirstRow();
            int cellIndex = cellRangeAddress.getFirstColumn();
            String value = sheet.getRow(rowIndex).getCell(cellIndex).getStringCellValue();
            for (int j = cellRangeAddress.getFirstRow(); j <= cellRangeAddress.getLastRow(); j++) {
                for (int k = cellRangeAddress.getFirstColumn(); k <= cellRangeAddress.getLastColumn(); k++) {
                    sheet.getRow(j).getCell(k).setCellValue(value);
                }
            }
        }
    }
}
