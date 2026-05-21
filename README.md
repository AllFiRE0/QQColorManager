```markdown
<div align="center">

# 🎨 QQColorManager

**Управление цветными заполнителями для Minecraft Paper 1.21.1+**

[![Paper](https://img.shields.io/badge/Paper-1.21.1%2B-blue?logo=paper&logoColor=white)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21%2B-orange?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![PlaceholderAPI](https://img.shields.io/badge/PlaceholderAPI-2.11.6%2B-green?logo=curseforge&logoColor=white)](https://www.spigotmc.org/resources/placeholderapi.6245/)
[![License](https://img.shields.io/badge/License-MIT-yellow?logo=opensourceinitiative&logoColor=white)](LICENSE)

[![Build](https://github.com/AllFiRE/QQColorManager/actions/workflows/build.yml/badge.svg)](https://github.com/AllFiRE/QQColorManager/actions/workflows/build.yml)
[![Release](https://img.shields.io/github/v/release/AllFiRE/QQColorManager?logo=github&logoColor=white)](https://github.com/AllFiRE/QQColorManager/releases)
[![Downloads](https://img.shields.io/github/downloads/AllFiRE/QQColorManager/total?logo=github&logoColor=white)](https://github.com/AllFiRE/QQColorManager/releases)

</div>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎨 **Color Templates** | Create unlimited color patterns with multiple slots |
| 🌈 **Gradients** | Full gradient support with custom fallback colors |
| 💾 **Multiple Storage** | YAML, H2 (embedded), and MySQL support |
| 🔌 **PlaceholderAPI** | Full integration with tab completion |
| 🎯 **Smart Parsing** | Supports HEX, names, Vanilla codes, CMI formats |
| 📋 **Tab Completion** | Auto-complete for all commands and arguments |
| 📄 **Pagination** | Paginated player info display |
| 🔒 **Permissions** | Granular permission system |

---

## 📋 Requirements

- **Server:** Paper 1.21.1 or higher
- **Java:** 21 or higher
- **Optional:** PlaceholderAPI (for placeholders)

---

## 📥 Installation

```bash
1. Download from Releases
2. Place JAR in plugins/ folder
3. Restart server
4. Configure config.yml
5. Run /qqcm reload
```

---

## 🎮 Commands

### Colors
```bash
/qqcm color set <id> <slot> <color> [player] [-s]    # Set color
/qqcm color get <id> [player] [-s]                   # Get color
/qqcm color remove <id> <slot> [player] [-s]         # Remove color
```

### Gradients
```bash
/qqcm gradient set <id> <slot> <color> [player] [-s]   # Set gradient color
/qqcm gradient get <id> [player] [-s]                  # Get gradient
/qqcm gradient remove <id> [player] [-s]               # Remove gradient
```

### Management
```bash
/qqcm info <player> [page] [-s]      # Show player info
/qqcm clear [player] [-s]            # Clear all player data
/qqcm list                           # List all templates
/qqcm reload                         # Reload configuration
/qqcm version                        # Show version
```

> **Parameters:** `[player]` - apply to another player | `[-s]` - silent mode | `[page]` - pagination page

---

## 🔌 Placeholders

### Single Colors
```
%qqcm_color_<id>_<slot>_<fallback>%
```

**Example:** `%qqcm_color_nickname_1_&7%`

### Gradients
```
%qqcm_gradient_<id>_start_<fallback>%
%qqcm_gradient_<id>_end_<fallback>%
```

**Example:** `%qqcm_gradient_grad3_start_&7%Text%qqcm_gradient_grad3_end_&7%`

---

## 🎨 Supported Color Formats

| Format | Example | Result |
|--------|---------|--------|
| HEX with # | `#FF5555` | `FF5555` |
| HEX without # | `FF5555` | `FF5555` |
| Color name | `red`, `lime` | `FF5555`, `55FF55` |
| CMI | `{#FF5555}`, `{#lime}` | `FF5555`, `55FF55` |
| Square brackets | `[#FF5555]`, `[#lime]` | `FF5555`, `55FF55` |
| Angle brackets | `<FF5555>`, `<lime>` | `FF5555`, `55FF55` |
| Hash + name | `#lime`, `#hotpink` | `55FF55`, `FF69B4` |
| Vanilla code | `&c`, `&6` | `FF5555`, `FFAA00` |
| Vanilla RGB | `&x&F&F&F&F&F&F` | `FFFFFF` |
| MiniMessage | `<color:#FF5555>` | `FF5555` |

**140+ HTML colors supported:** red, blue, green, yellow, orange, pink, purple, brown, gray, white and all their shades (light, dark, medium, pale, etc.)

---

## ⚙️ Configuration Example

```yaml
database:
  type: h2  # h2, mysql, yaml
  h2:
    file: data/colors

colors:
  nickname:
    1: "<color:#$1>"
    input_regex: "^#?[A-Fa-f0-9]{6}$"
  chat:
    1: "{#$1>}"
    2: "{#$2<}"

gradients:
  gradient3:
    slots: 3
    format_start: "<gradient:#$1:#$2:#$3>"
    format_end: "</gradient>"
    fallback_colors:
      1: "FFFFFF"
      2: "FFFFFF"
      3: "FFFFFF"
```

---

## 🔐 Permissions

| Permission | Description |
|------------|-------------|
| `qqcm.color.set` | Set own color |
| `qqcm.color.set.other` | Set color for others |
| `qqcm.gradient.set` | Set own gradient |
| `qqcm.gradient.set.other` | Set gradient for others |
| `qqcm.clear` | Clear own data |
| `qqcm.clear.other` | Clear others data |
| `qqcm.info` | View own info |
| `qqcm.info.other` | View others info |
| `qqcm.reload` | Reload config |
| `qqcm.list` | List templates |
| `qqcm.version` | View version |

---

## 💾 Storage Types

| Type | Best For | Pros | Cons |
|------|----------|------|------|
| **H2** | Small to medium servers | Zero config, fast, portable | Single server only |
| **MySQL** | Large servers, networks | Scalable, multi-server | Requires setup |
| **YAML** | Testing, tiny servers | Simple, human-readable | Slow for large data |

---

## 🛠️ Building from Source

```bash
git clone https://github.com/AllFiRE/QQColorManager.git
cd QQColorManager
mvn clean package
```

Output: `target/QQColorManager-1.0.0.jar`

---

## 📝 Examples

### Colored Nickname
```yaml
# config.yml
colors:
  nickname:
    1: "<color:#$1>"
```

```bash
/qqcm color set nickname 1 #FF5555
```

```
%qqcm_color_nickname_1_&7%%player_name%
```

### Gradient Tab Header
```yaml
# config.yml
gradients:
  tab:
    slots: 3
    format_start: "<gradient:#$1:#$2:#$3>"
    format_end: "</gradient>"
```

```bash
/qqcm gradient set tab 1 #FF0000
/qqcm gradient set tab 2 #00FF00
/qqcm gradient set tab 3 #0000FF
```

```
%qqcm_gradient_tab_start_&7%Welcome%qqcm_gradient_tab_end_&7%
```

---

## 📄 License

**MIT License** - Free for personal and commercial use.

---

## 👤 Author

**AllFiRE**

[![GitHub](https://img.shields.io/badge/GitHub-AllFiRE-181717?logo=github)](https://github.com/AllFiRE)

---

<div align="center">
  
**⭐ Star this repository if you find it useful! ⭐**

</div>
```
