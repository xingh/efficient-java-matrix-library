package org.ejml.alg.block.decomposition.qr;

import org.ejml.alg.block.BlockMatrixOps;
import org.ejml.alg.dense.decomposition.qr.QRDecompositionHouseholderTran;
import org.ejml.alg.generic.GenericMatrixOps;
import org.ejml.data.BlockMatrix64F;
import org.ejml.data.D1Submatrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.SimpleMatrix;
import org.ejml.ops.CommonOps;
import org.ejml.ops.RandomMatrices;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Peter Abeles
 */
public class TestBlockHouseHolder {

    Random rand = new Random(234);

    // the block length
    int r = 3;

    SimpleMatrix A, Y,V,W;

    @Test
    public void decomposeQR_block_col() {
        DenseMatrix64F A = RandomMatrices.createRandom(r*2+r-1,r,-1,1,rand);
        BlockMatrix64F Ab = BlockMatrixOps.convert(A,r);

        QRDecompositionHouseholderTran algTest = new QRDecompositionHouseholderTran();
        assertTrue(algTest.decompose(A));

        double gammas[] = new double[A.numCols];
        BlockHouseHolder.decomposeQR_block_col(r,new D1Submatrix64F(Ab),gammas);

        DenseMatrix64F expected = CommonOps.transpose(algTest.getQR(),null);

        assertTrue(GenericMatrixOps.isEquivalent(expected,Ab,1e-8));
    }

    @Test
    public void applyHouseholderCol() {
        double gamma = 2.5;
        A = SimpleMatrix.random(r*2+r-1,r,-1,1,rand);

        SimpleMatrix U = A.extractMatrix(0,A.numRows(),1,2);
        U.set(0,0,0);
        U.set(1,0,1);

        SimpleMatrix V = A.extractMatrix(0,A.numRows(),2,3);
        SimpleMatrix expected = V.minus(U.mult(U.transpose().mult(V)).scale(gamma));

        BlockMatrix64F Ab = BlockMatrixOps.convert(A.getMatrix(),r);

        BlockHouseHolder.applyHouseholderCol(r,new D1Submatrix64F(Ab),1,gamma);


        for( int i = 1; i < expected.numRows(); i++ ) {
            assertEquals(expected.get(i,0),Ab.get(i,2),1e-8);
        }
    }

    @Test
    public void divideElements() {

        double div = 1.5;
        int col = 1;
        BlockMatrix64F A = BlockMatrixOps.createRandom(r*2+r-1,r,-1,1,rand,r);
        BlockMatrix64F A_orig = A.copy();

        BlockHouseHolder.divideElements(r,new D1Submatrix64F(A),col,div);

        for( int i = col+1; i < A.numRows; i++ ) {
            assertEquals(A_orig.get(i,col)/div , A.get(i,col),1e-8);
        }

    }

    @Test
    public void computeTauAndDivide() {

        double max = 1.5;
        int col = 1;
        BlockMatrix64F A = BlockMatrixOps.createRandom(r*2+r-1,r,-1,1,rand,r);
        BlockMatrix64F A_orig = A.copy();

        // manual alg
        double expected = 0;
        for( int i = col; i < A.numRows; i++ ) {
            double val = A.get(i,col)/max;
            expected += val*val;
        }
        expected = Math.sqrt(expected);

        double found = BlockHouseHolder.computeTauAndDivide(r,new D1Submatrix64F(A),col,max);

        assertEquals(expected,found,1e-8);

        for( int i = col; i < A.numRows; i++ ) {
            assertEquals(A_orig.get(i,col)/max , A.get(i,col),1e-8);
        }

    }

    @Test
    public void findMaxCol() {
        BlockMatrix64F A = BlockMatrixOps.createRandom(r*2+r-1,r,-1,1,rand,r);

        // make sure it ignores the first element
        A.set(0,1,100000);
        A.set(5,1,-2346);

        double max = BlockHouseHolder.findMaxCol(r,new D1Submatrix64F(A),1);

        assertEquals(2346,max,1e-8);
    }

