package red.medusa.ui.controls.listener;

import com.intellij.openapi.ui.ComboBox;
import red.medusa.service.entity.Module;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Version;
import red.medusa.ui.SegmentAddOrEdit;
import red.medusa.ui.context.SegmentContextHolder;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class SegmentModuleVersionListener implements ItemListener ,DebounceWorkAction{
    private final ComboBox<Version> versionComboBox;

    public SegmentModuleVersionListener( ComboBox<Version> versionComboBox) {
        this.versionComboBox = versionComboBox;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Segment segment = SegmentContextHolder.getSegment();

            Object item = e.getItem();
            if (item instanceof Module) {
                Module module = (Module) e.getItem();
                versionComboBox.removeAllItems();
                versionComboBox.addItem(new Version().setName(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT));
                if(module.getName().equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                    return;
                }
                for (Version version : module.getVersions()) {
                    versionComboBox.addItem(version);
                }
//                if (!module.getVersions().isEmpty()) {
//                    versionComboBox.setSelectedIndex(0);
//                    segment.setVersion(versionComboBox.getItem());
//                }else{
//                    segment.setVersion(null);
//                }

                segment.setModule(module);
            } else {
                Version version = (Version) e.getItem();
                if(version.getName().equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                    return;
                }
                segment.setVersion(version);
                this.work();
            }
        }
    }
}



