# トッポギ メンバー用 GitHub作業テンプレート

この手順は、初回の環境構築と接続確認が終わったメンバー向けです。
毎回「自分の作業ブランチで変更 → 確認 → commit → push → PR → 他の人が確認 → developへ取り込み」の順で進めます。
コマンドはWindowsのPowerShellで、ブロックごとに実行してください。エラーが出たら次のブロックへ進まず、最後の「困ったとき」を確認します。

## 1 最初に覚える言葉

| 言葉 | このチームでの意味 |
|---|---|
| repository（リポジトリ） | コードを保存する場所。フロントとバックで2つある |
| clone | GitHubのコードを自分のPCへ初めて取得する |
| pull | GitHubの最新の変更を自分のPCへ取り込む |
| branch（ブランチ） | 作業する場所を分ける仕組み |
| commit | 自分のPCで変更内容をひとまとまりとして記録する |
| push | 記録したcommitをGitHubへ送る |
| Issue | 誰が何を作るか、完了条件を決める作業票 |
| PR（Pull Request） | 自分の変更をチームのコードへ取り込んでもらう依頼 |
| review | 別の人が変更内容と動作を確認する |
| merge | 確認済みの変更を取り込む |

commitの後にpushすると、GitHubにも変更が保存されます。PRをmergeすると、他のメンバーがpullして使えるようになります。

## 2 作業場所とチームのルール

| 場所 | 用途 |
|---|---|
| `C:\dev\attenda\attenda-frontend` | React・Next.js・画面の変更 |
| `C:\dev\attenda\attenda-backend` | Java・API・出席計算・DBの変更 |
| `main` | 提出できる状態。リーダーが取り込みを管理する |
| `develop` | 全員の変更を結合する場所 |
| `feature/...` | 自分が担当する1つの作業を進める場所 |

- VS Codeは「ファイル → フォルダーを開く」で `C:\dev\attenda` を開く。
- Gitコマンドは、変更する側のリポジトリ内で実行する。
- `main`・`develop`への変更はPRで取り込む。
- 最初は、別の1人が確認した後にリーダーがmergeする運用にする。
- ブランチ名は `feature/機能名-自分の識別名`。半角英数字・ハイフンを使う。
- 例: `feature/student-home-taro`、`feature/attendance-api-hana`。
- 1つの作業が終わったら、次の作業には新しいブランチ名を使う。
- `.env.local`・パスワード・学生の実データは共有しない。

## 3 作業票を作る

GitHubで担当するリポジトリを開き、「Issues → New issue → 作業依頼」から作成します。
右側の「Assignees」で担当者を選びます。同じファイルを同時に触りそうな場合は、先に担当者同士で調整してください。

コピーして使う作業票の例です。科目名や画面名は設計書に合わせます。

```text
タイトル：学生ホーム画面を作る

目的：学生が今日の授業と出席登録状況を確認できるようにする。
担当者：自分の名前

作るもの：
- 今日の授業一覧
- 授業ごとの出席登録状況
- QR読み取り画面へのボタン

完了条件：
- 今日の授業が表示される
- ボタンからQR読み取り画面へ移動できる
- スマホ幅で文字やボタンがはみ出さない

今回は含めないもの：QR登録APIとの連携（別Issueで対応）
参考：画面一覧No.2、画面レイアウトの学生ホーム画面
```

フロントとバックを両方変更する場合は、両方に作業票を作ってURLを相互に書きます。

## 4 作業を始める

担当側へ移動します。どちらか一方を実行してください。

フロントの場合:

```powershell
Set-Location C:\dev\attenda\attenda-frontend
```

バックの場合:

```powershell
Set-Location C:\dev\attenda\attenda-backend
```

今の状態を確認します。

```powershell
git branch --show-current
git status --short
```

`git status --short` が空なら次へ進みます。ファイル名が表示された場合は、前の作業が残っています。
前の作業を進めていたfeatureブランチで、変更内容を確認してcommitしてください。
どの作業か分からない場合は、ファイルを消さずリーダーに相談します。

次を実行し、最新のdevelopから自分の作業ブランチを作ります。

```powershell
git switch develop
if ($LASTEXITCODE -ne 0) { throw "切り替えに失敗しました。ここで止めて確認してください。" }

git pull --ff-only origin develop
if ($LASTEXITCODE -ne 0) { throw "更新に失敗しました。ここで止めて確認してください。" }

$workBranch = Read-Host "作業ブランチ名を入力（例 feature/student-home-taro）"
git switch -c $workBranch
```

