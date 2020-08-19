package xyz.kingsword.course;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.kingsword.course.service.impl.SortServiceImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SortCourseExcel {
    @Autowired
    private SortServiceImpl sortService;

    @Test
    public void excelImport() throws IOException {
//      '  String a = "C:\\Users\\wang1\\Desktop\\sortCourse.xls";
//        List<SortCourse> sortCourseList = sortService.excelImport(new FileInputStream(a));
//        sortCourseList.forEach(System.out::println);'
    }


    @Test
    public void excelExport() throws IOException {
        File file = new File("C:\\Users\\wang1\\Desktop\\text.xls");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        OutputStream outputStream = new FileOutputStream(file);
        Workbook workbook = sortService.excelExport("20211");
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
