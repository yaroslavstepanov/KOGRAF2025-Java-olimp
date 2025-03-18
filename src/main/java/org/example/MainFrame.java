package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Главное окно с вкладками "До" и "После", чекбоксом "Включить морфологию",
 * закруглёнными кнопками и т.д.
 */
public class MainFrame extends JFrame {

    private BufferedImage originalImage;
    private BufferedImage processedImage;

    // Вкладки: "До" (оригинал) и "После" (результат)
    private JTabbedPane tabbedPane;
    private JLabel originalLabel;
    private JLabel processedLabel;

    // Панель для настроек
    private JCheckBox morphCheck;
    private JSlider thresholdSlider;
    private JSlider radiusSlider;
    private JSlider lineSlider;

    // Статистика и время
    private JLabel timeLabel;
    private JLabel statsLabel;

    // Выбор формата сохранения
    private JComboBox<String> formatCombo;

    public MainFrame() {
        super("Highlighter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Верхняя часть: кнопки и прочие элементы в одной строке
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        topPanel.setBackground(new Color(230, 230, 230));

        // Кнопка "Загрузить"
        JButton loadBtn = createStyledButton("Загрузить");
        loadBtn.addActionListener(this::onLoadFile);

        // Чекбокс "Включить морфологию"
        morphCheck = new JCheckBox("Морфология (расширение / удаление линий)");
        morphCheck.setOpaque(false);
        morphCheck.addItemListener(this::onMorphCheckChanged);

        // Кнопка "Обработать"
        JButton processBtn = createStyledButton("Обработать");
        processBtn.addActionListener(this::onProcess);

        // Выбор формата
        formatCombo = new JComboBox<>(new String[] { "PNG", "JPEG" });

        // Кнопка "Сохранить"
        JButton saveBtn = createStyledButton("Сохранить");
        saveBtn.addActionListener(this::onSave);

        timeLabel = new JLabel("Время: —");
        statsLabel = new JLabel("Выделено: 0");

        // Добавляем элементы на верхнюю панель
        topPanel.add(loadBtn);
        topPanel.add(morphCheck);
        topPanel.add(processBtn);
        topPanel.add(new JLabel("Формат:"));
        topPanel.add(formatCombo);
        topPanel.add(saveBtn);
        topPanel.add(timeLabel);
        topPanel.add(statsLabel);

        add(topPanel, BorderLayout.NORTH);

        // Левая панель: слайдеры в BoxLayout (вертикально)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Порог
        thresholdSlider = createSlider(0, 255, 128, "Порог");
        leftPanel.add(new JLabel("Порог (0..255):"));
        leftPanel.add(thresholdSlider);
        leftPanel.add(Box.createVerticalStrut(20));

        // Радиус расширения
        radiusSlider = createSlider(0, 5, 2, "Радиус расширения");
        leftPanel.add(new JLabel("Радиус расширения (0..5):"));
        leftPanel.add(radiusSlider);
        leftPanel.add(Box.createVerticalStrut(20));

        // Длина удаления линий
        lineSlider = createSlider(0, 100, 50, "Длина удаления линий");
        leftPanel.add(new JLabel("Длина удаления линий (0..100):"));
        leftPanel.add(lineSlider);

        add(leftPanel, BorderLayout.WEST);

        // По умолчанию скрываем ползунки морфологии
        updateMorphSlidersVisibility(false);

        // Вкладки (tabbedPane)
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        // Вкладка "До" (оригинал)
        JPanel tabOriginal = new JPanel(new BorderLayout());
        originalLabel = new JLabel("Исходное изображение", SwingConstants.CENTER);
        tabOriginal.add(new DragDropPanel(img -> {
            checkSaveBeforeNewImage();
            originalImage = img;
            processedImage = null;
            updateTabs();
        }), BorderLayout.NORTH);
        tabOriginal.add(originalLabel, BorderLayout.CENTER);

        // Вкладка "После" (результат)
        JPanel tabProcessed = new JPanel(new BorderLayout());
        processedLabel = new JLabel("Результат", SwingConstants.CENTER);
        tabProcessed.add(processedLabel, BorderLayout.CENTER);

        tabbedPane.addTab("До", tabOriginal);
        tabbedPane.addTab("После", tabProcessed);

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Создаёт стилизованную кнопку с закруглённой границей и голубым фоном.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(100, 150, 255));
        btn.setForeground(Color.WHITE);
        btn.setBorder(new RoundedBorder(10)); // радиус 10
        return btn;
    }

