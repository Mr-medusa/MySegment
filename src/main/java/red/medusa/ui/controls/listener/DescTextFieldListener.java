package red.medusa.ui.controls.listener;

import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import red.medusa.ui.context.SegmentContextHolder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class DescTextFieldListener extends DocumentAdapter implements DebounceWorkAction {
    private final JTextArea descTextField;

    public DescTextFieldListener(JTextArea descTextField) {
        this.descTextField = descTextField;
    }

    @Override
    public void textChanged(@NotNull DocumentEvent e) {
        SegmentContextHolder.getSegment().setDescription(descTextField.getText());
        this.work();
    }
}










