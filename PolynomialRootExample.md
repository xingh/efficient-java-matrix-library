# Introduction #

Eigenvalue decomposition can be used to find the roots in a polynomial by constructing the so called [companion matrix](http://en.wikipedia.org/wiki/Companion_matrix).  While faster techniques do exist for root finding, this is one of the most stable and probably the easiest to implement.

Because the companion matrix is not symmetric a generalized eigenvalue [decomposition](MatrixDecomposition.md) is needed.  The roots of the polynomial may also be [complex](http://en.wikipedia.org/wiki/Complex_number).  Complex eigenvalues is the only instance in which EJML supports complex arithmetic.  Depending on the application one might need to check to see if the eigenvalues are real or complex.


# Example Code #

```
public class PolynomialRootFinder {

    /**
     * <p>
     * Given a set of polynomial coefficients, compute the roots of the polynomial.  Depending on
     * the polynomial being considered the roots may contain complex number.  When complex numbers are
     * present they will come in pairs of complex conjugates.
     * </p>
     *
     * <p>
     * Coefficients are ordered from least to most significant, e.g: y = c[0] + x*c[1] + x*x*c[2].
     * </p>
     *
     * @param coefficients Coefficients of the polynomial.
     * @return The roots of the polynomial
     */
    public static Complex64F[] findRoots(double... coefficients) {
        int N = coefficients.length-1;

        // Construct the companion matrix
        DenseMatrix64F c = new DenseMatrix64F(N,N);

        double a = coefficients[N];
        for( int i = 0; i < N; i++ ) {
            c.set(i,N-1,-coefficients[i]/a);
        }
        for( int i = 1; i < N; i++ ) {
            c.set(i,i-1,1);
        }

        // use generalized eigenvalue decomposition to find the roots
        EigenDecomposition<DenseMatrix64F> evd =  DecompositionFactory.eig(N,false);

        evd.decompose(c);

        Complex64F[] roots = new Complex64F[N];

        for( int i = 0; i < N; i++ ) {
            roots[i] = evd.getEigenvalue(i);
        }

        return roots;
    }
}
```