# KOGRAF2025-Java-olimp

Приложение на Java для подсвечивания (выделения жёлтым цветом) высоких или рукотворных объектов на монохромных снимках. Позволяет использовать либо простой пороговый алгоритм, либо расширенный метод с морфологическими операциями (расширение, удаление длинных линий).
Структура проекта
css
Копировать код

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
            
•	DragDropPanel.java — панель с пунктирной рамкой для перетаскивания (Drag & Drop) файла.
•	MainFrame.java — главное окно приложения, содержащие вкладки «До»/«После», кнопки, слайдеры, чекбокс для морфологии и логику сохранения.
•	Morfologiya.java — класс с морфологическими операциями (расширение, удаление длинных линий).
•	ObrabotkaIzobrazheniya.java — реализует два алгоритма: наивный (простой порог) и морфологический (бинаризация + расширение + удаление линий), а также считает количество выделенных пикселей.
Возможности приложения
1.	Режимы обработки
o	Простой пороговый: пиксели, у которых средняя яркость (R+G+B)/3 превышает заданный порог, окрашиваются в жёлтый цвет.
o	Морфологический: после пороговой бинаризации к изображениям применяются расширение (объединяет белые фрагменты) и удаление длинных линий (уменьшает влияние дорог).
2.	Drag & Drop
o	Пользователь может перетащить файл изображения прямо в окно приложения.
3.	Вкладки «До» и «После»
o	Исходное изображение и результат обработки располагаются на разных вкладках, между которыми можно переключаться.
4.	Регулировка параметров
o	Порог яркости (0–255).
o	Радиус расширения (0–5).
o	Длина удаления линий (0–100).
o	Ползунки для морфологии отображаются только при включённом чекбоксе «Морфология (расширение / удаление линий)».
5.	Сохранение результата
o	Экспорт только в PNG или JPEG.
o	Приложение добавляет нужное расширение автоматически, если пользователь его не указал.
6.	Статистика
o	Выводит время обработки в миллисекундах.
o	Показывает число «подсвеченных» (жёлтых) пикселей.
7.	Проверка перед загрузкой нового изображения
o	Если у пользователя уже есть готовый результат, программа предложит сохранить его, чтобы избежать потери данных.
