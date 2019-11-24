package ramdan.file.line.token;

public interface LineTokensHolder extends Tokens{
    LineToken getStartBlock();
    void setStartBlock(LineToken lineToken);
    LineToken getEndBlock();
    void setEndBlock(LineToken lineToken);

    LineToken headContent();
    LineToken tailContent();
    void reset();
    boolean hashNextContent();
    LineToken nextContent();
    boolean addContent(LineToken lineToken);
    State getState();
    Sort getSort();
    Sort mergeContent(LineTokensHolder left);
    enum State{
        MEMORY, DISK
    }
    enum Sort{
        UNSORT,SORTED
    }



}
