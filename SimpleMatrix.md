# Introduction #

SimpleMatrix is an interface that provides an easy to use object oriented way of doing linear algebra.  It is a wrapper around the operation interface in EJML and was originally inspired by [Jama](http://math.nist.gov/javanumerics/jama/).  When using SimpleMatrix memory management is automatically handled and it allows commands to be chained together.  Switching between SimpleMatrix and [DenseMatrix64F](DenseMatrix64F.md) is easy, enabling the two programming paradigms to be mixed in the same code base.

The typical function in SimpleMatrix takes at least one SimpleMatrix as an input and returns a SimpleMatrix as the output.  None of the input matrices, including the 'this' matrix, are modified during function calls.  There is a slight performance hit when using SimpleMatrix and less control over memory management.  See [SpeedSimpleMatrix](SpeedSimpleMatrix.md) for a comparison of runtime performance of the two interfaces.

Below is a brief overview different SimpleMatrix concepts.

  * [Click to go to the main tutorial with more detailed examples](EjmlManual.md).
  * [Click for a list of Matlab/Octave to SimpleMatrix Commands](MatlabFunctions.md)

## Chaining Operations ##

When using SimpleMatrix operations can be chained together.  Chained operations are often easier to read and write.
```
    public SimpleMatrix process( SimpleMatrix A , SimpleMatrix B ) {
        return A.transpose().mult(B).scale(12).invert();
    }
```
is equivalent to the following Matlab code: `C = inv((A' * B)*12.0)`


## Working with DenseMatrix64F ##

To convert a [DenseMatrix64F](DenseMatrix64F.md) into a SimpleMatrix call the wrap() function.  Then to get access to the internal DenseMatrix64F inside of a SimpleMatrix call getMatrix().
```
    public DenseMatrix64F compute( DenseMatrix64F A , DenseMatrix64F B ) {
        SimpleMatrix A_ = SimpleMatrix.wrap(A);
        SimpleMatrix B_ = SimpleMatrix.wrap(B);

        return A_.mult(B_).getMatrix();
    }
```

A DenseMatrix64F can also be passed into the SimpleMatrix constructor, but this will copy the input matrix.  Unlike with when wrap is used, changed to the new SimpleMatrix will not modify the original DenseMatrix64F.

## Accessors ##
  * get( row , col )
  * set( row , col , value )
    * Returns or sets the value of an element at the specified row and column.
  * get( index )
  * set( index )
    * Returns or sets the value of an element at the specified index.  Useful for vectors and element-wise operations.
  * iterator( boolean rowMajor, int minRow, int minCol, int maxRow, int maxCol )
    * An iterator that iterates through the sub-matrix by row or by column.

## Submatrices ##

A submatrix is a matrix whose elements are a subset of another matrix.  Several different functions are provided for manipulating submatrices.

  * extractMatrix : Extracts a rectangular submatrix from the original matrix.
  * extractDiag : Creates a column vector containing just the diagonal elements of the matrix.
  * extractVector : Extracts either an entire row or column.
  * insertIntoThis : Inserts the passed in matrix into 'this' matrix.
  * combine : Creates a now matrix that is a combination of the two inputs.

## Decompositions ##

Simplified ways to use popular matrix decompositions is provided.  These decompositions provide fewer choices than the equivalent for DenseMatrix64F, but should meet most people needs.

  * svd : Computes the singular value decomposition of 'this' matrix
  * eig : Computes the eigen value decomposition of 'this' matrix

Direct access to other decompositions (e.g. QR and Cholesky) is not provided in SimpleMatrix because solve() and inv() is provided instead.  In more advanced applications use the operator interface instead to compute those decompositions.

## Solve and Invert ##

  * solve : Computes the solution to the set of linear equations
  * inv : Computes the inverse of a square matrix
  * pinv : Computes the pseudo-inverse for an arbitrary matrix

See [SolvingLinearSystems](SolvingLinearSystems.md) for more details on solving systems of equations.

## Other Functions ##

SimpleMatrix provides many other functions.  For a complete list see the JavaDoc for [SimpleBase](http://efficient-java-matrix-library.googlecode.com/svn/javadoc/org/ejml/simple/SimpleBase.html) and [SimpleMatrix](http://efficient-java-matrix-library.googlecode.com/svn/javadoc/org/ejml/simple/SimpleMatrix.html).  Note that SimpleMatrix extends SimpleBase.