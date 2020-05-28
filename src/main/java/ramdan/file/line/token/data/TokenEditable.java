package ramdan.file.line.token.data;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import lombok.var;
import ramdan.file.line.token.Line;
import ramdan.file.line.token.LineToken;
import ramdan.file.line.token.handler.DoubleConversionErrorHandler;
import ramdan.file.line.token.handler.ErrorHandlers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenEditable extends LineTokenBase {

    @Getter
    private final Line source;
    @Getter
    private Integer end;
    @Getter @Setter
    private DecimalFormat amountFormat = new DecimalFormat("#,##0.0#");
    private DoubleConversionErrorHandler amountHandler = new AmountDoubleConversionErrorHandler();
    private final List<String> target;
    public TokenEditable(LineToken source , String name , boolean copy, int ... idx) {
        super(source);
        this.source = source.getSource();
        this.end=source.getEnd();
        target=new ArrayList<>(source.length());
        if(copy){
            if(idx!=null && idx.length> 0){
                copyTokensFrom(source,idx);
            }else {
                copyTokensFrom(source);
            }
        }
        if(name!=null){
            set(0,name);
        }
    }
    public TokenEditable(LineToken source , boolean copy, int ... idx){
        this(source,null,copy,idx);
    }
    public TokenEditable(LineToken source, String name, int ... idx) {
        this(source, name,idx!=null && idx.length> 0,idx);
    }
    public TokenEditable(LineToken source ) {
        this(source,false);
    }
    public TokenEditable(LineToken source, String name  ) {
        this(source,name,false);
    }
    public void setEnd(Integer end) {
        if(end == null) return;
        if(this.end==null || this.end< end)
        this.end = end;
    }

    @Override
    public int length() {
        return target.size();
    }

    public LineToken copyLineToken() {
        return newLineToken(this.getFileName(), this.getStart(), this.getEnd(), target.toArray(new String[target.size()]));
    }

    public void setTagname(String tagname){
        set(0,tagname);
    }
    public void setValue(String value){
        set(1,value);
    }
    @Override
    public String get(int i) {
        if(target.size()<=i || i<0) return "";
        return target.get(i);
    }

    public String set(int idx, String value) {
        val selisih= idx-target.size()+1;
        if(selisih==1){
            target.add(value);
            return value;
        }else
        if(selisih>1) {
            val strs = new String[selisih];
            for (int i = 0; i < selisih; i++) {
                strs[i]="";
            }
            target.addAll(Arrays.asList(strs));
        }
        return target.set(idx,value);
    }
    public String map(int idxSource, int idxTarget){
        val str = get(idxSource);
        return set(idxTarget,str);
    }
    public String set(int idx,double d){
        return  set(idx,Double.toString(d));
    }
    public String set(int idx,int d){
        return  set(idx,Integer.toString(d));
    }
    public void setupAmount(int idxAmount) {
        setAmount(idxAmount,getAmount(idxAmount));
    }
    public void setupAmount(int idxAmount, int idxCredit) {
        setAmount(idxAmount,idxCredit,getAmount(idxAmount,idxCredit));
    }
    public double getAmount(int idxAmount, int idxCredit){
        var targetAmount = getDouble(idxAmount,amountHandler);
        if(equalIgnoreCase(idxCredit,"Credit")) {
            targetAmount*=-1;
        }
        return targetAmount;
    }
    public void setAmount(int idxAmount, int idxCredit,double targetAmount){
        if(targetAmount <-0.0){
            set(idxCredit,"Credit");
        }else {
            set(idxCredit,"");
        }
        setAmount(idxAmount,Math.abs(targetAmount));
    }

    public void setAmount(int idxAmount, double targetAmount){
        set(idxAmount, amountFormat.format(targetAmount));
    }

    private double addAmount0(int idxAmount, int idxCredit, TokenEditable from){
        var targetAmount = getAmount(idxAmount,idxCredit);
        targetAmount+=from.getAmount(idxAmount,idxCredit);

        setAmount(idxAmount,idxCredit,targetAmount);
        return targetAmount;
    }

    public double addAmount(int idxAmount, int idxCredit, double targetAmount){
        //var targetAmount = getAmount(idxAmount,idxCredit);
        targetAmount+=getAmount(idxAmount,idxCredit);

        setAmount(idxAmount,idxCredit,targetAmount);
        return targetAmount;
    }

    public double getAmount(int idxAmount){
        return getDouble(idxAmount,amountHandler);
    }

    public double addAmount(int idxAmount,double d){
        val from = getDouble(idxAmount,amountHandler)+d;
        setAmount(idxAmount,from);
        return from;
    }

    private double addAmount0(int idxAmount, TokenEditable from){
        var targetAmount = getAmount(idxAmount);
        targetAmount+=from.getAmount(idxAmount);
        setAmount(idxAmount,targetAmount);
        return targetAmount;
    }
//    public double addAmount(int idxAmount, TokenEditable from){
//        var targetAmount = getAmount(idxAmount);
//        targetAmount+=from.getAmount(idxAmount);
//
//        setAmount(idxAmount,targetAmount);
//        return targetAmount;
//    }
    protected double addAmount0(int idxAmount, int idxCredit, LineToken from){
        if(from instanceof TokenEditable){
            return addAmount0(idxAmount,idxCredit,(TokenEditable)from);
        }
        var targetAmount = from.getDouble(idxAmount);
        if(from.equalIgnoreCase(idxCredit,"Credit")) {
            targetAmount*=-1;
        }
        targetAmount+=getAmount(idxAmount,idxCredit);
        setAmount(idxAmount,targetAmount);
        return targetAmount;
    }

    public double addAmount(int idxAmount, int idxCredit, LineToken... from){
        var targetAmount = 0.0;
        for (LineToken lt: from) {
            targetAmount = addAmount0(idxAmount,idxCredit,lt);
        }
        return targetAmount;
    }
    public double addAmount(int idxAmount, int idxCredit, List<LineToken> from){
        var targetAmount = 0.0;
        for (LineToken lt: from) {
            targetAmount = addAmount0(idxAmount,idxCredit,lt);
        }
        return targetAmount;
    }
    private double addAmount0(int idxAmount,  LineToken from){
        if(from instanceof TokenEditable){
            return addAmount0(idxAmount,(TokenEditable) from);
        }
        var targetAmount = getAmount(idxAmount);
        targetAmount+=from.getDouble(idxAmount);
        setAmount(idxAmount,targetAmount);
        return targetAmount;
    }
    public double addAmount(int idxAmount, LineToken...from){
        var targetAmount = 0.0;
        for (LineToken lt: from) {
            targetAmount = addAmount0(idxAmount,lt);
        }
        return targetAmount;
    }
    public double addAmount(int idxAmount, List<LineToken>from){
        var targetAmount = 0.0;
        for (LineToken lt: from) {
            targetAmount = addAmount0(idxAmount,lt);
        }
        return targetAmount;
    }


    public void copyTokensFrom(TokenEditable template) {
        if(target.isEmpty()){
            target.addAll(template.target);
        }else {
            val size = template.target.size();
            for (int i = 0; i < size; i++) {
                set(i,template.target.get(i));
            }
        }
    }

    public void copyTokensFrom(LineToken source) {
        if(source instanceof TokenEditable){
            copyTokensFrom((TokenEditable)source);
        }else {
            if(target.isEmpty()){
                target.addAll(Arrays.asList(source.copy(0)));
            }else {
                val size = source.length();
                for (int i = 0; i < size; i++) {
                    set(i,source.get(i));
                }
            }
        }

    }

    public TokenEditable mapTokensFrom(LineToken source, int ... idxs) {
        for (int i=0;i<idxs.length  ;i++) {
            set(i+1,source.get(idxs[i]));
        }
        return this;
    }
    public void copyTokensFrom(LineToken source, int ... idxs) {
        for (int i  :idxs) {
            set(i,source.get(i));
        }
    }
    public boolean equalTokens(LineToken lineToken, int ...idx) {
        boolean equal = false;// equal(i,lineToken.get(i));
        for (int in : idx) {
            equal=equal(in,lineToken.get(in));
            if(!equal) break;
        }
        return equal;
    }

    public int addInteger(int i, LineToken edit) {
        int target = getInt(i);
        target += edit.getInt(i);
        set(i,target);
        return target;
    }

    public int compareTo(LineToken t, int ... idxs) {
        var com= 0;
        for (int i = 0;com==0 && i<idxs.length; i++) {
            com=get(idxs[i]).compareTo(t.get(idxs[i]));
        }
        return com;
    }
    public void tagMapping(LineToken lineToken,String ... tagToken){
        for (int i = 0; i < tagToken.length; i++) {
            if(lineToken.equal(0,tagToken[i])){
                this.set(i+1,lineToken.getValue());
            }
        }

    }
    private class AmountDoubleConversionErrorHandler implements DoubleConversionErrorHandler {
        public double handle(String string) {
            try {
                return amountFormat.parse(string).doubleValue();
            }catch (Exception e){
                return ErrorHandlers.DOUBLE_CONVERSION_ERROR_HANDLER.handle(string);
            }
        }
    }

    public static TokenEditable ensureTokenEditable(LineToken lineToken){
        if(lineToken==null) return null;
        if (lineToken instanceof TokenEditable) return (TokenEditable) lineToken;
        return new TokenEditable(lineToken,true);
    }
}
