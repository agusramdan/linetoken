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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class StreamUtils {

    public static String relative(File directory, File file) {
        Path inputPathParent = directory.toPath();
        Path fileInputPath = file.getParentFile().toPath();
        Path path = inputPathParent.relativize(fileInputPath);
        return Paths.get(path.toString(),file.getName()).toString();
    }
    public static void readLineGzip(File input, LineListener listener) throws IOException {
        try(InputStream fis = new FileInputStream(input);
            GZIPInputStream gzipis = new GZIPInputStream(fis);
            InputStreamReader isr = new InputStreamReader(gzipis);
            LineNumberReader reader = new LineNumberReader(isr)){
            String str;
            while ((str = reader.readLine())!= null){
                Line line = new LineData(input,reader.getLineNumber(),str);
                listener.event(line);
            }
            listener.event(new LineData(input,reader.getLineNumber(),null));
        }
    }
    public static void readLine(File input, LineListener listener) throws IOException {
        try(InputStreamReader isr = new FileReader(input);
            LineNumberReader reader = new LineNumberReader(isr)){
            String str;
            while ((str = reader.readLine())!= null){
                Line line = new LineData(input,reader.getLineNumber(),str);
                listener.event(line);
            }
            listener.event(new LineData(input,reader.getLineNumber(),null));
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
            listener.event(new LineData(null,reader.getLineNumber(),null));
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
    public static void copy(File source, File dest) throws IOException {
        copy(source,dest,false);
    }

    public static void copy(File source, File dest,boolean append) throws IOException {
        try (
                InputStream is = new FileInputStream(source);
                OutputStream os = new FileOutputStream(dest,append);
        ){
            copy(is,os);
        }
    }

    public static void copy(InputStream is, File dest,boolean append) throws IOException {
        try (
                OutputStream os = new FileOutputStream(dest,append);
        ){
            copy(is,os);
        }
    }

    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
    }

    public static List<File> listFilesRecursive(File file, FileFilter fileFilter){
        List<File> fileList = new ArrayList<>();
        if(file.isDirectory()){
            File[] files=file.listFiles(fileFilter);
            for (File f:files) {
                fileList.addAll(listFilesRecursive(f,fileFilter));
            }
        }else
        if(file.isFile()){
            fileList.add(file);
        }
        return fileList;
    }

}
