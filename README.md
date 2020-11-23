# java_grep

## 速度比較用<br/>ディレクトリ再帰ファイルGREPプログラム<br/>Java版

 * 検索対象ワードは配列に設定してください。

com.example.Checker.java
``` java
    /**
     * 検索対象ワード
     */
    public static final String[] TARGET_WORDS = {
            "This",
            "Check",
            "Just",
    };

```

ex.
```
gradle run --args="/usr/local/Cellar .*\.md"
```
 * 引数
   * 0 : 再帰ディレクトリパス
   * 1 : 対象ファイル正規表現
