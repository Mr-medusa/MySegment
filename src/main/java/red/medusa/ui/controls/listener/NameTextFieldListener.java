package red.medusa.ui.controls.listener;

import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.SegmentTextField;

import javax.swing.event.DocumentEvent;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class NameTextFieldListener  extends DocumentAdapter implements DebounceWorkAction {
    private final SegmentTextField nameTextField;

    public NameTextFieldListener(SegmentTextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    @Override
    public void textChanged(@NotNull DocumentEvent e) {
        SegmentContextHolder.getSegment().setName(nameTextField.getText());
        this.work();

    }
}
