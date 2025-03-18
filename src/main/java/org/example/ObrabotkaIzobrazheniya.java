package org.example;

import java.awt.image.BufferedImage;

public class ObrabotkaIzobrazheniya {

    /**
     * Результат обработки: итоговое изображение + кол-во жёлтых пикселей.
     */
    public static class RezultatObrabotki {
        public final BufferedImage image;
        public final long highlightedCount;

        public RezultatObrabotki(BufferedImage image, long highlightedCount) {
            this.image = image;
            this.highlightedCount = highlightedCount;
        }
    }

    /**
     * Простой (наивный) алгоритм:
     * Если яркость > threshold, пиксель становится жёлтым, иначе — исходный.
     */
    public static RezultatObrabotki osvetitNaivno(BufferedImage original, int threshold) {
        if (original == null) {
            return new RezultatObrabotki(null, 0);
        }
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        long count = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;

                if (gray > threshold) {
                    result.setRGB(x, y, 0xFFFFFF00); // жёлтый
                    count++;
                } else {
                    result.setRGB(x, y, rgb);
                }
            }
        }
        return new RezultatObrabotki(result, count);
    }

    /**
     * Алгоритм с морфологией:
     * 1) Бинаризация (яркость > threshold => белый, иначе чёрный).
     * 2) Расширение (rasshirenie).
     * 3) Удаление длинных линий (udalenieLinij) по горизонтали и вертикали.
     * 4) Белые пиксели красим в жёлтый, остальные — исходный цвет.
     */
    public static RezultatObrabotki osvetitSMorfologiei(
            BufferedImage original,
            int threshold,
            int radius,
            int length
    ) {
        if (original == null) {
            return new RezultatObrabotki(null, 0);
        }

        int w = original.getWidth();
        int h = original.getHeight();

        // Бинаризация
        BufferedImage bin = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = original.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;

                if (gray > threshold) {
                    bin.setRGB(x, y, 0xFFFFFFFF); // белый
                } else {
                    bin.setRGB(x, y, 0xFF000000); // чёрный
                }
            }
        }

        // Расширение
        BufferedImage afterDilate = Morfologiya.rasshirenie(bin, radius);

        // Удаляем длинные линии (горизонтальные, затем вертикальные)
        BufferedImage afterHoriz = Morfologiya.udalenieLinij(afterDilate, length, true);
        BufferedImage afterVert = Morfologiya.udalenieLinij(afterHoriz, length, false);

        // Окрашиваем белые пиксели в жёлтый, остальные — исходные
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        long count = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int val = afterVert.getRGB(x, y);
                if ((val & 0x00FFFFFF) == 0xFFFFFF) {
                    result.setRGB(x, y, 0xFFFFFF00); // жёлтый
                    count++;
                } else {
                    result.setRGB(x, y, original.getRGB(x, y));
                }
            }
        }
        return new RezultatObrabotki(result, count);
    }
}