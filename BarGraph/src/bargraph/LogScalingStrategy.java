package bargraph;

/**
 * This class implements a logarithmic conversion strategy between data values
 * and pixels.
 *
 * @author E. Stark
 * @version 20180407
 */
public class LogScalingStrategy implements ScalingStrategy {
    
    private final double scale;
    private final double mag;
    
    /**
     * Initialize a LogScalingStrategy with a specified basic scale factor.
     *
     * @param mag  Value corresponding to one "order of magnitude" increase
     * in a data value.  For example, mag = 10 would correspond to a standard
     * "factor of 10" order of magnitude.
     * @param scale  The scale factor, in pixels per order of magnitude
     * of the data value.  That is, an increase of the data value by an
     * order of magnitude will be correspond to an increase in pixels of the
     * specified scale amount.
    */
    public LogScalingStrategy(double mag, double scale) {
        this.scale = scale;
        this.mag = mag;
    }

    @Override
    public double dataToPixels(double data) {
        return Math.log(data)/Math.log(mag) * scale;
    }

    @Override
    public double pixelsToData(double pixels) {
        return pixels/scale;
    }
   
}