    /**
     * Кастомная граница для закруглённых углов.
     */
    private static class RoundedBorder implements Border {
        private final int radius;
        public RoundedBorder(int radius) {
            this.radius = radius;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.GRAY);
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius+2, radius+2, radius+2, radius+2);
        }
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private JSlider createSlider(int min, int max, int value, String toolTip) {
        JSlider slider = new JSlider(min, max, value);
        slider.setMajorTickSpacing((max - min) / 4);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setToolTipText(toolTip);
        slider.addChangeListener(this::onSliderChange);
        return slider;
    }

    private void onSliderChange(ChangeEvent e) {
        // Не пересчитываем автоматически, чтобы не тормозить.
        // Можно было бы сразу вызвать onProcess(null).
    }

    /**
     * Включение/выключение морфологии.
     */
    private void onMorphCheckChanged(ItemEvent e) {
        boolean sel = (e.getStateChange() == ItemEvent.SELECTED);
        updateMorphSlidersVisibility(sel);
    }

    /**
     * Скрываем или показываем ползунки для морфологии.
     */
    private void updateMorphSlidersVisibility(boolean visible) {
        radiusSlider.setVisible(visible);
        lineSlider.setVisible(visible);
    }

    private void onLoadFile(ActionEvent e) {
        // Если уже есть обработанный результат, предложим сохранить
        checkSaveBeforeNewImage();

        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                originalImage = ImageIO.read(file);
                processedImage = null;
                updateTabs();
            } catch (Exception ex) {
                showError("Ошибка загрузки: " + ex.getMessage());
            }
        }
    }

    /**
     * Проверяем, не нужно ли сохранить текущий результат, прежде чем заменить изображение.
     */
    private void checkSaveBeforeNewImage() {
        if (processedImage != null) {
            int ans = JOptionPane.showConfirmDialog(
                    this,
                    "Сохранить текущее обработанное изображение?",
                    "Сохранение",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            if (ans == JOptionPane.YES_OPTION) {
                onSave(null);
            } else if (ans == JOptionPane.CANCEL_OPTION) {
                throw new RuntimeException("Отмена загрузки нового изображения");
            }
        }
    }

    private void onProcess(ActionEvent e) {
        if (originalImage == null) {
            showError("Сначала загрузите или перетащите изображение (во вкладку \"До\")");
            return;
        }

        int threshold = thresholdSlider.getValue();
        long start = System.nanoTime();

        ObrabotkaIzobrazheniya.RezultatObrabotki result;
        if (morphCheck.isSelected()) {
            int radius = radiusSlider.getValue();
            int length = lineSlider.getValue();
            result = ObrabotkaIzobrazheniya.osvetitSMorfologiei(originalImage, threshold, radius, length);
        } else {
            result = ObrabotkaIzobrazheniya.osvetitNaivno(originalImage, threshold);
        }

        long end = System.nanoTime();
        long ms = (end - start) / 1_000_000;

        processedImage = result.image;
        timeLabel.setText("Время: " + ms + " мс");
        statsLabel.setText("Выделено: " + result.highlightedCount);

        updateTabs();
        tabbedPane.setSelectedIndex(1); // переключаемся на вкладку "После"
    }

    private void onSave(ActionEvent e) {
        if (processedImage == null) {
            showError("Нет результата для сохранения");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String format = (String) formatCombo.getSelectedItem();
                String ext = format.equals("PNG") ? "png" : "jpg";

                // Если нет нужного расширения — добавим
                if (!file.getName().toLowerCase().endsWith("." + ext)) {
                    file = new File(file.getAbsolutePath() + "." + ext);
                }

                ImageIO.write(processedImage, ext, file);
                JOptionPane.showMessageDialog(this,
                        "Файл сохранён: " + file.getAbsolutePath(),
                        "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                showError("Ошибка сохранения: " + ex.getMessage());
            }
        }
    }

    /**
     * Обновляем изображения на вкладках.
     */
    private void updateTabs() {
        if (originalImage != null) {
            originalLabel.setIcon(new ImageIcon(scaleToFit(
                    originalImage, originalLabel.getWidth(), originalLabel.getHeight()
            )));
            originalLabel.setText(null);
        } else {
            originalLabel.setIcon(null);
            originalLabel.setText("Исходное изображение");
        }

        if (processedImage != null) {
            processedLabel.setIcon(new ImageIcon(scaleToFit(
                    processedImage, processedLabel.getWidth(), processedLabel.getHeight()
            )));
            processedLabel.setText(null);
        } else {
            processedLabel.setIcon(null);
            processedLabel.setText("Результат");
        }
    }

    private Image scaleToFit(BufferedImage img, int maxW, int maxH) {
        int w = img.getWidth();
        int h = img.getHeight();
        double ratio = Math.min((double) maxW / w, (double) maxH / h);
        int newW = (int) (w * ratio);
        int newH = (int) (h * ratio);
        return img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}