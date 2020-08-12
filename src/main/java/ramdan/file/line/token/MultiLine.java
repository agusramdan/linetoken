package ramdan.file.line.token;

import java.util.Collection;

public interface MultiLine extends Tokens{
    Tokens index(int idx);
    int sizeLine();
    void addTo(Collection collection);
}
