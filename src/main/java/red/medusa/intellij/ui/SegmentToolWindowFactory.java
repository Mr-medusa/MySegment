package red.medusa.intellij.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import red.medusa.intellij.SdkIcons;
import red.medusa.service.service.SegmentEntityService;
import red.medusa.ui.SegmentAddOrEdit;
import red.medusa.ui.SegmentDetail;
import red.medusa.ui.SegmentHome;
import red.medusa.ui.SegmentModule;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SegmentToolWindowFactory implements ToolWindowFactory {

    private static final Map<SegmentContentType, SegmentComponent> map = new HashMap<>(4);

    static {

        // [ApplicationImpl pooled thread 6]
        map.put(SegmentContentType.SegmentHome, new SegmentHome());
        map.put(SegmentContentType.SegmentDetail, new SegmentDetail());
        map.put(SegmentContentType.SegmentAddOrEdit, new SegmentAddOrEdit());
        map.put(SegmentContentType.SegmentModule, new SegmentModule());
    }

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(SdkIcons.Sdk_Farmer_icon);

        // [AWT-EventQueue-0]
        // 添加tab
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content contentHome = contentFactory.createContent(map.get(SegmentContentType.SegmentHome).getJComponent(), "Ho", true);
        Content contentDetail = contentFactory.createContent(map.get(SegmentContentType.SegmentDetail).getJComponent(), "De", true);
        Content contentEdit = contentFactory.createContent(map.get(SegmentContentType.SegmentAddOrEdit).getJComponent(), "Ed", true);
        Content contentModule = contentFactory.createContent(map.get(SegmentContentType.SegmentModule).getJComponent(), "Mo", true);

        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(contentHome);
        contentManager.addContent(contentEdit);
        contentManager.addContent(contentDetail);
        contentManager.addContent(contentModule);

        initListener(contentManager);

        SegmentEntityService.getInstance().startService();
        ((SegmentHome)map.get(SegmentContentType.SegmentHome)).refresh2();
    }

    public void initListener(ContentManager contentManager) {
        contentManager.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                if (event.getOperation() == ContentManagerEvent.ContentOperation.add) {
                    for (int i = 0; i < SegmentContentType.values().length; i++) {
                        if (event.getIndex() == i) {
                            SegmentContentType value = SegmentContentType.values()[i];
                            SegmentToolWindowFactory.get(value).refresh();
                        }
                    }
                }
            }
        });
    }

    public static SegmentComponent get(SegmentContentType key) {
        return map.get(key);
    }

}
