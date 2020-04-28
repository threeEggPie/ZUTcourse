package xyz.kingsword.course.service.calendarExport;

import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.MiniTableRenderPolicy;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import java.util.List;

public class TableRenderPolicy extends DynamicTableRenderPolicy {
    /**
     * 数据渲染起始行
     */
    private final int startRow = 1;

    @Override
    @SuppressWarnings("unchecked")
    public void render(XWPFTable table, @NonNull Object data) {
        // 教学内容循环渲染
        List<RowRenderData> rowRenderDataList = (List<RowRenderData>) data;
//        MiniTableRenderPolicy.Helper.renderRow(table, 1, rowRenderDataList.get(1));
        for (int i = 0; i < rowRenderDataList.size(); i++) {
            XWPFTableRow insertNewTableRow = table.insertNewTableRow(startRow + i);
            for (int j = 0; j < 6; j++) {
                XWPFTableCell cell = insertNewTableRow.createCell();
//                水平居中
                if (j != 3 && j != 5) {
                    CTTc cttc = cell.getCTTc();
                    CTTcPr ctPr = cttc.addNewTcPr();
                    ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
                    cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
                }
//                垂直居中
                cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
            }
            // 渲染单行数据
            MiniTableRenderPolicy.Helper.renderRow(table, i + startRow, rowRenderDataList.get(i));
        }
    }

}