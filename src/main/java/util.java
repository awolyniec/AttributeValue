/**
 * Created by tehredwun on 3/20/16.
 */
public class util {
    static double roundDoubleToXSigFigs(double input, int x) {
        //don't count sig figs by the number of digits; leading 0's are not sig figs
        if (x > Double.toString(input).length() - 2) {
            x = Double.toString(input).length() - 2;
        }
        input = Double.parseDouble(Double.toString(input).substring(0, x+2));

        int mul = 1;
        for (int i = 0; i < x-1; i++) {
            mul *= 10;
        }

        long inputRoundX = Math.round(input * mul);
        return ((double)inputRoundX)/mul;
    }

    public static String genDoubleString (double doubloon, int sigFigs) {
        String expon = Double.toString(doubloon);
        int lengthMinusExponents = expon.length();
        boolean eflag = false;
        int digitCut = 0;
        for (int i = 0; i < expon.length(); i++) {
            if (expon.charAt(i) == 'E') {
                eflag = true;
                digitCut += expon.length() - i - 3;
                expon = expon.substring(i, expon.length());
                break;
            }
        }
        //ensure there are at least 2 significant figures
        if (sigFigs - digitCut < 2) { digitCut = sigFigs - 2; }
        if (eflag) { lengthMinusExponents -= expon.length(); }
        //no rounding if the string has fewer than "sigFigs" sig-figs
        if (lengthMinusExponents <= sigFigs) { return Double.toString(doubloon); }

        String doubleString = Double.toString(util.roundDoubleToXSigFigs(doubloon, sigFigs - digitCut)); //rounding errors
        if (eflag) doubleString += expon;
        return doubleString;
    }
}
