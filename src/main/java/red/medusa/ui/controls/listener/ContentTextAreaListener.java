package red.medusa.ui.controls.listener;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import red.medusa.ui.context.SegmentContextHolder;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class ContentTextAreaListener  implements DocumentListener,DebounceWorkAction {
    private final EditorTextField contentTextArea;

    public ContentTextAreaListener(EditorTextField contentTextArea) {
        this.contentTextArea = contentTextArea;
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        SegmentContextHolder.getSegment().setContent(contentTextArea.getText());
        this.work();
    }
}