package red.medusa.ui.controls.listener;

import com.intellij.openapi.ui.ComboBox;
import red.medusa.service.entity.Category;
import red.medusa.service.entity.Module;
import red.medusa.service.entity.Segment;
import red.medusa.ui.SegmentAddOrEdit;
import red.medusa.ui.context.SegmentContextHolder;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
public class SegmentModuleCategoryListener implements ItemListener ,DebounceWorkAction{
    private final ComboBox<Category> categoryComboBox;

    public SegmentModuleCategoryListener(ComboBox<Category> categoryComboBox) {
        this.categoryComboBox = categoryComboBox;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Segment segment = SegmentContextHolder.getSegment();

            Object item = e.getItem();
            if (item instanceof Module) {
                Module module = (Module) e.getItem();
                categoryComboBox.removeAllItems();
                categoryComboBox.addItem(new Category().setName(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT));
                if(module.getName().equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                    return;
                }
                for (Category category : module.getCategories()) {
                    categoryComboBox.addItem(category);
                }

                segment.setModule(module);
            } else {
                Category category = (Category) e.getItem();
                if(category.getName().equals(SegmentAddOrEdit.COMBOBOX_FIRST_SELECT)){
                    return;
                }
                segment.setCategory(category);
                this.work();
            }
        }
    }
}



