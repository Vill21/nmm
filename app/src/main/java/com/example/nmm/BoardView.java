package com.example.nmm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BoardView extends View {

    final float SPACE;
    final float RADIUS;
    final int POINTS_IN_ROW = 3;
    final float FIGURE_RADIUS;
    final private int BOARD_CIRCLES_CAPACITY = 24;
    final private int BOARD_LINES_CAPACITY = 32;

    ArrayList<PointF> board;
    ArrayList<ArrayList<Integer>> connections;
    RectF mill;
    ArrayList<PointF> player1;
    ArrayList<PointF> player2;
    PointF chosenPoint;

    Paint paintLines;
    Paint paintPlaces;
    Paint playerPaint1;
    Paint playerPaint2;
    Paint chosenPointPaint;
    Paint millPaint;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics display = getResources().getDisplayMetrics();
        SPACE = (float) display.widthPixels / 7.2F;
        RADIUS = SPACE / 10F;
        FIGURE_RADIUS = 3 * RADIUS;
        makeBoard();
        makeConnections();

        player1 = new ArrayList<PointF>();
        player2 = new ArrayList<PointF>();
        chosenPoint = new PointF();
        mill = new RectF();

        paintLines = new Paint();
        paintLines.setStyle(Paint.Style.FILL_AND_STROKE);
        paintLines.setStrokeWidth(5F);
        paintLines.setColor(Color.BLACK);
        paintPlaces = new Paint();
        paintPlaces.setStyle(Paint.Style.FILL_AND_STROKE);
        paintPlaces.setStrokeWidth(5F);
        paintPlaces.setColor(Color.LTGRAY);
        playerPaint1 = new Paint();
        playerPaint1.setColor(Color.RED);
        playerPaint1.setStyle(Paint.Style.FILL);
        playerPaint2 = new Paint();
        playerPaint2.setColor(Color.BLUE);
        playerPaint2.setStyle(Paint.Style.FILL);
        chosenPointPaint = new Paint();
        chosenPointPaint.setColor(Color.GREEN);
        chosenPointPaint.setStyle(Paint.Style.STROKE);
        chosenPointPaint.setAntiAlias(true);
        chosenPointPaint.setStrokeWidth(10F);
        millPaint = new Paint();
        millPaint.setColor(Color.rgb(255,215,0));
        millPaint.setStyle(Paint.Style.STROKE);
        millPaint.setAntiAlias(true);
        millPaint.setStrokeWidth(10F);
    }

    private void makeConnections() {
        final int CONNECTIONS = 16;
        connections = new ArrayList<ArrayList<Integer>>(CONNECTIONS);

        for (int i = 0; i < 8; ++i) {
            connections.add(new ArrayList<Integer>());
            for (int j = 0; j < 3; ++j) {
                connections.get(i).add(3 * i + j);
            }
        }

        for (int i = 0; i < 8; ++i) {
            connections.add(new ArrayList<Integer>());
        }
        for (int i = 0; i < 3; ++i) {
            connections.get(i + 8).add(3 * i);
            connections.get(i + 8).add(9 + i);
            connections.get(i + 8).add(21 - 3 * i);
        }
        for (int i = 0; i < 3; ++i) {
            connections.get(11).add(1 + 3 * i);
            connections.get(12).add(16 + 3 * i);
        }
        for (int i = 0; i < 3; ++i) {
            connections.get(i + 13).add(2 + 3 * i);
            connections.get(i + 13).add(14 - i);
            connections.get(i + 13).add(23 - 3 * i);
        }
    }

    private void makeBoard() {

        float x = 3 * RADIUS;
        float y = 3 * RADIUS;

        board = new ArrayList<PointF>(BOARD_CIRCLES_CAPACITY);

        // Upper part of board
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                board.add(new PointF(x + SPACE * i + (3 - i) * SPACE * j, y + SPACE * i));
            }
        }

        // Left central line of board
        for (int i = 0; i < 3; ++i) {
            board.add(new PointF(x + SPACE * i, y + 3 * SPACE));
        }

        // Right central line of board
        for (int i = 0; i < 3; ++i) {
            board.add(new PointF(x + 4 * SPACE + SPACE * i, y + 3 * SPACE));
        }

        // Lower part of board
        for (int i = 2; i >= 0; --i) {
            for (int j = 0; j < 3; ++j) {
                board.add(new PointF(x + SPACE * i + (3 - i) * SPACE * j, y + SPACE * (6 - i)));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < 23; ++i) {
            if ((i + 1) % 3 == 0) continue;

            float xl = board.get(i).x;
            float xr = board.get(i + 1).x;
            float yl = board.get(i).y;
            float yr = board.get(i + 1).y;

            canvas.drawLine(xl, yl, xr, yr, paintLines);
        }
        for (int i = 0; i < 3; ++i) {
            float xTop = board.get(3 * i).x;
            float yTop = board.get(3 * i).y;
            float xBottom = board.get(i + 9).x;
            float yBottom = board.get(i + 9).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);

            xTop = xBottom;
            yTop = yBottom;
            xBottom = board.get(21 - 3 * i).x;
            yBottom = board.get(21 - 3 * i).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);

            xTop = board.get(3 * i + 2).x;
            yTop = board.get(3 * i + 2).y;
            xBottom = board.get(14 - i).x;
            yBottom = board.get(14 - i).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);

            xTop = xBottom;
            yTop = yBottom;
            xBottom = board.get(23 - 3 * i).x;
            yBottom = board.get(23 - 3 * i).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);
        }
        for (int i = 0; i < 2; ++i) {
            float xTop = board.get(1 + 3 * i).x;
            float yTop = board.get(1 + 3 * i).y;
            float xBottom = board.get(4 + 3 * i).x;
            float yBottom = board.get(4 + 3 * i).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);

            xTop = board.get(16 + 3 * i).x;
            yTop = board.get(16 + 3 * i).y;
            xBottom = board.get(19 + 3 * i).x;
            yBottom = board.get(19 + 3 * i).y;

            canvas.drawLine(xTop, yTop, xBottom, yBottom, paintLines);
        }

        for (int i = 0; i < BOARD_CIRCLES_CAPACITY; ++i) {
            float x = board.get(i).x;
            float y = board.get(i).y;
            canvas.drawCircle(x, y, RADIUS, paintPlaces);
        }

        for (int i = 0; i < player1.size(); ++i) {
            canvas.drawCircle(player1.get(i).x, player1.get(i).y, FIGURE_RADIUS, playerPaint1);
        }
        for (int i = 0; i < player2.size(); ++i) {
            canvas.drawCircle(player2.get(i).x, player2.get(i).y, FIGURE_RADIUS, playerPaint2);
        }
        if (!mill.isEmpty()) {
            canvas.drawRect(mill, millPaint);
        }

        if (!chosenPoint.equals(0, 0)) {
            canvas.drawCircle(chosenPoint.x, chosenPoint.y, FIGURE_RADIUS, chosenPointPaint);
        }
    }

    static PointF closestPoint(PointF anchor, ArrayList<PointF> points) {
        PointF closest = new PointF(points.get(0).x, points.get(0).y);
        float minDist = Calculator.euclideanDistance(anchor, closest);

        for (int i = 1; i < points.size(); ++i) {
            float dist = Calculator.euclideanDistance(anchor, points.get(i));
            if (dist < minDist) {
                minDist = dist;
                closest.set(points.get(i));
            }
        }

        return closest;
    }

    private int calculateSide() {
        return (int)(6 * SPACE + 2 * FIGURE_RADIUS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = calculateSide() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = calculateSide() + getPaddingTop() + getPaddingBottom();

        int resolvedWidth = resolveSize(desiredWidth, widthMeasureSpec);
        int resolvedHeight = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }
}
