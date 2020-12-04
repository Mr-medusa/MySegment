package red.medusa.ui.controls.listener;

import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;
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
    public void documentChanged(com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
        SegmentContextHolder.getSegment().setContent(contentTextArea.getText());
        this.work();
    }
}