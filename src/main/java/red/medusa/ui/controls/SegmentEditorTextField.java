package red.medusa.ui.controls;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.impl.FoldingModelImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import red.medusa.service.entity.LangType;

import java.awt.*;

/**
 * @author huguanghui
 * @since 2020/11/27 周五
 */
public class SegmentEditorTextField extends EditorTextField {

    private final int orientation;

    public SegmentEditorTextField(String text, Project project, String fileType, int minimumHeight, boolean readOnly) {
        this(text, project, fileType, new Dimension(0, minimumHeight), readOnly, EditorEx.VERTICAL_SCROLLBAR_LEFT);
    }

    public SegmentEditorTextField(String text, Project project, String fileType, Dimension dimension, boolean readOnly, int orientation) {
        super(text != null ? text : "", project, findFileType(fileType));
        // 设置一个合适的高度
        this.setPreferredSize(dimension);
        this.setOneLineMode(false);
        this.getDocument().setReadOnly(readOnly);
        this.orientation = orientation;
    }

    public static FileType findFileType(String fileExe) {
        fileExe = fileExe != null ? fileExe : LangType.JAVA.value;
        FileTypeManagerImpl service = (FileTypeManagerImpl) FileTypeManager.getInstance();
        return service.getFileTypeByExtension(fileExe);
    }

    @Override
    protected EditorEx createEditor() {
        EditorImpl editor = (EditorImpl) super.createEditor();
        editor.setVerticalScrollbarVisible(true);
        editor.setVerticalScrollbarOrientation(orientation);

        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setGutterIconsShown(true);
        settings.setFoldingOutlineShown(true);
        settings.setAutoCodeFoldingEnabled(true);
        settings.setUseTabCharacter(true);
        settings.setSmartHome(true);
        settings.setAnimatedScrolling(true);
        settings.setAdditionalPageAtBottom(true);
        settings.setDndEnabled(true);
        settings.setWheelFontChangeEnabled(true);
        settings.setMouseClickSelectionHonorsCamelWords(true);

        FoldingModelImpl foldingModel = editor.getFoldingModel();
        foldingModel.setFoldingEnabled(true);

        return editor;
    }
}


