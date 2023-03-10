package com.cgvsu.math.Math.Vector;

/**
 * Класс вектора размерности 3, реализация абстрактного класса Vector
 */

public class Vector3f extends Vector {

    private static final int size = 3;

    private float[] vector = new float[size];

    public Vector3f(float[] vector) {
        super(vector, size);
        this.vector = vector;
    }

    public Vector3f() {
        super(new float[size], size);
    }
    
    public Vector3f(float x, float y, float z) {
        super(new float[size], size);
        vector[0] = x;
        vector[1] = y;
        vector[2] = z;
        //this.vector = vector;
    }

    @Override
    public Vector getZeroVector(int size) {
        if (size != this.getSize()) {
            size = this.getSize();
        }
        return new Vector3f(new float[size]);
    }

    public void crossProduct(final Vector3f vector1, final Vector3f vector2) {
        if (isEqualSize(vector1, vector2)) {
            this.getVector()[0] = vector1.get(1) * vector2.get(2) - vector1.get(2) * vector2.get(1);
            this.getVector()[1] = vector1.get(2) * vector2.get(0) - vector1.get(0) * vector2.get(2);
            this.getVector()[2] = vector1.get(0) * vector2.get(1) - vector1.get(1) * vector2.get(0);
        }
    }
    
    //public float getX() {
    //    return vector[0];
   // }

   // public float getY() {
   //     return vector[1];
   // }

   // public float getZ() {
   //     return vector[2];
   // }
}
