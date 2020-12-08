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
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
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
        JBLabel segmentImageLabel = new JBLabel();
        ImageIcon image = new ImageIcon(imgPanel.getImg().getImage());
        Dimension dimension = new Dimension(image.getIconWidth(), image.getIconHeight() + 10);
        segmentImageLabel.setIcon(image);
        segmentImageLabel.setSize(dimension);
        Icon icon = SdkIcons.Sdk_checked_blue_icon;
        JLabel checkedLabel = new JLabel(icon);
        checkedLabel.setBounds(20, 30, 32, 32);
        checkedLabel.setVisible(false);

        jLayeredPane.add(segmentImageLabel, 0, 1);
        jLayeredPane.add(checkedLabel, 1, 0);

        jLayeredPane.setSize(dimension);
        jLayeredPane.setPreferredSize(dimension);
        jLayeredPane.setMinimumSize(dimension);

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
    public String getMyName(ImgPanel imgPanel){
        Img img = imgPanel.getImg();
        String filename = img.getFilename();
        return filename != null ? filename : String.valueOf(img.getId());
    }
}