    @Test
    public void computeW_Column() {
        double betas[] = new double[]{1.2,2,3};

        A = SimpleMatrix.random(r*2+r-1,r,-1,1,rand);

        // Compute W directly using SimpleMatrix
        SimpleMatrix V = A.extractMatrix(0,A.numRows(),0,1);
        V.set(0,0,1);
        SimpleMatrix Y = V;
        SimpleMatrix W = V.scale(-betas[0]);

        for( int i = 1; i < A.numCols(); i++ ) {
            V = A.extractMatrix(0,A.numRows(),i,i+1);

            for( int j = 0; j < i; j++ )
                V.set(j,0,0);
            V.set(i,0,1);

            SimpleMatrix z = V.plus(W.mult(Y.transpose().mult(V))).scale(-betas[i]);
            W = W.combine(0,i,z);
            Y = Y.combine(0,i,V);
        }

        // now compute it using the block matrix stuff
        double temp[] = new double[ r ];

        BlockMatrix64F Ab = BlockMatrixOps.convert(A.getMatrix(),r);
        BlockMatrix64F Wb = new BlockMatrix64F(Ab.numRows,Ab.numCols,r);

        D1Submatrix64F Ab_sub = new D1Submatrix64F(Ab);
        D1Submatrix64F Wb_sub = new D1Submatrix64F(Wb);

        BlockHouseHolder.computeW_Column(r,Ab_sub,Wb_sub,temp,betas,0);

        // see if the result is the same
        assertTrue(GenericMatrixOps.isEquivalent(Wb,W.getMatrix(),1e-8));
    }

    @Test
    public void initializeW() {
        initMatrices(r-1);

        double beta = 1.5;

        BlockMatrix64F Wb = BlockMatrixOps.convert(W.getMatrix(),r);
        BlockMatrix64F Ab = BlockMatrixOps.convert(A.getMatrix(),r);

        D1Submatrix64F Wb_sub = new D1Submatrix64F(Wb,0, W.numRows(), 0, r);
        D1Submatrix64F Yb_sub = new D1Submatrix64F(Ab,0, A.numRows(), 0, r);

        BlockHouseHolder.initializeW(r,Wb_sub,Yb_sub,r,beta);

        assertEquals(-beta,Wb.get(0,0),1e-8);

        for( int i = 1; i < Wb.numRows; i++ ) {
            assertEquals(-beta*Ab.get(i,0),Wb.get(i,0),1e-8);
        }
    }

    @Test
    public void computeZ() {
        int M = r-1;
        initMatrices(M);

        double beta = 2.5;

        BlockMatrix64F Ab = BlockMatrixOps.convert(A.getMatrix());
        BlockMatrix64F Aw = BlockMatrixOps.convert(W.getMatrix());

        // need to extract only the elements in W that are currently being used when
        // computing the expected Z
        W = W.extractMatrix(0,W.numRows(),0,M);
        SimpleMatrix T = SimpleMatrix.random(M,1,-1,1,rand);

        // -beta * (V + W*T)
        SimpleMatrix expected = V.plus(W.mult(T)).scale(-beta);

        BlockHouseHolder.computeZ(r,new D1Submatrix64F(Ab,0, A.numRows(), 0, r),new D1Submatrix64F(Aw,0, A.numRows(), 0, r),
                M,T.getMatrix().data,beta);

        for( int i = 0; i < A.numRows(); i++ ) {
            assertEquals(expected.get(i),Aw.get(i,M),1e-8);
        }
    }

    @Test
    public void computeY_t_V() {
        int M = r-2;
        initMatrices(M);

        // Y'*V
        SimpleMatrix expected = Y.transpose().mult(V);

        BlockMatrix64F Ab = BlockMatrixOps.convert(A.getMatrix());
        double found[] = new double[ M ];

        BlockHouseHolder.computeY_t_V(r,new D1Submatrix64F(Ab,0, A.numRows(), 0, r),M,found);

        for( int i = 0; i < M; i++ ) {
            assertEquals(expected.get(i),found[i],1e-8);
        }
    }

    private void initMatrices( int M ) {
        A = SimpleMatrix.random(r*2+r-1,r,-1,1,rand);

        // create matrices that are used to test
        Y = A.extractMatrix(0,A.numRows(),0,M);
        V = A.extractMatrix(0,A.numRows(),M,M+1);

        // add in zeros and ones
        setZerosY();
        for( int i = 0; i < M; i++ ) {
            V.set(i,0);
        }
        V.set(M,1);

        W = SimpleMatrix.random(r*2+r-1,r,-1,1,rand);
    }

    private void setZerosY() {
        for( int j = 0; j < Y.numCols(); j++ ) {
            for( int i = 0; i < j; i++ ) {
                Y.set(i,j,0);
            }
            Y.set(j,j,1);
        }
    }
}