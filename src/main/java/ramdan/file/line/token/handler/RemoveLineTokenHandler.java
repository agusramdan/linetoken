package ramdan.file.line.token.handler;

import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.Tokens;
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
    protected Tokens endTagHandle(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }

    @Override
    protected Tokens startTagHandle(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }

    @Override
    protected Tokens matchContent(LineToken lineToken) {
        return LineTokenData.EMPTY;
    }
}
