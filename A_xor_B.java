/**
 * Created by Soeren Berken-Mersmann on 10.04.15.
 * Alpha Composition using the A exclusive or B operator
 * (short A xor B)
 */
public class A_xor_B extends Alpha_Blending {

    @Override
    protected float calculateAlpha(float alpha1Normalized, float alpha2Normalized) {
        // xor: algebraic normal form: alpha_A xor alpha_B = (alpha_A + alpha_B) mod 2
        return (alpha1Normalized + alpha2Normalized) % 2;
    }
}
