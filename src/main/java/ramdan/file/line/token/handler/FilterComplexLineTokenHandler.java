package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.FilterComplex;

public class FilterComplexLineTokenHandler extends DefaultLineTokenHandler {

    private FilterComplex filterComplex;
    private boolean start;

    public FilterComplexLineTokenHandler(FilterComplex filterComplex) {
        this.filterComplex = filterComplex;
    }
    @Override
    public Tokens process(LineToken lineToken) {
        String tag = lineToken.get(0);
        if(start){
            if(filterComplex.isMatchEnd(tag)){
                boolean remove = filterComplex.isRemove();
                if (filterComplex.isRoot()){
                    start =false;
                }else {
                    filterComplex = filterComplex.getParent();
                }
                return remove? LineTokenData.EMPTY:lineToken;
            }
            if(!filterComplex.isRemove()) {
                FilterComplex child = filterComplex.findMatchChild(tag);
                if (child != null) {
                    filterComplex = child;
                    return child.isRemove()? LineTokenData.EMPTY:lineToken;
                }
                if (filterComplex.isMatchContent(tag)) {
                    return lineToken;
                }
            }
        }else
        if(filterComplex.isMatchStart(tag)){
            start =true;
            return lineToken;
        }

        return LineTokenData.EMPTY;
    }

}
