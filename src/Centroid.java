import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Centroid {
    private Point2D.Double centroid;
    private ArrayList<Point2D.Double> points;

    public Centroid(Point2D.Double centroid)
    {
        this.centroid = centroid;
        points = new ArrayList<>();
    }

    public Centroid(Point2D.Double centroid, ArrayList<Point2D.Double> points)
    {
        this.centroid = centroid;
        this.points = points;
    }

    public void resetPoints()
    {
        points = new ArrayList<>();
    }

    public Centroid(Centroid c)
    {
        centroid = c.getCentroid();
        points = c.getPoints();
    }

    public void setAverageCentroid() {
        centroid = averageCenter();
    }

    public Point2D.Double getCentroid() {
        return centroid;
    }

    public ArrayList<Point2D.Double> getPoints() {
        return points;
    }

    public void addPoint(Point2D.Double point)
    {
        if(points == null)
        {
            points = new ArrayList<>();
        }
        points.add(point);
    }

    public double distance(Point2D.Double p1, Point2D.Double p2)
    {
        return Math.hypot(p1.getX()-p2.getX(), p1.getY()-p2.getY());
    }

    public Point2D.Double averageCenter()
    {
        double averageX = 0, averageY = 0;

        for(Point2D.Double p : points)
        {
            averageX += p.getX();
            averageY += p.getY();
        }

        averageX /= points.size();
        averageY /= points.size();

        return new Point2D.Double(averageX, averageY);
    }

    public double sumInnerDistance()
    {
        double sum = 0;

        for(Point2D.Double p : points)
        {
            sum += distance(p, centroid);
        }

        return sum;
    }

    @Override
    public String toString() {
        return "Centroid{" +
                "centroid=" + centroid +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Centroid centroid1 = (Centroid) o;
        double x1 = Math.round(centroid.getX()*100)/100;
        double x2 = Math.round(centroid1.getCentroid().getX()*100)/100;
        double y1 = Math.round(centroid.getY()*100)/100;
        double y2 = Math.round(centroid1.getCentroid().getY()*100)/100;

        return x1 == x2 && y1 == y2;
    }

}
