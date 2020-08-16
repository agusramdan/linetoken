package ramdan.file.line.token.mapping;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import ramdan.file.line.token.data.LineTokenData;
import ramdan.file.line.token.filter.DefaultMultiLineTokenFilter;
import ramdan.file.line.token.handler.MappingContentLineTokenHandler;

public class MappingContentLineTokenHandlerTest {
    @Test
    public void endTest() {
        val mapping = new MappingContentLineTokenHandler(new DefaultMultiLineTokenFilter(
                "Invoice", "DOCSTART_\\w*", "DOCEND", "TREATMENT_TYPE", "CUSTOMERTYPE", "ACCOUNTNO", "B(START|END)GROUP", "B(START|END)PRODITEM"
        ), false);

        mapping.process(LineTokenData.parse("DOCSTART_1"));

        val account = mapping.process(LineTokenData.parse("ACCOUNTNO|12"));

        val docEnd = mapping.process(LineTokenData.parse("DOCEND"));

        Assert.assertFalse(docEnd.isEmpty());
    }
}
