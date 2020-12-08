package red.medusa.ui.controls;

import javax.swing.*;
import java.awt.*;

public class SegmentLabel extends JLabel {
    public SegmentLabel(String label) {
        this(label,60);
    }

    /*
        宽度固定 高度可能变化
     */
    public SegmentLabel(String label, int width) {
        super(" " + label + ":  ");
        super.setHorizontalAlignment(SwingConstants.LEFT);
        super.setVerticalAlignment(SwingConstants.TOP);
        super.setPreferredSize(new Dimension(width, 30));
        super.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
    }

    /*
        宽度高度都固定
     */
    public SegmentLabel(String label, int width,int height) {
        super(" " + label + ":  ");
        super.setHorizontalAlignment(SwingConstants.LEFT);
        super.setVerticalAlignment(SwingConstants.TOP);
        super.setMaximumSize(new Dimension(width, height));
        super.setPreferredSize(new Dimension(width, height));
    }
}