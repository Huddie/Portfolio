
import java.math.BigInteger;
import java.lang.*;
import java.util.*;

public class Cubic {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        int numberOfCases;
        BigInteger caseVal, testVal, lastDigit, truncLength;
        String inString;

        numberOfCases = Integer.parseInt(scan.next());
        for ( int i = 0; i < numberOfCases; i += 1 ) {
            inString = scan.next();
            truncLength = new BigInteger( new Integer(inString.length()).toString() );
            caseVal = new BigInteger(inString);
            lastDigit = caseVal.mod(BigInteger.TEN);
            
            switch ( lastDigit.intValue() ) {
            case 7:
                cubify (
                    caseVal, 
                    truncLength, 
                    new BigInteger("3")
                );
                break;
            case 3:
                cubify (
                    caseVal, 
                    truncLength, 
                    new BigInteger("7")
                );
                break;
            default:
                cubify (
                    caseVal, 
                    truncLength, 
                    lastDigit
                );
            }
        }
    }

    public static void cubify (
        BigInteger caseVal, 
        BigInteger truncLength, BigInteger answer) {
            for ( BigInteger digitPlace = new BigInteger("2"); digitPlace.compareTo(truncLength) <= 0 ; digitPlace = digitPlace.add(BigInteger.ONE) ) {
                BigInteger placeValueTemplate = BigInteger.TEN.pow(digitPlace.intValue()-1);
                for ( BigInteger digitValue = BigInteger.ONE; digitValue.compareTo(BigInteger.TEN) == -1; digitValue = digitValue.add(BigInteger.ONE) ) {
                    BigInteger possibleNewVal = placeValueTemplate.multiply(digitValue).add(answer);
                    if ( caseVal.mod(BigInteger.TEN.pow(digitPlace.intValue())).equals(possibleNewVal.modPow(new BigInteger("3"), BigInteger.TEN.pow(digitPlace.intValue()))) ) {
                        answer = possibleNewVal;
                        break;
                    }
                }
            }
            System.out.println(answer);
    }
}