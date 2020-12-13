package red.medusa.ui;

import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.entity.Category;
import red.medusa.service.entity.Module;
import red.medusa.ui.controls.SegmentLabel;
import red.medusa.ui.segment_action.CategoryAction;
import red.medusa.ui.segment_action.ModuleAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/25 周三
 */
@Slf4j
public class SegmentModule implements ActionListener, SegmentComponent {
    /*
        bar
     */
    private final JPanel bar = new JPanel();
    private final Box body = Box.createVerticalBox();
    /*
        layout
     */
    private final Box moduleBox = Box.createHorizontalBox();
    private final Box versionBox = Box.createHorizontalBox();

    /*
        model
     */
    private final DefaultListModel<Module> moduleModel = new DefaultListModel<>();
    private final DefaultListModel<Category> categoryListModel = new DefaultListModel<>();

    private final JBList<Module> moduleList = new JBList<>();
    private final JBList<Category> categoryList = new JBList<>();

    /*
        button
     */
    private final Box operationBtn = Box.createHorizontalBox();
    private final static String ADD_VERSION = "<html><font>添加版本</font></html>";
    private final static String DELETE_VERSION = "<html><font color=red>删除版本</font></html>";
    private final static String ADD_MODULE = "<html><font>添加模块</font></html>";
    private final static String DELETE_MODULE = "<html><font color=red>删除模块</font></html>";
    private final JTextField addText = new JTextField();
    private final JButton addModuleBtn = new JButton(ADD_MODULE);
    private final JButton deleteModuleBtn = new JButton(DELETE_MODULE);
    private final JButton addVersionBtn = new JButton(ADD_VERSION);
    private final JButton deleteVersionBtn = new JButton(DELETE_VERSION);

    public SegmentModule() {
        initControlsProp();
        initControlsEvent();
        initFrame();
        fillControlContent();
    }

    @Override
    public void refresh() {
        List<Module> modules = new ModuleAction().list();
        moduleModel.removeAllElements();
        categoryListModel.removeAllElements();
        for (Module module : modules) {
            moduleModel.add(moduleModel.size(), module);
        }
        if (modules.size() > 0) {
            moduleList.setSelectedIndex(0);
            List<Category> categories = modules.get(0).getCategories();
            if (categories != null && !categories.isEmpty()) {
                for (Category category : categories) {
                    categoryListModel.add(categoryListModel.size(), category);
                }
                categoryList.setSelectedIndex(0);
            }
        }
    }

    private void initFrame() {
        bar.setLayout(new BorderLayout());
        int strut = 10;
        body.add(Box.createVerticalStrut(strut));
        body.add(new JBScrollPane(moduleBox));
        body.add(Box.createVerticalStrut(strut));
        body.add(new JBScrollPane(versionBox));
        body.add(Box.createVerticalStrut(strut));

        body.add(addText);

        operationBtn.add(ADD_VERSION, addVersionBtn);
        operationBtn.add(ADD_MODULE, addModuleBtn);
        operationBtn.add(DELETE_VERSION, deleteVersionBtn);
        operationBtn.add(DELETE_MODULE, deleteModuleBtn);
        body.add(operationBtn);

        bar.add(body);
    }

    private void fillControlContent() {
        moduleBox.add(new SegmentLabel("模块"));
        moduleBox.add(moduleList);

        versionBox.add(new SegmentLabel("版本"));
        versionBox.add(categoryList);
    }

    private void initControlsProp() {
        addText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        moduleList.setModel(moduleModel);
        categoryList.setModel(categoryListModel);
        moduleList.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        categoryList.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }


    private void initControlsEvent() {
        moduleList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    categoryListModel.clear();
                    Module module = moduleList.getSelectedValue();
                    for (Category category : module.getCategories()) {
                        categoryListModel.add(categoryListModel.size(), category);
                    }
                    if (module.getCategories() != null && !module.getCategories().isEmpty()) {
                        categoryList.setSelectedIndex(0);
                    }
                }
            }
        });

        addModuleBtn.addActionListener(this);
        deleteModuleBtn.addActionListener(this);
        addVersionBtn.addActionListener(this);
        deleteVersionBtn.addActionListener(this);
    }

    @Override
    public JComponent getJComponent() {
        return bar;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String text = addText.getText();
        switch (e.getActionCommand()) {
            case ADD_MODULE:
                if (text != null && text.trim().length() > 0) {
                    Module newModule = new Module().setName(text);
                    moduleModel.add(moduleModel.size(), newModule);
                    moduleList.setSelectedIndex(moduleModel.size() - 1);
                    new ModuleAction().persist(newModule);
                }
                break;
            case DELETE_MODULE:
                Module delModule = moduleList.getSelectedValue();
                if (delModule != null) {
                    if (categoryListModel.isEmpty()) {
                        new ModuleAction().delete(delModule);

                        moduleModel.remove(moduleList.getSelectedIndex());

                        if (!moduleModel.isEmpty())
                            moduleList.setSelectedIndex(0);
                    } else {
                        NotifyUtils.notifyWarning("先删除所有Version!");
                    }
                }
                break;
            case ADD_VERSION:
                Module module = moduleList.getSelectedValue();
                if (module == null) {
                    NotifyUtils.notifyWarning("请先选择模块在添加版本");
                    break;
                }
                if (text != null && text.trim().length() > 0) {
                    Category newCategory = new Category().setModule(module).setName(text);
                    module.getCategories().add(newCategory);

                    categoryListModel.add(0, newCategory);
                    categoryList.setSelectedIndex(categoryListModel.getSize() - 1);

                    new CategoryAction().persist(newCategory);
                }
                break;
            case DELETE_VERSION:
                Category category = categoryList.getSelectedValue();
                if (category != null) {
                    new CategoryAction().delete(category);

                    Module m = moduleList.getSelectedValue();
                    m.getCategories().remove(category);
                    categoryListModel.remove(categoryList.getSelectedIndex());
                    if (!categoryListModel.isEmpty()) {
                        categoryList.setSelectedIndex(0);
                    }
                }
                break;
        }
        addText.setText("");
    }
}
