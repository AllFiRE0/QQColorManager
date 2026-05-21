```markdown
# QQColorManager

[![Build](https://github.com/AllFiRE/QQColorManager/actions/workflows/build.yml/badge.svg)](https://github.com/AllFiRE/QQColorManager/actions/workflows/build.yml)
[![Version](https://img.shields.io/github/v/release/AllFiRE/QQColorManager)](https://github.com/AllFiRE/QQColorManager/releases)
[![Paper 1.21](https://img.shields.io/badge/Paper-1.21.1%2B-blue)](https://papermc.io/)
[![Java 21](https://img.shields.io/badge/Java-21%2B-orange)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

**QQColorManager** — мощный Minecraft плагин для управления цветными заполнителями через PlaceholderAPI. Поддерживает MiniMessage, CMI форматы, градиенты и множество цветовых форматов.

## ✨ Особенности

- 🎨 **Гибкие шаблоны** — создавайте неограниченное количество цветовых и градиентных шаблонов
- 🌈 **Градиенты** — поддержка градиентов с произвольным количеством цветов и fallback-значениями
- 💾 **Несколько хранилищ** — YAML, H2 (встроенная), MySQL
- 🔌 **PlaceholderAPI** — полная интеграция с автодополнением заполнителей
- 🎯 **Умный парсинг** — поддерживает HEX, Vanilla коды, названия цветов, CMI форматы
- 📋 **Tab Completion** — автодополнение для всех команд и аргументов
- 📄 **Пагинация** — удобный просмотр информации об игроке
- 🔒 **Права** — гибкая система разрешений для каждой команды

## 📋 Требования

- **Сервер**: Paper 1.21.1+
- **Java**: 21 или выше
- **Плагины**: PlaceholderAPI (опционально, для заполнителей)

## 📥 Установка

1. Скачай последний релиз из [Releases](https://github.com/AllFiRE/QQColorManager/releases)
2. Помести `QQColorManager-1.0.0.jar` в папку `plugins/`
3. Перезапусти сервер или выполни `plugman load QQColorManager`
4. Настрой `config.yml` под свои нужды
5. Выполни `/qqcm reload`

## ⚙️ Конфигурация

### Базовый `config.yml`

```yaml
# Тип базы данных: h2, mysql, yaml
database:
  type: h2
  h2:
    file: data/colors

# Шаблоны цветов
colors:
  nickname:
    1: "<color:#$1>"
    input_regex: "^#?[A-Fa-f0-9]{6}$"
  chat:
    1: "{#$1>}"
    2: "{#$2<}"

# Шаблоны градиентов
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

## 🎮 Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/qqcm color set <id> <slot> <color> [player] [-s]` | Установить цвет | `qqcm.color.set` |
| `/qqcm color get <id> [player] [-s]` | Показать цвет | `qqcm.color.get` |
| `/qqcm color remove <id> <slot> [player] [-s]` | Удалить цвет | `qqcm.color.remove` |
| `/qqcm gradient set <id> <slot> <color> [player] [-s]` | Установить цвет градиента | `qqcm.gradient.set` |
| `/qqcm gradient get <id> [player] [-s]` | Показать градиент | `qqcm.gradient.get` |
| `/qqcm gradient remove <id> [player] [-s]` | Удалить градиент | `qqcm.gradient.remove` |
| `/qqcm info <player> [page] [-s]` | Информация об игроке | `qqcm.info` |
| `/qqcm clear [player] [-s]` | Очистить все данные | `qqcm.clear` |
| `/qqcm list` | Список шаблонов | `qqcm.list` |
| `/qqcm reload` | Перезагрузить конфиг | `qqcm.reload` |
| `/qqcm version` | Версия плагина | `qqcm.version` |
| `/qqcm placeholders` | Список заполнителей | `qqcm.use` |

**Параметры:**
- `[player]` — для применения команды к другому игроку (требует права `.other`)
- `[-s]` — silent режим, подавляет вывод сообщений
- `[page]` — номер страницы для пагинации

## 🔌 Заполнители PlaceholderAPI

### Цвета (одиночные)
```
%qqcm_color_<id>_<slot>_<fallback>%
```
**Пример:** `%qqcm_color_nickname_1_&7%`

### Градиенты
```
%qqcm_gradient_<id>_start_<fallback>%
%qqcm_gradient_<id>_end_<fallback>%
```
**Пример:** `%qqcm_gradient_gradient3_start_&7%Текст%qqcm_gradient_gradient3_end_&7%`

