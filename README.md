# Line Token 

![Java CI with Maven](https://github.com/agusramdan/linetoken/workflows/Java%20CI%20with%20Maven/badge.svg)

Line token ini parser untuk Convergys IRB (RBM) atau Geneva format file.

## Definition

Geneva file format

Line Token data format adalah salah satu struktur strktur penyimpanan data dalam bentuk file.

Line to Token adalah parse text delimiter per line menjadi multiple token


Deleimiter derbagi menjadi 2 jenis 
1. tag delimiter
2. token delimiter
```text
TAGNAME| token1|token2
```

Token0 adalah header line atau atau tag atau tag name.

Contoh text dengan pipe delimiter |

```text
TAGNAME| token1|token2
```

Maka di parser menjadi

```text
TAGNAME="TAGNAME"
token[1]="token1"
token[2]="token2"
```

Tag ini juga bisa dikembangkan menjadi multiple line atau kita sebut block atau table.

Misal Block PRODUCT maka kita harus mendefinisikan awal block BSTARTPRODCUT dan akhir block BENDPROCUT. 

```text

BSTARTPRODUCT|
PRODUCT_ID|1234|
PRODUCT_NAME|Sleep with enemy|
BENDPRODUCT|

```

## Tag mapping

Tag adalah index token 0. Tag mpping adalah merubah tag menjadi tag yang lain

contoh:  

merubah tagA->tagB

```text
tagA|value1|value2
```
setelah mapping menjadi

```text
tagB|value1|value2
```
## prameter

```

linetoken.sh -map "tagA->tagB;tagC->tagD" -i input.txt -ix .txt -o output.txt -ox .txt

```


# ref
https://www.tutorialspoint.com/telecom-billing/quick-billing-guide.htm
