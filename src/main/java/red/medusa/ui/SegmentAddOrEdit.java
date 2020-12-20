package red.medusa.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.ContentManager;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.service.entity.Category;
import red.medusa.service.entity.Module;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Url;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.SegmentImageBrowserButton;
import red.medusa.ui.controls.SegmentLabel;
import red.medusa.ui.controls.SegmentLabel2;
import red.medusa.ui.controls.SegmentTextField;
import red.medusa.ui.controls.content.ContentPanelList;
import red.medusa.ui.controls.img.SegmentImgListPanel;
import red.medusa.ui.controls.listener.*;
import red.medusa.ui.segment_action.ModuleAction;
import red.medusa.ui.segment_action.SegmentAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author huguanghui
 * @since 2020/11/25 周三
 */
@Slf4j
public class SegmentAddOrEdit extends MouseAdapter implements SegmentComponent {
    private final Box body = Box.createVerticalBox();
    private final JBScrollPane scrollPane = new JBScrollPane(body);

    /*
        model
     */
    private final DefaultListModel<Url> urlListModel = new DefaultListModel<>();
    private final JBList<Url> urlListForAdd = new JBList<>(urlListModel);

    private final SegmentImgListPanel segmentImgListPanel = new SegmentImgListPanel();

    public static final String IMG_LABEL = "请选择图片...  ";
    private final List<VirtualFile> tempImgListModel = new ArrayList<>();
    private final SegmentLabel2 imgLabelField = new SegmentLabel2(IMG_LABEL, true);
    private final SegmentTextField urlTextField = new SegmentTextField();
    private final JButton addUrlBtn = new JButton(SegmentButtonListener.ADD);
    private final JButton addImgBtn = new JButton(SegmentButtonListener.ADD);
    private final JButton delUrlBtn = new JButton(SegmentButtonListener.DEL);
    private final JButton delImgBtn = new JButton(SegmentButtonListener.DEL);

    private final ComboBox<Module> moduleComboBox = new ComboBox<>();
    private final ComboBox<Category> categoryComboBox = new ComboBox<>();

    private final SegmentTextField nameTextField = new SegmentTextField();
    private final JTextArea descTextField = new JTextArea();

    /*
        layout
     */
    public final static String COMBOBOX_FIRST_SELECT = " -请选择- ";
    // 名字
    private final Box nameBox = Box.createHorizontalBox();
    // 模块版本
    private final JPanel ModuleCategoryBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
    // 描述
    private final Box descBox = Box.createHorizontalBox();
    // 链接
    private final Box urlBox = Box.createHorizontalBox();
    private final Box urlListWithAddBox = Box.createVerticalBox();
    // 图片
    private final Box imgBox = Box.createHorizontalBox();
    private final Box imgListWithAddBox = Box.createVerticalBox();

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      Editor            ++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private final ContentPanelList contentPanelList = new ContentPanelList();

    /*
        listener
     */
    private SegmentModuleCategoryListener moduleVersionItemListener;

    private NameTextFieldListener nameTextFieldListener;
    private DescTextFieldListener descTextFieldListener;


    public SegmentAddOrEdit() {
        /*
            Prop
         */
        initControlsProp();
        /*
            view
         */
        fillFrameContent();
        /*
            Layout
         */
        initFrame();

        /*
            Event
         */
        addImmutableControlsEvent();

    }

