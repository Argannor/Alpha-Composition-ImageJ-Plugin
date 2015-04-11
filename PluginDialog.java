import ij.ImagePlus;
import ij.gui.ImageWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Arga on 11.04.2015.
 */
public class PluginDialog extends JFrame {
    private JList<Operator> operatorList;
    private JComboBox<ImagePlus> imageASelector;
    private JComboBox<ImagePlus> imageBSelector;
    private JButton composeButton;
    private JButton cancelButton;
    private JPanel rootPanel;

    private java.util.List<ImagePlus> images;

    public PluginDialog(java.util.List<ImagePlus> images) {
        super("dufus");
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.images = images;

        initList();
        initButtons();
        initSelectors();

        pack();
        setVisible(true);
        requestFocus();
    }

    private void initSelectors() {
        images.forEach(imagePlus -> {
            imageASelector.addItem(imagePlus);
            imageBSelector.addItem(imagePlus);
        });
    }

    private void initButtons() {
        composeButton.addActionListener(e -> new Thread(() -> operatorList.getSelectedValue().get().
                execute((ImagePlus) imageASelector.getSelectedItem(), (ImagePlus) imageBSelector.getSelectedItem())).run());
         cancelButton.addActionListener(e -> this.dispose());
    }

    private void initList() {
        Operator[] operators = {Operator.A_OVER_B, Operator.A_IN_B, Operator.A_OUT_B, Operator.A_ATOP_B, Operator.A_XOR_B};
        operatorList.setListData(operators);
    }
}
