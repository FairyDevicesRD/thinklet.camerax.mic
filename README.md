# THINKLET向けCameraX マイクプラグイン
- [THINKLET向けCameraX](https://github.com/FairyDevicesRD/thinklet.camerax) 向けのマイクプラグインのソースコード，導入手順，及びサンプルアプリのソースコードを記載しています．
- 本プロジェクトのマイクプラグインは，THINKLETのみ動作しますが，この実装を参考に，独自のマイク実装をCameraXに入れ込むことも可能です．
  - 例えば，音声認識と音声合成したデータを与えることで，理論上ノイズが一切入らない人の発話のみを録音する機能を実現できます．
## サンプル
### THINKLETのみ
- [MultiMicCameraXRecorder](./samples/MultiMicCameraXRecorder)
### THINKLET, Android向け
- [VadMicCameraXRecorder](./samples/VadMicCameraXRecorder)
## 導入手順
実装の詳細については，[サンプル](./samples)を確認ください．
### 1. アクセストークン取得
- GitHub Packages経由でライブラリを取得できるよう，[こちら](https://docs.github.com/ja/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)を参考に個人用アクセス トークンを発行してください．
### 2. アクセストークンを設定
- アクセストークンは，クレデンシャルですので，Git管理しないように，`local.properties` などに保存を推奨します．
  ```
  TOKEN=<github token>
  USERNAME=<github username>
  ```
### 3. Projectレベルの `settings.gradle.kts` に設定
- GitHub Packagesを参照する設定を追加します．ここでは手順2でアクセストークンは，`local.properties` に記載済みであるとしています．
  ```diff
  dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
          google()
          mavenCentral()
  +       maven {
  +           name = "GitHubPackages"
  +           setUrl("https://maven.pkg.github.com/FairyDevicesRD/thinklet.app.sdk")
  +           content {
  +               includeGroup("ai.fd.thinklet")
  +           }
  +           credentials {
  +               val properties = java.util.Properties()
  +               properties.load(file("local.properties").inputStream())
  +               username = properties.getProperty("USERNAME") ?: ""
  +               password = properties.getProperty("TOKEN") ?: ""
  +           }
  +       }
      }
  ```
- また，GradleのVersionによっては，Projectの `build.gradle`, `build.gradle.kts` に記載する場合もあります．
### 4. Module(appなど)レベルの `build.gradle.kts` に設定
  ```diff
   dependencies {
  +     val cameraX = "1.4.0"

  +    // AndroidX標準のCameraXを追加
  +    implementation("androidx.camera:camera-core:$cameraX")
  +    implementation("androidx.camera:camera-camera2:$cameraX")
  +    implementation("androidx.camera:camera-lifecycle:$cameraX")
  +    implementation("androidx.camera:camera-view:$cameraX") {
  +        // AndroidX標準のCamera-Videoを削除．
  +        exclude("androidx.camera", "camera-video")
  +    }
  +    val thinkletCameraX = "1.4.0"
  +    // 代わりに，THINKLETカスタムのCamera－Videoを追加
  +    implementation("ai.fd.thinklet:camerax-camera-video:$thinkletCameraX")

  +    val thinkletSdk = "0.1.6"
  +    val thinkletCameraXMic = "0.0.1"

  +    // THINKLET向けのマイクを追加
  +    implementation("ai.fd.thinklet:camerax-mic-core:$thinkletCameraXMic")
  +    implementation("ai.fd.thinklet:sdk-audio:$thinkletSdk")
  +    // 5chを使用する場合
  +    implementation("ai.fd.thinklet:camerax-mic-multi-channel:$thinkletCameraXMic")
  +    // THINKLET AppSDKの 音声処理機能(※1) を使用する場合
  +    implementation("ai.fd.thinklet:camerax-mic-xfe:$thinkletCameraXMic")
   }
  ```
> [!IMPORTANT]
> 上記手順を行うと，標準の `camera-video` の実装を置き換えます．
> `androidx.camera:camera-video` への依存が含まれる `camera-view` などを使用する際には `camera-video`を依存から削除する必要があります．（上記参考）
> プロジェクトレベルで対応するには，[Samples](./samples/MultiMicCameraXRecorder/build.gradle.kts) を確認ください．

> [!NOTE] 
> (※1) THINKLET AppSDKの 音声処理機能については，[こちら](https://github.com/FairyDevicesRD/thinklet.app.sdk?tab=readme-ov-file#%E8%A9%A6%E9%A8%93%E7%9A%84%E6%A9%9F%E8%83%BD-%E9%9F%B3%E5%A3%B0%E5%87%A6%E7%90%86) を確認ください．

## 5. 利用
### すでにCameraXを使っている場合
- 利用するには，すでにCameraXのRecorderを利用している場合は，ライブラリの導入と，`Recorder.Builder` に `setThinkletMic` を追加します．
  - （例）5chマイクで録音する
  ```diff
  val recorder = Recorder.Builder()
      .setExecutor(recorderExecutor)
      .setQualitySelector(QualitySelector.from(Quality.FHD))
  +   .setThinkletMic(ThinkletMics.FiveCh)
      .build()
  ```
### 新規の場合
- 例えば，[スタートガイド](https://fairydevicesrd.github.io/thinklet.app.developer/docs/startGuide/buildRecord/) を参考にCameraXを用いた録画アプリを作成し，`Recorder.Builder # setThinkletMic` を追加します． 
