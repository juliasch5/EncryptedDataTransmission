package src;

import java.math.BigInteger;
import java.util.*;

public class RSA {

    int tamPrimo;
    private BigInteger n, q, p;
    private BigInteger totient;
    private BigInteger e, d;

    public RSA(int tamPrimo) {
        this.tamPrimo = tamPrimo;
        generatePrimes();             //Generate p y q
        generateKeys();             //Generate e y d

    }

    public RSA(BigInteger p,BigInteger q,int tamPrimo) {
        this.tamPrimo=tamPrimo;
        this.p=p;
        this.q=q;
        generateKeys();             //Generate e y d
    }
    

    public void generatePrimes()
    {
        p = new BigInteger(tamPrimo, 100, new Random());
        do q = new BigInteger(tamPrimo, 100, new Random());
            while(q.compareTo(p)==0);
    }

    public void generateKeys()
    {
        // n = p * q
        n = p.multiply(q);
        // toltient = (p-1)*(q-1)
        totient = p.subtract(BigInteger.valueOf(1));
        totient = totient.multiply(q.subtract(BigInteger.valueOf(1)));
        // We choose an e coprime of y and less than n
        do e = new BigInteger(2 * tamPrimo, new Random());
            while((e.compareTo(totient) != -1) ||
		 (e.gcd(totient).compareTo(BigInteger.valueOf(1)) != 0));
        // d = e^1 mod totient
        d = e.modInverse(totient);
    }



    public BigInteger getP() {return(p);}
    public BigInteger getQ() {return(q);}
    public BigInteger getTotient() {return(totient);}
    public BigInteger getN() {return(n);}
    public BigInteger getE() {return(e);}
    public BigInteger getD() {return(d);}

}
