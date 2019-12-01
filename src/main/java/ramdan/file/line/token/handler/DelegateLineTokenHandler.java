package ramdan.file.line.token.handler;

import lombok.Setter;
import lombok.val;
import ramdan.file.line.token.*;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.data.MultiLineData;
import ramdan.file.line.token.data.Traceable;

import java.util.ArrayList;
import java.util.List;

public class DelegateLineTokenHandler implements CallbackChain<Tokens,Tokens>  {
    private AdapterLineTokensHandler tail;
    private AdapterLineTokensHandler head;
    private Callback<Tokens> next;

    private final AdapterLineTokensHandler[] handlers;
    private final boolean cleanTrace ;
    private static AdapterLineTokensHandler[] warp(LineTokenHandler ... handlers){
        if(handlers==null||handlers.length==0) return new AdapterLineTokensHandler[0];
        List<AdapterLineTokensHandler> list = new ArrayList<>();
        AdapterLineTokensHandler pre,current;
        pre=null;
        for (LineTokenHandler h :handlers) {
            if(h!=null){
                current=new AdapterLineTokensHandler(h);
                list.add(current);
                if(pre!=null){
                    pre.setNext(current);
                }
                pre = current;
            }
        }
        return list.toArray(new AdapterLineTokensHandler[list.size()]);
    }

    public DelegateLineTokenHandler(boolean cleanTrace, LineTokenHandler ... handlers) {
        this.handlers = warp(handlers);
        this.cleanTrace =cleanTrace;
        head = this.handlers[0];
        tail = this.handlers[this.handlers.length-1];
    }
    public DelegateLineTokenHandler(LineTokenHandler ... handlers) {
        this(true,handlers);

    }
    public DelegateLineTokenHandler(boolean cleanTrace, List<LineTokenHandler> handlers) {
        this(cleanTrace,handlers.toArray(new LineTokenHandler[handlers.size()]));
    }
    public DelegateLineTokenHandler(List<LineTokenHandler> handlers) {
        this(handlers.toArray(new LineTokenHandler[handlers.size()]));
    }

    public void setNext(Callback<Tokens> next) {
        this.next=next;
        tail.setNext(this.next);
    }

//    public Tokens process(LineToken lineToken) {
//        Traceable source = null;
//        if(cleanTrace && lineToken instanceof Traceable){
//            source =(Traceable) lineToken;
//        }
//        Tokens tokens = lineToken;
//        for (TokensHandler h: handlers) {
//            tokens= h.process(tokens);
//        }
//        if(source!=null){
//            source.clearSource();
//        }
//        return tokens;
//    }

    public void call(Tokens lineToken) {
        Traceable source = null;
        if(cleanTrace && lineToken instanceof Traceable){
            source =(Traceable) lineToken;
        }
        if(head!=null) {
            head.call(lineToken);
        }

        if(source!=null){
            source.clearSource();
        }
    }


    public static class AdapterLineTokensHandler implements CallbackChain<Tokens,Tokens>{

        private static Callback<Tokens> defaultNext = new DefaultCallback();
        private LineTokenHandler delegated;


        private Callback<Tokens> next=defaultNext;


        public AdapterLineTokensHandler(LineTokenHandler delegated) {
            this.delegated = delegated;
        }

        @Override
        public void setNext(Callback<Tokens> next) {
            this.next = next;
        }

        private Tokens processLine(LineToken lineToken){
            return delegated.process(lineToken);
        }
        private void processLineTokensHolder(LineTokensBlock lineTokensBlock){
            try {
                next.call(processLine(lineTokensBlock.getStartBlock()));
                while (lineTokensBlock.hashNextContent()) {
                    next.call(processLine(lineTokensBlock.nextContent()));
                }
                next.call(processLine(lineTokensBlock.getEndBlock()));
            }finally {
                StreamUtils.closeIgnore(lineTokensBlock);
                StreamUtils.destroyIgnore(lineTokensBlock);
            }
            //return MultiLineData.tokens(holder);
        }
        private void processMultiLine(MultiLine lineToken){
            int size = lineToken.sizeLine();
            try{
                for (int i = 0; i < size; i++) {
                    call(lineToken.index(i));
                }
            }finally {
                StreamUtils.closeIgnore(lineToken);
                StreamUtils.destroyIgnore(lineToken);
            }
        }
        @Override
        public void call(Tokens tokens) {
            if(tokens!=null&&(tokens.isEOF()||!tokens.isEmpty())){
                if(tokens instanceof LineTokensBlock){
                    processLineTokensHolder((LineTokensBlock) tokens);
                }else
                if(tokens instanceof MultiLine){
                    processMultiLine((MultiLine) tokens);
                }else
                if(tokens instanceof LineToken){
                    next.call(processLine((LineToken)tokens));
                }
            }
        }

    }
}
