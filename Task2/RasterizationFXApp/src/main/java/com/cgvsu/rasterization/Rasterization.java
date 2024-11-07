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
            int temp = ang1;
            ang1 = ang2;
            ang2 = temp;
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
        int x = (int) (xc+r*Math.cos(Math.toRadians((ang1/90))*90)); int y = (int) (yc-r*Math.sin(Math.toRadians((ang1/90))*90));
        while (true) {
            int dx = x - xc; int dy = y - yc; // нужно для угла и определения четверти
            double angleForDeBug = angle(dx,dy);
            if (angle(dx,dy) == 0) {
                counter++;
            }
            if (angle(dx,dy) + counter * 360 >= ang1 && angle(dx,dy) <= ang2) {
                pixelWriter.setColor(x,y,mainColor);
            }
            if (angle(dx,dy) >= 0 && angle(dx,dy) < 90) {
                flag = 1;
            }
            else if (angle(dx,dy) >= 90 && angle(dx,dy) < 180) {
                flag = 2;
            }
            else if (angle(dx,dy) >= 180 && angle(dx,dy) < 270) {
                flag = 3;
            }
            else if (angle(dx,dy) >= 270 && angle(dx,dy) < 360) {
                flag = 4;
            }
            switch (flag) {
                case 1: {
                    if (isCenterInCircle(xc,yc,r,x,y-1)) {
                        y-=1;
                    } else if (isCenterInCircle(xc,yc,r,x-1,y)){
                        x-=1;
                    } else {
                        y-=1;
                        x-=1;
                    }
                    break;
                }
                case 2: {
                    if (isCenterInCircle(xc,yc,r,x-1,y)) {
                        x-=1;
                    } else if (isCenterInCircle(xc,yc,r,x,y+1)) {
                        y+=1;
                    }
                    else {
                        x-=1;
                        y+=1;
                    }
                    break;
                }
                case 3: {
                    if (isCenterInCircle(xc,yc,r,x,y+1)) {
                        y+=1;
                    } else if(isCenterInCircle(xc,yc,r,x+1,y)) {
                        x+=1;
                    } else {
                        x+=1;
                        y+=1;
                    }
                    break;
                }
                case 4: {
                    if (isCenterInCircle(xc,yc,r,x+1,y)) {
                        x+=1;
                    } else if (isCenterInCircle(xc,yc,r,x,y-1)) {
                        y-=1;
                    }else {
                        x+=1;
                        y-=1;
                    }
                    break;
                }
            }
            if (angle(dx,dy) > ang2 || counter > 0) {
                break;
            }
        }
    }


    public static void drawArcInterpolation(final GraphicsContext graphicsContext, int xc, int yc, int r, int ang1, Color color1, int ang2, Color color2) {

    }

    public static double angle(int dx, int dy) {
        if (dy == 0 && dx > 0) {
            return 0;
        }
        if (dx == 0 && dy < 5) {
            return 90;
        }
        if (dx > 0 && dy > 0) {
            return 270+Math.toDegrees(Math.atan((double)dx/dy));
        }
        else if (dx < 0 && dy < 0) {
            return 90+Math.toDegrees(Math.atan((double)dx/dy));
        }
        else if (dx > 0 && dy < 0) {
            return 90+Math.toDegrees(Math.atan((double)dx/dy));
        }
        else { // (dx < 0 && dy > 0)
            return 270+Math.toDegrees(Math.atan((double)dx/dy));
        }
    }

    public static boolean isCenterInCircle(int xc, int yc, int r, int x, int y) {
        return Math.pow((x-0.5) - xc, 2) + Math.pow((y-0.5) - yc, 2) - r * r <= 0;
    }
}
