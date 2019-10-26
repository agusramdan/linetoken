package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.filter.MultiLineTokenFilter;

/**
 *
 * ramdan.file.bpp.vfree.rbm.mapping.ProductItemSummaryRowHandler
 *
 */
public class RemoveLineTokenHandler extends MappingContentLineTokenHandler {

    public RemoveLineTokenHandler(MultiLineTokenFilter filter) {
        super(filter,false);
    }

    @Override
    protected LineToken endTagHandle(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }

    @Override
    protected LineToken startTagHandle(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }

    @Override
    protected LineToken alreadyStartTagHandle(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }

    @Override
    protected LineToken matchContent(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }
}
