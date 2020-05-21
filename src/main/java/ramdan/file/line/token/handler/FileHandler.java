package ramdan.file.line.token.handler;

import ramdan.file.line.token.callback.Callback;


import java.io.File;

public interface FileHandler<T> {
    void process(File input , Callback<T> callback);
}
