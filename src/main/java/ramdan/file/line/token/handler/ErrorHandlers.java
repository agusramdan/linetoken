package ramdan.file.line.token.handler;

public class ErrorHandlers {
    public final static IntegerConversionErrorHandler INTEGER_CONVERSION_ERROR_HANDLER =
            new IntegerConversionErrorHandler(){
                @Override
                public int handle(String string) {
                    return 0;
                }
            };

}