入力したブランチ名を覚えておきます。例の `taro` は自分の識別名に変えてください。

```powershell
git branch --show-current
```

入力した `feature/...` が表示されれば、VS Codeでコードを書き始めます。

## 5 途中で作業を終える と 翌日再開する

完成していなくても、自分のfeatureで変更を確認し、commit・pushして作業を残せます。
PRは「Draft」で作り、本文に未完了の項目を書いてください。

翌日は担当側のフォルダーで、自分の作業ブランチへ戻ります。
下のブランチ名は、自分が作った名前に置き換えます。

```powershell
git switch feature/student-home-taro
git pull --ff-only
```

作業途中のブランチを、毎日 `git switch -c` で作り直す必要はありません。

## 6 動作を確認する

フロント担当は、動作画面の確認に加えて以下を実行します。

```powershell
npm.cmd run lint
npm.cmd run typecheck
npm.cmd run build
```

バック担当は、PostgreSQLを起動し、テスト用ターミナルにもDBパスワードを設定して実行します。

```powershell
$dbSecret = Read-Host "アプリ用DBパスワード" -AsSecureString
$env:DB_PASSWORD = ([System.Net.NetworkCredential]::new("", $dbSecret)).Password
.\mvnw.cmd test
```

起動中のアプリのターミナルはそのままにし、確認用の別ターミナルを使えます。
結果をPRに書きます。失敗した確認や未実施の操作も、そのまま明記してください。
途中の作業を保存するためのcommit・pushはできますが、確認が終わるまでは取り込み依頼をDraftにします。

## 7 変更をcommitしてGitHubへ送る

まず保存対象を確認します。

```powershell
git status
git diff
```

`src` 内の変更を保存する場合の例です。

```powershell
git add src
git diff --cached
```

`git diff --cached` で、自分の作業に必要な変更だけが入っていることを確認します。
設定ファイルも変更した場合は、必要なものを個別に追加します。
例えばフロントの依存パッケージを変更した場合:

```powershell
git add package.json package-lock.json
```

バックの依存設定を変更した場合:

```powershell
git add pom.xml
```

保存して送信します。commitメッセージは自分の変更内容を入力してください。

```powershell
$workBranch = git branch --show-current
if ($workBranch -notlike "feature/*") { throw "featureブランチで実行してください。" }

$commitMessage = Read-Host "変更内容を入力（例 学生ホームに今日の授業一覧を追加）"
git commit -m $commitMessage
if ($LASTEXITCODE -ne 0) { throw "commitできませんでした。ここで状態を確認してください。" }

git push -u origin $workBranch
```

push時にログイン画面が出たら、招待を承諾した自分のGitHubアカウントでログインします。

## 8 PRを作る

GitHubで「Pull requests → New pull request」を開きます。
上部の2つの選択を、次のようにします。

| 項目 | 選ぶもの |
|---|---|
| base | `develop` |
| compare | 自分の `feature/...` |

表示された差分が、自分の作業に合っていることを確認します。
タイトルと本文を入力し、「Create pull request」を押します。
まだ未完了なら「Create draft pull request」を選びます。
右側の「Reviewers」で確認してもらうメンバーを選びます。

PR本文の記入例:

```text
関連Issue：#12

変更内容：
- 学生ホームに今日の授業一覧を追加した
- QR読み取り画面へ移動するボタンを追加した

確認方法：
1. npm.cmd run devでフロントを起動する
2. 対象画面を開く
3. 今日の授業一覧が表示されることを確認する
4. QR読み取りボタンで対象画面へ移動することを確認する

確認結果：lint・typecheck・build成功。スマホ幅で表示確認済み。
未完了：API接続は別Issue。今回は仮データで表示。
画像：変更した画面のスクリーンショットを添付。
```

フロントとバック両方のPRがある場合は、相手のPRのURLと、取り込む順番を本文に書きます。

## 9 レビューする 修正する

レビューする人は「Files changed」で変更内容を確認し、PRに書かれた手順で動作を確認します。
自分の未commitの作業がないことを確認してから、同じリポジトリでレビュー用のブランチへ切り替えます。

```powershell
git status --short
```

何か表示された場合は、先に自分の作業を保存します。
下の名前はPRのcompareに表示されたブランチ名へ置き換えます。

