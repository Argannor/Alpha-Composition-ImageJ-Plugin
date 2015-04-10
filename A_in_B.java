/**
 * Created by argannor on 10.04.15.
 */
public class A_in_B extends Alpha_Blending {

    @Override
    protected int calculateColor(int color1, int color2, float alpha1Normalized, float alpha2Normalized, float alphaNorm) {
        if(alpha2Normalized == 0) {
            return 0;
        }
        return color2;
    }

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        if(alpha2Normalized == 0) {
            return 0;
        }
        return super.calculateAlpha(alpha1Normalized, alpha2Normalized);
    }
}
