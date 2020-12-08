package red.medusa.ui.controls.img;

import lombok.Data;
import lombok.EqualsAndHashCode;
import red.medusa.service.entity.Img;

import javax.swing.*;

@Data
public class ImgPanel {
    private Img img;
    @EqualsAndHashCode.Exclude
    private JLabel checkedLabel;

    public ImgPanel(Img img) {
        this.img = img;
    }
}