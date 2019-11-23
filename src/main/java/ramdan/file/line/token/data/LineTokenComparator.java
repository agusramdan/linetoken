package ramdan.file.line.token.data;

import ramdan.file.line.token.LineToken;

import java.util.Comparator;

public class LineTokenComparator implements Comparator<LineToken> {
    private int[] idxs;

    public LineTokenComparator(int ... idxs) {
        this.idxs = idxs;
    }

    @Override
    public int compare(LineToken left, LineToken right) {
        return left.compareTo(right,idxs) ;
    }
}
