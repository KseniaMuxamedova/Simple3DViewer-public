package com.cgvsu.render_engine;

import java.util.ArrayList;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.TransformedModel;
import javafx.scene.canvas.GraphicsContext;
import javax.vecmath.*;
import com.cgvsu.model.Model;
import static com.cgvsu.render_engine.MyGraphicConveyor.*;

public class RenderEngine {

    public static void render(
            final GraphicsContext graphicsContext,
            final Camera camera,
            final ArrayList<TransformedModel> models,
            final int width,
            final int height,
            boolean isRasterize) {
        for (TransformedModel transformedModel : models) {
            Matrix4f modelMatrix = transformedModel.rotateScaleTranslate();
            Matrix4f viewMatrix = camera.getViewMatrix();
            Matrix4f projectionMatrix = camera.getProjectionMatrix();

            Matrix4f modelViewProjectionMatrix1 = new Matrix4f(modelMatrix.getVector());
            Matrix4f modelViewProjectionMatrix2 = Matrix4f.multiplicateMatrices(viewMatrix, modelViewProjectionMatrix1);
            Matrix4f modelViewProjectionMatrix3 =
                    Matrix4f.multiplicateMatrices(projectionMatrix, modelViewProjectionMatrix2);

            Model actualModel = transformedModel.getPrevModel();
            final int nPolygons = actualModel.polygons.size();
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
                 if (!isRasterize) {
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
             } else {
                 //растеризация
                 }
            }
        }
    }
}
