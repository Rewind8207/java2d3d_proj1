package com.local;

import java.io.InputStream;
import java.util.Scanner;
import java.util.ArrayList;
import java.awt.geom.Point2D;
import java.util.Locale;

public class VertFileReader {

    public static ArrayList<ArrayList<Point2D.Double>> loadCurves(String resourceName) {
        var curves = new ArrayList<ArrayList<Point2D.Double>>();
        
        InputStream is = VertFileReader.class.getClassLoader().getResourceAsStream(resourceName);
        
        if (is == null) {
            System.err.println("Error: Can't find file: " + resourceName);
            return curves;
        }
        
        try (Scanner scanner = new Scanner(is)) {
            scanner.useLocale(Locale.US);
            
            if (scanner.hasNextInt()) {
                int numComponents = scanner.nextInt();
                for (int i = 0; i < numComponents; i++) {
                    var currentCurve = new ArrayList<Point2D.Double>();
                    if (scanner.hasNextInt()) {
                        int numVertices = scanner.nextInt();
                        for (int j = 0; j < numVertices; j++) {
                            if (scanner.hasNextDouble()) {
                                double x = scanner.nextDouble();
                                double y = scanner.nextDouble();
                                currentCurve.add(new Point2D.Double(x, y));
                            }
                        }
                    }
                    curves.add(currentCurve);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return curves;
    }
    

    public static void main(String[] args) {
        
        String fileName = "superior.vert"; 
        var data = loadCurves(fileName);
        
        if (!data.isEmpty() && !data.get(0).isEmpty()) {
            for (int i = 0; i < data.size(); i++) {
                System.out.println("Curve " + i + ":");
                for (int j =0; j < data.get(i).size() && j < 5; j++) {
                    Point2D.Double point = data.get(i).get(j);
                    System.out.println("Point " + j + ": (" + point.x + ", " + point.y + ")");
                }
            }
        }
    }

}
