package com.cgvsu.render_engine;

import java.awt.*;
import java.util.ArrayList;

import com.cgvsu.GuiController;
import com.cgvsu.Rasterization.*;
import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.TransformedModel;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import static com.cgvsu.Rasterization.Rasterization.fillTriangle;
import static com.cgvsu.render_engine.MyGraphicConveyor.*;

public class RenderEngine {
    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final ArrayList<TransformedModel> models,
            final int width,
            final int height,
            boolean isRasterize, GraphicsUtils graphicsUtils, boolean isKeepMesh, ColorPicker modelColorPicker) {


        for (TransformedModel transformedModel : models) {
            Color mColor = modelColorPicker.getValue();
           // ZBuffer zBuffer = new ZBuffer(width, height, mColor);

            Matrix4f modelMatrix = transformedModel.rotateScaleTranslate();
            Matrix4f viewMatrix = camera.getViewMatrix();
            Matrix4f projectionMatrix = camera.getProjectionMatrix();


            Matrix4f modelViewProjectionMatrix1 = new Matrix4f(modelMatrix.getVector());
            Matrix4f modelViewProjectionMatrix2 = Matrix4f.multiplicateMatrices(viewMatrix, modelViewProjectionMatrix1);
            Matrix4f modelViewProjectionMatrix3 =
                    Matrix4f.multiplicateMatrices(projectionMatrix, modelViewProjectionMatrix2);

            Model actualModel = transformedModel.getPrevModel();
            final int nPolygons = actualModel.polygons.size();


            //int nPolygons;

            if (isRasterize) {
               // nPolygons = actualModel.triangulatePolygons.size();
                graphicsContext.setLineWidth(5);

            } else {
               // nPolygons = actualModel.polygons.size();
                graphicsContext.setLineWidth(1);

            }

            ArrayList<Vector3f> sceneVertexes = new ArrayList<>();
            ArrayList<Point2f> screenVertexes = new ArrayList<>();

            for (Vector3f vertexVecmath : actualModel.vertices) {
               // Vector3f vertexVecmath = new Vector3f(currVertex.getX(), currVertex.getY(), currVertex.getZ());
                Vector3f currSceneVertex = multiplyMatrix4ByVector3(modelViewProjectionMatrix3, vertexVecmath);
                sceneVertexes.add(currSceneVertex);
                Point2f resultPoint = vertexToPoint(currSceneVertex, width, height);
                screenVertexes.add(resultPoint);
            }


            for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
                final int nVerticesInPolygon = actualModel.polygons.get(polygonInd).getVertexIndices().size();

                ArrayList<Point2f> resultPoints = new ArrayList<>();


                for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                    int index = actualModel.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd);
                    Vector3f vertex = actualModel.vertices.get(index);
                    Vector3f newVertex = multiplyMatrix4ByVector3(modelMatrix, vertex);
                    transformedModel.getTransformedVertices().set(index, newVertex);
                    Point2f resultPoint =
                            vertexToPoint(multiplyMatrix4ByVector3(modelViewProjectionMatrix3, vertex), width, height);
                    resultPoints.add(resultPoint);
                }
                if (!isRasterize  || (isKeepMesh && isRasterize)) {
                    for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
                        graphicsContext.strokeLine(
                                resultPoints.get(vertexInPolygonInd - 1).x,
                                resultPoints.get(vertexInPolygonInd - 1).y,
                                resultPoints.get(vertexInPolygonInd).x,
                                resultPoints.get(vertexInPolygonInd).y);
                    }

                    if (nVerticesInPolygon > 0)
                        graphicsContext.strokeLine(
                                resultPoints.get(nVerticesInPolygon - 1).x,
                                resultPoints.get(nVerticesInPolygon - 1).y,
                                resultPoints.get(0).x,
                                resultPoints.get(0).y);
                }
                if (isRasterize) {
                    //растеризация

//                        ArrayList<Polygon> triangles = Model.getTriangles(actualModel);
//                       for (Polygon curTriangle : triangles) {
//                            zBuffer.PutTriangle(curTriangle,sceneVertexes,screenVertexes,mColor);
//                            System.out.println(curTriangle.getVertexIndices());
//                    }


                     for (int i = 0; i < nVerticesInPolygon - 2; i++) {
                        MyPoint2D first = new MyPoint2D(resultPoints.get(i).x, resultPoints.get(i).y);
                        MyPoint2D second = new MyPoint2D(resultPoints.get(i + 1).x, resultPoints.get(i + 1).y);
                        MyPoint2D third = new MyPoint2D(resultPoints.get(resultPoints.size() - 1).x, resultPoints.get(resultPoints.size() - 1).y);
                        MyColor color = new MyColor(mColor.getRed(), mColor.getGreen(), mColor.getBlue());
                        fillTriangle(graphicsUtils, first, second, third, color, color, color);
                          //System.out.println(resultPoints.get(i).x + " " + resultPoints.get(i).y);
                          //System.out.println(resultPoints.get(i+1).x + " " + resultPoints.get(i+1).y);
                          //System.out.println(resultPoints.get(resultPoints.size() - 1).x + " " + resultPoints.get(resultPoints.size() - 1).y);
                          //System.out.println();
                    }
                }
            }
            //if (isRasterize) zBuffer.show(graphicsContext);

        }
    }
}


/*
else {
        //растеризация
        ArrayList<Polygon> triangles = Model.getTriangles(actualModel);

        for (Polygon curTriangle : triangles) {
        int f = curTriangle.getVertexIndices().get(0);
        int s = curTriangle.getVertexIndices().get(1);
        int t = curTriangle.getVertexIndices().get(2);
        double fx = actualModel.getVertices().get(f).getX();
        double fy = actualModel.getVertices().get(f).getY();

        double sx = actualModel.getVertices().get(s).getX();
        double sy = actualModel.getVertices().get(s).getY();


        double tx = actualModel.getVertices().get(t).getX();
        double ty = actualModel.getVertices().get(t).getY();


        MyPoint2D first = new MyPoint2D(fx, fy);
        MyPoint2D second = new MyPoint2D(sx, sy);
        MyPoint2D third = new MyPoint2D(tx, ty);

        Color mColor = modelColorPicker.getValue();
        MyColor color = new MyColor(mColor.getRed(), mColor.getGreen(), mColor.getBlue());
        fillTriangle(graphicsUtils, first, second, third, color, color, color);
        }
        }
 */