package red.medusa.ui.controls;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class SegmentTextField extends JTextField {

    public SegmentTextField() {
        this.setBorder(BorderFactory.createEmptyBorder());
        super.setPreferredSize(new Dimension(0, 30));
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    }

    @Override
    public Insets getInsets() {
        return JBUI.emptyInsets();
    }
}