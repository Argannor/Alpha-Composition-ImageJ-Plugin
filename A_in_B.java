/**
 * Created by Soeren Berken-Mersmann on 10.04.15.
 * Alpha Composition using the A in B operator
 */
public class A_in_B extends Alpha_Blending {

    @Override
    protected int calculateColor(int color1, int color2, float alpha1Normalized, float alpha2Normalized, float alphaNorm) {
        // the only visible color is from A
        return color1;
    }

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        if(alpha2Normalized > 0 && alpha1Normalized > 0) {
            // if both alpha channels are greater then zero, we're in the intersecting set.
            return super.calculateAlpha(alpha1Normalized, alpha2Normalized);
        }
        return 0;
    }
}
