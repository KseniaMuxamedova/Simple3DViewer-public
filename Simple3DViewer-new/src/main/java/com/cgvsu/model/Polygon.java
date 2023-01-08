package com.cgvsu.model;

import java.util.ArrayList;

public class Polygon {

    private ArrayList<Integer> vertexIndices;
    private ArrayList<Integer> textureVertexIndices;
    private ArrayList<Integer> normalIndices;


    public Polygon() {
        vertexIndices = new ArrayList<Integer>();
        textureVertexIndices = new ArrayList<Integer>();
        normalIndices = new ArrayList<Integer>();
    }
    
    public ArrayList<Integer> triangulation(ArrayList<Integer> initialList) {
        ArrayList<Integer> convertedList = new ArrayList<>();
        for (int i = 0; i < initialList.size() - 2; i++) {
            convertedList.add(initialList.get(0));
            convertedList.add(initialList.get(i + 1));
            convertedList.add(initialList.get(i + 2));
        }
        return convertedList;
    }


    public static ArrayList<Polygon> triangulatePolygons(final ArrayList<Polygon> initialList) {
        ArrayList<Polygon> onlyTrianglePolygons = new ArrayList<>();
        for (Polygon polygon : initialList) {
            for (int i = 0; i < polygon.getVertexIndices().size() - 2; i++) {
                Polygon triangle = new Polygon();

                ArrayList<Integer> vertexIndices = new ArrayList<>();
                vertexIndices.add(polygon.getVertexIndices().get(0));
                vertexIndices.add(polygon.getVertexIndices().get(i + 1));
                vertexIndices.add(polygon.getVertexIndices().get(i + 2));
                triangle.setVertexIndices(vertexIndices);

                if (polygon.getTextureVertexIndices().size() != 0) {
                    ArrayList<Integer> textureVertexIndices = new ArrayList<>();
                    textureVertexIndices.add(polygon.getTextureVertexIndices().get(0));
                    textureVertexIndices.add(polygon.getTextureVertexIndices().get(i + 1));
                    textureVertexIndices.add(polygon.getTextureVertexIndices().get(i + 2));
                    triangle.setTextureVertexIndices(textureVertexIndices);
                }

                if (polygon.getNormalIndices().size() != 0) {
                    ArrayList<Integer> normalVertexIndices = new ArrayList<>();
                    normalVertexIndices.add(polygon.getNormalIndices().get(0));
                    normalVertexIndices.add(polygon.getNormalIndices().get(i + 1));
                    normalVertexIndices.add(polygon.getNormalIndices().get(i + 2));
                    triangle.setNormalIndices(normalVertexIndices);
                }

                onlyTrianglePolygons.add(triangle);
            }
        }
        return onlyTrianglePolygons;
    }



    public void setVertexIndices(ArrayList<Integer> vertexIndices) {
        assert vertexIndices.size() >= 3;
        this.vertexIndices = vertexIndices;
    }

    public void setTextureVertexIndices(ArrayList<Integer> textureVertexIndices) {
        assert textureVertexIndices.size() >= 3;
        this.textureVertexIndices = textureVertexIndices;
    }

    public void setNormalIndices(ArrayList<Integer> normalIndices) {
        assert normalIndices.size() >= 3;
        this.normalIndices = normalIndices;
    }

    public ArrayList<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public ArrayList<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public ArrayList<Integer> getNormalIndices() {
        return normalIndices;
    }
}
