package red.medusa.intellij.ui;

import javax.swing.*;

/**
 * @author huguanghui
 * @since 2020/11/30 周一
 */
public interface SegmentComponent {

    JComponent getJComponent();

    default void refresh(){

    }

}
