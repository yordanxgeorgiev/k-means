import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

// If the program doesn't compile add this to the configurations
// --module-path "C:\Program Files\Java\javafx-sdk-13\lib" --add-modules javafx.controls,javafx.fxml
// where the path to the lib folder is correct

public class Main extends Application {

    private static final boolean kMeansPlusPlus = true;
    private int k;
    private static final int randomRestarts = 10;
    private String fileName = "";
    private String filePath= "";
    private static final int sceneWidth = 700;
    private static final int sceneHeight = 700;
    private static final Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
                                            Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.CHARTREUSE,
                                            Color.SPRINGGREEN, Color.AZURE, Color.VIOLET, Color.PINK};

    @Override
    public void start(Stage primaryStage) {

        // get input from user
        userInput();
        // read the data
        ArrayList<Point2D.Double> points = readData(filePath);

        // run the kMeans algorithm with random restart and return the best solution
        System.out.println("Calculating...");
        double minDistance = Double.MAX_VALUE;
        ArrayList<Centroid> minCentroids = new ArrayList<>();

        for(int i = 0; i < randomRestarts; i++)
        {
            ArrayList<Centroid> centroids = kMeans(points);
            double sumDistance = 0;
            for (Centroid centroid : centroids) {
                sumDistance += centroid.sumInnerDistance();
            }
            if(sumDistance < minDistance)
            {
                minDistance = sumDistance;
                minCentroids = centroids;
            }
        }

        primaryStage.setTitle("kMeans");
        System.out.println("Visualizing...");

        // visualize the points
        Group root = new Group();
        for(int i = 0; i < minCentroids.size(); i++)
        {
            addToGroup(minCentroids.get(i), colors[i], root);
        }

        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void userInput()
    {
        System.out.print("Enter file(normal.txt or unbalance.txt): ");
        Scanner scanner = new Scanner(System.in);
        fileName = scanner.nextLine();
        filePath = "data/" + fileName;
        System.out.print("Enter clusters: ");
        k = scanner.nextInt();
    }


    private ArrayList<Centroid> kMeans(ArrayList<Point2D.Double> points)
    {
        ArrayList<Centroid> centroids = new ArrayList<>();
        ArrayList<Centroid> nextCentroids = new ArrayList<>();

        // initialize the centroids
        initialize(nextCentroids, points);

        // repeat while centroids change position
        while(!sameCentroids(centroids, nextCentroids))
        {
            centroids = new ArrayList<>();
            for (Centroid centroid : nextCentroids) {
                centroids.add(new Centroid(centroid));
                centroid.resetPoints();
            }

            // find closest centroid for each point
            for (Point2D.Double point : points) {
                closestCentroid(point, nextCentroids).addPoint(point);
            }
            // reposition centroids
            for (Centroid nextCentroid : nextCentroids) {
                nextCentroid.setAverageCentroid();
            }
        }

        return nextCentroids;
    }

    private void initialize(ArrayList<Centroid> centroids, ArrayList<Point2D.Double> points)
    {
        Random rnd = new Random();
        Point2D.Double chosenPoint;
        if(!kMeansPlusPlus)
        {
            for(int i = 0; i < k; i++)
            {
                // randomly choose a point
                chosenPoint = points.get(rnd.nextInt(points.size()));
                // add it to the centroids
                centroids.add(new Centroid(chosenPoint));
            }
        }
        else
        {
            // randomly select and add the first centroid from the points
            chosenPoint = points.get(rnd.nextInt(points.size()));
            centroids.add(new Centroid(chosenPoint));

            // next select the points furthest from the nearest centroid
            for(int i = 1; i < k; i++)
            {
                double maxDistance = Double.MIN_VALUE;
                chosenPoint = null;
                for (Point2D.Double point : points) {
                    double minDistance = minDistance(centroids, point);

                    if (minDistance != Double.MAX_VALUE && minDistance > maxDistance && !centroids.contains(new Centroid(point))) {
                        //System.out.println(i + " " + point.toString());
                        maxDistance = minDistance;
                        chosenPoint = point;
                    }
                }
                centroids.add(new Centroid(chosenPoint));
            }
        }
    }

    private double minDistance(ArrayList<Centroid> centroids, Point2D.Double point)
    {
        double minDistance = Double.MAX_VALUE;
        double distance;
        for (Centroid centroid : centroids) {
            distance = distance(point, centroid);
            if (distance < minDistance && distance > 0.01) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private void addToGroup(Centroid centroid, Color color, Group group)
    {
        ArrayList<Point2D.Double> points = centroid.getPoints();
        for (Point2D.Double point : points) {
            Circle circle = new Circle();
            circle.setCenterX(point.getX());
            circle.setCenterY(sceneHeight - point.getY());
            circle.setRadius(5);
            circle.setFill(color);
            circle.setStroke(Color.BLACK);
            group.getChildren().add(circle);
        }

        Circle circle = new Circle();
        circle.setCenterX(centroid.getCentroid().getX());
        circle.setCenterY(sceneHeight - centroid.getCentroid().getY());
        circle.setRadius(10);
        circle.setFill(Color.BLACK);
        circle.setStroke(Color.BLACK);
        group.getChildren().add(circle);
    }

    private boolean sameCentroids(ArrayList<Centroid> a, ArrayList<Centroid> b)
    {
        if(a.size() != b. size())
            return false;

        for(int i = 0; i < a.size(); i++)
        {
            if(!a.get(i).equals(b.get(i)))
            {
                return false;
            }
        }

        return true;
    }


    private Centroid closestCentroid(Point2D.Double p, ArrayList<Centroid> centroids)
    {
        Centroid minCentroid = null;
        double minDist = Double.MAX_VALUE;
        double distance;
        for (Centroid centroid : centroids) {
            distance = distance(p, centroid);
            if (distance < minDist) {
                minDist = distance;
                minCentroid = centroid;
            }
        }

        return minCentroid;
    }

    private double distance(Point2D.Double p, Centroid c)
    {
        return c.distance(p, c.getCentroid());
    }

    private ArrayList<Point2D.Double> readData(String filePath)
    {
        System.out.println("Reading data...");
        ArrayList<Point2D.Double> points = new ArrayList<>();

        double scale = 0;
        double offsetX = 0;
        double offsetY = 0;

        if(fileName.equals("normal.txt"))
        {
            scale = 250;
            offsetX = -900;
            offsetY = -900;
        }
        else if(fileName.equals("unbalance.txt"))
        {
            scale = (double) 1/800;
            offsetX = -100;
            offsetY = -100;
        }
        else
        {
            System.out.println("Scaling settings not found for file");
            System.exit(0);
        }

        try {
            File file = new File(filePath);
            Scanner reader = new Scanner(file).useLocale(Locale.US); // Locale.US is for reading '.' doubles
            while (reader.hasNextDouble()) {
                points.add(
                        new Point2D.Double(reader.nextDouble()*scale + offsetX,
                                                reader.nextDouble()*scale + offsetY
                        )
                );
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return points;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
