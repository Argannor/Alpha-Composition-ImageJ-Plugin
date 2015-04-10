/**
 * Created by argannor on 10.04.15.
 */
public class A_out_B extends Alpha_Blending {
    @Override
    protected int calculateColor(int color1, int color2, float alpha1Normalized, float alpha2Normalized, float alphaNorm) {
        if(alpha1Normalized != 0 && alpha2Normalized < 1) {
            return color1;
        }
        return 0;
    }

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        if (alpha2Normalized == 0)
            return alpha1Normalized;
        // TODO
        return super.calculateAlpha(alpha1Normalized, alpha2Normalized);
    }
}
