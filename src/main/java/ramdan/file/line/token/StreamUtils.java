package ramdan.file.line.token;

import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.listener.DefaultLineTokenListener;
import ramdan.file.line.token.listener.DelegateLineListener;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenListener;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StreamUtils {
    public static interface ConfigHandel{

    }
    public static String relative(File directory, File file) {
        Path inputPathParent = directory.toPath();
        Path fileInputPath = file.getParentFile().toPath();
        Path path = inputPathParent.relativize(fileInputPath);
        return Paths.get(path.toString(),file.getName()).toString();
    }

    public static void readLine(File input, LineListener listener) throws IOException {
        try(InputStreamReader isr = new FileReader(input);
            LineNumberReader reader = new LineNumberReader(isr)){
            String str;
            while ((str = reader.readLine())!= null){
                Line line = new LineData(input,reader.getLineNumber(),str);
                listener.event(line);
            }
        }
    }
    public static void readLine(InputStream input, LineListener listener) throws IOException {
        try(InputStreamReader isr = new InputStreamReader(input);
            LineNumberReader reader = new LineNumberReader(isr)){
            String str;
            while ((str = reader.readLine())!= null){
                Line line = new LineData(null,reader.getLineNumber(),str);
                listener.event(line);
            }
        }
    }
    public static void readLine(File input, LineTokenListener listener) throws IOException {
        readLine(input,new DelegateLineListener(listener));
    }
    public static void readLine(InputStream input, LineTokenListener listener) throws IOException {
        readLine(input,new DelegateLineListener(listener));
    }
    public static void flushIgnore(PrintStream closeable)   {
        if(closeable !=null){
            closeable.flush();
        }
    }
    public static void closeIgnore(Closeable closeable)   {
        if(closeable !=null){
            try {
                closeable.close();
            } catch (IOException e) {
                ;
            }
        }
    }

}
