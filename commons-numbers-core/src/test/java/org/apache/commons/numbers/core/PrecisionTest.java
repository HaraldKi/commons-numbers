/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.numbers.core;

import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link Precision} class.
 *
 */
public class PrecisionTest {
    @Test
    public void testEqualsWithRelativeTolerance() {
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(0d, 0d, 0d));
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(0d, 1 / Double.NEGATIVE_INFINITY, 0d));

        final double eps = 1e-14;
        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(1.987654687654968, 1.987654687654988, eps));
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(1.987654687654968, 1.987654687654987, eps));
        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(1.987654687654968, 1.987654687654948, eps));
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(1.987654687654968, 1.987654687654949, eps));

        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Precision.SAFE_MIN, 0.0, eps));

        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(1.0000000000001e-300, 1e-300, eps));
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(1.00000000000001e-300, 1e-300, eps));

        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Double.NEGATIVE_INFINITY, 1.23, eps));
        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Double.POSITIVE_INFINITY, 1.23, eps));

        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, eps));
        Assertions.assertTrue(Precision.equalsWithRelativeTolerance(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, eps));
        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, eps));

        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Double.NaN, 1.23, eps));
        Assertions.assertFalse(Precision.equalsWithRelativeTolerance(Double.NaN, Double.NaN, eps));
    }

    @Test
    public void testEqualsIncludingNaN() {
        double[] testArray = {
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
            1d,
            0d };
        for (int i = 0; i < testArray.length; i++) {
            for (int j = 0; j < testArray.length; j++) {
                if (i == j) {
                    Assertions.assertTrue(Precision.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assertions.assertTrue(Precision.equalsIncludingNaN(testArray[j], testArray[i]));
                } else {
                    Assertions.assertTrue(!Precision.equalsIncludingNaN(testArray[i], testArray[j]));
                    Assertions.assertTrue(!Precision.equalsIncludingNaN(testArray[j], testArray[i]));
                }
            }
        }
    }

    @Test
    public void testEqualsWithAllowedDelta() {
        Assertions.assertTrue(Precision.equals(153.0000, 153.0000, .0625));
        Assertions.assertTrue(Precision.equals(153.0000, 153.0625, .0625));
        Assertions.assertTrue(Precision.equals(152.9375, 153.0000, .0625));
        Assertions.assertFalse(Precision.equals(153.0000, 153.0625, .0624));
        Assertions.assertFalse(Precision.equals(152.9374, 153.0000, .0625));
        Assertions.assertFalse(Precision.equals(Double.NaN, Double.NaN, 1.0));
        Assertions.assertTrue(Precision.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assertions.assertTrue(Precision.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assertions.assertFalse(Precision.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
    }

    @Test
    public void testMath475() {
        final double a = 1.7976931348623182E16;
        final double b = Math.nextUp(a);

        double diff = Math.abs(a - b);
        // Because they are adjacent floating point numbers, "a" and "b" are
        // considered equal even though the allowed error is smaller than
        // their difference.
        Assertions.assertTrue(Precision.equals(a, b, 0.5 * diff));

        final double c = Math.nextUp(b);
        diff = Math.abs(a - c);
        // Because "a" and "c" are not adjacent, the tolerance is taken into
        // account for assessing equality.
        Assertions.assertTrue(Precision.equals(a, c, diff));
        Assertions.assertFalse(Precision.equals(a, c, (1 - 1e-16) * diff));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedDelta() {
        Assertions.assertTrue(Precision.equalsIncludingNaN(153.0000, 153.0000, .0625));
        Assertions.assertTrue(Precision.equalsIncludingNaN(153.0000, 153.0625, .0625));
        Assertions.assertTrue(Precision.equalsIncludingNaN(152.9375, 153.0000, .0625));
        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.NaN, Double.NaN, 1.0));
        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1.0));
        Assertions.assertFalse(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1.0));
        Assertions.assertFalse(Precision.equalsIncludingNaN(153.0000, 153.0625, .0624));
        Assertions.assertFalse(Precision.equalsIncludingNaN(152.9374, 153.0000, .0625));
    }

    // Tests for floating point equality
    @Test
    public void testFloatEqualsWithAllowedUlps() {
        Assertions.assertTrue(Precision.equals(0.0f, -0.0f), "+0.0f == -0.0f");
        Assertions.assertTrue(Precision.equals(0.0f, -0.0f, 1), "+0.0f == -0.0f (1 ulp)");
        float oneFloat = 1.0f;
        Assertions.assertTrue(Precision.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat))), "1.0f == 1.0f + 1 ulp");
        Assertions.assertTrue(Precision.equals(oneFloat, Float.intBitsToFloat(1 + Float.floatToIntBits(oneFloat)), 1), "1.0f == 1.0f + 1 ulp (1 ulp)");
        Assertions.assertFalse(Precision.equals(oneFloat, Float.intBitsToFloat(2 + Float.floatToIntBits(oneFloat)), 1), "1.0f != 1.0f + 2 ulp (1 ulp)");

        Assertions.assertTrue(Precision.equals(153.0f, 153.0f, 1));

        // These tests need adjusting for floating point precision
