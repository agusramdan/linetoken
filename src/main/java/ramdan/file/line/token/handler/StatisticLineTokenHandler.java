package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.LineTokenData;
import ramdan.file.line.token.Statistic;

public class StatisticLineTokenHandler extends DefaultLineTokenHandler{

    private Statistic printStream;
    public StatisticLineTokenHandler(Statistic printStream){
        this.printStream = printStream;
    }

    public LineToken process(LineToken lineToken) {
        printStream.add(lineToken);
        return lineToken;
    }
}
