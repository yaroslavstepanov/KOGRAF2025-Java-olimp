# HighlighterApp

Проект представляет собой приложение для обработки изображений с использованием пороговой бинаризации и морфологических операций.

## Структура проекта
HighlighterApp/
├─ .gradle/
├─ build/
├─ gradlew
├─ gradlew.bat
├─ settings.gradle
├─ build.gradle
└─ src/
└─ main/
└─ java/
└─ org/example/
├─ DragDropPanel.java
├─ MainFrame.java
├─ Morfologiya.java
└─ ObrabotkaIzobrazheniya.java

Copy

### Описание классов

- **DragDropPanel.java** — панель с пунктирной рамкой для перетаскивания (Drag & Drop) файла.
- **MainFrame.java** — главное окно приложения, содержащее вкладки «До»/«После», кнопки, слайдеры, чекбокс для морфологии и логику сохранения.
- **Morfologiya.java** — класс с морфологическими операциями (расширение, удаление длинных линий).
- **ObrabotkaIzobrazheniya.java** — реализует два алгоритма: наивный (простой порог) и морфологический (бинаризация + расширение + удаление линий), а также считает количество выделенных пикселей.

## Возможности приложения

1. **Режимы обработки**
   - **Простой пороговый**: пиксели, у которых средняя яркость (R+G+B)/3 превышает заданный порог, окрашиваются в жёлтый цвет.
   - **Морфологический**: после пороговой бинаризации к изображениям применяются расширение (объединяет белые фрагменты) и удаление длинных линий (уменьшает влияние дорог).

2. **Drag & Drop**
   - Пользователь может перетащить файл изображения прямо в окно приложения.

3. **Вкладки «До» и «После»**
   - Исходное изображение и результат обработки располагаются на разных вкладках, между которыми можно переключаться.

4. **Регулировка параметров**
   - Порог яркости (0–255).
   - Радиус расширения (0–5).
   - Длина удаления линий (0–100).
   - Ползунки для морфологии отображаются только при включённом чекбоксе «Морфология (расширение / удаление линий)».

5. **Сохранение результата**
   - Экспорт только в PNG или JPEG.
   - Приложение добавляет нужное расширение автоматически, если пользователь его не указал.

6. **Статистика**
   - Выводит время обработки в миллисекундах.
   - Показывает число «подсвеченных» (жёлтых) пикселей.

7. **Проверка перед загрузкой нового изображения**
   - Если у пользователя уже есть готовый результат, программа предложит сохранить его, чтобы избежать потери данных.

## Ограничения и замечания

- Алгоритм не предназначен для идеального распознавания сложных форм. Он использует простые морфологические операции и порог яркости, что достаточно для базового выделения.
- Для больших снимков (более нескольких тысяч пикселей по каждой стороне) время обработки может быть заметным.
- При работе в оконном режиме интерфейс оптимизирован, но если окно слишком сильно уменьшить, часть элементов может «прятаться» в сворачиваемых панелях или в полосах прокрутки.

## Обратная связь

Любые предложения по улучшению или сообщения об ошибках можно оставлять в разделе Issues или присылать Pull Request.

## Запуск приложения

1) Для сборки и запуска приложения требуется установленный Gradle. Используйте следующие команды:

```bash
./gradlew build
./gradlew run

