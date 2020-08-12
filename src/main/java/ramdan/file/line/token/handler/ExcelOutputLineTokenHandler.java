package ramdan.file.line.token.handler;

import lombok.Setter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.StringUtils;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.DataType;
import ramdan.file.line.token.data.TokenDataType;

import java.io.*;

public class ExcelOutputLineTokenHandler implements LineTokenHandler , Closeable {

    @Setter
    private boolean removeTagname=true;
    @Setter
    private int rowAccessWindowSize=100;
    @Setter
    private String sheetName ;

    private int rownumber;

    private SXSSFWorkbook workbook;
    private SXSSFSheet sheet;

    public void ensureWorkbookReady() {
        if(workbook==null) {
            workbook = new SXSSFWorkbook(rowAccessWindowSize);
            if(StringUtils.notEmpty(sheetName)){
                sheet= workbook.createSheet(sheetName);
            }else {
                sheet= workbook.createSheet();
            }
            rownumber =0;
        }
    }

    public void write(File fileOutput) throws IOException{
        try(OutputStream os = new FileOutputStream(fileOutput)){
            workbook.write(os);
        }
    }

    @Override
    public Tokens process(LineToken lineToken) {

        int idxSource = 0;
        int idxTarget = 0;
        int length = lineToken.length();
        TokenDataType tokenDataType;
        if(lineToken instanceof TokenDataType){
            tokenDataType = (TokenDataType) lineToken;
        }else {
            tokenDataType = new TokenDataType() {
                @Override
                public DataType getDataType(int index) {
                    return DataType.STRING;
                }
            };
        }
        if(removeTagname){
            idxSource=1;
        }
        ensureWorkbookReady();
        SXSSFRow row = sheet.createRow(rownumber);
        rownumber++;
        while (idxSource<length){
            if(!lineToken.isEmpty(idxSource)){
                SXSSFCell cell = row.createCell(idxTarget);
                switch (tokenDataType.getDataType(idxSource)){
                    case DOUBLE:
                        cell.setCellValue(lineToken.getDouble(idxSource));
                        cell.setCellType(CellType.NUMERIC);
                        break;
                    default:
                    case STRING:cell.setCellValue(lineToken.get(idxSource)); break;
                }
            }
            idxSource++;
            idxTarget++;
        }
        return lineToken;
    }

    @Override
    public void close() throws IOException {

        workbook.close();

    }
}
