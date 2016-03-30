/**
 * Created by tehredwun on 3/20/16.
 */
public class util {
    static double roundDoubleToXSigFigs(double input, int x) {
        if (x > Double.toString(input).length() - 2) {
            x = Double.toString(input).length() - 2;
        }
        input = Double.parseDouble(Double.toString(input).substring(0, x+1));

        int mul = 1;
        for (int i = 0; i < x-1; i++) {
            mul *= 10;
        }

        long inputRoundX = Math.round(input * mul);
        return ((double)inputRoundX)/mul;
    }

    public static String genDoubleString (double doubloon, int sigFigs) {
        String doubleString = Double.toString(util.roundDoubleToXSigFigs(doubloon, sigFigs));
        String expon = Double.toString(doubloon);
        expon = expon.substring(expon.length() - 3, expon.length());
        if (expon.charAt(0) == 'E') {
            doubleString += expon;
        }
        return doubleString;
    }
}
