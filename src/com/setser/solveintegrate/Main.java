package com.setser.solveintegrate;

import com.setser.solveintegrate.utils.*;

import com.sun.xml.internal.fastinfoset.algorithm.DoubleEncodingAlgorithm;
import com.sun.xml.internal.messaging.saaj.soap.impl.TextImpl;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.function.Function;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private void testIntegrate() {
        System.out.println("s(sin(x), -3.14, 3.14) = " + Double.toString(MathUtils.integrate(Math::sin, -3.14, 3.14, 0.0001)));
        System.out.println("s(cos(x), -3.14, 3.14) = " + Double.toString(MathUtils.integrate(Math::cos, -3.14, 3.14, 0.0001)));
        System.out.println("s(sin(x), -1.57, 1.57) = " + Double.toString(MathUtils.integrate(Math::sin, -1.57, 1.57, 0.0001)));
        System.out.println("s(cos(x), -1.57, 1.57) = " + Double.toString(MathUtils.integrate(Math::cos, -1.57, 1.57, 0.0001)));
        System.out.println("s(x, 0, 3) = " + Double.toString(MathUtils.integrate((Double x) -> x, 0, 3, 0.0001)));
        System.out.println("s(5, 0, 3) = " + Double.toString(MathUtils.integrate((Double x) -> 5.0, 0, 3, 0.0001)));
        System.out.println("s(x^2, 0, 3) = " + Double.toString(MathUtils.integrate((Double x) -> x * x, 0, 3, 0.0001)));
    }

    private void testSolve() {
        System.out.println("sin(x) = 0 when x = " + Double.toString(MathUtils.solve(Math::sin, Math::cos, -2, 2, 0.0001)));
        System.out.println("x = 0 when x = " + Double.toString(MathUtils.solve((Double x) -> x, (Double x) -> 1.0, -2, 7, 0.0001)));
        System.out.println("x^2 - 3*x + 2 = 0 when x = " + Double.toString(MathUtils.solve((Double x) -> (x * x - 3 * x + 2),
                (Double x) -> (2 * x - 3), -5, 5, 0.0001)));
        System.out.println("x^3 - cos(x) = 0 when x = " + Double.toString(MathUtils.solve((Double x) -> (x * x * x - Math.cos(x)),
                (Double x) -> (3 * x * x + Math.sin(x)), -1, 3, 0.0001)));
    }

    private void testDraw(Canvas canvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        draw(canvas.getGraphicsContext2D(), 10, 10, width / 2 - 20, height - 20,
                (Double x) -> (3 * x + MathUtils.integrate((Double y) -> 3*y + Math.pow(Math.sin(y) + 2, 4), 0, x, 0.01)), -20, 20);
        draw(canvas.getGraphicsContext2D(), width / 2 + 10, 10, width / 2 - 20, height - 20,
                (Double x) -> 3*x + Math.pow(Math.sin(x) + 2, 4), -20, 20);
    }

    private void draw(GraphicsContext graphicsContext, double xleft, double ytop, double width, double height,
                      Function<Double, Double> f, double a, double b) {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        int n = 1000;
        graphicsContext.translate(xleft, ytop);
        graphicsContext.clearRect(0, 0, width, height);
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeRect(0, 0, width, height);
        graphicsContext.save();
        int n1 = 20, n2 = 20;
        double step1, step2;
        step1 = (b - a) / n1;
        double ymin = f.apply(a);
        double ymax = f.apply(a);
        double eps = 0.000001;
        double step = (b - a) / n;
        for(int i = 1; i <= n; ++i) {
            if(ymin - f.apply(a + i * step) >= -eps) ymin = f.apply(a + i * step);
            if(f.apply(a + i * step) - ymax >= -eps) ymax = f.apply(a + i * step);
        }
        double xa = 10;
        double ya = 10;
        double xcoef = width / (b - a);
        double ycoef;
        double xshift = width * a / (a - b);
        double yshift = height * ymax / (ymax - ymin);
        if(Math.abs(ymax - ymin) < eps) {
            ycoef = xcoef;
            step2 = step1;
        }
        else {
            ycoef = height / (ymin - ymax);
            step2 = (ymax - ymin) / n2;
        }
        if ((a <= eps) && (b >= -eps)) {
            graphicsContext.beginPath();
            graphicsContext.moveTo(xcoef * 0 + xshift, 0);
            graphicsContext.lineTo(xcoef * 0 + xshift, height);
            graphicsContext.stroke();
        }
        if ((ymin <= eps) && (ymax >= eps)) {
            graphicsContext.beginPath();
            graphicsContext.moveTo(0, ycoef * 0 + yshift);
            graphicsContext.lineTo(width, ycoef * 0 + yshift);
            graphicsContext.stroke();
        }
        graphicsContext.beginPath();
        graphicsContext.moveTo(xcoef * a + xshift, ycoef * f.apply(a) + yshift);
        for (int i = 1; i <= n; ++i) {
            graphicsContext.lineTo(xcoef * (a + i * step) + xshift, ycoef * f.apply(a + i * step) + yshift);
            graphicsContext.moveTo(xcoef * (a + i * step) + xshift, ycoef * f.apply(a + i * step) + yshift);
        }
        double eps1 = 0.5;
        if(((step1 * 1000 - (int)(step1 * 1000)) <= eps1) || ((int)(step1 * 1000 + 1) - step1 * 1000) <= eps1) {
            if((step1 * 1000 - (int)(step1 * 1000)) <= eps1) step1 = ((int)(step1 * 1000)) / 1000.0;
            else step1 = ((int)(step1 * 1000 + 1)) / 1000.0;
            double begin = (1000 * a - (int)(1000 * a) >= eps) ? ((int)(1000 * a + 1)) / 1000.0 : a;
            while(b - begin >= eps) {
                graphicsContext.moveTo(xcoef * begin + xshift, ycoef * 0 + yshift + 5);
                graphicsContext.lineTo(xcoef * begin + xshift, ycoef * 0 + yshift - 5);
                graphicsContext.fillText(decimalFormat.format(begin), xcoef * begin + xshift, ycoef * 0 + yshift + 10);
                begin += step1;
            }
        }
        if(((step2 * 1000 - (int)(step2 * 1000)) <= eps1) || ((int)(step2 * 1000 + 1) - step2 * 1000) <= eps1) {
            if((step2 * 1000 - (int)(step2 * 1000)) <= eps1) step2 = ((int)(step2 * 1000)) / 1000.0;
            else step2 = ((int)(step2 * 1000 + 1)) / 1000.0;
            double begin = (1000 * ymin - (int)(1000 * ymin) >= eps) ? ((int)(1000 * ymin + 1)) / 1000.0 : ymin;
            while(ymax - begin >= eps) {
                graphicsContext.moveTo(xcoef * 0 + xshift + 5, ycoef * begin + yshift);
                graphicsContext.lineTo(xcoef * 0 + xshift - 5, ycoef * begin + yshift);
                graphicsContext.fillText(decimalFormat.format(begin), xcoef * 0 + xshift + 10, ycoef * begin + yshift);
                begin += step2;
            }
        }
/*        for(int i = 0; i < n1; ++i) {
            graphicsContext.moveTo(xcoef * (a + i * step1) + xshift, ycoef * 0 + yshift + 5);
            graphicsContext.lineTo(xcoef * (a + i * step1) + xshift, ycoef * 0 + yshift - 5);
            graphicsContext.fillText(df.for(a + i * step1), xcoef * (a + i * step1) + xshift, ycoef * 0 + yshift + 10);
        }
        for(int i = 0; i < n2; ++i) {
            graphicsContext.moveTo(xcoef * 0 + xshift + 5, ycoef * (ymin + i * step2) + yshift);
            graphicsContext.lineTo(xcoef * 0 + xshift - 5, ycoef * (ymin + i * step2) + yshift);
            graphicsContext.fillText(Double.toString(ymin + i * step2), xcoef * 0 + xshift + 10, ycoef * (ymin + i * step2) + yshift);
        }*/
        graphicsContext.stroke();
        graphicsContext.translate(-xleft, -ytop);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();
        VBox mainVBox = new VBox();
        int width = 1200;
        int height = 500;
        Canvas canvas = new Canvas(width, height);
        Function<Double, Double> f = (Double x) -> { return 3*x + MathUtils.integrate(
                (Double y) -> { return 3*y + Math.pow(Math.sin(y) + 2, 4); },
        0, x, 0.01); };
        Function<Double, Double> df = (Double x) -> { return 3*x + Math.pow(Math.sin(x) + 2, 4) - 13; };
        VBox vBoxab = new VBox();
        HBox hBoxa = new HBox();
        Label aLabel = new Label("a:");
        TextField aField = new TextField("-1000");
        hBoxa.getChildren().addAll(aLabel, aField);
        HBox hBoxb = new HBox();
        Label bLabel = new Label("b:");
        TextField bField = new TextField("1000");
        aField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                try {
                    Double a = Double.valueOf(newValue);
                    Double b = Double.valueOf(bField.getText());
                    draw(canvas.getGraphicsContext2D(), 10, 10, (width - 20), (height - 20), f, a, b);
                } catch (NumberFormatException nfe) {
                }
            }
        }));
        bField.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(newValue != null) {
                try {
                    Double a = Double.valueOf(aField.getText());
                    Double b = Double.valueOf(newValue);
                    draw(canvas.getGraphicsContext2D(), 10, 10, (width - 20), (height - 20), f, a, b);
                } catch(NumberFormatException nfe) {
                }
            }
        }));
        hBoxb.getChildren().addAll(bLabel, bField);
        vBoxab.getChildren().addAll(hBoxa, hBoxb);
        HBox hBoxSolve = new HBox();
        VBox vBoxSolve = new VBox();
        Label labelSolution = new Label("Solution:");
        HBox aHBoxSolve = new HBox();
        Label aLabelSolve = new Label("a:");
        TextField aFieldSolve = new TextField();
        aHBoxSolve.getChildren().addAll(aLabelSolve, aFieldSolve);
        HBox bHBoxSolve = new HBox();
        Label bLabelSolve = new Label("b:");
        TextField bFieldSolve = new TextField();
        bHBoxSolve.getChildren().addAll(bLabelSolve, bFieldSolve);
        Button buttonSubmit = new Button("Submit");
        vBoxSolve.getChildren().addAll(labelSolution, aHBoxSolve, bHBoxSolve, buttonSubmit);
        Label labelResult = new Label();
        hBoxSolve.getChildren().addAll(vBoxSolve, labelResult);
        mainVBox.getChildren().addAll(canvas, vBoxab, hBoxSolve);
        buttonSubmit.setOnAction((ActionEvent ev) -> {
            try {
                Double a = Double.valueOf(aFieldSolve.getText());
                Double b = Double.valueOf(bFieldSolve.getText());
                Double solve = MathUtils.solve(f, df, a, b, 0.01);
                labelResult.setText("Root is " + solve.toString());
            } catch (NumberFormatException nfe) {
            }
        });
        draw(canvas.getGraphicsContext2D(), 10, 10, (width - 20), (height - 20), f, -1000, 1000);
        stackPane.getChildren().add(mainVBox);
        primaryStage.setScene(new Scene(stackPane));
        primaryStage.show();
    }
}
