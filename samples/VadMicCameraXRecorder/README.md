# VadMicCameraXRecorder
- THINKLET向けCameraX を応用した人の声を検出したときだけ音声にいれる 録画サンプルアプリです．  
Pixelなどの一般的なAndroidデバイスでも動作可能です．
- 録画の開始と停止は，音量ダウンキー で切り替えます．
- 録画ファイルは，`/sdcard/Android/data/com.example.fd.camerax.vadrecorder/files/` 以下にmp4形式で保存します．

## 動作確認デバイス
- Pixel 7
  - Android 15
  - AP3A.241105.007

## ビルド
- ビルドするには，`local.properties` ファイルに以下を追記してください．

```
# GitHub Packages経由でライブラリを取得します．下記を参考にアクセストークンを発行ください．
# https://docs.github.com/ja/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token
# read:packages の権限が必須です
TOKEN=<github token>
USERNAME=<github username>
```

- `gradlew` でのビルド

```bash
# デバッグインストール
./gradlew installDebug
# リリースビルド
./gradlew assembleRelease
```

> [!NOTE] 
> アプリ起動前に，事前にカメラとマイクのPermissionを許可してください．
