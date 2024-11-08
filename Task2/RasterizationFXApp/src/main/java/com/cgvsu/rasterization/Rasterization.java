package com.cgvsu.rasterization;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class Rasterization {

    public static void drawRectangle(
            final GraphicsContext graphicsContext,
            final int x, final int y,
            final int width, final int height,
            final Color color)
    {
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();

        for (int row = y; row < y + height; ++row)
            for (int col = x; col < x + width; ++col)
                pixelWriter.setColor(col, row, color);
    }

    /*
    исправить проблему, когда при указании 0,360 программа бесконечно выполняется
     */
    public static void drawArc(final GraphicsContext graphicsContext, int xc, int yc, int r, int ang1, int ang2) {
        if (ang2 < ang1) { // в ang1 меньший угол, в ang2 - больший
            int temp = ang1 - (ang1 / 361) * 360;
            ang1 = ang2 - (ang2 / 361) * 360;
            ang2 = temp;
        } else {
            ang1 = ang1 - (ang1 / 361) * 360;
            ang2 = ang2 - (ang2 / 361) * 360;
        }
        final PixelWriter pixelWriter = graphicsContext.getPixelWriter();
        final Color mainColor = Color.BLACK;
        int flag = 0; // для отслеживания четверти
        int counter = 0; // если мы нарисовали всю окружность, то угол вновь становится 0 => будет бесконечный цикл
        for (int i = xc; i < xc+r; i++) {
            pixelWriter.setColor(i,yc,Color.RED);
        }
        for (int i = xc-r; i < xc; i++) {
            pixelWriter.setColor(i,yc,Color.BLUE);
        }
        for (int i = yc; i < yc+r; i++) {
            pixelWriter.setColor(xc,i,Color.GREEN);
        }
        for (int i = yc-r; i < yc; i++) {
            pixelWriter.setColor(xc,i,Color.DARKGRAY);
        }
        pixelWriter.setColor(xc, yc, Color.BLACK);
        int x = (int) (xc+r*Math.cos(Math.toRadians((ang1/90))*90)); int y = (int) (yc-r*Math.sin(Math.toRadians((ang1/90))*90)); // начинаем с начала четверти
        while (true) {
            int dx = x - xc; int dy = y - yc; // нужно для угла и определения четверти
            double angle = angle(dy,dx);
            if (angle == 0 && flag > 1) {
                counter++;
            }
            if (angle + counter * 360 >= ang1 && angle + counter * 360 <= ang2) {
                pixelWriter.setColor(x,y,mainColor);
            }
            if (angle >= 0 && angle < 90) {
                flag = 1;
            }
            else if (angle >= 90 && angle < 180) {
                flag = 2;
            }
            else if (angle >= 180 && angle < 270) {
                flag = 3;
            }
            else if (angle >= 270 && angle < 360) {
                flag = 4;
            }
            switch (flag) {
                case 1 -> {
                    if (isCenterInCircle(xc, yc, r, x, y - 1)) {
                        y -= 1;
                    } else if (isCenterInCircle(xc, yc, r, x - 1, y - 1)) {
                        x -= 1;
                        y -= 1;
                    } else {
                        x -= 1;
                    }
                }
                case 2 -> {
                    if (isCenterInCircle(xc, yc, r, x - 1, y)) {
                        x -= 1;
                    } else if (isCenterInCircle(xc, yc, r, x - 1, y + 1)) {
                        y += 1;
                        x -= 1;
                    } else {
                        y += 1;
                    }
                }
                case 3 -> {
                    if (isCenterInCircle(xc, yc, r, x, y + 1)) {
                        y += 1;
                    } else if (isCenterInCircle(xc, yc, r, x + 1, y + 1)) {
                        x += 1;
                        y += 1;
                    } else {
                        x += 1;
                    }
                }
                case 4 -> {
                    if (isCenterInCircle(xc, yc, r, x + 1, y)) {
                        x += 1;
                    } else if (isCenterInCircle(xc, yc, r, x + 1, y - 1)) {
                        y -= 1;
                        x += 1;
                    } else {
                        y -= 1;
                    }
                }
            }
            if (angle > ang2 || counter > 0) {
                break;
            }
        }
    }


    public static void drawArcInterpolation(final GraphicsContext graphicsContext, int xc, int yc, int r, int ang1, Color color1, int ang2, Color color2) {

    }

    public static double angle(int dy, int dx) {
        if (dx == 0 && dy < 0) {
            return 90;
        } else if (dx == 0 && dy > 0) {
            return 270;
        }
        else if (dx > 0 && dy > 0) {
            return 360-Math.toDegrees(Math.atan((double)dy/dx));
        }
        else if (dx > 0 && dy <= 0) {
            return Math.abs(Math.toDegrees(Math.atan((double)dy/dx)));
        }
        else if (dx < 0 && dy < 0) {
            return 180-Math.toDegrees(Math.atan((double)dy/dx));
        }
        else { // (dx < 0 && dy > 0)
            return 180-Math.toDegrees(Math.atan((double)dy/dx));
        }
    }

    public static boolean isCenterInCircle(int xc, int yc, int r, int x, int y) {
        return Math.pow((x-0.5) - (xc-0.5), 2) + Math.pow((y-0.5) - (yc-0.5), 2) - ((r+0.5) * (r+0.5)) <= 0;
    }
}
