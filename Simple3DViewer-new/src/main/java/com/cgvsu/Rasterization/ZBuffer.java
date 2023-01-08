package com.cgvsu.Rasterization;

import com.cgvsu.model.Polygon;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;


public class ZBuffer {
    private int width;
    private int height;
    private Cell[][] buffer;

    public ZBuffer(int width, int height, Color background) {
        this.width = width;
        this.height = height;
        buffer = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell curCell = new Cell();
                curCell.z = Double.MAX_VALUE;
                curCell.color = background;
                buffer[i][j] = curCell;
            }
        }
    }

    public void PutTriangle(Polygon triangle,
                            ArrayList<Vector3f> sceneVertexes,
                            ArrayList<Point2f> screenVertexes,
                            Color colorTriangle) {
        int[] x = new int[3];
        int[] y = new int[3];
        float[] z = new float[3];

        for (int i = 0; i < 3; i++) {
            x[i] = (int) screenVertexes.get(triangle.getVertexIndices().get(i)).x;
            y[i] = (int) screenVertexes.get(triangle.getVertexIndices().get(i)).y;
            z[i] = sceneVertexes.get(triangle.getVertexIndices().get(i)).z;
        }

        int yMax = Math.max(Math.max(y[0], y[1]), y[2]);
        int yMin = Math.min(Math.min(y[0], y[1]), y[2]);

        int xMax = Math.max(Math.max(x[0], x[1]), x[2]);
        int xMin =  Math.min(Math.min(x[0], x[1]), x[2]);

        if (yMax <= 0 || yMin >= height || xMax <= 0 || xMin >= width) return;

        yMin = Math.max(0, yMin);
        yMax = Math.min(height, yMax);

        int xEdge[] = new int[2];
        double zEdge[] = new double[2];

        for (int curY = yMin; curY < yMax; curY++) {
            int edgeIndex = 0;
            for (int currVertexInd = 0; currVertexInd < 3; currVertexInd++) {
                int nextVertexInd = (currVertexInd + 1) % 3;

                if (y[currVertexInd] < y[nextVertexInd]) {
                    if (y[nextVertexInd] <= curY || curY < y[currVertexInd])
                        continue; //currY за пределами текущего ребра

                } else if (y[currVertexInd] > y[nextVertexInd]) {
                    if (y[nextVertexInd] > curY || curY >= y[currVertexInd]) continue; //противоп ситуац

                } else continue;

                double partOfEdge = ((double) (y[currVertexInd] - curY)) / (y[currVertexInd] - y[nextVertexInd]);

                xEdge[edgeIndex] = x[currVertexInd] + (int) (partOfEdge * (x[nextVertexInd] - x[currVertexInd]));
                zEdge[edgeIndex] = z[currVertexInd] + partOfEdge * (z[nextVertexInd] - z[currVertexInd]);
                edgeIndex++;
            }
            if (xEdge[1] < xEdge[0]) {
                int u = xEdge[1];
                xEdge[1] = xEdge[0];
                xEdge[0] = u;
                double v = zEdge[1];
                zEdge[1] = zEdge[0];
                zEdge[0] = v;
            }

            xMin = Math.max(xEdge[0], 0);
            xMax = Math.min(xEdge[1], width);

            for (int curX = xMin; curX < xMax; curX++) {
                double partOfEdge = ((double) (xEdge[0] - curX)) / (xEdge[0] - xEdge[1]);
                double curZ = zEdge[0] + partOfEdge * (zEdge[1] - zEdge[0]);


                //Дальше весь смысл буффера
                //Если полученная глубина пиксела меньше той,
                //что находится в Z-Буфере - заменяем храняшуюся на новую.

                if (curY < 0 || curX < 0) {
                    System.out.println("curY = " + curY + "; curX = " + curX);
                }

                Cell curCell = buffer[curY][curX];
                if (curZ < curCell.z) {
                    curCell.color = colorTriangle;
                    curCell.z = curZ;
                }
            }
        }

    }

    public void show(GraphicsContext graphicsContext) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (buffer[i][j].color == Color.GOLD)
                    graphicsContext.getPixelWriter().setColor(j, i, buffer[i][j].color);
            }
        }
    }

    public void clear(Color background) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell curCell = buffer[i][j];
                curCell.z = Double.MAX_VALUE;
                curCell.color = background;
            }
        }
    }

}
