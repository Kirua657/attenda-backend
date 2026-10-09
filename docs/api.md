# 開発用API

## GET /api/health

PostgreSQLで `SELECT 1` を実行し、APIとDBの接続を確認します。

成功: HTTP 200

```json
{"status":"ok","db":1}
```

DB接続に失敗した場合は成功レスポンスを返しません。
フロントからは `/api/health` へ接続し、Next.jsの転送設定を通します。
このAPIは開発用です。業務機能の公開前にSpring Securityによる認証・権限確認を追加し、この確認APIの公開範囲も決めます。
