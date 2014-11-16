/* @author Haohan Zhu
 * @email zhu@bu.edu
 * This code is for the client to use
 * This is for encryption only, cannot decrypt
 */
 
import java.math.*;
import java.util.*;

public class PaillierEncryptOnly {

    public BigInteger n;
    public BigInteger nsquare;
    private BigInteger g;
    private int bitLength;

    public PaillierEncryptOnly() {
        this.bitLength = 64;
        this.g = BigInteger.ZERO;
        this.n = BigInteger.ZERO;
        this.nsquare = n.multiply(n);
    }
    public void setPaillierEncryptOnly(int bitLengthVal, BigInteger g, BigInteger n){
        this.bitLength = bitLengthVal;
        this.g = g;
        this.n = n;
        this.nsquare = n.multiply(n);
    }
    
    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function explicitly requires random input r to help with encryption.
     * @param m plaintext as a BigInteger
     * @param r random plaintext to help with encryption
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m, BigInteger r) {
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);
    }
    /**
     * Encrypts plaintext m. ciphertext c = g^m * r^n mod n^2. This function automatically generates random input r (to help with encryption).
     * @param m plaintext as a BigInteger
     * @return ciphertext as a BigInteger
     */
    public BigInteger Encryption(BigInteger m) {
        BigInteger r = new BigInteger(bitLength, new Random());
        return g.modPow(m, nsquare).multiply(r.modPow(n, nsquare)).mod(nsquare);

    }
    /* homomorphic properties -> D(E(m1)*E(m2) mod n^2) = (m1 + m2) mod n */
    public BigInteger HomomorphicAddition(BigInteger em1, BigInteger em2){
        if(em1.compareTo(BigInteger.ZERO) == 0){
            return em2;
        }
        else if(em2.compareTo(BigInteger.ZERO) == 0){
            return em1;
        }
        else{
            BigInteger eSum = em1.multiply(em2).mod(nsquare);
            return eSum;
        }
    }
    
    /* homomorphic properties -> D(E(m1)^m2 mod n^2) = (m1*m2) mod n */
    public BigInteger HomomorphicMultiplication(BigInteger em1, BigInteger m2){
        if(em1.compareTo(BigInteger.ZERO) == 0){
            return BigInteger.ZERO;
        }
        else if(m2.compareTo(BigInteger.ZERO) == 0){
            return BigInteger.ZERO;
        }
        else{
            BigInteger eProduct = em1.modPow(m2, nsquare);
            return eProduct;
        }
    }
}