//        Assert.assertTrue(Precision.equals(153.0f, 153.00000000000003f, 1));
//        Assert.assertFalse(Precision.equals(153.0f, 153.00000000000006f, 1));
//        Assert.assertTrue(Precision.equals(153.0f, 152.99999999999997f, 1));
//        Assert.assertFalse(Precision.equals(153f, 152.99999999999994f, 1));
//
//        Assert.assertTrue(Precision.equals(-128.0f, -127.99999999999999f, 1));
//        Assert.assertFalse(Precision.equals(-128.0f, -127.99999999999997f, 1));
//        Assert.assertTrue(Precision.equals(-128.0f, -128.00000000000003f, 1));
//        Assert.assertFalse(Precision.equals(-128.0f, -128.00000000000006f, 1));

        Assertions.assertTrue(Precision.equals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equals(Double.MAX_VALUE, Float.POSITIVE_INFINITY, 1));

        Assertions.assertTrue(Precision.equals(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equals(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, 1));

        Assertions.assertFalse(Precision.equals(Float.NaN, Float.NaN, 1));
        Assertions.assertFalse(Precision.equals(Float.NaN, Float.NaN, 0));
        Assertions.assertFalse(Precision.equals(Float.NaN, 0, 0));
        Assertions.assertFalse(Precision.equals(Float.NaN, Float.POSITIVE_INFINITY, 0));
        Assertions.assertFalse(Precision.equals(Float.NaN, Float.NEGATIVE_INFINITY, 0));

        Assertions.assertFalse(Precision.equals(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsWithAllowedUlps() {
        Assertions.assertTrue(Precision.equals(0.0, -0.0, 1));

        Assertions.assertTrue(Precision.equals(1.0, 1 + Math.ulp(1d), 1));
        Assertions.assertFalse(Precision.equals(1.0, 1 + 2 * Math.ulp(1d), 1));

        final double nUp1 = Math.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = Math.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assertions.assertTrue(Precision.equals(1.0, nUp1, 1));
        Assertions.assertTrue(Precision.equals(nUp1, nnUp1, 1));
        Assertions.assertFalse(Precision.equals(1.0, nnUp1, 1));

        Assertions.assertTrue(Precision.equals(0.0, Math.ulp(0d), 1));
        Assertions.assertTrue(Precision.equals(0.0, -Math.ulp(0d), 1));

        Assertions.assertTrue(Precision.equals(153.0, 153.0, 1));

        Assertions.assertTrue(Precision.equals(153.0, 153.00000000000003, 1));
        Assertions.assertFalse(Precision.equals(153.0, 153.00000000000006, 1));
        Assertions.assertTrue(Precision.equals(153.0, 152.99999999999997, 1));
        Assertions.assertFalse(Precision.equals(153, 152.99999999999994, 1));

        Assertions.assertTrue(Precision.equals(-128.0, -127.99999999999999, 1));
        Assertions.assertFalse(Precision.equals(-128.0, -127.99999999999997, 1));
        Assertions.assertTrue(Precision.equals(-128.0, -128.00000000000003, 1));
        Assertions.assertFalse(Precision.equals(-128.0, -128.00000000000006, 1));

        Assertions.assertTrue(Precision.equals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equals(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assertions.assertTrue(Precision.equals(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equals(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assertions.assertFalse(Precision.equals(Double.NaN, Double.NaN, 1));
        Assertions.assertFalse(Precision.equals(Double.NaN, Double.NaN, 0));
        Assertions.assertFalse(Precision.equals(Double.NaN, 0, 0));
        Assertions.assertFalse(Precision.equals(Double.NaN, Double.POSITIVE_INFINITY, 0));
        Assertions.assertFalse(Precision.equals(Double.NaN, Double.NEGATIVE_INFINITY, 0));

        Assertions.assertFalse(Precision.equals(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testEqualsIncludingNaNWithAllowedUlps() {
        Assertions.assertTrue(Precision.equalsIncludingNaN(0.0, -0.0, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(1.0, 1 + Math.ulp(1d), 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(1.0, 1 + 2 * Math.ulp(1d), 1));

        final double nUp1 = Math.nextAfter(1d, Double.POSITIVE_INFINITY);
        final double nnUp1 = Math.nextAfter(nUp1, Double.POSITIVE_INFINITY);
        Assertions.assertTrue(Precision.equalsIncludingNaN(1.0, nUp1, 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(nUp1, nnUp1, 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(1.0, nnUp1, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(0.0, Math.ulp(0d), 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(0.0, -Math.ulp(0d), 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(153.0, 153.0, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(153.0, 153.00000000000003, 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(153.0, 153.00000000000006, 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(153.0, 152.99999999999997, 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(153, 152.99999999999994, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(-128.0, -127.99999999999999, 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(-128.0, -127.99999999999997, 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(-128.0, -128.00000000000003, 1));
        Assertions.assertFalse(Precision.equalsIncludingNaN(-128.0, -128.00000000000006, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
        Assertions.assertTrue(Precision.equalsIncludingNaN(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, 1));

        Assertions.assertTrue(Precision.equalsIncludingNaN(Double.NaN, Double.NaN, 1));

        Assertions.assertFalse(Precision.equalsIncludingNaN(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 100000));
    }

    @Test
    public void testCompareToEpsilon() {
        Assertions.assertEquals(0, Precision.compareTo(152.33, 152.32, .011));
        Assertions.assertTrue(Precision.compareTo(152.308, 152.32, .011) < 0);
        Assertions.assertTrue(Precision.compareTo(152.33, 152.318, .011) > 0);
        Assertions.assertEquals(0, Precision.compareTo(Double.MIN_VALUE, +0.0, Double.MIN_VALUE));
        Assertions.assertEquals(0, Precision.compareTo(Double.MIN_VALUE, -0.0, Double.MIN_VALUE));
    }

    @Test
    public void testCompareToMaxUlps() {
        double a     = 152.32;
        double delta = Math.ulp(a);
        for (int i = 0; i <= 10; ++i) {
            if (i <= 5) {
                Assertions.assertEquals( 0, Precision.compareTo(a, a + i * delta, 5));
                Assertions.assertEquals( 0, Precision.compareTo(a, a - i * delta, 5));
            } else {
                Assertions.assertEquals(-1, Precision.compareTo(a, a + i * delta, 5));
                Assertions.assertEquals(+1, Precision.compareTo(a, a - i * delta, 5));
            }
        }

        Assertions.assertEquals( 0, Precision.compareTo(-0.0, 0.0, 0));

        Assertions.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, -0.0, 0));
        Assertions.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, -0.0, 1));
        Assertions.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, +0.0, 0));
        Assertions.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, +0.0, 1));

        Assertions.assertEquals(+1, Precision.compareTo( Double.MIN_VALUE, -0.0, 0));
        Assertions.assertEquals( 0, Precision.compareTo( Double.MIN_VALUE, -0.0, 1));
        Assertions.assertEquals(+1, Precision.compareTo( Double.MIN_VALUE, +0.0, 0));
        Assertions.assertEquals( 0, Precision.compareTo( Double.MIN_VALUE, +0.0, 1));

        Assertions.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 0));
        Assertions.assertEquals(-1, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 1));
        Assertions.assertEquals( 0, Precision.compareTo(-Double.MIN_VALUE, Double.MIN_VALUE, 2));

        Assertions.assertEquals( 0, Precision.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 1));
        Assertions.assertEquals(-1, Precision.compareTo(Double.MAX_VALUE, Double.POSITIVE_INFINITY, 0));

        Assertions.assertEquals(+1, Precision.compareTo(Double.MAX_VALUE, Double.NaN, Integer.MAX_VALUE));
        Assertions.assertEquals(+1, Precision.compareTo(Double.NaN, Double.MAX_VALUE, Integer.MAX_VALUE));
    }

    @Test
    public void testRoundDouble() {
        double x = 1.234567890;
        Assertions.assertEquals(1.23, Precision.round(x, 2), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4), 0.0);

        // JIRA MATH-151
        Assertions.assertEquals(39.25, Precision.round(39.245, 2), 0.0);
        Assertions.assertEquals(39.24, Precision.round(39.245, 2, RoundingMode.DOWN), 0.0);
        double xx = 39.0;
        xx += 245d / 1000d;
        Assertions.assertEquals(39.25, Precision.round(xx, 2), 0.0);

        // BZ 35904
        Assertions.assertEquals(30.1d, Precision.round(30.095d, 2), 0.0d);
        Assertions.assertEquals(30.1d, Precision.round(30.095d, 1), 0.0d);
        Assertions.assertEquals(33.1d, Precision.round(33.095d, 1), 0.0d);
        Assertions.assertEquals(33.1d, Precision.round(33.095d, 2), 0.0d);
        Assertions.assertEquals(50.09d, Precision.round(50.085d, 2), 0.0d);
        Assertions.assertEquals(50.19d, Precision.round(50.185d, 2), 0.0d);
        Assertions.assertEquals(50.01d, Precision.round(50.005d, 2), 0.0d);
        Assertions.assertEquals(30.01d, Precision.round(30.005d, 2), 0.0d);
        Assertions.assertEquals(30.65d, Precision.round(30.645d, 2), 0.0d);

        Assertions.assertEquals(1.24, Precision.round(x, 2, RoundingMode.CEILING), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3, RoundingMode.CEILING), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4, RoundingMode.CEILING), 0.0);
        Assertions.assertEquals(-1.23, Precision.round(-x, 2, RoundingMode.CEILING), 0.0);
        Assertions.assertEquals(-1.234, Precision.round(-x, 3, RoundingMode.CEILING), 0.0);
        Assertions.assertEquals(-1.2345, Precision.round(-x, 4, RoundingMode.CEILING), 0.0);

        Assertions.assertEquals(1.23, Precision.round(x, 2, RoundingMode.DOWN), 0.0);
        Assertions.assertEquals(1.234, Precision.round(x, 3, RoundingMode.DOWN), 0.0);
        Assertions.assertEquals(1.2345, Precision.round(x, 4, RoundingMode.DOWN), 0.0);
        Assertions.assertEquals(-1.23, Precision.round(-x, 2, RoundingMode.DOWN), 0.0);
        Assertions.assertEquals(-1.234, Precision.round(-x, 3, RoundingMode.DOWN), 0.0);
        Assertions.assertEquals(-1.2345, Precision.round(-x, 4, RoundingMode.DOWN), 0.0);

        Assertions.assertEquals(1.23, Precision.round(x, 2, RoundingMode.FLOOR), 0.0);
        Assertions.assertEquals(1.234, Precision.round(x, 3, RoundingMode.FLOOR), 0.0);
        Assertions.assertEquals(1.2345, Precision.round(x, 4, RoundingMode.FLOOR), 0.0);
        Assertions.assertEquals(-1.24, Precision.round(-x, 2, RoundingMode.FLOOR), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-x, 3, RoundingMode.FLOOR), 0.0);
        Assertions.assertEquals(-1.2346, Precision.round(-x, 4, RoundingMode.FLOOR), 0.0);

        Assertions.assertEquals(1.23, Precision.round(x, 2, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(-1.23, Precision.round(-x, 2, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-x, 3, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(-1.2346, Precision.round(-x, 4, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(1.234, Precision.round(1.2345, 3, RoundingMode.HALF_DOWN), 0.0);
        Assertions.assertEquals(-1.234, Precision.round(-1.2345, 3, RoundingMode.HALF_DOWN), 0.0);

        Assertions.assertEquals(1.23, Precision.round(x, 2, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(-1.23, Precision.round(-x, 2, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-x, 3, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(-1.2346, Precision.round(-x, 4, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(1.234, Precision.round(1.2345, 3, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(-1.234, Precision.round(-1.2345, 3, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(1.236, Precision.round(1.2355, 3, RoundingMode.HALF_EVEN), 0.0);
        Assertions.assertEquals(-1.236, Precision.round(-1.2355, 3, RoundingMode.HALF_EVEN), 0.0);

        Assertions.assertEquals(1.23, Precision.round(x, 2, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(-1.23, Precision.round(-x, 2, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-x, 3, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(-1.2346, Precision.round(-x, 4, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(1.235, Precision.round(1.2345, 3, RoundingMode.HALF_UP), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-1.2345, 3, RoundingMode.HALF_UP), 0.0);

        Assertions.assertEquals(-1.23, Precision.round(-1.23, 2, RoundingMode.UNNECESSARY), 0.0);
        Assertions.assertEquals(1.23, Precision.round(1.23, 2, RoundingMode.UNNECESSARY), 0.0);

        try {
            Precision.round(1.234, 2, RoundingMode.UNNECESSARY);
            Assertions.fail();
        } catch (ArithmeticException ex) {
            // expected
        }

        Assertions.assertEquals(1.24, Precision.round(x, 2, RoundingMode.UP), 0.0);
        Assertions.assertEquals(1.235, Precision.round(x, 3, RoundingMode.UP), 0.0);
        Assertions.assertEquals(1.2346, Precision.round(x, 4, RoundingMode.UP), 0.0);
        Assertions.assertEquals(-1.24, Precision.round(-x, 2, RoundingMode.UP), 0.0);
        Assertions.assertEquals(-1.235, Precision.round(-x, 3, RoundingMode.UP), 0.0);
        Assertions.assertEquals(-1.2346, Precision.round(-x, 4, RoundingMode.UP), 0.0);

        // MATH-151
        Assertions.assertEquals(39.25, Precision.round(39.245, 2, RoundingMode.HALF_UP), 0.0);

        // special values
        TestUtils.assertEquals(Double.NaN, Precision.round(Double.NaN, 2), 0.0);
        Assertions.assertEquals(0.0, Precision.round(0.0, 2), 0.0);
        Assertions.assertEquals(Double.POSITIVE_INFINITY, Precision.round(Double.POSITIVE_INFINITY, 2), 0.0);
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, Precision.round(Double.NEGATIVE_INFINITY, 2), 0.0);
        // comparison of positive and negative zero is not possible -> always equal thus do string comparison
        Assertions.assertEquals("-0.0", Double.toString(Precision.round(-0.0, 0)));
        Assertions.assertEquals("-0.0", Double.toString(Precision.round(-1e-10, 0)));
    }


    @Test
    public void testIssue721() {
        Assertions.assertEquals(-53,   Math.getExponent(Precision.EPSILON));
        Assertions.assertEquals(-1022, Math.getExponent(Precision.SAFE_MIN));
    }


    @Test
    public void testRepresentableDelta() {
        int nonRepresentableCount = 0;
        final double x = 100;
        final int numTrials = 10000;
        for (int i = 0; i < numTrials; i++) {
            final double originalDelta = Math.random();
            final double delta = Precision.representableDelta(x, originalDelta);
            if (delta != originalDelta) {
                ++nonRepresentableCount;
            }
        }

        Assertions.assertTrue(nonRepresentableCount / (double) numTrials > 0.9);
    }

    @Test
    public void testMath843() {
        final double afterEpsilon = Math.nextAfter(Precision.EPSILON,
                                                   Double.POSITIVE_INFINITY);

        // a) 1 + EPSILON is equal to 1.
        Assertions.assertTrue(1 + Precision.EPSILON == 1);

        // b) 1 + "the number after EPSILON" is not equal to 1.
        Assertions.assertFalse(1 + afterEpsilon == 1);
    }

    @Test
    public void testMath1127() {
        Assertions.assertFalse(Precision.equals(2.0, -2.0, 1));
        Assertions.assertTrue(Precision.equals(0.0, -0.0, 0));
        Assertions.assertFalse(Precision.equals(2.0f, -2.0f, 1));
        Assertions.assertTrue(Precision.equals(0.0f, -0.0f, 0));
    }
}
