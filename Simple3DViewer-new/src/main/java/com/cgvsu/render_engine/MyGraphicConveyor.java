package com.cgvsu.render_engine;

import com.cgvsu.math.Math.Matrix.Matrix;
import com.cgvsu.math.Math.Matrix.Matrix4f;
import com.cgvsu.math.Math.Vector.Vector;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.math.Math.Vector.Vector4f;

import javax.vecmath.Point2f;

public class MyGraphicConveyor {

    private static final float EPS = 1e-5f;

    public static Matrix4f rotateScaleTranslate(Vector3f rotateVector, Vector3f scaleVector, Vector3f translateVector)
            throws Matrix.MatrixException {
        Matrix4f matrix4f = (Matrix4f) new Matrix4f().createIdentityMatrix();
        setScale(matrix4f, scaleVector);
        matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getRotateMatrix(rotateVector), matrix4f);
        addTranslation(matrix4f, translateVector);
        return matrix4f;
    }

    public static Matrix getRotateMatrix(Vector3f rotateVector) throws Matrix.MatrixException {
        Matrix4f matrix4f = (Matrix4f) new Matrix4f().createIdentityMatrix();
        if (Math.abs(rotateVector.getX()) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getXRotationMatrix(rotateVector.getX()), matrix4f);
        }
        if (Math.abs(rotateVector.getY()) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getYRotationMatrix(rotateVector.getY()), matrix4f);
        }
        if (Math.abs(rotateVector.getZ()) > EPS) {
            matrix4f = (Matrix4f) Matrix4f.multiplicateMatrices(getZRotationMatrix(rotateVector.getZ()), matrix4f);
        }
        return matrix4f;
    }

    public static Matrix getXRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                1, 0, 0, 0,
                0, cos, sin, 0,
                0, -sin, cos, 0,
                0, 0, 0, 1});
    }

    public static Matrix getYRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                cos, 0, -sin, 0,
                0, 1, 0, 0,
                sin, 0, cos, 0,
                0, 0, 0, 1});
    }

    public static Matrix getZRotationMatrix(float alfa) {
        alfa = (float) Math.toRadians(alfa);
        final float cos = (float) Math.cos(alfa);
        final float sin = (float) Math.sin(alfa);

        return new Matrix4f(new float[]{
                cos, sin, 0, 0,
                -sin, cos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1});
    }

    public static void setScale(Matrix4f matrix4f, Vector3f scaleVector) {
        int index = 0;
        int size = matrix4f.getSize();
        for (float value : scaleVector.getVector()) {
            if (Math.abs(value) > EPS) {
                matrix4f.set(index * size + index, value);
            }
            index++;
        }
    }

    public static void addTranslation(Matrix4f matrix4f, Vector3f translateVector) {
        int indexRow = 0;
        int size = matrix4f.getSize();
        for (float value : translateVector.getVector()) {
            matrix4f.set(indexRow * size + (size - 1), value);
            indexRow++;
        }
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target) throws Vector.VectorException {
        return lookAt(eye, target, new Vector3f(new float[]{0, 1, 0}));
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f target, Vector3f up) throws Vector.VectorException {
        Vector3f resultX = new Vector3f();
        Vector3f resultY = new Vector3f();
        Vector3f resultZ = new Vector3f();

        resultZ = (Vector3f) resultZ.minusVector(target, eye);
        resultX.crossProduct(up, resultZ);
        resultY.crossProduct(resultZ, resultX);

        resultX = (Vector3f) resultX.normalizeVector();
        resultY = (Vector3f) resultY.normalizeVector();
        resultZ = (Vector3f) resultZ.normalizeVector();

        // Переход в систему координат камеры
        float[] matrix = new float[]{
                resultX.getX(), resultY.getX(), resultZ.getX(), -resultX.dotProduct(eye),
                resultX.getY(), resultY.getY(), resultZ.getY(), -resultY.dotProduct(eye),
                resultX.getZ(), resultY.getZ(), resultZ.getZ(), -resultZ.dotProduct(eye),
                0, 0, 0, 1
        };

        return new Matrix4f(matrix);
    }

    public static Matrix4f perspective(
            final float fov,
            final float aspectRatio,
            final float nearPlane,
            final float farPlane) {
        Matrix4f result = new Matrix4f();
        float tangentMinusOnDegree = (float) (1.0F / (Math.tan(fov * 0.5F)));
        result.set(0, tangentMinusOnDegree / aspectRatio);
        result.set(5, tangentMinusOnDegree);
        result.set(10, (farPlane + nearPlane) / (farPlane - nearPlane));
        result.set(11, 2 * (nearPlane * farPlane) / (nearPlane - farPlane));
        result.set(14, 1.0F);
        return result;
    }

    public static Vector3f multiplyMatrix4ByVector3(final Matrix4f matrix, final Vector3f vertex) {
        try {
            Vector4f vector4f = (Vector4f) matrix.multiplicateOnVector(
                    new Vector4f(new float[]{vertex.getX(), vertex.getY(), vertex.getZ(), 1}));
            return (Vector3f) new Vector3f(new float[]{vector4f.getX(), vector4f.getY(), vector4f.getZ()}).
                    divideVectorOnConstant(vector4f.get(3));
        } catch (Vector.VectorException | Matrix.MatrixException e) {
            throw new RuntimeException(e);
        }
    }

    public static Point2f vertexToPoint(final Vector3f vertex, final int width, final int height) {
        return new Point2f(vertex.getX() * width + width / 2.0F, -vertex.getY() * height + height / 2.0F);
    }
}
