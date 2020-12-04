package red.medusa.ui.controls;

import javax.swing.*;
import java.awt.*;

public class SegmentLabel2 extends JLabel {

    public SegmentLabel2() {
        this("");
    }

    /*
        宽高可伸缩
     */
    public SegmentLabel2(String label) {
        super(label + ":  ");
        super.setHorizontalAlignment(SwingConstants.LEFT);
        super.setVerticalAlignment(SwingConstants.TOP);

        super.setPreferredSize(new Dimension(0, 30));
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    /*
        宽高可伸缩、居中对齐
     */
    public SegmentLabel2(String label, boolean pureLabel) {
        super(pureLabel ? label : label + ":  ");
        super.setHorizontalAlignment(SwingConstants.LEFT);
        super.setVerticalAlignment(SwingConstants.TOP);

        super.setPreferredSize(new Dimension(0, 30));
        super.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        super.setVerticalAlignment(SwingConstants.CENTER);
    }
}