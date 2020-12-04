package red.medusa.ui.controls.listener;

import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.NotNull;
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
    public void documentChanged(com.intellij.openapi.editor.event.@NotNull DocumentEvent event) {
        SegmentContextHolder.getSegment().setDependence(dependenceTextArea.getText());
        this.work();
    }
}












