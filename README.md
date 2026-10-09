# Attenda バックエンド

出席管理Webシステムの開発用の土台です。現時点ではDB接続確認APIのみを実装しています。
認証、出席登録、出席計算、管理APIはこれから実装します。

## 必要な環境

- JDK 21（JREのみでは不可。作成時はTemurin 21.0.12.1）
- PostgreSQL 17、Git、VS Code
- Spring Boot 4.1.1 / Maven Wrapper
- フロント: https://github.com/Kirua657/attenda-frontend

Mavenは `mvnw.cmd` を使うため別途インストール不要です。
初回起動はMavenと依存パッケージをダウンロードするためインターネット接続が必要です。

## 1 プロジェクトを取得する

PowerShellで実行します。既に取得済みならcloneは不要です。

```powershell
New-Item -ItemType Directory -Force C:\dev\attenda
Set-Location C:\dev\attenda
git clone https://github.com/Kirua657/attenda-backend.git
Set-Location .\attenda-backend
git switch develop
java -version
javac -version
.\mvnw.cmd -v
```

Javaが見つからない場合はJDK 21の導入とPATH・JAVA_HOMEを確認してPowerShellを開き直します。

## 2 自分のPCにDBを作る

PostgreSQLサービスが起動している状態で実行します。

```powershell
& "C:\Program Files\PostgreSQL\17\bin\psql.exe" -h localhost -U postgres -d postgres
```

インストール時に決めたpostgresのパスワードを入力します。
以下はPowerShellではなく、接続後のpsql画面で実行します。
既にユーザー・DBがある場合は作り直さず確認してください。

```sql
CREATE ROLE attenda_app LOGIN;
\password attenda_app
CREATE DATABASE attenda OWNER attenda_app;
\q
```

`\password` で自分のアプリ用パスワードを設定します。全員で同じものにする必要はありません。

## 3 バックを起動する

プロジェクトのフォルダーで、PowerShellにパスワードを設定して起動します。

```powershell
$dbSecret = Read-Host "DB password" -AsSecureString
$env:DB_PASSWORD = ([System.Net.NetworkCredential]::new("", $dbSecret)).Password
.\mvnw.cmd spring-boot:run
```

パスワードはファイルやGitHubに保存しません。ターミナルを開き直したら再入力します。
必要なら同じターミナルで `DB_URL`、`DB_USER` を設定できます。
既定値は `jdbc:postgresql://localhost:5432/attenda`、`attenda_app` です。
Spring Bootは `.env` を自動では読み込まないため、この手順では環境変数を使います。

## 4 接続を確認する

バックを起動したまま別のPowerShellで実行します。

```powershell
Invoke-RestMethod http://localhost:8080/api/health
```

`status: ok`、`db: 1` が成功です。
フロントも起動したら、次でフロント → バック → DBの経路を確認できます。

```powershell
Invoke-RestMethod http://localhost:3000/api/health
```

停止はCtrl+Cです。

## DB設計と変更

業務用テーブルはまだ作っていません。ER図とDB設計を確定した後、
`src/main/resources/db/migration/V1__create_tables.sql` から追加します。
Flywayが起動時に未適用SQLを実行します。適用済みSQLを書き換えず、新しい番号で変更します。
JPAの `ddl-auto` は `none`、SQLの自動初期化は無効です。
架空の初期データの作成手順を共有し、学生の実データ・DBバックアップはコミットしません。

## 日常の開発

```powershell
git status
git switch develop
git pull --ff-only origin develop
git switch -c feature/attendance-api
```

`controller` はAPI、`service` は判定・計算、`repository` はDB操作、
`entity` は保存データ、`dto` は入出力を置きます。
実装したら、自分のDBと `DB_PASSWORD` を準備したターミナルで実行します。

```powershell
.\mvnw.cmd test
git diff
git add src
git diff --cached
git commit -m "feat: add attendance API"
git push -u origin feature/attendance-api
```

`pom.xml` や `docs/api.md` を変更した場合はそのファイルも個別に追加します。
GitHubで **base: develop / compare: 自分のfeature** のPRを作り、他の1人が確認してからマージします。
`main` は提出用、`develop` は開発の結合先です。

## よくある問題

- 接続拒否: PostgreSQLまたはバックが起動しているか確認
- DB認証失敗: `DB_USER` と入力した `DB_PASSWORD` を確認
- ポート使用中: 5432・8080を使用する既存プロセスを確認
- WrapperでJavaが見つからない: JDK 21と `JAVA_HOME` を確認
- Flyway検証エラー: 適用済みSQLの変更や番号の重複を確認

認証・アクセス制御は未実装です。開発用APIを実データ入りの共有環境へ公開する前に実装します。

## チームのGitHub作業手順

[メンバー用GitHub作業テンプレート](docs/github-guide.md)に、作業開始・保存・PR・レビュー・取り込みの手順と記入例をまとめています。
