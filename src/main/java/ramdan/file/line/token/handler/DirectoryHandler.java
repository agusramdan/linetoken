package ramdan.file.line.token.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.filter.FilterComplex;
import ramdan.file.line.token.filter.MultiLineTokenFilter;
import ramdan.file.line.token.filter.RegexMatchRule;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DirectoryHandler implements Runnable{

    @Getter
    @Setter
    private Map<String,String> parameters;

    @Getter
    @Setter
    private File inputDirectory;

    @Getter
    @Setter
    private File outputDirectory;

    @Getter
    @Setter
    private ExecutorService executorService;

    @Setter
    @Getter
    private String outputExtension;

    private List<Future> data = new ArrayList<>();

    private boolean ready=false;
    public void prepare() {
        if(ready) return;
        ready=true;
        if(outputExtension==null) {
            String extension = parameters.get("-ox");
            if (extension != null){
                if(!extension.startsWith(".")) extension = "." + extension;
                outputExtension=extension;
            }
        }
    }

    protected void submit( Runnable handler){
        data.add(executorService.submit(handler));
    }

    protected void waitUntilFinish(){
        int i=0;
        while (!data.isEmpty()){
            try {
                data.get(i).get(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            if(data.get(i).isDone()){
                data.remove(i);
            }else {
                i++;
            }
            if(i>=data.size()){
                i=0;
            }
        }
    }
    @Override
    public void run() {
        prepare();
        val fileList = getFilesInput();
        for (File fileInput:fileList) {
            submit(createFileHandler(fileInput));
        }
        waitUntilFinish();
    }

    protected Runnable createFileHandler(File fileInput) {
        throw new NotImplementedException();
    }

    private Map <String,Object> cacheInstance = new HashMap<>();

    private Object getInstance(String c){
        if(c == null || c.isEmpty()) return null;
        Object obj =null;
        if(cacheInstance.containsKey(c)){
            obj = cacheInstance.get(c);
        }
        if(obj== null) {
            try {
                obj = Class.forName(c).newInstance();
                if(obj instanceof FilterComplex ||
                        obj instanceof RegexMatchRule ||
                        obj instanceof MultiLineTokenFilter ||
                        obj instanceof HandlerFactory
                ){
                    cacheInstance.put(c,obj);
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        return obj;
    }


    protected List<File> getFilesInput(){
       return StreamUtils.listFilesRecursive(inputDirectory,  new ExtensionFileFilter(parameters.get("-ix")));
    }
    protected void addFilterHandlers(List<LineTokenHandler> list)  {
        if (!parameters.containsKey("-fcs")) {
            return;
        }
        String cls = parameters.get("-fcs");
        String[] clss = cls.split(",");
        for (String c: clss) {
            Object obj = getInstance(c);
            if(obj!= null) {
                if(obj instanceof FilterComplex){
                    list.add(new FilterComplexLineTokenHandler((FilterComplex)obj));
                }else if(obj instanceof RegexMatchRule){
                    list.add(new RegexLineTokenHandler((RegexMatchRule)obj));
                }else{
                    list.add((LineTokenHandler) obj);
                }
            }
        }
    }
    protected void addMappingHandlers(List<LineTokenHandler> list)  {
        if (!parameters.containsKey("-mcs")) {
            return;
        }
        String cls = parameters.get("-mcs");
        String[] clss = cls.split(",");
        for (String c: clss) {
            Object obj = getInstance(c);
            if(obj!= null) {
                if(obj instanceof LineTokenHandler){
                    list.add((LineTokenHandler) obj);
                }
            }
        }
    }
    public static class ExtensionFileFilter implements FileFilter{
        private String[] extensions;

        public ExtensionFileFilter(String inputExtension) {
            this.extensions = inputExtension!=null? inputExtension.trim().split("\\s*,\\s*"):new String[0];
        }

        public boolean accept(String name){
            if(extensions.length==0) return true;
            for (String str: extensions) {
                if(name.endsWith(str)){
                    return true;
                }
            }
            return false;
        }
        @Override
        public boolean accept(File file) {
            return file.isDirectory() ||
                    (file.isFile() && accept(file.getName()));
        }
    }
}
