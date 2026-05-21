# QQColorManager

Minecraft плагин для управления цветными заполнителями через PlaceholderAPI. Поддерживает MiniMessage, CMI форматы, градиенты и 140+ цветов.

## Ссылки

- Paper 1.21.1+: https://papermc.io/
- Java 21+: https://adoptium.net/
- PlaceholderAPI: https://www.spigotmc.org/resources/placeholderapi.6245/
- GitHub: https://github.com/AllFiRE/QQColorManager

## Требования

- Сервер: Paper 1.21.1 или выше
- Java: 21 или выше
- PlaceholderAPI (опционально, для заполнителей)

## Установка

1. Скачай JAR из раздела Releases
2. Помести файл в папку plugins/
3. Перезапусти сервер
4. Настрой config.yml
5. Выполни /qqcm reload

## Команды

### Цвета

/qqcm color set <id> <slot> <color> [player] [-s] - установить цвет
/qqcm color get <id> [player] [-s] - показать цвет
/qqcm color remove <id> <slot> [player] [-s] - удалить цвет

### Градиенты

/qqcm gradient set <id> <slot> <color> [player] [-s] - установить цвет градиента
/qqcm gradient get <id> [player] [-s] - показать градиент
/qqcm gradient remove <id> [player] [-s] - удалить градиент

### Управление

/qqcm info <player> [page] [-s] - информация об игроке
/qqcm clear [player] [-s] - очистить все данные игрока
/qqcm list - список всех шаблонов
/qqcm reload - перезагрузить конфиг
/qqcm version - версия плагина
/qqcm placeholders - список заполнителей

Параметры:
[player] - применить к другому игроку
[-s] - silent режим (без вывода сообщений)
[page] - номер страницы для пагинации

## Заполнители PlaceholderAPI

### Цвета (одиночные)

%qqcm_color_<id>_<slot>_<fallback>%

Пример: %qqcm_color_nickname_1_&7%

### Градиенты

%qqcm_gradient_<id>_start_<fallback>%
%qqcm_gradient_<id>_end_<fallback>%

Пример: %qqcm_gradient_grad3_start_&7%Текст%qqcm_gradient_grad3_end_&7%

## Поддерживаемые форматы цветов

HEX с #: #FF5555
HEX без #: FF5555
Название цвета: red, lime, hotpink
CMI: {#FF5555}, {#FF5555>}, {#lime}
Квадратные скобки: [#FF5555], [#lime]
Угловые скобки: <FF5555>, <lime>
Решётка + название: #lime, #hotpink
Vanilla код: &c, &6, &a
Vanilla RGB: &x&F&F&F&F&F&F
MiniMessage: <color:#FF5555>

Поддерживается 140+ HTML цветов: red, blue, green, yellow, orange, pink, purple, brown, gray, white и все их оттенки (light, dark, medium, pale)

## Пример конфигурации

database:
  type: h2
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

## Права

qqcm.color.set - установка цвета себе
qqcm.color.set.other - установка цвета другим
qqcm.color.get - просмотр своего цвета
qqcm.color.get.other - просмотр цвета других
qqcm.color.remove - удаление своего цвета
qqcm.color.remove.other - удаление цвета у других

qqcm.gradient.set - установка градиента себе
qqcm.gradient.set.other - установка градиента другим
qqcm.gradient.get - просмотр своего градиента
qqcm.gradient.get.other - просмотр градиента других
qqcm.gradient.remove - удаление своего градиента
qqcm.gradient.remove.other - удаление градиента у других

qqcm.clear - очистка своих данных
qqcm.clear.other - очистка данных других

qqcm.info - просмотр своей информации
qqcm.info.other - просмотр информации других

qqcm.list - список шаблонов
qqcm.reload - перезагрузка конфига
qqcm.version - просмотр версии
qqcm.use - базовый доступ

## Типы хранилищ

H2 - для небольших и средних серверов (рекомендуется)
MySQL - для больших серверов и сетей
YAML - для тестирования и очень маленьких серверов

## Примеры использования

### Цветной ник

В config.yml:
colors:
  nickname:
    1: "<color:#$1>"

Команда:
/qqcm color set nickname 1 #FF5555

Заполнитель:
%qqcm_color_nickname_1_&7%%player_name%

### Градиент в табе

В config.yml:
gradients:
  tab:
    slots: 3
    format_start: "<gradient:#$1:#$2:#$3>"
    format_end: "</gradient>"

Команды:
/qqcm gradient set tab 1 #FF0000
/qqcm gradient set tab 2 #00FF00
/qqcm gradient set tab 3 #0000FF

Заполнитель:
%qqcm_gradient_tab_start_&7%Welcome%qqcm_gradient_tab_end_&7%

## Сборка из исходников

git clone https://github.com/AllFiRE/QQColorManager.git
cd QQColorManager
mvn clean package

Готовый JAR находится в target/QQColorManager-1.0.0.jar

## Лицензия

MIT License - свободно для личного и коммерческого использования.

## Автор

AllFiRE

GitHub: https://github.com/AllFiRE
