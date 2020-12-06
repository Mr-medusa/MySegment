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
public class ContentSyntaxComboListener   implements ItemListener,DebounceWorkAction {
    private final EditorTextField contentTextArea;

    public ContentSyntaxComboListener(EditorTextField contentTextArea) {
        this.contentTextArea = contentTextArea;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            LangType langType = (LangType) e.getItem();
            if(langType.value.equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                return;
            }
            contentTextArea.setFileType(SegmentEditorTextField.findFileType(langType.name()));

            SegmentContextHolder.getSegment().setLangContent(langType);
            this.work();
        }
    }
}
















