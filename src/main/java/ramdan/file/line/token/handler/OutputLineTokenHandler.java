package ramdan.file.line.token.handler;

import java.io.Closeable;
import java.io.File;

public interface OutputLineTokenHandler extends LineTokenHandler, Closeable {
    void setTagdelimiter(String tagdelimiter);
    void setTokendelimiter(String tokendelimiter);
    void setPrintLine(boolean printLine);
    void setExtension(String extension);
    void setFileInput(File input);
    void setBaseDirectoryInput(File baseInput);
    void setFileOutput(File input);
    void setBaseDirectoryOutput(File baseInput);
    void flush();
}
