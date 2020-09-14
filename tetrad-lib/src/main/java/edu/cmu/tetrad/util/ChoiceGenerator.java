///////////////////////////////////////////////////////////////////////////////
// For information as to what this class does, see the Javadoc, below.       //
// Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006,       //
// 2007, 2008, 2009, 2010, 2014, 2015 by Peter Spirtes, Richard Scheines, Joseph   //
// Ramsey, and Clark Glymour.                                                //
//                                                                           //
// This program is free software; you can redistribute it and/or modify      //
// it under the terms of the GNU General Public License as published by      //
// the Free Software Foundation; either version 2 of the License, or         //
// (at your option) any later version.                                       //
//                                                                           //
// This program is distributed in the hope that it will be useful,           //
// but WITHOUT ANY WARRANTY; without even the implied warranty of            //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             //
// GNU General Public License for more details.                              //
//                                                                           //
// You should have received a copy of the GNU General Public License         //
// along with this program; if not, write to the Free Software               //
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA //
///////////////////////////////////////////////////////////////////////////////

package edu.cmu.tetrad.util;


import static edu.cmu.tetrad.util.ProbUtils.lngamma;
import static java.lang.Math.exp;
import static java.lang.Math.round;

/**
 * Generates (nonrecursively) all of the combinations of a choose b, where a, b
 * are nonnegative integers and a >= b.  The values of a and b are given in the
 * constructor, and the sequence of choices is obtained by repeatedly calling
 * the next() method.  When the sequence is finished, null is returned.</p> </p>
 * <p>A valid combination for the sequence of combinations for a choose b
 * generated by this class is an array x[] of b integers i, 0 <= i < a, such
 * that x[j] < x[j + 1] for each j from 0 to b - 1.
 * <p>
 * To see what this class does, try calling ChoiceGenerator.testPrint(5, 3), for
 * instance.
 *
 * @author Joseph Ramsey
 */
@SuppressWarnings({"WeakerAccess"})
public final class ChoiceGenerator {

    /**
     * The number of objects being selected from.
     */
    private int a;

    /**
     * The number of objects in the desired selection.
     */
    private int b;

    /**
     * The difference between a and b (should be nonnegative).
     */
    private int diff;

    /**
     * The internally stored choice.
     */
    private int[] choiceLocal;

    /**
     * The choice that is returned. Used, since the returned array can be
     * modified by the user.
     */
    private int[] choiceReturned;

    /**
     * Indicates whether the next() method has been called since the last
     * initialization.
     */
    private boolean begun;

    /**
     * Constructs a new choice generator for a choose b. Once this
     * initialization has been performed, successive calls to next() will
     * produce the series of combinations.  To begin a new series at any time,
     * call this init method again with new values for a and b.
     *
     * @param a the number of objects being selected from.
     * @param b the number of objects in the desired selection.
     */
    public ChoiceGenerator(int a, int b) {
        if ((a < 0) || (b < 0) || (a < b)) {
            throw new IllegalArgumentException(
                    "For 'a choose b', a and b must be " +
                            "nonnegative with a >= b: " + "a = " + a +
                            ", b = " + b);
        }

        this.a = a;
        this.b = b;
        choiceLocal = new int[b];
        choiceReturned = new int[b];
        diff = a - b;

        // Initialize the choice array with successive integers [0 1 2 ...].
        // Set the value at the last index one less than it would be in such
        // a series, ([0 1 2 ... b - 2]) so that on the first call to next()
        // the first combination ([0 1 2 ... b - 1]) is returned correctly.
        for (int i = 0; i < b - 1; i++) {
            choiceLocal[i] = i;
        }

        if (b > 0) {
            choiceLocal[b - 1] = b - 2;
        }

        begun = false;
    }

    /**
     * @return the next combination in the series, or null if the series is
     * finished.
     */
    public synchronized int[] next() {
        int i = getB();

        // Scan from the right for the first index whose value is less than
        // its expected maximum (i + diff) and perform the fill() operation
        // at that index.
        while (--i > -1) {
            if (this.choiceLocal[i] < i + this.diff) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                fill(i);
                begun = true;
                System.arraycopy(choiceLocal, 0, choiceReturned, 0, b);
                return choiceReturned;
            }
        }

        if (this.begun) {
            return null;
        } else {
            begun = true;
            System.arraycopy(choiceLocal, 0, choiceReturned, 0, b);
            return choiceReturned;
        }
    }

    /**
     * This static method will print the series of combinations for a choose b
     * to System.out.
     *
     * @param a the number of objects being selected from.
     * @param b the number of objects in the desired selection.
     */
    @SuppressWarnings({"SameParameterValue"})
    public static void testPrint(int a, int b) {
        ChoiceGenerator cg = new ChoiceGenerator(a, b);
        int[] choice;

        System.out.println();
        System.out.println(
                "Printing combinations for " + a + " choose " + b + ":");
        System.out.println();

        while ((choice = cg.next()) != null) {
            if (choice.length == 0) {
                System.out.println("zero-length array");
            } else {
                for (int aChoice : choice) {
                    System.out.print(aChoice + "\t");
                }

                System.out.println();
            }
        }

        System.out.println();
    }

    /**
     * @return Ibid.
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public int getA() {
        return this.a;
    }

    /**
     * @return Ibid.
     */
    public int getB() {
        return this.b;
    }

    /**
     * Fills the 'choice' array, from index 'index' to the end of the array,
     * with successive integers starting with choice[index] + 1.
     *
     * @param index the index to begin this incrementing operation.
     */
    private void fill(int index) {
        this.choiceLocal[index]++;

        for (int i = index + 1; i < getB(); i++) {
            this.choiceLocal[i] = this.choiceLocal[i - 1] + 1;
        }
    }

    public static int getNumCombinations(int a, int b) {
        return (int) round(exp(lngamma(a + 1) - lngamma(b + 1) - lngamma((a - b) + 1)));
    }
}





