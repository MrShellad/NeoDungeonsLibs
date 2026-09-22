# NeoDungeonsLibs

适用于 Minecraft 1.21.1 NeoForge 平台的基础核心支持库。本模组为《我的世界：地下城》（Minecraft Dungeons）系列模组（包括 NeoDungeonsMobs 等）提供底层能力支持、实体架构与扩展系统。

---

## 目录

- [简介](#简介)
- [运行环境与依赖](#运行环境与依赖)
- [核心系统与功能](#核心系统与功能)
- [开发与构建指南](#开发与构建指南)
- [鸣谢与致敬](#鸣谢与致敬)
- [开源许可协议](#开源许可协议)

---

## 简介

NeoDungeonsLibs 是地下城系列模组的核心依赖库。项目源于 Patrigan 创作的 **Dungeons Libraries**，并经过 Firefox Salesman 的移植与现代化重构，现已完整适配 Minecraft 1.21.1 与 NeoForge 架构。

---

## 运行环境与依赖

| 依赖项 | 必需版本 / 建议版本 | 说明 |
| :--- | :--- | :--- |
| Minecraft | 1.21.1 | 基础游戏版本 |
| NeoForge | 21.1.137 或更高版本 | 模组加载器 |
| Java | Java 21 (JDK 21) | 运行环境 |
| GeckoLib (NeoForge) | 4.7.4 或更高版本 | 骨骼动画支持库 |
| Curios API (NeoForge) | 9.0.0+ (可选) | 提供饰品栏集成能力 |

---

## 核心系统与功能

NeoDungeonsLibs 集中封装了多项通用的实体能力与机制：

1. **仆从与首领系统 (Minion & Leader System)**：
   - 为实体提供随从追踪、保护领袖以及协同攻击的 AI 目标与数据能力（Follower / Leader Goals）。
2. **灵魂系统 (Soul System)**：
   - 提供灵魂计量槽、灵魂能量收集以及施法消耗能力（Soul Caster），支持地下城法术驱动。
3. **附魔与投掷物强化 (Enchanted Projectile & Built-in Enchantments)**：
   - 允许为投掷物与实体赋予自定义特殊附魔效果与弹道修正。
4. **精英怪物能力 (Elite Mob Capabilities)**：
   - 支持动态为生物附加精英词缀、粒子光环以及特殊抗性属性。
5. **UI 与动画计时器 (GUI & Animation Timers)**：
   - 统一管理地下城风格的快捷法宝栏渲染（Artifacts Bar）与通用关键帧动画计时器。

---

## 开发与构建指南

### 构建步骤
使用仓库自带的 Gradle Wrapper 进行构建：

```bash
# 构建整个库并生成 Jar 包
./gradlew build

# 仅打包 Jar 文件
./gradlew jar
```

构建完成的 Jar 文件保存在 `build/libs/` 目录下。

---

## 鸣谢与致敬

- **Mojang Studios**：Minecraft 与 Minecraft Dungeons 原作内容。
- **Patrigan**：Dungeons Libraries 与 Dungeons Mobs 的原作者。
- **Firefox Salesman**：维护并推进 Dungeons 系列模组的跨版本移植。
- **GeckoLib Team** 与 **NeoForged Team**：提供底层动画与模组加载支持。

---

## 开源许可协议

本项目遵循 **MIT 许可证**，保留原作者 Patrigan 的版权声明：

```text
MIT License

Copyright (c) 2022 Patrigan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

详情请参见 [LICENSE.txt](LICENSE.txt)。
