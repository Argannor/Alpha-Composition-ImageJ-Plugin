import ij.ImagePlus;

import javax.swing.*;


/**
 * Created by Soeren Berken-Mersmann on 11.04.2015.
 */
public class PluginDialog extends JFrame {
    private JComboBox<Operator> operatorSelector;

    private JComboBox<ImagePlus> imageASelector;
    private JComboBox<ImagePlus> imageBSelector;
    private JComboBox<ImagePlus> alphaASelector;
    private JComboBox<ImagePlus> alphaBSelector;

    private JButton composeButton;
    private JButton cancelButton;
    private JPanel rootPanel;

    private java.util.List<ImagePlus> images;

    public PluginDialog(java.util.List<ImagePlus> images) {
        super("Alpha Composition Plugin");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.images = images;

        initButtons();
        initSelectors();

        pack();
        setVisible(true);
        requestFocus();
    }

    private void initSelectors() {
        // image selectors
        images.forEach(imagePlus -> {
            imageASelector.addItem(imagePlus);
            imageBSelector.addItem(imagePlus);

            alphaASelector.addItem(imagePlus);
            alphaBSelector.addItem(imagePlus);
        });

        // operator selector
        Operator[] operators = {Operator.A_OVER_B, Operator.A_IN_B, Operator.A_OUT_B, Operator.A_ATOP_B, Operator.A_XOR_B};
        for (Operator operator : operators) {
            operatorSelector.addItem(operator);
        }
    }

    private void initButtons() {
        composeButton.addActionListener(e -> new Thread(() -> {
            Alpha_Blending operator = ((Operator) operatorSelector.getSelectedItem()).get();
            operator.execute((ImagePlus) imageASelector.getSelectedItem(), (ImagePlus) imageBSelector.getSelectedItem(), (ImagePlus) alphaASelector.getSelectedItem(), (ImagePlus) alphaBSelector.getSelectedItem());
        }).run());
         cancelButton.addActionListener(e -> this.dispose());
    }
}
