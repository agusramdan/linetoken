package ramdan.file.line.token.handler;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractHandlerFactory  implements HandlerFactory{

    protected Map<String,String> parameters = new HashMap<>();
    private boolean ready=false;

    public void setParameters(Map<String,String> parameters){
        this.parameters=parameters;
    }

    public void prepare() {
        if(ready) return;
        ready = true;
    }
}
