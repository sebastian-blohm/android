package hm.blo.paperlog.ui.main;

import junit.framework.TestCase;

import org.junit.Assert;

public class WayPointFragmentTest extends TestCase {

    public void testDistanceInNauticalMiles() {
        // 1 degred lat is 60 nm
        double expected = 60;
        double result = WayPointFragment.DistanceInNauticalMiles(0.0, 0.0, 1.0, 0.0);
        Assert.assertEquals(expected, result, 0.15);
        result = WayPointFragment.DistanceInNauticalMiles(45.0, 50.0, 46.0, 50.0);
        Assert.assertEquals(expected, result, 0.15);
        result = WayPointFragment.DistanceInNauticalMiles(20.0, -10.0, 21.0, -10.0);
        Assert.assertEquals(expected, result, 0.15);

        // https://www.distance.to/Seattle/London
        expected = 4157.813175;
        result = WayPointFragment.DistanceInNauticalMiles(47.606209,-122.332069, 51.500153,-0.126236);
        Assert.assertEquals(expected, result, 10.0);
    }

    public void testBearingDegrees() {
        double expected = 0;
        double result = WayPointFragment.BearingDegrees(0.0, 0.0, 1.0, 0.0);
        Assert.assertEquals(expected, result, 0.0015);

        expected = 90;
        result = WayPointFragment.BearingDegrees(0.0, 0.0, 0.0, 1.0);
        Assert.assertEquals(expected, result, 0.0015);

        expected = 270;
        result = WayPointFragment.BearingDegrees(0.0, 0.0, 0.0, -1.0);
        Assert.assertEquals(expected, result, 0.0015);

        // https://www.distance.to/Cambridge,Cambridgeshire,England,GBR/London
        expected = 192.1;
        result = WayPointFragment.BearingDegrees(52.200000,0.116670, 51.500153,-0.126236);
        Assert.assertEquals(expected, result, 0.1);
    }
}