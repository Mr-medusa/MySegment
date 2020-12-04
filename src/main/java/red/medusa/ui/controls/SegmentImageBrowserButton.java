package red.medusa.ui.controls;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.utils.SegmentAppUtils;


import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/02 周三
 */
@Slf4j
public class SegmentImageBrowserButton extends TextFieldWithBrowseButton {

    private static final Set<String> imgFiles = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp"));

    public SegmentImageBrowserButton(JLabel jLabel, List<VirtualFile> tempImgListModel) {
        FileChooser.chooseFiles(
                new FileChooserDescriptor(true, false, false, false, false, true).withFileFilter(new Condition<VirtualFile>() {
                    @Override
                    public boolean value(VirtualFile virtualFile) {
                        String extension = virtualFile.getExtension();
                        return imgFiles.contains(extension != null ? extension.toLowerCase() : "");
                    }
                }),
                // 可以从当前项目路径定位
                SegmentAppUtils.getProject(),
                null,
                (List<VirtualFile> virtualFiles) -> {
                    tempImgListModel.clear();
                    StringBuilder fileNames= new StringBuilder();
                    for (VirtualFile virtualFile : virtualFiles) {
                        tempImgListModel.add(virtualFile);
                        fileNames.append(virtualFile.getName());
                        fileNames.append(" ");
                        jLabel.setText(String.valueOf(fileNames));
                    }
                }
        );
    }
}
