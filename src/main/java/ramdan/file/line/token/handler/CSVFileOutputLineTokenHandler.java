package ramdan.file.line.token.handler;

import lombok.Setter;
import lombok.val;
import org.apache.commons.lang.StringEscapeUtils;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;

public class CSVFileOutputLineTokenHandler extends DefaultOutputLineTokenHandler {

    @Setter
    private boolean removeTagname=true;


    @Override
    public Tokens process(LineToken lineToken) {

        int idxSource = 0;
        int idxTarget = 0;
        int length = lineToken.length();
        String[] tokens;

        if(removeTagname){
            idxSource=1;
            tokens=new String[length-1];
        }else {
            tokens=new String[length];
        }
        while (idxSource<length){
            tokens[idxTarget]=StringEscapeUtils.escapeCsv(lineToken.get(idxSource));
            idxSource++;
            idxTarget++;
        }
        super.process(LineTokenData.newInstance(lineToken.getFileName(),",",lineToken.getStart(),tokens));

        return lineToken;
    }
}
