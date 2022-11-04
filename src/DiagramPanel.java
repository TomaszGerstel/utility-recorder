import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

class DiagramPanel extends JPanel {

    LinkedHashMap<Date, Float> records;
    Set<Map.Entry<Date, Float>> recordsToIterable;
    Stream<Map.Entry<Date, Float>> recStream;
    int height, width;
    final float marginYRatio = 10;
    float average;
    int leftEdgeOfDiagram, bottomEdgeOfDiagram, topEdgeOfDiagram, rightEdgeOfDiagram;
    int bottomEdgeOfDrawing, topEdgeOfDrawing;
    int recNumbers;
    int[] xPoints, yPoints;
    float[] values;
    float yScale;
    float drawingHeight;


    DiagramPanel(LinkedHashMap<Date, Float> records, float average) {

        this.records = records;
        this.average = average;
        recordsToIterable = records.entrySet();
        recNumbers = recordsToIterable.size();
        xPoints = new int[recNumbers];
        yPoints = new int[recNumbers];
        values = new float[recNumbers];
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        setMinimumSize(new Dimension(500, 250));
        setPreferredSize(new Dimension(550, 250));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        height = getHeight();
        width = getWidth();
        leftEdgeOfDiagram = width/40;
        bottomEdgeOfDiagram = height-(height/20);
        topEdgeOfDiagram = height/20;
        rightEdgeOfDiagram = width-width/40;
        bottomEdgeOfDrawing = bottomEdgeOfDiagram-(height/20);
        topEdgeOfDrawing = topEdgeOfDiagram + height/20;
        drawingHeight = height - (height-bottomEdgeOfDrawing) - topEdgeOfDrawing;

        yScale = getYScale();

        generateDataToDiagram();
        drawDiagramLines(g);
        drawAverageLine(g);

        Graphics2D g2 = (Graphics2D) g;
        drawDiagram(g2);
        drawPoints(g2);
    }

    private void drawDiagram(Graphics2D g2) {
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.ORANGE);
        g2.drawPolyline(xPoints, yPoints, recNumbers);
    }

    private void drawPoints(Graphics2D g) {
        g.setStroke(new BasicStroke(5));
        g.setColor(Color.GRAY);
        for(int i = 0; i < recNumbers; i++) {
            g.fillOval(xPoints[i]-3, yPoints[i]-3, 6,6);
            g.drawString(String.valueOf(values[i]), xPoints[i], yPoints[i]);
        }
    }

//    private int roundToFullNumberLevel(float n) {
//        int m =  Math.round(n);
//        int rank = 1;
//        int it = String.valueOf(m).length() - 1;
//        for(int i = 0; i < it; i++) {
//            rank = rank * 10;
//        }
//        m = m / rank * rank;
//        return m;
//    }

    private void generateDataToDiagram() {

        int xStep = (width) / (recNumbers+1);
        int xStepCum = xStep;
        int tabIndex = 0;
        for (Map.Entry<Date, Float> entry : recordsToIterable) {
            xPoints[tabIndex] = xStepCum;
            yPoints[tabIndex] = getYToPoint(entry.getValue());
            values[tabIndex] = entry.getValue();
            xStepCum += xStep;
            tabIndex += 1;
        }
    }

    private void drawDiagramLines(Graphics g) {
        g.drawLine(leftEdgeOfDiagram, bottomEdgeOfDiagram, leftEdgeOfDiagram, topEdgeOfDiagram);
        g.drawLine(leftEdgeOfDiagram, bottomEdgeOfDiagram, rightEdgeOfDiagram, bottomEdgeOfDiagram);
    }

    private void drawAverageLine(Graphics g) {
        g.drawLine(leftEdgeOfDiagram, Math.round(bottomEdgeOfDrawing - (average * yScale) + (getMinY() * yScale)),
                width-width/40, Math.round(bottomEdgeOfDrawing - (average * yScale) + (getMinY() * yScale)));
        g.drawString("average: "+ String.format("%.01f", average), leftEdgeOfDiagram+5,
                Math.round(bottomEdgeOfDrawing - (average * yScale) + (getMinY() * yScale))-5);
    }

    private float getYScale() {
        float range = getRangeY();
        if (range == 0) range = getMaxY() * 2;
        return (drawingHeight / range);
    }

    private float getRangeY() {
        return getMaxY() - getMinY();
    }

    private int getMinY() {
        recStream = recordsToIterable.stream();
        return (int) Math.round(recStream.mapToDouble(Map.Entry::getValue).min().getAsDouble());
    }

    private int getMaxY() {
        recStream = recordsToIterable.stream();
        return (int) Math.round(recStream.mapToDouble(Map.Entry::getValue).max().getAsDouble());
    }

    private int getYToPoint(double val) {
        float scale = getYScale();
        int y;
        if(getRangeY() == 0) y = (int) Math.round((height-(height/marginYRatio))  - (val * scale));
        else y = (int) Math.round((height-(height/marginYRatio))  - (val * scale) + (getMinY() * scale));
        return y;
    }
}

