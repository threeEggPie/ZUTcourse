package xyz.kingsword.course;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.style.TableStyle;
import org.junit.Before;
import org.junit.Test;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

public class PaymentExample {

    PaymentData datas = new PaymentData();

    TableStyle rowStyle = new TableStyle();

    @Before
    public void init() {
        DetailData detailTable = new DetailData();
        RowRenderData good = RowRenderData.build("4", "墙纸", "书房+卧室", "1500", "/", "400", "1600");
        good.setRowStyle(rowStyle);
        List<RowRenderData> goods = Arrays.asList(good, good, good);
        RowRenderData labor = RowRenderData.build("油漆工", "2", "200", "400");
        labor.setRowStyle(rowStyle);
        List<RowRenderData> labors = Arrays.asList(labor, labor, labor, labor);
        detailTable.setGoods(goods);
        detailTable.setLabors(labors);
        datas.setDetailTable(detailTable);
    }

    @Test
    public void testResumeExample() throws Exception {
        Configure config = Configure.newBuilder().customPolicy("detail_table", new DetailTablePolicy()).build();
        XWPFTemplate template = XWPFTemplate.compile("src/test/resources/付款通知书.docx", config).render(datas);
        FileOutputStream out = new FileOutputStream("C:\\Users\\wang1\\Desktop\\out_付款通知书.docx");
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }

}
