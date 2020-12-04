package red.medusa.ui.renderer;

import com.intellij.ui.components.JBLabel;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.SdkIcons;
import red.medusa.service.entity.Img;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author huguanghui
 * @since 2020/12/02 周三
 */
@Slf4j
public class SegmentImgListCellRenderer extends JPanel implements ListCellRenderer<Img> {

    private final Map<Integer, Component> renderMap = new HashMap<>(3);
    private final Map<Integer, Component> renderCheckedMap = new HashMap<>(3);

    public SegmentImgListCellRenderer() {

    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Img> list, Img value, int index, boolean isSelected, boolean cellHasFocus) {

        if (!renderMap.containsKey(index)) {
            JLayeredPane jLayeredPane = new JLayeredPane();

            JBLabel segmentImageLabel = new JBLabel();
            ImageIcon image = new ImageIcon(value.getImage());
            Dimension dimension = new Dimension(image.getIconWidth(), image.getIconHeight() + 10);
            segmentImageLabel.setIcon(image);
            segmentImageLabel.setSize(dimension);

            Icon icon = SdkIcons.Sdk_checked_blue_icon;
            JLabel checkedLabel = new JLabel(icon);
            checkedLabel.setBounds(20, 30, 32, 32);

            jLayeredPane.add(segmentImageLabel, 0, 1);
            jLayeredPane.add(checkedLabel, 1, 0);

            jLayeredPane.setSize(dimension);
            jLayeredPane.setPreferredSize(dimension);
            jLayeredPane.setMaximumSize(dimension);

            /*
                告诉JCombobox当前的Cell尺寸多大
             */
            this.add(jLayeredPane);
            this.setSize(dimension);
            this.setPreferredSize(dimension);
            this.setMaximumSize(dimension);

            renderMap.put(index, jLayeredPane);
            renderCheckedMap.put(index, checkedLabel);
        }

        renderCheckedMap.get(index).setVisible(isSelected);

        return renderMap.get(index);
    }

    public void removeByIndex(Integer index){
        this.renderMap.remove(index);
        this.renderCheckedMap.remove(index);
    }


    public void clear() {
        this.renderMap.clear();
        this.renderCheckedMap.clear();
    }
}




