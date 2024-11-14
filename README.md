# THINKLET向けCameraX 拡張マイク
- THINKLET向けCameraX に適応可能な拡張マイクを提供します．
- 原則として，本プロジェクトでは，THINKLETのみ動作しますが，この実装を参考に，独自のマイク実装をCameraXに入れ込むことも可能です．
  - 例えば，音声認識と音声合成したデータを与えることで，理論上ノイズが一切入らない人の発話のみを録音する機能を実現できます．
## サンプル
### THINKLETのみ
- [MultiMicCameraXRecorder](./sample/MultiMicCameraXRecorder/README.md)
### THINKLET, Android向け
- [VadMicCameraXRecorder](./sample/VadMicCameraXRecorder/README.md)
## 導入手順
詳細については，サンプルを確認ください．
### 1. アクセストークン取得
- GitHub Packages経由でライブラリは取得できます．[こちら](https://docs.github.com/ja/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)を参考に個人用アクセス トークンを発行してください．
### 2. アクセストークンを設定
- クレデンシャルですので，Git管理しないように，`local.properties` などに保存を推奨します．
  ```
  TOKEN=<github token>
  USERNAME=<github username>
  ```
### 3. Projectレベルの `settings.gradle.kts` に設定
- GitHub Packages まで参照できるように設定を追加します．ここでは1のアクセストークンは，`local.properties` にあるものとしています．
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
- GradleのVersionによっては，Projectの `build.gradle`, `build.gradle.kts` に記載する場合もあります．
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
  +    // 代わりに，THINKLETカスタムのCamera－Videoを追加．
  +    implementation("ai.fd.thinklet:camerax-camera-video:$thinkletCameraX")

  +    val thinkletSdk = "0.1.6"
  +    val thinkletCameraXMic = "0.0.1"

  +    // THINKLET向けのマイクを追加
  +    implementation("ai.fd.thinklet:camerax-mic-core:$thinkletCameraXMic")
  +    // 5chを使用する場合
  +    implementation("ai.fd.thinklet:camerax-mic-multi-channel:$thinkletCameraXMic")
  +    // XFE を使用する場合
  +    implementation("ai.fd.thinklet:camerax-mic-xfe:$thinkletCameraXMic")
  +    implementation("ai.fd.thinklet:sdk-audio:$thinkletSdk")
   }
  ```
> [!IMPORTANT]
> `camera-view` を使う場合，AndroidX標準の `camera-video` を削除，置き換えする必要があります．（上記参考）  
> プロジェクトレベルで対応するには，[Sample](./sample/MultiMicCameraXRecorder/build.gradle.kts) をご確認ください．
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