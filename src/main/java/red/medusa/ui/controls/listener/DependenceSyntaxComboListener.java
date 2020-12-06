package red.medusa.ui.controls.listener;

import com.intellij.ui.EditorTextField;
import red.medusa.service.entity.LangType;
import red.medusa.ui.SegmentAddOrEdit;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.SegmentEditorTextField;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class DependenceSyntaxComboListener implements ItemListener ,DebounceWorkAction {
    private final EditorTextField dependenceTextArea;

    public DependenceSyntaxComboListener(EditorTextField dependenceTextArea) {
        this.dependenceTextArea = dependenceTextArea;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            LangType langType = (LangType) e.getItem();
            if(langType.value.equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                return;
            }
            dependenceTextArea.setFileType(SegmentEditorTextField.findFileType(langType.name()));

            SegmentContextHolder.getSegment().setLangDep(langType);
            this.work();
        }
    }
}
