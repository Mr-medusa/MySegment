package red.medusa.intellij.ui;

import com.intellij.ui.content.ContentManager;

/**
 * 注意这里的顺序不能变
 *
 * @see SegmentToolWindowFactory#initListener(ContentManager)
 */
public enum SegmentContentType {
    SegmentHome, SegmentAddOrEdit, SegmentDetail, SegmentModule
}