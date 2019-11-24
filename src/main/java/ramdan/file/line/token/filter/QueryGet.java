package ramdan.file.line.token.filter;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.Status;

import java.util.ArrayList;
import java.util.List;

import static ramdan.file.line.token.data.Status.CLOSE_WITHOUT_END;

public class QueryGet {




    public class BlockDef {
        private final String name;
        private final RegexMatchRule start;
        private final RegexMatchRule end;
        public BlockDef(String name, RegexMatchRule start, RegexMatchRule end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
        public BlockDef(String name, String start, String end) {
            this( name, new RegexMatchRule(start),new RegexMatchRule(end));
        }
        public boolean isStart(String string){
            return start.isMatchRule(string);
        }
        public boolean isStart(LineToken string){
            return start.accept(string);
        }

        public boolean isEnd(String string){
            return end.isMatchRule(string);
        }
        public boolean isEnd(LineToken string){
            return end.accept(string);
        }
    }

    public class BlockData{
        private final BlockDef blockDef;
        private final BlockData parent;
        private Status status = Status.EMPTY; // EMPTY, OPEN,CLOSE
        private List<LineToken> content = new ArrayList<>();
        public BlockData(BlockDef blockDef, BlockData parent) {
            this.blockDef = blockDef;
            this.parent = parent;
        }
        public BlockData(BlockDef blockDef) {
            this(blockDef, null);
        }
        private Status addContent(LineToken lineToken){

            if(parent!= null){
                return parent.add(lineToken);
            }
            return Status.ACCEPT;
        }
        public Status add(LineToken lineToken){
            Status result = Status.REJECT;
            switch (status){
                case OPEN:
                    result= addContent(lineToken);
                    if(blockDef.isEnd(lineToken)){
                        result=Status.ACCEPT_AND_CLOSE;
                    }else if(result==Status.ACCEPT_AND_CLOSE){
                        status = CLOSE_WITHOUT_END;
                    }
                case EMPTY:
                    if(blockDef.isStart(lineToken)){
                        result= addContent(lineToken);
                        return Status.ACEEPT_AND_OPEN;
                    }
            }

            return result;
        }

        public void pushContent(BlockData blockData){
            for (LineToken lt: content) {
                blockData.add(lt);
            }
        }
    }
}