```powershell
git fetch origin
git switch --track origin/feature/student-home-taro
```

同じローカルブランチを既に取得済みの場合は、次を使います。

```powershell
git switch feature/student-home-taro
git pull --ff-only
```

依存関係が変わったフロントでは、起動を停止して `npm.cmd ci` を実行してから確認します。
動作確認後は「Files changed → Review changes」から、修正が必要なら「Request changes」、取り込めるなら「Approve」を選びます。
自分で確認していない操作は、その旨を書いてください。

レビューで修正を依頼する例:

```text
確認した操作：スマホ幅で学生ホームを開いた
起きたこと：科目名がボタンに重なっていた
期待する動作：科目名が折り返され、ボタンを押せる
お願い：スマホ幅で重ならないように調整してください
```

承認コメントの例:

```text
確認した操作：今日の授業表示、QR画面への移動、スマホ幅の表示
結果：問題なく動作しました
未確認：APIとの接続（今回の対象外）
```

作成者が修正するときは、同じfeatureに追加でcommit・pushします。元のPRが更新されるので、新しいPRは不要です。

## 10 リーダーが取り込む 全員が更新する

リーダーは、baseがdevelop、確認結果が揃っている、別の人が確認済み、競合がないことを確認します。
「Merge pull request」で取り込みます。最初は「Create a merge commit」に統一します。
取り込み後に「Delete branch」で完了したfeatureを削除して構いません。developは残します。
作業票はdevelopへの取り込み後、完了条件を確認して手動でCloseします。

完了した作業の担当者と、作業中の変更がないメンバーは以下で更新します。

```powershell
git switch develop
git pull --ff-only origin develop
```

別のfeatureで作業途中の人は、無理に切り替えず、その作業を保存してから取り込みます。
developからmainへの提出用PRは、リーダーが別途作ります。

## 11 困ったとき

| 表示・症状 | 対応 |
|---|---|
| git・npmが見つからない | インストールを確認し、下のPATH再読み込みを実行 |
| Repository not found・権限エラー | URL、招待承諾、自分のログインアカウントを確認 |
| nothing to commit | VS Codeでファイルを保存したか、git status、git addの対象を確認 |
| branch already exists | 同じ作業の再開ならgit switch、新しい作業なら別名で作成 |
| local changes would be overwritten | 今の作業をcommitしてから切り替える |
| push rejected・non-fast-forward | 自分のブランチ名と他の人の変更を確認。force pushはせず相談 |
| merge conflict・<<<<<<< | 競合しているファイルと両方の変更内容を確認し、変更した人と調整 |
| 接続できません | バックが起動中か、PostgreSQLとDB_PASSWORDを確認 |

PATHを再読み込みするコマンド:

```powershell
$env:Path = [Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [Environment]::GetEnvironmentVariable("Path", "User")
```

相談するときのテンプレート:

```text
担当：自分の名前
対象：フロント／バック
作業ブランチ：feature/...
やろうとしたこと：
実行したコマンド：
エラー全文：
最後に成功した操作：
自分で試したこと：
```

パスワード・トークン・実在学生の個人情報は、ログや画像から除いてください。
困ったときに `git reset --hard`、`git clean -fd`、`git push --force` を自己判断で実行せず、現在のファイルを残して相談します。

## 12 リーダーからメンバーへの共有文

```text
GitHubの作業手順をそろえました。

フロント：https://github.com/Kirua657/attenda-frontend
バック：https://github.com/Kirua657/attenda-backend

各リポジトリのREADMEから「メンバー用GitHub作業テンプレート」を開いてください。
作業する前にIssueで担当と完了条件を決め、最新developから自分のfeatureを作ります。
変更後は確認 → commit → push → develop宛てPRの順です。
最初は、別の1人が確認した後にリーダーが取り込みます。
分からないエラーが出たら次のコマンドへ進まず、手順書の相談テンプレートで共有してください。
```

## 参考

- [GitHub公式 PRの作成](https://docs.github.com/en/pull-requests/how-tos/create-pull-requests/creating-a-pull-request)
- [GitHub公式 PRテンプレート](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/creating-a-pull-request-template-for-your-repository)
- [GitHub公式 Issueテンプレート](https://docs.github.com/en/communities/using-templates-to-encourage-useful-issues-and-pull-requests/configuring-issue-templates-for-your-repository)
