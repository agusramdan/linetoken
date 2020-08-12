package ramdan.file.line.token.listener;

import ramdan.file.line.token.LineToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class FileOutputLineTokenListener implements LineTokenListener{

    private File file;
    private PrintStream printStream;
    public FileOutputLineTokenListener(File file) {
        this.file = file;
    }

    @Override
    public synchronized void event(LineToken lineToken) {
        ensureOpen();
        lineToken.println(printStream);
    }

    private void ensureOpen() {
        if(printStream==null){
            try {
                printStream = new PrintStream(new FileOutputStream(file,true),true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
