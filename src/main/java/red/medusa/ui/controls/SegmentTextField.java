package red.medusa.ui.controls;

import javax.swing.*;
import java.awt.*;

public  class SegmentTextField extends JTextField {
    public SegmentTextField() {
        super.setPreferredSize(new Dimension(0, 35));
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
    }
}