package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.MultiLine;

import java.util.List;

public class ListLineTokenHandler extends DefaultLineTokenHandler {

    private List<LineToken> lineTokenList;

    public ListLineTokenHandler(List<LineToken> lineTokenList) {
        this.lineTokenList = lineTokenList;
    }

    @Override
    public LineToken process(LineToken lineToken) {
        if(lineToken instanceof MultiLine){
            ((MultiLine) lineToken).addTo(lineTokenList);
        }else {
            lineTokenList.add(lineToken);
        }
        return lineToken;
    }
}
