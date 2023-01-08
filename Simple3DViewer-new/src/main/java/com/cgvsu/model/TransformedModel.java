package com.cgvsu.model;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.render_engine.MyGraphicConveyor;

import java.util.ArrayList;

public class TransformedModel {
    public Model actualModel;
    public ArrayList<Vector3f> transformedVertices = new ArrayList<>();

    private float scaleXParams = 1;
    private float scaleYParams = 1;
    private float scaleZParams = 1;

    private float rotateXParams = 0;
    private float rotateYParams = 0;
    private float rotateZParams = 0;

    private float translateXParams = 0;
    private float translateYParams = 0;
    private float translateZParams = 0;

    public TransformedModel(
            final Model actualMatrix,
            final float scaleXParams, final float scaleYParams, final float scaleZParams,
            final float rotateXParams, final float rotateYParams, final float rotateZParams,
            final float translateXParams, final float translateYParams, final float translateZParams) {
        this.actualModel = actualMatrix;
        this.scaleXParams = scaleXParams;
        this.scaleYParams = scaleYParams;
        this.scaleZParams = scaleZParams;
        this.rotateXParams = rotateXParams;
        this.rotateYParams = rotateYParams;
        this.rotateZParams = rotateZParams;
        this.translateXParams = translateXParams;
        this.translateYParams = translateYParams;
        this.translateZParams = translateZParams;
    }

    public TransformedModel(final Model actualModel) {
        this.actualModel = actualModel;
    }

    public void setActualModel(final Model actualModel) {
        this.actualModel = actualModel;
    }

    public void setTransformedModel() {
        this.transformedVertices = copy(actualModel.getVertices());
    }

    public ArrayList<Vector3f> getTransformedVertices() {
        return transformedVertices;
    }

    public ArrayList<Vector3f> copy(final ArrayList<Vector3f> list) {
        return new ArrayList<>(list);
    }

    public void setRotateParams(float rotateXParams, float rotateYParams, float rotateZParams) {
        this.rotateXParams += rotateXParams;
        this.rotateYParams += rotateYParams;
        this.rotateZParams += rotateZParams;
    }

    public void setRotateXParam(float rotateParam) {
        this.rotateXParams += rotateParam;
    }

    public void setRotateYParam(float rotateParam) {
        this.rotateYParams += rotateParam;
    }

    public void setRotateZParam(float rotateParams) {
        this.rotateZParams += rotateParams;
    }

    public Vector3f getRotateParams() {
        return new Vector3f(new float[]{rotateXParams, rotateYParams, rotateZParams});
    }

    public void setScaleParams(float scaleXParams, float scaleYParams, float scaleZParams) {
        this.scaleXParams += scaleXParams;
        this.scaleYParams += scaleYParams;
        this.scaleZParams += scaleZParams;
    }

    public void setScaleParams(int index, float scaleParams) {
        if (index == 0) {
            setScaleXParams(scaleParams);
        } else if (index == 1) {
            setScaleYParams(scaleParams);
        } else if (index == 2) {
            setScaleZParams(scaleParams);
        }
    }

    public void setScaleXParams(float scaleParam) {
        this.scaleXParams += scaleParam;
    }

    public void setScaleYParams(float scaleParam) {
        this.scaleYParams += scaleParam;
    }

    public void setScaleZParams(float scaleParam) {
        this.scaleZParams += scaleParam;
    }

    public Vector3f getScaleParams() {
        return new Vector3f(new float[]{scaleXParams, scaleYParams, scaleZParams});
    }

    public void setTranslateParams(float translateXParams, float translateYParams, float translateZParams) {
        this.translateXParams += translateXParams;
        this.translateYParams += translateYParams;
        this.translateZParams += translateZParams;
    }

    public void setTranslateXParam(float translateParam) {
        this.translateXParams += translateParam;
    }

    public void setTranslateYParam(float translateParam) {
        this.translateYParams += translateParam;
    }

    public void setTranslateZParam(float translateParam) {
        this.translateZParams += translateParam;
    }

    public Vector3f getTranslateParams() {
        return new Vector3f(new float[]{translateXParams, translateYParams, translateZParams});
    }

    public Model getPrevModel() {
        return this.actualModel;
    }

    public Matrix4f rotateScaleTranslate() {
        try {
            return MyGraphicConveyor.rotateScaleTranslate(getRotateParams(), getScaleParams(), getTranslateParams());
        } catch (Matrix.MatrixException e) {
            throw new RuntimeException(e);
        }
    }

    public Model getTransformedModel() {
        return new Model(transformedVertices,
                actualModel.getTextureVertices(), actualModel.getNormals(), actualModel.getPolygons());
    }
}
