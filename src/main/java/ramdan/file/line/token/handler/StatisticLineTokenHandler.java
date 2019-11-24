package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.Statistic;

public class StatisticLineTokenHandler extends DefaultLineTokenHandler{

    private Statistic printStream;
    public StatisticLineTokenHandler(Statistic printStream){
        this.printStream = printStream;
    }

    public Tokens process(LineToken lineToken) {
        printStream.add(lineToken);
        return lineToken;
    }
}