    private void initFrame() {
        int strut = 10;
        body.add(new JSeparator());
        body.add(nameBox);
        body.add(Box.createVerticalStrut(strut));
        body.add(descBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(urlBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(imgBox);

        body.add(new JSeparator());
        body.add(Box.createVerticalStrut(strut));
        body.add(ModuleCategoryBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SegmentLabel segmentLabel = new SegmentLabel("内容"){
            @Override
            public int getVerticalAlignment() {
                return SwingConstants.CENTER;
            }
        };
        jPanel.add(segmentLabel);
        body.add(jPanel);
        body.add(new JSeparator());
        body.add(contentPanelList);
        body.add(Box.createVerticalGlue());
    }

    private void fillFrameContent() {
        nameBox.add(new SegmentLabel("名字", 60, 30));
        nameBox.add(nameTextField);

        descBox.add(new SegmentLabel("描述"));
        descBox.add(descTextField);

        int labelWidth = 70;
        int labelHeight = 30;
        ModuleCategoryBox.add(new SegmentLabel("模块", labelWidth, labelHeight));
        ModuleCategoryBox.add(moduleComboBox);
        ModuleCategoryBox.add(new SegmentLabel("分类", labelWidth, labelHeight));
        ModuleCategoryBox.add(categoryComboBox);
        /*
            URL 列表
         */
        JPanel urlPanel = new JPanel(new GridLayout(1, 1));

        urlPanel.add(urlListForAdd);
        /*
            Img 列表
         */
        JPanel imgListPanel = new JPanel(new GridLayout(1, 1));
        imgListPanel.add(segmentImgListPanel);

        this.urlBox.add(new SegmentLabel("链接"));
        Box urlAddUrlBox = Box.createHorizontalBox();
        urlAddUrlBox.add(urlTextField);
        urlAddUrlBox.add(addUrlBtn);
        urlAddUrlBox.add(delUrlBtn);
        urlListWithAddBox.add(urlPanel);
        urlListWithAddBox.add(Box.createVerticalStrut(5));
        urlListWithAddBox.add(urlAddUrlBox);
        this.urlBox.add(urlListWithAddBox);

        imgBox.add(new SegmentLabel("图片"));
        Box imgAddUrlBox = Box.createHorizontalBox();
        imgAddUrlBox.add(imgLabelField);
        imgAddUrlBox.add(addImgBtn);
        imgAddUrlBox.add(delImgBtn);
        imgListWithAddBox.add(imgListPanel);
        imgListWithAddBox.add(imgAddUrlBox);
        imgBox.add(imgListWithAddBox);
    }

    /**
     * @see red.medusa.intellij.ui.SegmentToolWindowFactory#initListener(ContentManager)
     */
    @Override
    public void refresh() {
        removeControlEvent();

        Segment segment = new SegmentAction().find();

        nameTextField.setText(segment.getName());
        descTextField.setText(segment.getDescription());

        /*
            模块 版本
         */
        moduleComboBox.removeAllItems();
        categoryComboBox.removeAllItems();
        moduleComboBox.addItem(new Module().setName(COMBOBOX_FIRST_SELECT));
        categoryComboBox.addItem(new Category().setName(COMBOBOX_FIRST_SELECT));
        List<Module> modules = new ModuleAction().list();
        int i = 1;
        for (Module module : modules) {
            moduleComboBox.addItem(module);
            if (segment.getId() != null && segment.getModule().getId().equals(module.getId())) {
                moduleComboBox.setSelectedIndex(i);
                // 版本从当前模块获取
                int j = 1;
                for (Category category : module.getCategories()) {
                    categoryComboBox.addItem(category);
                    if (segment.getCategory() !=null && segment.getCategory().getId().equals(category.getId()))
                        categoryComboBox.setSelectedIndex(j);
                    j++;
                }
            }
            i++;
        }
        if (segment.getId() == null) {
            if (modules.size() > 0) {
                moduleComboBox.setSelectedIndex(0);
                categoryComboBox.setSelectedIndex(0);
            }
        }

        /*
            图片 url
         */
        urlListModel.removeAllElements();
        segmentImgListPanel.clearAllImg();
        if (segment.getUrls() != null)
            segment.getUrls().forEach(urlListModel::addElement);
        if (segment.getImgs() != null)
            segmentImgListPanel.addImgs(segment.getImgs());

        contentPanelList.refresh();

        addControlsEvent();
    }

    private void initControlsProp() {
        addUrlBtn.setActionCommand(SegmentButtonListener.ADD_URL_COMMAND);
        delUrlBtn.setActionCommand(SegmentButtonListener.DEL_URL_COMMAND);
        addImgBtn.setActionCommand(SegmentButtonListener.ADD_IMG_COMMAND);
        delImgBtn.setActionCommand(SegmentButtonListener.DEL_IMG_COMMAND);

        int h = 30;
        int w = 150;
        moduleComboBox.setMaximumSize(new Dimension(w, h));
        categoryComboBox.setMaximumSize(new Dimension(w, h));
        moduleComboBox.setPreferredSize(new Dimension(w, h));
        categoryComboBox.setPreferredSize(new Dimension(w, h));

        urlListForAdd.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /*
        Event
     */
    private void addImmutableControlsEvent() {
        /*
            img and url
         */
        /*
        Event
     */
        ActionListener urlImgButtonListener = new SegmentButtonListener(
                urlListModel,
                tempImgListModel,
                urlTextField,
                imgLabelField,
                urlListForAdd,
                segmentImgListPanel
        );
        addUrlBtn.addActionListener(urlImgButtonListener);
        addImgBtn.addActionListener(urlImgButtonListener);
        delUrlBtn.addActionListener(urlImgButtonListener);
        delImgBtn.addActionListener(urlImgButtonListener);

        imgLabelField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SegmentImageBrowserButton(imgLabelField, tempImgListModel);
            }
        });

        body.addMouseListener(new SegmentClearPopupEventListener(this));
    }

    private void addControlsEvent() {
        /*
            模块 版本
         */
        moduleVersionItemListener = new SegmentModuleCategoryListener(categoryComboBox);
        moduleComboBox.addItemListener(moduleVersionItemListener);
        categoryComboBox.addItemListener(moduleVersionItemListener);

        /*
            名字 描述 依赖
         */
        descTextFieldListener = new DescTextFieldListener(descTextField);
        nameTextFieldListener = new NameTextFieldListener(nameTextField);
        nameTextField.getDocument().addDocumentListener(nameTextFieldListener);
        descTextField.getDocument().addDocumentListener(descTextFieldListener);
    }

    private void removeControlEvent() {
        /*
            模块 版本
         */
        moduleComboBox.removeItemListener(moduleVersionItemListener);
        categoryComboBox.removeItemListener(moduleVersionItemListener);

        /*
            名字 描述 依赖
         */
        nameTextField.getDocument().removeDocumentListener(nameTextFieldListener);
        descTextField.getDocument().removeDocumentListener(descTextFieldListener);
    }

    public void clear() {
        SegmentContextHolder.setSegment(new Segment());
        removeControlEvent();

        urlListModel.removeAllElements();
        segmentImgListPanel.clearAllImg();
        imgLabelField.setText(IMG_LABEL);
        urlTextField.setText("");

        List<Module> modules = new ModuleAction().list();

        moduleComboBox.removeAllItems();
        categoryComboBox.removeAllItems();
        moduleComboBox.addItem(new Module().setName(COMBOBOX_FIRST_SELECT));
        categoryComboBox.addItem(new Category().setName(COMBOBOX_FIRST_SELECT));
        for (Module module : modules) {
            moduleComboBox.addItem(module);
        }
        if (modules.size() > 0) {
            moduleComboBox.setSelectedIndex(0);
            for (Category category : modules.get(0).getCategories()) {
                categoryComboBox.addItem(category);
            }
            if (modules.get(0).getCategories().size() > 0) {
                categoryComboBox.setSelectedIndex(0);
            }
        }

        nameTextField.setText("");
        descTextField.setText("");

        contentPanelList.refresh();

        body.repaint();

        scrollPane.scrollRectToVisible(new Rectangle(0, 0));

        addControlsEvent();
    }

    @Override
    public JComponent getJComponent() {
        return scrollPane;
    }

}
