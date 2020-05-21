package ramdan.file.line.token;

import lombok.extern.slf4j.Slf4j;
import ramdan.file.line.token.data.LineData;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.listener.DelegateLineListener;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenListener;

import javax.security.auth.Destroyable;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Slf4j
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
            listener.event(new LineData(input,reader.getLineNumber()));
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
            listener.event(new LineData(input,reader.getLineNumber()));
        }catch (Exception e){
            e.printStackTrace();
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
            listener.event(new LineData(null,reader.getLineNumber()));
        }catch (Exception e){
            e.printStackTrace();
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
    public static void closeIgnore(Object object)   {
        if(object instanceof Closeable){
            closeIgnore((Closeable) object);
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
    public static void destroyIgnore(Object object)   {
        if(object instanceof Destroyable){
            destroyIgnore((Destroyable) object);
        }
    }
    public static void destroyIgnore(Destroyable destroyable)   {
        if(destroyable !=null){
            try {
                destroyable.destroy();
            } catch (Exception e) {
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
        if(file==null) return fileList;
        if(file.isDirectory()){
            File[] files=file.listFiles(fileFilter);
            if (files == null) return fileList;
            for (File f:files) {
                fileList.addAll(listFilesRecursive(f,fileFilter));
            }
        }else
        if(file.isFile()){
            fileList.add(file);
        }
        return fileList;
    }

    public static <E> E next(Iterator<E> iterator){
        return (iterator.hasNext() ? iterator.next() : null);
    }
    public static <T>void mergeSort(Iterator<T> iter1, Iterator<T> iter2, Comparator<T> comparator, Callback<T> merged){
        T value1 = (iter1.hasNext() ? iter1.next() : null);
        T value2 = (iter2.hasNext() ? iter2.next() : null);

        // Loop while values remain in either list
        while (value1 != null || value2 != null) {

            // Choose list to pull value from
            if (value2 == null || (value1 != null && comparator.compare(value1,value2) <= 0)) {

                // Add list1 value to result and fetch next value, if available
                merged.call(value1);
                value1 = (iter1.hasNext() ? iter1.next() : null);
                if(value1==null) {
                    log.debug("data 1 empty");
                }
            } else {

                // Add list2 value to result and fetch next value, if available
                merged.call(value2);
                value2 = (iter2.hasNext() ? iter2.next() : null);
                if(value2==null){
                    log.debug("data 2 empty");
                }

            }
        }
    }
    public static <T>void mergeSort(Iterable<T> iter1, Iterable<T> iter2, Comparator<T> comparator, Callback<T> merged){
        mergeSort(iter1.iterator(),iter2.iterator(),comparator,merged);
    }

    public static <T>void copy(Iterator<T> iterator, Callback<T> merged){
        // Loop while values remain in either list
        while (iterator.hasNext()) {
            merged.call(iterator.next());
        }
        log.debug("done");
    }
    public static <T>void copy(Iterable<T> i, Callback<T> merged){
        // Loop while values remain in either list
        for (T t : i) {
            merged.call(t);
        }
        log.debug("done");
    }
}