## 🎨 Поддерживаемые форматы цветов

| Формат | Пример |
|--------|--------|
| HEX с # | `#FF5555` |
| HEX без # | `FF5555` |
| Название цвета | `red`, `lime`, `hotpink` |
| CMI | `{#FF5555}`, `{#FF5555>}`, `{#lime}` |
| Квадратные скобки | `[#FF5555]`, `[#lime]` |
| Угловые скобки | `<FF5555>`, `<lime>` |
| Решётка + название | `#lime`, `#hotpink` |
| Vanilla код | `&c`, `&6`, `&a` |
| Vanilla RGB | `&x&F&F&F&F&F&F` |
| MiniMessage | `<color:#FF5555>` |

**140+ HTML цветов:** `red`, `blue`, `green`, `yellow`, `orange`, `pink`, `purple`, `brown`, `gray`, `white` и их оттенки (light, dark, medium, pale и т.д.)

## 🔐 Права

```yaml
# Цвета
qqcm.color.set           # Установка цвета себе
qqcm.color.set.other     # Установка цвета другим
qqcm.color.get           # Просмотр своего цвета
qqcm.color.get.other     # Просмотр цвета других
qqcm.color.remove        # Удаление своего цвета
qqcm.color.remove.other  # Удаление цвета у других

# Градиенты
qqcm.gradient.set        # Установка градиента себе
qqcm.gradient.set.other  # Установка градиента другим
qqcm.gradient.get        # Просмотр своего градиента
qqcm.gradient.get.other  # Просмотр градиента других
qqcm.gradient.remove     # Удаление своего градиента
qqcm.gradient.remove.other # Удаление градиента у других

# Управление данными
qqcm.clear               # Очистка своих данных
qqcm.clear.other         # Очистка данных других

# Информация
qqcm.info                # Просмотр своей информации
qqcm.info.other          # Просмотр информации других

# Администрирование
qqcm.list                # Список шаблонов
qqcm.reload              # Перезагрузка конфига
qqcm.version             # Просмотр версии
qqcm.use                 # Базовый доступ
```

## 💾 Хранение данных

Поддерживаются три типа хранилищ:

### YAML (для небольших серверов)
```yaml
database:
  type: yaml
  yaml:
    file: data.yml
```

### H2 (рекомендуется)
```yaml
database:
  type: h2
  h2:
    file: data/colors
```

### MySQL (для больших серверов)
```yaml
database:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: qqcolor
    user: root
    password: ""
    pool-size: 10
```

## 🛠️ Примеры использования

### 1. Простой цвет ника
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

### 2. CMI обёртка для чата
```yaml
# config.yml
colors:
  chat:
    1: "{#$1>}"
    2: "{#$2<}"
```
```bash
/qqcm color set chat 1 #FF5555
/qqcm color set chat 2 #000000
```
```
%qqcm_color_chat_1_&f%%player_name%%qqcm_color_chat_2_&f%
```

### 3. Градиент в табе
```yaml
# config.yml
gradients:
  tab_gradient:
    slots: 3
    format_start: "<gradient:#$1:#$2:#$3>"
    format_end: "</gradient>"
    fallback_colors:
      1: "FFFFFF"
      2: "FFFFFF"
      3: "FFFFFF"
```
```bash
/qqcm gradient set tab_gradient 1 #FF0000
/qqcm gradient set tab_gradient 2 #00FF00
/qqcm gradient set tab_gradient 3 #0000FF
```
```
%qqcm_gradient_tab_gradient_start_&7%%player_name%%qqcm_gradient_tab_gradient_end_&7%
```

## 🏗️ Сборка из исходников

```bash
git clone https://github.com/AllFiRE/QQColorManager.git
cd QQColorManager
mvn clean package
```

Готовый JAR находится в `target/QQColorManager-1.0.0.jar`

## 📝 Лицензия

MIT License

## 👤 Автор

**AllFiRE**

- GitHub: [@AllFiRE](https://github.com/AllFiRE)

---

## ⭐ Поддержка

Если тебе нравится плагин, поставь звезду на GitHub! Это помогает проекту развиваться. 🚀
```

Этот README содержит:
- Описание и особенности
- Требования и установку
- Полную конфигурацию
- Все команды с правами
- Заполнители с примерами
- Поддерживаемые форматы цветов
- Типы хранилищ
- Примеры использования
- Сборку из исходников
- Лицензию и контакты
