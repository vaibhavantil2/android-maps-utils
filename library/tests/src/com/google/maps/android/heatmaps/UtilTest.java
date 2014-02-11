package com.google.maps.android.heatmaps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Bounds;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Tests for heatmap utility functions
 */
public class UtilTest extends TestCase {

    public void testGenerateKernel() {
        double[] testKernel = HeatmapTileProvider.generateKernel(5, 1.5);
        double[] expectedKernel = {0.0038659201394728076, 0.028565500784550377, 0.1353352832366127,
                0.41111229050718745, 0.8007374029168081, 1.0, 0.8007374029168081,
                0.41111229050718745, 0.1353352832366127, 0.028565500784550377,
                0.0038659201394728076};

        assertTrue(Arrays.equals(testKernel, expectedKernel));
    }

    public void testConvolveCorners() {
        /*
         1 0 0 0 1
         0 0 0 0 0
         0 0 0 0 0
         0 0 0 0 0
         1 0 0 0 1
         */
        double[][] grid = new double[5][5];
        grid[0][0] = 1;
        grid[4][4] = 1;
        grid[4][0] = 1;
        grid[0][4] = 1;
        double[] testKernel = {0.5, 1, 0.5};
        double[][] convolved = HeatmapTileProvider.convolve(grid, testKernel);
        double[][] expected = {{0.25, 0, 0.25}, {0, 0, 0}, {0.25, 0, 0.25}};
        assertTrue(Arrays.deepEquals(convolved, expected));
    }

    public void testConvolveEdges() {
        /*
         0 0 1 0 0
         0 0 0 0 0
         1 0 0 0 1
         0 0 0 0 0
         0 0 1 0 0
         */
        double[][] grid = new double[5][5];
        grid[0][2] = 1;
        grid[2][0] = 1;
        grid[2][4] = 1;
        grid[4][2] = 1;
        double[] testKernel = {0.5, 1, 0.5};
        double[][] convolved = HeatmapTileProvider.convolve(grid, testKernel);
        double[][] expected = {{0.5, 0.5, 0.5}, {0.5, 0, 0.5}, {0.5, 0.5, 0.5}};
        assertTrue(Arrays.deepEquals(convolved, expected));
    }

    public void testConvolveCentre() {
        /*
         0 0 0 0 0
         0 0 1 0 0
         0 1 2 1 0
         0 0 1 0 0
         0 0 0 0 0
         */
        double[][] grid = new double[5][5];
        grid[2][2] = 2;
        grid[2][1] = 1;
        grid[1][2] = 1;
        grid[2][3] = 1;
        grid[3][2] = 1;
        double[] testKernel = {0.5, 1, 0.5};
        double[][] convolved = HeatmapTileProvider.convolve(grid, testKernel);
        double[][] expected = {{1.5, 2.5, 1.5}, {2.5, 4.0, 2.5}, {1.5, 2.5, 1.5}};
        assertTrue(Arrays.deepEquals(convolved, expected));
    }

    public void testGetBounds() {

        /*
        y
        ^
        |  3
        |     1
        |        2
        ------------> x
         */

        ArrayList<WeightedLatLng> data = new ArrayList<WeightedLatLng>();
        WeightedLatLng first = new WeightedLatLng(new LatLng(10, 20));
        data.add(first);
        double x1 = first.getPoint().x;
        double y1 = first.getPoint().y;

        Bounds bounds = HeatmapTileProvider.getBounds(data);
        Bounds expected = new Bounds(x1, x1 + HeatmapTileProvider.SIGMA, y1,
                y1 + HeatmapTileProvider.SIGMA);

        assertTrue(bounds.contains(expected) && expected.contains(bounds));

        WeightedLatLng second = new WeightedLatLng(new LatLng(20, 30));
        data.add(second);
        double x2 = second.getPoint().x;
        double y2 = second.getPoint().y;

        bounds = HeatmapTileProvider.getBounds(data);
        expected = new Bounds(x1, x2 + HeatmapTileProvider.SIGMA, y2,
                y1 + HeatmapTileProvider.SIGMA);

        assertTrue(bounds.contains(expected) && expected.contains(bounds));

        WeightedLatLng third = new WeightedLatLng(new LatLng(5, 10));
        data.add(third);
        double x3 = third.getPoint().x;
        double y3 = third.getPoint().y;

        bounds = HeatmapTileProvider.getBounds(data);
        expected = new Bounds(x3, x2 + HeatmapTileProvider.SIGMA, y2,
                y3 + HeatmapTileProvider.SIGMA);
        assertTrue(bounds.contains(expected) && expected.contains(bounds));
    }
}