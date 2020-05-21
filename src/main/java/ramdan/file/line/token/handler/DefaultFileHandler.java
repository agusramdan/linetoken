package ramdan.file.line.token.handler;

import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.StreamUtils;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.callback.Callback;
import ramdan.file.line.token.listener.TokensCallbackLineListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Setter
public class DefaultFileHandler implements FileHandler<Tokens> ,Runnable{

    private File input;
    private List<LineTokenHandler> list;
    private LineTokenConverter converter;

    @Override
    public void process(File input, Callback<Tokens> callback) {
        try{
            DelegateLineTokenHandler handler = new DelegateLineTokenHandler(true,list);
            handler.setNext(callback);
            val listener = new TokensCallbackLineListener(handler,converter);
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
