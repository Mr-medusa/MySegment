package red.medusa.ui.controls.listener;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import red.medusa.ui.context.SegmentContextHolder;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class DependenceTextAreaListener  implements DocumentListener,DebounceWorkAction {
    private final EditorTextField dependenceTextArea;

    public DependenceTextAreaListener(EditorTextField dependenceTextArea) {
        this.dependenceTextArea = dependenceTextArea;
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        SegmentContextHolder.getSegment().setDependence(dependenceTextArea.getText());
        this.work();
    }
}












