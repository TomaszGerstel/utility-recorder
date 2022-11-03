import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.Stream;

class DiagramPanel extends JPanel {

    Insets ins;
    LinkedHashMap<Date, Float> records;
    Set<Map.Entry<Date, Float>> recordsToIterable;
    Stream<Map.Entry<Date, Float>> recStream;
    int height, width;
    final float marginYRatio = 10;

    DiagramPanel(LinkedHashMap<Date, Float> records) {

        this.records = records;
        recordsToIterable = records.entrySet();
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        setMinimumSize(new Dimension(500, 250));
        setPreferredSize(new Dimension(550, 250));
    }

    protected void paintComponent(Graphics g) {
        // Zawsze należy najpierw wywołać met. nadklasy
        super.paintComponent(g);
        height = getHeight();
        width = getWidth();
        ins = getInsets();
        int recNumbers = recordsToIterable.size();
//        double scale = getYScale();

        int[] xPoints = new int[recNumbers];
        int[] yPoints = new int[recNumbers];
        float[] values = new float[recNumbers];

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




//        int val = (int)Math.floor(getMin());

//        float yScale = getYScale();
//        int minValue = getMin();
//        int minLineY = Math.round(roundToFullNumberLevel(minValue) * yScale);

        g.drawLine(width/40, height-(height/20), width/40, height/20);
        g.drawLine(width/40, (height-(height/20)),width-width/40, (height-(height/20)));

//        g.drawLine(width/40, height-minLineY, width-width/40, height-minLineY);


        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.ORANGE);
        g2.drawPolyline(xPoints, yPoints, recordsToIterable.size());

        g2.setStroke(new BasicStroke(5));
        g2.setColor(Color.GRAY);

//        g.fillOval(xPoints[1]-3, yPoints[1]-3, 6,6);
//        g.fillOval(xPoints[2], yPoints[2], 6,6);

        for(int i = 0; i < recNumbers; i++) {
            g.fillOval(xPoints[i]-3, yPoints[i]-3, 6,6);
            g.drawString(String.valueOf(values[i]), xPoints[i], yPoints[i]);
        }



//        g.drawString(String.valueOf(val), width/30, (int)Math.floor(getMin()));






    }

    private int roundToFullNumberLevel(float n) {
        int m =  Math.round(n);
        int rank = 1;
        int it = String.valueOf(m).length() - 1;

        for(int i = 0; i < it; i++) {
            rank = rank * 10;
        }
        m = m / rank * rank;
        return m;
    }


    private float getYScale() {
        float range = getRangeY();
        if (range == 0) range = getMaxY() * 2;
        return ((height-(height/(marginYRatio/2))) / range);
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

