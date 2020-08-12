package ramdan.file.line.token.handler;

public class ErrorHandlers {
    public final static IntegerConversionErrorHandler INTEGER_CONVERSION_ERROR_HANDLER =
            new IntegerConversionErrorHandler(){
                @Override
                public int handle(String string) {
                    return 0;
                }
            };

    public static final DoubleConversionErrorHandler DOUBLE_CONVERSION_ERROR_HANDLER =
            new DoubleConversionErrorHandler() {
                @Override
                public double handle(String string) {
                    return 0;
                }
            };
}
