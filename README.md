# 消息哨兵 (NotiGuard)

一款 Android 原生（Kotlin + Jetpack Compose）移动应用：实时监听**系统通知**与**短信**，对用户自定义的关键字进行匹配，命中后通过**系统通知 / 应用内悬浮窗弹窗 / 声音 / 震动**立即提醒。配置项完全开放：可自由设置监听来源、关键字列表与提醒方式。

> ⚠️ **平台说明**：读取其它 App 的通知内容、读取短信，是 Android 才提供的系统能力。iOS 出于隐私沙盒限制，第三方 App 无法访问其它 App 的通知与短信，因此本应用**仅支持 Android**（minSdk 26 / Android 8.0+），无法在 iOS 上等价运行。

---

## 功能特性

- **多来源监听**
  - 系统通知：基于 `NotificationListenerService`，覆盖所有已授权 App
  - 短信：基于 `ContentObserver` 监听收件箱（`READ_SMS`）
- **关键字匹配引擎**（纯函数，可单测）
  - 包含（忽略大小写）
  - 完全相等
  - 正则表达式
- **多渠道提醒**
  - 系统通知（高优先级、可绕过勿扰）
  - 应用内悬浮窗弹窗（`SYSTEM_ALERT_WINDOW`，8 秒自动消失）
  - 声音、震动可独立开关
- **自定义配置**
  - 监听来源开关（按 App 维度）
  - 关键字列表增删改、启停、匹配模式
  - 提醒方式自由组合
- **命中记录**：时间线形式留存，可逐条删除或一键清空
- **扁平化 UI**：Material 3，浅灰背景 + 靛蓝主色，卡片留白、交互轻量

---

## 架构

```
com.mass.notiguard
├── NotiGuardApplication.kt        # 应用入口，持有 DB / Settings / AlertEngine
├── domain/                        # 平台无关核心
│   ├── MatchMode / SourceType     # 领域枚举
│   ├── KeywordMatcher.kt          # 关键字匹配引擎（纯函数）
│   ├── AlertEngine.kt             # 来源→匹配→记录+提醒 统一编排
│   ├── Notifier.kt                # 系统通知通道与构造
│   ├── Ringer.kt                  # 声音 / 震动
│   └── OverlayLauncher.kt         # 启动悬浮窗
├── data/                          # 数据层
│   ├── entity/                    # KeywordRule / MonitoredSource / AlertRecord
│   ├── dao/                       # Room DAO
│   ├── AppDatabase.kt
│   ├── Converters.kt
│   └── SettingsRepository.kt      # DataStore 全局开关
├── service/
│   ├── NotificationListener.kt    # 系统通知监听
│   ├── SmsObserver.kt             # 短信监听
│   ├── GuardForegroundService.kt  # 前台保活 + 短信监听
│   └── AlertOverlayService.kt     # 命中时悬浮窗弹窗
├── receiver/BootReceiver.kt       # 开机自启
└── ui/                            # Jetpack Compose 界面
    ├── theme/                     # 配色 + 悬浮窗卡片
    ├── components/                # 通用卡片 / 开关行
    ├── screens/                   # 首页 / 来源 / 关键字 / 记录
    └── viewmodel/                 # 各页 ViewModel
```

**数据流**：`通知/短信 → Service → AlertEngine →（关键字匹配）→ 写库 + 多渠道提醒`

---

## 所需权限

| 权限 | 用途 | 获取方式 |
| --- | --- | --- |
| `BIND_NOTIFICATION_LISTENER_SERVICE` | 监听系统通知 | 在「设置 → 通知使用权」中手动开启（首页按钮跳转） |
| `READ_SMS` | 读取短信 | 运行时弹窗授权 |
| `POST_NOTIFICATIONS` | 发送提醒通知 | 运行时弹窗授权（Android 13+） |
| `SYSTEM_ALERT_WINDOW` | 应用内悬浮窗弹窗 | 在「设置 → 应用 → 悬浮窗」手动开启（首页按钮跳转） |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_DATA_SYNC` | 前台保活短信监听 | 声明即授予 |
| `VIBRATE` / `RECEIVE_BOOT_COMPLETED` | 震动 / 开机自启 | 普通权限 |

> 通知监听与悬浮窗属于敏感权限，必须由用户手动在系统设置中授予；App 首次进入首页会展示授权引导。

---

## 构建与运行

**方式一（推荐）：Android Studio**
1. 用 Android Studio（Hedgehog / Iguana 及以上，AGP 8.5+）打开 `NotiGuard` 目录
2. 等待 Gradle 同步（会自动下载 `gradle-8.9` 与依赖）
3. 连接 Android 8.0+ 真机（模拟器无短信、通知监听受限），点击 ▶ Run

**方式二：命令行**
```bash
cd NotiGuard
./gradlew assembleDebug        # Linux / macOS
gradlew.bat assembleDebug      # Windows
```
> 若本地没有 Gradle Wrapper，先执行 `gradle wrapper`（需已安装 Gradle 8.9）。

---

## 使用步骤

1. 打开 App，首页「监听总开关」置开
2. 按提示授予 **通知监听权限** 与 **悬浮窗权限**
3. 「来源」页勾选需要监听的 App（短信在首页开关）
4. 「关键字」页添加关键字（如 `验证码`、`快递`、`美团`、或正则 `\d{6}`）
5. 在首页设置提醒方式（通知 / 弹窗 / 声音 / 震动）
6. 当被监听来源出现含关键字的消息，立即收到提醒；命中记录可在「记录」页查看

---

## 已知限制与合规提示

- **仅 Android**：iOS 无法等价实现（系统隐私沙盒限制）。
- **通知内容完整性**：部分 App（如微信）会隐藏通知文本，需在系统设置中允许该 App 显示完整通知内容，否则只能拿到标题。
- **前台服务保活**：后台进程可能被厂商 ROM 杀掉，建议在电池优化中设为「不受限制」。
- **合规**：请仅用于本人设备的合法提醒场景；读取短信/通知涉及隐私，勿在未经授权的情况下用于监控他人设备。
- **短信正则**：`REGEX` 模式对非法表达式会安全跳过（不会崩溃）。

---

## 技术栈

- Kotlin 1.9.24 · AGP 8.5.2 · Gradle 8.9
- Jetpack Compose (BOM 2024.06.00) · Material 3
- Room 2.6.1 · DataStore 1.1.1 · Navigation Compose 2.7.7
- Lifecycle / ViewModel / Coroutines
- minSdk 26 · targetSdk 34
