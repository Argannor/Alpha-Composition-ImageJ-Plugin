/**
 * Created by Soeren Berken-Mersmann on 10.04.15.
 * Alpha Composition using the A atop B operator
 */
public class A_atop_B extends Alpha_Blending {

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        if(alpha2Normalized == 0) {
            // if alpha_B == 0, we're outside of B -> alpha_O = 0
            return 0;
        }
        return super.calculateAlpha(alpha1Normalized, alpha2Normalized);
    }

}
