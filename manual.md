# NAME
  
linetoken - linetoken filter and transformations 

# SYNOPSIS  

```text
java -jar linetoken.jar [OPTION] 
``` 
    
# DESCRIPTION

Linetoken is tool for filter and transformations Convergys IRB (RBM) or Geneva File Format .
 
| option  | description | sample
| ---------- | ----------- | ---------
| -v , --version        | Version | 
| -h , --help        | Help  |
| -d , --donate        | Donate |
| -i [INPUT] | input file or directory |  -i /data/input/rbm
| -ix [EXTENSIONFILE]  | input extension | -ix .GMF,.TXT
| -o [OUTPUT] | output dir or file | -o /data/input/EXP
| -ox [EXTENSIONFILE]  | output extension | -ox .spl
| -cfg | config file filter/capture , transformation mapping and rule | -cfg filter.cfg,mapping-adj.cfg,mapping-rental.dfg.mapping-usage.cfg,capture.cfg
| -ds [DOCSTAR] | parameter doc start tag regex default DOCSTART_\d | -ds DOCSTART_1
| -de [DOCEND] | parameter doc end tag regex default DOCEND | -ds DOCEND
| -dt [TAG] | filter tag coma delimiter default ACCOUNTNO |  -dt ACCOUNTNO
| -dv [DOCVALUE] | filter value comma delimiter  | -dv 101001010,121221,1212,12122
| -dtxv [DOCVALUE] | filter index value | -dtxv PRODUCT,1,GPRS
| -dvf [FILE] | get filter value from file   | -dvf account.txt
| -dfx  | filter exclude or inverse filter  | -dfx


# Example


## Filter document by account number in file

```text
java -jar linetoken.jar -i /data/input/rbm -o /data/output/EXP -ox .spl -dvf account.txt
``` 

## Exclude document by account number in file

```text
java -jar linetoken.jar -dfx -i /data/input/rbm -o /data/output/EXP -ox .spl -dvf account.txt

``` 

java -jar linetoken.jar -dfx -i /data/Input/Dunning/TESTBIN/20200204 -o /data/Output/DUNNING/agus/EXP -ox .spl -dvf /data/Input/Dunning/TESTBIN/listtestba.txt   -vv
    
java -jar linetoken.jar -dfx -i  DUNNING  -o EXP -ox .spl -dvf listtest.txt


 

