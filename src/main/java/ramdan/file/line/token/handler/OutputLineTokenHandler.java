package ramdan.file.line.token.handler;

import java.io.Closeable;
import java.io.File;

public interface OutputLineTokenHandler extends LineTokenHandler, Closeable {
    void setFileInput(File input);
    void setBaseDirectoryInput(File baseInput);
    void setFileOutput(File input);
    void setBaseDirectoryOutput(File baseInput);
    void flush();
}
