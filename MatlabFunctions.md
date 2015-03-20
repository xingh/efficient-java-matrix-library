Many functions in Matlab have equivalent or similar functions in EJML.  To help port Matlab code into EJML two list are provided for SimpleMatrix and the procedural API.  If a function is not provided by SimpleMatrix it is probably provided by the more advanced procedural API.

Looking for a Matlab interface to use in Java?  Check out the new EJML module Equations.

**[Equations Tutorial](Equation.md)**

# Simple Matrix #

A subset of EJML's functionality is provided in SimpleMatrix.  If SimpleMatrix does not provide the functionality you desire then look at the list of procedural functions below.

| **Matlab** | **SimpleMatrix** |
|:-----------|:-----------------|
| eye(3) | SimpleMatrix.identity(3) |
| diag(`[1 2 3]`) | SimpleMatrix.diag(1,2,3) |
| C(1,2) = 5 | A.set(0,1,5) |
| C(:) = A | C.set(A) |
| C(:) = 5 | C.set(5) |
| C(2,:) = [1,2,3] | C.setRow(1,0,1,2,3) |
| C(:,2) = [1,2,3] | C.setColumn(1,0,1,2,3) |
| C = A(2:4,3:8) | C = A.extractMatrix(1,4,2,8) |
| A(:,2:end) = B | A.insertIntoThis(0,1,B); |
| C = diag(A) | C = A.extractDiag() |
| C = [A,B] | C = A.combine(0,A.numCols(),B) |
| C = A' | C = A.transpose() |
| C = -A | C = A.negative() |
| C = A`*`B | C = A.mult(B) |
| C = A + B | C = A.plus(B) |
| C = A - B | C = A.minus(B) |
| C = 2`*`A | C = A.scale(2) |
| C = A / 2 | C = A.divide(2) |
| C = inv(A) | C = A.invert() |
| C = pinv(A) | C = A.pinv()|
| C = A \ B | C = A.solve(B) |
| C = trace(A) | C = A.trace() |
| det(A) | A.det() |
| C=kron(A,B) | C=A.kron(B) |
| norm(A,"fro") | A.normf() |
| max(abs(A(:))) | A.elementMaxAbs() |
| sum(A(:)) | A.elementSum() |
| rank(A) | A.svd(true).rank() |
| [U,S,V] = svd(A) | A.svd(false) |
| [U,S,V] = svd(A,0) | A.svd(true) |
| [V,L] = eig(A) | A.eig() |

# Procedural Interface #

Functions and classes in the procedural interface takes in DenseMatrix64F as input.  Since SimpleMatrix is a wrapper around DenseMatrix64F its internal matrix can be extracted and passed into any of these functions.

| **Matlab** | **EJML** |
|:-----------|:---------|
| eye(3) | CommonOps.identity(3) |
| C(1,2) = 5 | A.set(0,1,5) |
| C(:) = A | C.setTo(A) |
| C(2,:) = [1,2,3] | CommonOps.insert(new DenseMatrix64F(1,3,true,1,2,3),C,1,0)  |
| C(:,2) = [1,2,3] | CommonOps.insert(new DenseMatrix64F(3,1,true,1,2,3),C,0,1) |
| C = A(2:4,3:8) | CommonOps.extract(A,1,4,2,8) |
| diag(`[1 2 3]`) | CommonOps.diag(1,2,3) |
| C = A' | CommonOps.transpose(A,C) |
| A = A' | CommonOps.transpose(A) |
| A = -A | CommonOps.changeSign(A) |
| C = A `*` B | CommonOps.mult(A,B,C) |
| C = A .`*` B | CommonOps.elementMult(A,B,C) |
| A = A .`*` B | CommonOps.elementMult(A,B) |
| C = A ./ B | CommonOps.elementDiv(A,B,C) |
| A = A ./ B | CommonOps.elementDiv(A,B) |
| C = A + B | CommonOps.add(A,B,C) |
| C = A - B | CommonOps.sub(A,B,C) |
| C = 2 `*` A | CommonOps.scale(2,A,C) |
| A = 2 `*` A | CommonOps.scale(2,A) |
| C = A / 2 | CommonOps.divide(2,A,C) |
| A = A / 2 | CommonOps.divide(2,A) |
| C = inv(A) | CommonOps.invert(A,C) |
| A = inv(A) | CommonOps.invert(A) |
| C = pinv(A) | CommonOps.pinv(A) |
| C = trace(A) | C = CommonOps.trace(A) |
| C = det(A) | C = CommonOps.det(A) |
| C=kron(A,B) | CommonOps.kron(A,B,C) |
| B=rref(A) | B = CommonOps.rref(A,-1,null) |
| norm(A,"fro") | NormOps.normf(A) |
| norm(A,1) | NormOps.normP1(A) |
| norm(A,2) | NormOps.normP2(A) |
| norm(A,Inf) | NormOps.normPInf(A) |
| max(abs(A(:))) | CommonOps.elementMaxAbs(A) |
| sum(A(:)) | CommonOps.elementSum(A) |
| rank(A,tol) | svd.decompose(A); SingularOps.rank(svd,tol); |
| [U,S,V] = svd(A) | DecompositionFactory.svd(A.numRows,A.numCols,true,true,false) |
|  | SingularOps.descendingOrder(U,false,S,V,false) |
| [U,S,V] = svd(A,0) | DecompositionFactory.svd(A.numRows,A.numCols,true,true,true) |
|  | SingularOps.descendingOrder(U,false,S,V,false) |
| S = svd(A) | DecompositionFactory.svd(A.numRows,A.numCols,false,false,true) |
| [V,D] = eig(A) |  eig = DecompositionFactory.eig(A.numCols); eig.decompose(A);  |
|  | V = EigenOps.createMatrixV(eig); D = EigenOps.createMatrixD(eig); |
| [Q,R] = qr(A) | decomp = DecompositionFactory.qr(A.numRows,A.numCols) |
|  | Q = decomp.getQ(null,false); R = decomp.getR(null,false); |
| [Q,R] = qr(A,0) | decomp = DecompositionFactory.qr(A.numRows,A.numCols) |
|  | Q = decomp.getQ(null,true); R = decomp.getR(null,true); |
| [Q,R,P] = qr(A) | decomp = DecompositionFactory.qrp(A.numRows,A.numCols) |
|  | Q = decomp.getQ(null,false); R = decomp.getR(null,false); |
|  | P = decomp.getPivotMatrix(null); |
| [Q,R,P] = qr(A,0) | decomp = DecompositionFactory.qrp(A.numRows,A.numCols) |
|  | Q = decomp.getQ(null,true); R = decomp.getR(null,true); |
|  | P = decomp.getPivotMatrix(null); |
| R = chol(A) | DecompositionFactory.chol(A.numCols,false) |
| [L,U,P] = lu(A) |DecompositionFactory.lu(A.numCols)  |