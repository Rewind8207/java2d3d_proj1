package com.local;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
// import java.util.List;

class DrawCurves extends JPanel{
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        double minX = initialMinX;
        double maxX = initialMaxX;
        double minY = initialMinY;
        double maxY = initialMaxY;
        
        double dataW = maxX-minX;
        double dataH = maxY-minY;
        
        // set margin = 50 pixels
        int margin = 50;
        // usable width and height in panel
        int w = getWidth()-2*margin;
        int h = getHeight()-2*margin;
        // compute scale factor
        double scaleX = w/dataW;
        double scaleY = h/dataH;
        double scale = Math.min(scaleX, scaleY);
        // compute center of data
        double cx = (minX+maxX)/2.0;
        double cy = (minY+maxY)/2.0;
        // center of panel
        double panelCX = getWidth()/2.0;
        double panelCY = getHeight()/2.0;

        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2.0f));

        for (var curve : this.curves){
            var path = new Path2D.Double();
            var p0 = curve.get(0);
            double sx = (p0.x-cx)*scale+panelCX;
            // y axis is flipped in screen coordinates
            double sy = panelCY-(p0.y-cy)*scale;
            path.moveTo(sx, sy);
            
            for (int i=1;i<curve.size();i++){
                var p = curve.get(i);
                double px = (p.x-cx)*scale+panelCX;
                double py = panelCY-(p.y-cy)*scale;
                path.lineTo(px, py);
            }
            path.closePath();
            g2d.draw(path);
            
            if (showTangentsAndNormals == true){
                drawTangentAndNormal(g2d, curve, scale, cx, cy, panelCX, panelCY);
            }
        }
    }

    public void setCurves(ArrayList<ArrayList<Point2D.Double>> newCurves){
        this.curves = newCurves;
        
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        
        for (var curve : this.curves) {
            for (Point2D.Double p : curve) {
                if (p.x < minX) minX = p.x;
                if (p.x > maxX) maxX = p.x;
                if (p.y < minY) minY = p.y;
                if (p.y > maxY) maxY = p.y;
            }
        }
        
        this.initialMinX = minX;
        this.initialMaxX = maxX;
        this.initialMinY = minY;
        this.initialMaxY = maxY;

        repaint();
    }

    // loop index function for closed curves
    private int getIndex(int i, int size){
        if (i<0) return (size-1);
        if (i>=size) return 0;
        return i;
    }

    public void setShowTangentsAndNormals(boolean show){
        this.showTangentsAndNormals = show;
        repaint();
    }
    
    private void drawTangentAndNormal(Graphics2D g2d, ArrayList<Point2D.Double> curve, 
        double scale, double cx, double cy, double panelCX, double panelCY){
    
        int n = curve.size();
        
        // set arrow length 20 pixels
        double arrowLen = 20.0;

        for (int i=0;i<n;i++) {
            var prev = curve.get(getIndex(i-1, n));
            var curr = curve.get(i);
            var next = curve.get(getIndex(i+1, n));

            // compute tangent vector using central difference
            double tx = next.x-prev.x;
            double ty = next.y-prev.y;

            // normalize tangent
            double len = Math.sqrt(tx*tx+ty*ty);
            tx = tx/len;
            ty = ty/len;

            // compute normal vector (rotate tangent by 90 degrees counter-clockwise)
            double nx = -ty;
            double ny = tx;

            double sx = (curr.x-cx)*scale+panelCX;
            double sy = panelCY-(curr.y-cy)*scale;

            //  draw tangent vector
            g2d.setColor(Color.BLUE);
            var tangentLine = new java.awt.geom.Line2D.Double(sx, sy, sx+tx*arrowLen, sy-ty*arrowLen);
            g2d.draw(tangentLine);

            // draw normal vector
            g2d.setColor(Color.GREEN);
            var normalLine = new java.awt.geom.Line2D.Double(sx, sy, sx+nx*arrowLen, sy-ny*arrowLen);
            g2d.draw(normalLine);
        }
    }

    public void evolve(double dt, int repaintInterval, int iter){

        var newCurves = new ArrayList<ArrayList<Point2D.Double>>();

        for (var curve : this.curves) {
            var newCurve = new ArrayList<Point2D.Double>();
            int n = curve.size();

            for (int i = 0; i < n; i++) {
                var prev = curve.get(getIndex(i-1, n));
                var curr = curve.get(i);
                var next = curve.get(getIndex(i+1, n));

                // compute curvature (Kappa_A)
                double v1x = curr.x-prev.x;
                double v1y = curr.y-prev.y;
                double len1 = Math.sqrt(v1x*v1x+v1y*v1y);
                double v2x = next.x-curr.x;
                double v2y = next.y-curr.y;
                double len2 = Math.sqrt(v2x*v2x+v2y*v2y);

                // use atan2 to compute angle between v1 and v2
                double angle1 = Math.atan2(v1y, v1x);
                double angle2 = Math.atan2(v2y, v2x);
                double theta = angle2 - angle1;
                
                if (theta <= -Math.PI) theta += 2*Math.PI;
                if (theta > Math.PI) theta -= 2*Math.PI;

                // compute tangent vector using central difference
                double tx = next.x-prev.x;
                double ty = next.y-prev.y;

                // normalize tangent
                double len = Math.sqrt(tx*tx+ty*ty);
                tx = tx/len;
                ty = ty/len;

                // compute normal vector (rotate tangent by 90 degrees counter-clockwise)
                double nx = -ty;
                double ny = tx;
                
                double avgLen = (len1 + len2) / 2.0;
                double curvature = theta / avgLen;
                
                // update point cordinates
                double deltaX = dt * curvature * nx;
                double deltaY = dt * curvature * ny;

                newCurve.add(new Point2D.Double(curr.x + deltaX, curr.y + deltaY));
            }
            newCurves.add(newCurve);
        }
        
        this.curves = newCurves;

        // if (iter % repaintInterval == 0 && iter > 0){
        //     repaint();
        // }

        repaint();
    }

    private ArrayList<ArrayList<Point2D.Double>> curves;
    private boolean showTangentsAndNormals = false;

    private Double initialMinX = null;
    private Double initialMaxX = null;
    private Double initialMinY = null;
    private Double initialMaxY = null;
}

public class Main{
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("Project 1: 2D Curve Viewer (Evolving)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawCurves myPanel = new DrawCurves();

        boolean showTangentsAndNormals = true;
        boolean showCurvatureEvole = false;
        
        myPanel.setCurves(VertFileReader.loadCurves("riderr.vert"));
        myPanel.setShowTangentsAndNormals(showTangentsAndNormals);
        
        frame.add(myPanel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        int iter = 0;
        double dt = 0.001;
        int repaintInterval = 1000;
        // while (true) {
        //     myPanel.evolve(dt, repaintInterval, iter);
        //     iter++;
        // }

        if (showCurvatureEvole) {
            Timer timer = new Timer(50, e -> {
            myPanel.evolve(dt, repaintInterval, iter);
            });
            timer.setInitialDelay(1000); 
            timer.start();
        }
        
    }
}