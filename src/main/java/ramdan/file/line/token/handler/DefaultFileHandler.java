package ramdan.file.line.token.handler;

import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.listener.LineListener;
import ramdan.file.line.token.listener.LineTokenCallbackLineListener;
import ramdan.file.line.token.listener.TokensCallbackLineListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DefaultFileHandler implements FileHandler<Tokens> ,Runnable{

    private File input;
    private List<LineTokenHandler> list;
    public void setInput(File input) {
        this.input = input;
    }

    public void setList(List<LineTokenHandler> list) {
        this.list = list;
    }

    @Override
    public void process(File input, Callback<Tokens> callback) {
        //OutputLineTokenHandler outputLineTokenHandler = null;
        try{
            DelegateLineTokenHandler handler = new DelegateLineTokenHandler(true,list);
            handler.setNext(callback);
            LineListener listener = new TokensCallbackLineListener(handler);
            if(input != null){
                String fileName=input.getName();
                if(fileName.endsWith(".gz")){
                    StreamUtils.readLineGzip(input,listener);
                }else {
                    StreamUtils.readLine(input,listener);
                }
            }else {
                StreamUtils.readLine(System.in,listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //callback.call(e);
        } finally{
            for (Object o: list) {
                StreamUtils.closeIgnore(o);
                StreamUtils.destroyIgnore(o);
            }
            callback.call(null);
        }

    }

    @Override
    public void run() {
        process(input, new Callback<Tokens>() {
            @Override
            public void call(Tokens o) {

            }
        });
    }
}
