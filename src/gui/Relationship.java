package gui;

import com.util.*;
import com.test.*;
import com.model.*;

import javax.swing.*;
import java.awt.*;

public class Relationship {
    Color color = Color.black;
    JPanel from, to;
    public Relationship(JPanel from, JPanel to) {
        this.from = from;
        this.to = to;
    }

    public Relationship(JPanel from, JPanel to, Color color) {
        this.color = color;
        this.from = from;
        this.to = to;
    }

    public JPanel getFrom() {
        return from;
    }

    public JPanel getTo() {
        return to;
    }

    private void drawArrow(Graphics g, Point f, Point t, Point p1, Point p2){
        g.setColor(color);
        g.drawLine(f.x, f.y, (p1.x+p2.x)/2, (p1.y+p2.y)/2);
        g.drawLine(p1.x, p1.y, t.x, t.y);
        g.drawLine(p2.x, p2.y, t.x, t.y);
        g.drawLine(p2.x, p2.y, p1.x, p1.y);
        g.setColor(Color.BLACK);
    }

    private boolean inside(Point p, Rectangle rec) {
        if (p.x>= rec.x && p.x<= rec.x+rec.width && p.y>=rec.y && p.y<=rec.y+rec.height) return  true;
        return false;
    }

    private boolean isOverlap(Rectangle rec1, Rectangle rec2){
        int x1 = rec1.x;
        int y1 = rec1.y;
        int w1 = rec1.width;
        int h1 = rec1.height;
        int x2 = rec2.x;
        int y2 = rec2.y;
        int w2 = rec2.width;
        int h2 = rec2.height;

        if (inside(new Point(x1,y1) , rec2)) return true;
        if (inside(new Point(x1,y1+h1) , rec2)) return true;
        if (inside(new Point(x1+w1,y1) , rec2)) return true;
        if (inside(new Point(x1+w1,y1+h1) , rec2)) return true;
        if (inside(new Point(x2,y2) , rec1)) return true;
        if (inside(new Point(x2,y2+h2) , rec1)) return true;
        if (inside(new Point(x2+w2,y2) , rec1)) return true;
        if (inside(new Point(x2+w2,y2+h2) , rec1)) return true;
        return false;
    }

    public void draw(Graphics g) {
        Rectangle rectf = new Rectangle();
        rectf.setLocation(from.getLocation());
        rectf.setSize(new Dimension(from.getSize()));

        Rectangle rectt = new Rectangle();
        rectt.setLocation(to.getLocation());
        rectt.setSize(new Dimension(to.getSize()));

        if (isOverlap(rectf,rectt)) return;

        Point pfr = new Point(rectf.x + rectf.width / 2, rectf.y + rectf.height / 2);
        Point pto = new Point(rectt.x + rectt.width / 2, rectt.y + rectt.height / 2);
        double goX = (pto.x != pfr.x) ? (pto.x - pfr.x) / Math.abs(pto.x - pfr.x) : 0;
        double goY = (pto.y != pfr.y) ? (pto.y - pfr.y) / Math.abs(pto.y - pfr.y) : 0;

        double X, Y;
        if (Math.abs(pfr.x - pto.x) > Math.abs(pfr.y - pto.y)) {
            X = pfr.x;
            Y = pfr.y;
            for (int i = 0; i <= Math.abs(pfr.x - pto.x); i++) {
                X += goX;
                Y += goY * Math.abs(pto.y - pfr.y) / Math.abs(pto.x - pfr.x);
                if (Y >= rectf.y + rectf.height || Y <= rectf.y || X >= rectf.x + rectf.width || X <= rectf.x) {
                    pfr.setLocation(X, Y);
                    break;
                }
            }
            X = pto.x;
            Y = pto.y;
            for (int i = 0; i <= Math.abs(pfr.x - pto.x); i++) {
                X -= goX;
                Y -= goY * Math.abs(pto.y - pfr.y) / Math.abs(pto.x - pfr.x);
                if (Y >= rectt.y + rectt.height || Y <= rectt.y || X >= rectt.x + rectt.width || X <= rectt.x) {
                    pto.setLocation(X, Y);
                    break;
                }
            }
            Point p = new Point();
            for (int i=0; i<=20; i++){
                X -= goX;
                Y -= goY * Math.abs(pto.y - pfr.y) / Math.abs(pto.x - pfr.x);
                if (Math.sqrt((pto.y-Y)*(pto.y-Y) + (pto.x-X)*(pto.x-X))>=10){
                    p.setLocation(X,Y);
                    break;
                }
            }
            Point p1 = new Point();
            Point p2 = new Point();
            for (int i=0; i<=10; i++){
                X -= goY * Math.abs(pto.y - pfr.y) / Math.abs(pto.x - pfr.x);
                Y += goX;
                if (Math.sqrt((p.y-Y)*(p.y-Y) + (p.x-X)*(p.x-X))>=6){
                    p1.setLocation(X,Y);
                    break;
                }
            }
            p2.setLocation(p.x*2 - p1.x, p.y*2 - p1.y);
            drawArrow(g,pfr,pto,p1,p2);
            //g.drawLine(pfr.x, pfr.y, pto.x, pto.y);
            return;
        }

        X = pfr.x;
        Y = pfr.y;
        for (int i = 0; i <= Math.abs(pfr.y - pto.y); i++) {
            Y += goY;
            X += goX * Math.abs(pto.x - pfr.x) / Math.abs(pto.y - pfr.y);
            if (Y >= rectf.y + rectf.height || Y <= rectf.y || X >= rectf.x + rectf.width || X <= rectf.x) {
                pfr.setLocation(X, Y);
                break;
            }
        }
        X = pto.x;
        Y = pto.y;
        for (int i = 0; i <= Math.abs(pfr.y - pto.y); i++) {
            Y -= goY;
            X -= goX * Math.abs(pto.x - pfr.x) / Math.abs(pto.y - pfr.y);
            if (Y >= rectt.y + rectt.height || Y <= rectt.y || X >= rectt.x + rectt.width || X <= rectt.x) {
                pto.setLocation(X, Y);
                break;
            }
        }

        Point p = new Point();
        for (int i=0; i<=20; i++){
            Y -= goY;
            X -= goX * Math.abs(pto.x - pfr.x) / Math.abs(pto.y - pfr.y);
            if (Math.sqrt((pto.y-Y)*(pto.y-Y) + (pto.x-X)*(pto.x-X))>=10){
                p.setLocation(X,Y);
                break;
            }
        }
        Point p1 = new Point();
        Point p2 = new Point();
        for (int i=0; i<=10; i++){
            Y -= goX * Math.abs(pto.x - pfr.x) / Math.abs(pto.y - pfr.y);
            X += goY;
            if (Math.sqrt((p.y-Y)*(p.y-Y) + (p.x-X)*(p.x-X))>=6){
                p1.setLocation(X,Y);
                break;
            }
        }
        p2.setLocation(p.x*2 - p1.x, p.y*2 - p1.y);
        drawArrow(g,pfr,pto,p1,p2);
        //g.drawLine(pfr.x, pfr.y, pto.x, pto.y);
    }
}