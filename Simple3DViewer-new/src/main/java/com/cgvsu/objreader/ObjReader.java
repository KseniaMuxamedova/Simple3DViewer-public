package com.cgvsu.objreader;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.math.Math.Vector.Vector2f;
import com.cgvsu.model.TransformedModel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ObjReader {
	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";
	private static final String OBJ_COMMENT_TOKEN = "#";
	private static final String OBJ_GROUP_TOKEN = "g";
	private static final String OBJ_EMPTY_TOKEN = "";

	public static Model read(final String fileContent, boolean writeInfo) {

		Model resultModel = new Model();
		int lineInd = 0;
		Scanner scanner = new Scanner(fileContent);
		if (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("\ufeff")) {
				throw new ObjReaderExceptions.WrongFileException("Save your file without BOM");
			}
			while (true) {
				++lineInd;
				final List<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));
				if (wordsInLine.isEmpty()) {
					if (!scanner.hasNextLine()) {
						break;
					}
					line = scanner.nextLine();
					continue;
				}
				final String keyWord = wordsInLine.remove(0);
				switch (keyWord) {
					case OBJ_VERTEX_TOKEN -> resultModel.getVertices().add(parseVertex(wordsInLine, lineInd));
					case OBJ_TEXTURE_TOKEN -> resultModel.getTextureVertices().add(parseTextureVertex(wordsInLine, lineInd));
					case OBJ_NORMAL_TOKEN -> resultModel.getNormals().add(parseNormal(wordsInLine, lineInd));
					case OBJ_FACE_TOKEN -> resultModel.getPolygons().add(parseFace(wordsInLine, lineInd));
					case OBJ_COMMENT_TOKEN -> {if (writeInfo) System.out.println("Comment on the line: " + lineInd);}
					case OBJ_GROUP_TOKEN -> {if (writeInfo) System.out.println("Group not supported: " + lineInd);}
					case OBJ_EMPTY_TOKEN -> {}
					default -> throw new ObjReaderExceptions.ObjReaderException("Wrong key word.", lineInd);
				}
				if (!scanner.hasNextLine()) {
					break;
				}
				line = scanner.nextLine();
			}
		}
		resultModel.checkConsistency();
		return resultModel;
	}

	// Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
	protected static Vector3f parseVertex(final List<String> listOfWordsWithoutToken, final int lineInd) {
		if (listOfWordsWithoutToken.size() != 3)
			throw new ObjReaderExceptions.ObjReaderException("Wrong number of vertex arguments.", lineInd);
		try {
			float[] current = {Float.parseFloat(listOfWordsWithoutToken.get(0)),
			                   Float.parseFloat(listOfWordsWithoutToken.get(1)),
					           Float.parseFloat(listOfWordsWithoutToken.get(2))};
			return new Vector3f(current);
		} catch(NumberFormatException e) {
			throw new ObjReaderExceptions.ObjReaderException("Failed to parse float value.", lineInd);
		}
	}

	protected static Vector2f parseTextureVertex(final List<String> listOfWordsWithoutToken, final int lineInd) {
		if (listOfWordsWithoutToken.size() != 2 && listOfWordsWithoutToken.size() != 3)
			throw new ObjReaderExceptions.ObjReaderException("Wrong number of texture vertex arguments.", lineInd);
		try {
			float[] current = {Float.parseFloat(listOfWordsWithoutToken.get(0)),
					Float.parseFloat(listOfWordsWithoutToken.get(1))};
			return new Vector2f(current);
		} catch(NumberFormatException e) {
			throw new ObjReaderExceptions.ObjReaderException("Failed to parse float value.", lineInd);
		}
	}

	protected static Vector3f parseNormal(final List<String> listOfWordsWithoutToken, final int lineInd) {
		if (listOfWordsWithoutToken.size() != 3)
			throw new ObjReaderExceptions.ObjReaderException("Wrong number of normal arguments.", lineInd);
		try {
			float[] current = {Float.parseFloat(listOfWordsWithoutToken.get(0)),
					Float.parseFloat(listOfWordsWithoutToken.get(1)),
					Float.parseFloat(listOfWordsWithoutToken.get(2))};
			return new Vector3f(current);
		} catch(NumberFormatException e) {
			throw new ObjReaderExceptions.ObjReaderException("Failed to parse float value.", lineInd);
		}
	}

	protected static Polygon parseFace(final List<String> listOfWordsWithoutToken, final int lineInd) {
		if (listOfWordsWithoutToken.size() < 3) {
			throw new ObjReaderExceptions.ObjReaderException("Not enough vertexes for polygon.", lineInd);
		}
		ArrayList<Integer> onePolygonVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonNormalIndices = new ArrayList<>();
		for (String s : listOfWordsWithoutToken) {
			parseOneFaceWord(s, onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd);
		}
		Polygon resultPolygon = new Polygon();
		resultPolygon.setVertexIndices(onePolygonVertexIndices);
		resultPolygon.setTextureVertexIndices(onePolygonTextureVertexIndices);
		resultPolygon.setNormalIndices(onePolygonNormalIndices);
		return resultPolygon;
	}

	protected static void parseOneFaceWord(
			final String wordInLine,
			final List<Integer> onePolygonVertexIndices,
			final List<Integer> onePolygonTextureVertexIndices,
			final List<Integer> onePolygonNormalIndices,
			final int lineInd) {
		try {
			String[] someVertexDescriptionInString = wordInLine.split("/");
			Integer[] someVertexDescription = new Integer[someVertexDescriptionInString.length];
			for (int i = 0; i < someVertexDescriptionInString.length; i++) {
				someVertexDescription[i] = Integer.parseInt(someVertexDescriptionInString[i]);
				if (someVertexDescription[i] < 1) {
					throw new ObjReaderExceptions.ObjReaderException("Some vector reference cannot be negative.", lineInd);
				}
			}
			switch (someVertexDescription.length) {
				case 1 -> {
					onePolygonVertexIndices.add(someVertexDescription[0] - 1);
				}
				case 2 -> {
					onePolygonVertexIndices.add(someVertexDescription[0] - 1);
					onePolygonTextureVertexIndices.add(someVertexDescription[1] - 1);
				}
				case 3 -> {
					onePolygonVertexIndices.add(someVertexDescription[0] - 1);
					if (!someVertexDescriptionInString[1].equals("")) {
						onePolygonTextureVertexIndices.add(someVertexDescription[1] - 1);
					}
					onePolygonNormalIndices.add(someVertexDescription[2] - 1);
				}
				default -> throw new ObjReaderExceptions.ObjReaderException("Invalid element size.", lineInd);
			}
		} catch(NumberFormatException e) {
			throw new ObjReaderExceptions.ObjReaderException("Failed to parse int value.", lineInd);
		}
	}
}
