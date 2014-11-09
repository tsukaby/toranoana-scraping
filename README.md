toranoana-scraping
==================

友人に頼まれて作った、とらのあなランキング収集ツール。
こいつで調べてから、書く同人誌決めるとかなんとか。

## build
sbt assembly

## 使い方
1. toranoana-scraping-assembly-1.0.jarをDLします。<https://raw.githubusercontent.com/tsukaby/toranoana-scraping/master/toranoana-scraping-assembly-1.0.jar>
1. デスクトップなど、DLした場所でShift + 右クリックして「コマンドウィンドウをここで開く」を選択します。(Windows)
1. 以下のコマンドで起動します。yyyyMMddの日付は収集開始日、収集終了日の順で指定してください。yyyyMMddは20141015というような感じで記述します。
`java -jar toranoana-scraping-assembly-1.0.jar [yyyyMMdd] [yyyyMMdd]`
1. 集計が始まります。
1. 完了メッセージが出た後、フォルダ内を見るとcsvが存在するはずです。これが結果です。

## 注意
ランキングが存在しない日付を含むとエラーが出るので注意してください。

一度に大量の期間を指定すると大量アクセスのせいでアクセス禁止などを食らう可能性があります。

javaコマンドが実行失敗した場合はJavaがインストールされていないのでインストールしてください。java インストール windowsとかで適宜ぐぐってjavaコマンドが使える状態にしてください。
