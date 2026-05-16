# PhotoSuit Android

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="PhotoSuit Android 应用图标" width="128" height="128" />
</p>

PhotoSuit Android 是一款面向摄影照片后期展示的 Android 应用，聚焦于照片水印边框、拍摄参数展示和 LLM 风格化处理。应用可以读取照片 EXIF 信息，将相机型号、镜头、焦距、光圈、快门、ISO、拍摄时间等信息渲染到照片边框中，并支持通过大模型配置与对话能力辅助生成风格化内容。

## 主要功能

- 选择本地照片并进入编辑流程
- 读取照片 EXIF 拍摄参数
- 为照片生成带边框的水印图
- 支持相机型号、镜头、焦距、光圈、快门、ISO、拍摄时间等信息展示
- 支持不同边框样式，包括普通信息栏、全边框和胶片风格
- 支持水印配置、样式预设和本地持久化
- 支持 LLM 配置管理与对话入口
- 支持设置页面集中管理应用配置

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Hilt
- Room
- Retrofit / OkHttp
- Coil
- AndroidX ExifInterface
- DataStore
- Gradle Kotlin DSL

## 项目结构

```text
app/src/main/java/win/liuping/photosuit_android/
├── data/          # 本地数据库、DAO、远程 API、Repository
├── di/            # Hilt 依赖注入模块
├── domain/        # 领域模型
├── ui/            # Compose 页面、导航和主题
└── util/          # EXIF 读取与水印渲染工具
```

## 构建环境

- Android Gradle Plugin：8.13.2
- Kotlin：2.0.21
- compileSdk：36
- minSdk：24
- 建议使用 JDK 17 构建

## 本地构建

```bash
./gradlew assembleDebug
```

构建 release APK 和 AAB：

```bash
./gradlew assembleRelease bundleRelease
```

## GitHub Release

项目已配置 GitHub Actions，在推送 git tag 时自动构建 release 产物并创建 GitHub Release。

示例：

```bash
git tag v1.0.0
git push origin v1.0.0
```

触发后会生成并上传：

- `app/build/outputs/apk/release/*.apk`
- `app/build/outputs/bundle/release/*.aab`

## 许可证

当前仓库尚未声明许可证。如需开源发布，请先补充 LICENSE 文件。
