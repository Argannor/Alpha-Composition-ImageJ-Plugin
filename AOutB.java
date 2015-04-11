/**
 * Created by Soeren Berken-Mersmann on 10.04.15.
 * Alpha Composition using the A held out by B operator
 * (short A out B)
 */
public class AOutB extends Alpha_Blending {
    @Override
    protected int calculateColor(int color1, int color2, float alpha1Normalized, float alpha2Normalized, float alphaNorm) {
        // The color is always Color_A
        return color1;
    }

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        // If alpha_B == 0, we're outside of B, therefore we can return alpha_A
        if (alpha2Normalized == 0) {
            return alpha1Normalized;
        }
        // otherwise we calculate the alpha value by multiplying alpha_A with alpha_B
        return alpha1Normalized * alpha2Normalized;
    }
}
