package org.example;

import java.awt.image.BufferedImage;

public class Morfologiya {

    /**
     * Операция "расширение".
     * Если в квадратной окрестности радиуса radius есть хотя бы один белый пиксель,
     * текущий пиксель становится белым.
     */
    public static BufferedImage rasshirenie(BufferedImage input, int radius) {
        int w = input.getWidth();
        int h = input.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                boolean foundWhite = false;
                for (int dy = -radius; dy <= radius && !foundWhite; dy++) {
                    for (int dx = -radius; dx <= radius && !foundWhite; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < w && ny >= 0 && ny < h) {
                            int argb = input.getRGB(nx, ny);
                            // Если пиксель не чёрный => он белый
                            if ((argb >>> 24) != 0 && (argb & 0x00FFFFFF) != 0x000000) {
                                foundWhite = true;
                            }
                        }
                    }
                }
                if (foundWhite) {
                    output.setRGB(x, y, 0xFFFFFFFF); // белый
                } else {
                    output.setRGB(x, y, 0xFF000000); // чёрный
                }
            }
        }
        return output;
    }

    /**
     * Операция "удаление длинных линий".
     * Проверяем подряд length пикселей по горизонтали или вертикали.
     * Если все белые, то пиксель остаётся белым, иначе становится чёрным.
     */
    public static BufferedImage udalenieLinij(BufferedImage input, int length, boolean horizontal) {
        int w = input.getWidth();
        int h = input.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                boolean allWhite = true;
                for (int i = 0; i < length; i++) {
                    int nx = horizontal ? (x + i) : x;
                    int ny = horizontal ? y : (y + i);
                    if (nx >= w || ny >= h) {
                        allWhite = false;
                        break;
                    }
                    int argb = input.getRGB(nx, ny);
                    if ((argb & 0x00FFFFFF) == 0x000000) {
                        allWhite = false;
                        break;
                    }
                }
                if (allWhite) {
                    output.setRGB(x, y, 0xFFFFFFFF);
                } else {
                    output.setRGB(x, y, 0xFF000000);
                }
            }
        }
        return output;
    }
}