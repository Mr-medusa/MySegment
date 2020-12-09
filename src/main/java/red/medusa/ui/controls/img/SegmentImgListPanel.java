package red.medusa.ui.controls.img;

import com.intellij.ui.components.JBLabel;
import red.medusa.intellij.SdkIcons;
import red.medusa.service.entity.Img;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/09 周三
 */
public class SegmentImgListPanel extends JPanel {
    private final Box verticalBox = Box.createVerticalBox();
    private final Set<ImgPanel> imgPanelSet = new LinkedHashSet<>();
    private volatile String currentCheckedName = "";

    public SegmentImgListPanel() {
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);
        this.setLayout(flowLayout);
        this.getInsets().set(0, 0, 0, 0);
        this.setBorder(BorderFactory.createEmptyBorder());

        this.add(verticalBox);

        verticalBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component componentAt = verticalBox.getComponentAt(e.getPoint());
                currentCheckedName = componentAt.getName();
                for (ImgPanel imgPanel : imgPanelSet) {
                    imgPanel.getCheckedLabel().setVisible(getMyName(imgPanel).equals(currentCheckedName));
                }
            }
        });
    }

    public JLayeredPane getJLayeredPane(ImgPanel imgPanel) {
        JLayeredPane jLayeredPane = new JLayeredPane();
        // img
        JBLabel segmentImageLabel = new JBLabel();
        ImageIcon image = new ImageIcon(imgPanel.getImg().getImage());
        Dimension dimension = new Dimension(image.getIconWidth(), image.getIconHeight());
        segmentImageLabel.setIcon(image);
        segmentImageLabel.setSize(dimension);

        // checked
        Icon icon = SdkIcons.Sdk_partially_checked_icon;
        JBLabel checkedLabel = new JBLabel(icon);
        Dimension checkedDimension = new Dimension(32, 32);
        checkedLabel.setPreferredSize(checkedDimension);
        checkedLabel.setMaximumSize(checkedDimension);
        checkedLabel.setMinimumSize(checkedDimension);
        checkedLabel.setBounds(10, 10, 32, 32);
        checkedLabel.setVisible(false);

        // jLayeredPane
        jLayeredPane.add(segmentImageLabel, 0);
        jLayeredPane.add(checkedLabel, 1, 0);

        Dimension barDimension = new Dimension(dimension.width, dimension.height + 10);
        jLayeredPane.setSize(barDimension);
        jLayeredPane.setPreferredSize(barDimension);
        jLayeredPane.setMinimumSize(barDimension);

        jLayeredPane.setName(getMyName(imgPanel));

        imgPanel.setCheckedLabel(checkedLabel);

        return jLayeredPane;
    }

    public void addImgs(Set<Img> set) {
        for (Img img : set) {
            ImgPanel imgPanel = new ImgPanel(img);
            imgPanelSet.add(imgPanel);
            verticalBox.add(getJLayeredPane(imgPanel));
        }
        verticalBox.revalidate();
    }

    public Img delImg() {
        int i = 0;
        Iterator<ImgPanel> iterator = imgPanelSet.iterator();
        ImgPanel next = null;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (getMyName(next).equals(currentCheckedName)) {
                iterator.remove();
                break;
            }
            i++;
        }
        verticalBox.remove(i);
        verticalBox.revalidate();
        return next != null ? next.getImg() : null;
    }

    public void clearAllImg() {
        imgPanelSet.clear();
        verticalBox.removeAll();
        verticalBox.revalidate();
    }

    public String getMyName(ImgPanel imgPanel) {
        Img img = imgPanel.getImg();
        String filename = img.getFilename();
        return filename != null ? filename : String.valueOf(img.getId());
    }
}




