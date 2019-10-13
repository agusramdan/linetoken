package ramdan.file.line.token.handler;

import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenHandlerLineListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultFileHandler implements FileHandler ,Runnable{


    private File input;
    private List<LineTokenHandler> list;
    public void setInput(File input) {
        this.input = input;
    }

    public void setList(List<LineTokenHandler> list) {
        this.list = list;
    }

    @Override
    public void process(File input, Callback callback) {
        OutputLineTokenHandler outputLineTokenHandler = null;
        try{
            LineTokenHandler handler = new DelegatedLineTokenHandler(true,list);
            LineListener listener = new LineTokenHandlerLineListener(handler);
            if(input != null){
                String fileName=input.getName();
                if(fileName.endsWith(".gz")||fileName.endsWith(".zip")){
                    StreamUtils.readLineGzip(input,listener);
                }else {
                    StreamUtils.readLine(input,listener);
                }
            }else {
                StreamUtils.readLine(System.in,listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.call(e);
        } finally{
            if(outputLineTokenHandler != null){
                outputLineTokenHandler.flush();
            }
            StreamUtils.closeIgnore(outputLineTokenHandler);
            callback.call(null);
        }

    }

    @Override
    public void run() {
        process(input, new Callback() {
            @Override
            public void call(Object o) {

            }
        });
    }
}
