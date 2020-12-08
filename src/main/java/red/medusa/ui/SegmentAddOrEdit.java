package red.medusa.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.ContentManager;
import lombok.extern.slf4j.Slf4j;
import red.medusa.intellij.ui.SegmentComponent;
import red.medusa.intellij.utils.SegmentAppUtils;
import red.medusa.service.entity.*;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.*;
import red.medusa.ui.controls.listener.*;
import red.medusa.ui.controls.img.SegmentImgListPanel;
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
    private final ComboBox<Version> versionComboBox = new ComboBox<>();

    private final ComboBox<LangType> dependenceSyntaxComboBox = new ComboBox<>();
    private final ComboBox<LangType> contentSyntaxComboBox = new ComboBox<>();

    private final SegmentTextField nameTextField = new SegmentTextField();
    private final JTextArea descTextField = new JTextArea(3, 5);

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++      Editor            ++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private EditorTextField contentTextArea = new SegmentEditorTextField("", SegmentAppUtils.getProject(), null, new Dimension(0, 100));
    private EditorTextField dependenceTextArea = new SegmentEditorTextField("", SegmentAppUtils.getProject(), null, new Dimension(0, 100));

    /*
        layout
     */
    public final static String COMBOBOX_FIRST_SELECT = " -请选择- ";
    // 名字
    private final Box nameBox = Box.createHorizontalBox();
    // 模块版本
    private final JPanel versionModuleBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
    // 描述
    private final Box descBox = Box.createHorizontalBox();
    // 链接
    private final Box urlBox = Box.createHorizontalBox();
    private final Box urlListWithAddBox = Box.createVerticalBox();
    // 图片
    private final Box imgBox = Box.createHorizontalBox();
    private final Box imgListWithAddBox = Box.createVerticalBox();
    // 类型
    private final Box syntaxBox = Box.createHorizontalBox();
    private final JPanel syntaxFlow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    // 依赖
    private final Box dependenceBox = Box.createHorizontalBox();
    // 内容
    private final Box contentBox = Box.createHorizontalBox();

    /*
        listener
     */
    private SegmentModuleVersionListener moduleVersionItemListener;
    private DependenceSyntaxComboListener dependenceSyntaxComboListener;
    private ContentSyntaxComboListener contentSyntaxComboListener;
    private NameTextFieldListener nameTextFieldListener;
    private DescTextFieldListener descTextFieldListener;

    private DependenceTextAreaListener dependenceTextAreaListener;
    private ContentTextAreaListener contentTextAreaListener;

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
        body.add(Box.createVerticalStrut(strut));
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

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(versionModuleBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(syntaxBox);


        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(contentBox);

        body.add(Box.createVerticalStrut(strut));
        body.add(new JSeparator());
        body.add(dependenceBox);


    }

    private void fillFrameContent() {
        nameBox.add(new SegmentLabel("名字"));
        nameBox.add(nameTextField);

        descBox.add(new SegmentLabel("描述"));
        descBox.add(descTextField);

        int labelWidth = 70;
        int labelHeight = 30;
        versionModuleBox.add(new SegmentLabel("模块", labelWidth, labelHeight));
        versionModuleBox.add(moduleComboBox);
        versionModuleBox.add(new SegmentLabel("版本", labelWidth, labelHeight));
        versionModuleBox.add(versionComboBox);

        syntaxFlow.add(new SegmentLabel("依赖类型", labelWidth, labelHeight));
        syntaxFlow.add(dependenceSyntaxComboBox);
        syntaxFlow.add(new SegmentLabel("内容类型", labelWidth, labelHeight));
        syntaxFlow.add(contentSyntaxComboBox);
        syntaxBox.add(syntaxFlow);

        dependenceBox.add(new SegmentLabel("依赖"));
        dependenceBox.add(dependenceTextArea);

        contentBox.add(new SegmentLabel("内容"));
        contentBox.add(contentTextArea);


        /*
            URL 列表
         */
        JPanel urlPanel = new JPanel(new GridLayout(1, 1));

        urlPanel.add(urlListForAdd);
        /*
            Img 列表
         */
        JPanel imgListPanel = new JPanel(new GridLayout(1, 1));

//        imgListPanel.add(imgListForAdd);

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
        imgListWithAddBox.add(Box.createVerticalStrut(5));
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
        versionComboBox.removeAllItems();
        moduleComboBox.addItem(new Module().setName(COMBOBOX_FIRST_SELECT));
        versionComboBox.addItem(new Version().setName(COMBOBOX_FIRST_SELECT));
        List<Module> modules = new ModuleAction().list();
        int i = 1;
        for (Module module : modules) {
            moduleComboBox.addItem(module);
            if (segment.getId() != null && segment.getModule().getId().equals(module.getId())) {
                moduleComboBox.setSelectedIndex(i);
                // 版本从当前模块获取
                int j = 1;
                for (Version version : segment.getModule().getVersions()) {
                    versionComboBox.addItem(version);
                    if (segment.getVersion().getId().equals(version.getId()))
                        versionComboBox.setSelectedIndex(j);
                    j++;
                }
            }
            i++;
        }
        if (segment.getId() == null) {
            if (modules.size() > 0) {
                moduleComboBox.setSelectedIndex(0);
                List<Version> versions = modules.get(0).getVersions();
                if (!versions.isEmpty()) {
                    for (Version version : versions) {
                        versionComboBox.addItem(version);
                    }
                    versionComboBox.setSelectedIndex(0);
                }
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
        /*
            类型
         */
        dependenceSyntaxComboBox.removeAllItems();
        contentSyntaxComboBox.removeAllItems();
        dependenceSyntaxComboBox.addItem(LangType.COMBOBOX_FIRST_SELECT);
        contentSyntaxComboBox.addItem(LangType.COMBOBOX_FIRST_SELECT);
        int j = 1;
        for (LangType value : LangType.values()) {
            if (value == LangType.COMBOBOX_FIRST_SELECT)
                continue;
            dependenceSyntaxComboBox.addItem(value);
            contentSyntaxComboBox.addItem(value);

            if (segment.getLangDep() == value) {
                dependenceSyntaxComboBox.setSelectedIndex(j);
            }

            if (segment.getLangContent() == value) {
                contentSyntaxComboBox.setSelectedIndex(j);
            }
            j++;
        }
        /*
            内容 和 依赖
         */
        String langDep = segment.getLangDep() != null ? segment.getLangDep().name() : "java";
        String langContent = segment.getLangDep() != null ? segment.getLangContent().name() : "java";

        dependenceBox.remove(dependenceTextArea);
        contentBox.remove(contentTextArea);

        dependenceTextArea = new SegmentEditorTextField(segment.getDependence(), SegmentAppUtils.getProject(), langDep, new Dimension(0, 150));
        contentTextArea = new SegmentEditorTextField(segment.getContent(), SegmentAppUtils.getProject(), langContent, new Dimension(0, 300));

        dependenceBox.add(dependenceTextArea);
        contentBox.add(contentTextArea);

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
        versionComboBox.setMaximumSize(new Dimension(w, h));
        moduleComboBox.setPreferredSize(new Dimension(w, h));
        versionComboBox.setPreferredSize(new Dimension(w, h));

        dependenceSyntaxComboBox.setMaximumSize(new Dimension(w, h));
        contentSyntaxComboBox.setMaximumSize(new Dimension(w, h));
        dependenceSyntaxComboBox.setPreferredSize(new Dimension(w, h));
        contentSyntaxComboBox.setPreferredSize(new Dimension(w, h));

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
        moduleVersionItemListener = new SegmentModuleVersionListener(versionComboBox);
        moduleComboBox.addItemListener(moduleVersionItemListener);
        versionComboBox.addItemListener(moduleVersionItemListener);

        /*
            语法类型
         */
        dependenceSyntaxComboListener = new DependenceSyntaxComboListener(dependenceTextArea);
        contentSyntaxComboListener = new ContentSyntaxComboListener(contentTextArea);
        dependenceSyntaxComboBox.addItemListener(dependenceSyntaxComboListener);
        contentSyntaxComboBox.addItemListener(contentSyntaxComboListener);

        /*
            名字 描述 依赖
         */
        descTextFieldListener = new DescTextFieldListener(descTextField);
        nameTextFieldListener = new NameTextFieldListener(nameTextField);
        nameTextField.getDocument().addDocumentListener(nameTextFieldListener);
        descTextField.getDocument().addDocumentListener(descTextFieldListener);

        /*
            依赖 内容
         */
        dependenceTextAreaListener = new DependenceTextAreaListener(dependenceTextArea);
        contentTextAreaListener = new ContentTextAreaListener(contentTextArea);
        dependenceTextArea.getDocument().addDocumentListener(dependenceTextAreaListener);
        contentTextArea.getDocument().addDocumentListener(contentTextAreaListener);
    }

    private void removeControlEvent() {
        /*
            模块 版本
         */
        moduleComboBox.removeItemListener(moduleVersionItemListener);
        versionComboBox.removeItemListener(moduleVersionItemListener);

        /*
            依赖类型 内容类型
         */
        dependenceSyntaxComboBox.removeItemListener(dependenceSyntaxComboListener);
        contentSyntaxComboBox.removeItemListener(contentSyntaxComboListener);

        /*
            名字 描述 依赖
         */
        nameTextField.getDocument().removeDocumentListener(nameTextFieldListener);
        descTextField.getDocument().removeDocumentListener(descTextFieldListener);

        /*
            依赖 内容
         */
        if (dependenceTextAreaListener != null)
            dependenceTextArea.getDocument().removeDocumentListener(dependenceTextAreaListener);
        if (contentTextAreaListener != null)
            contentTextArea.getDocument().removeDocumentListener(contentTextAreaListener);
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
        versionComboBox.removeAllItems();
        moduleComboBox.addItem(new Module().setName(COMBOBOX_FIRST_SELECT));
        versionComboBox.addItem(new Version().setName(COMBOBOX_FIRST_SELECT));
        for (Module module : modules) {
            moduleComboBox.addItem(module);
        }
        if (modules.size() > 0) {
            moduleComboBox.setSelectedIndex(0);
            for (Version version : modules.get(0).getVersions()) {
                versionComboBox.addItem(version);
            }
            if (modules.get(0).getVersions().size() > 0) {
                versionComboBox.setSelectedIndex(0);
            }
        }

        dependenceSyntaxComboBox.setSelectedIndex(0);
        contentSyntaxComboBox.setSelectedIndex(0);

        nameTextField.setText("");
        descTextField.setText("");

        contentTextArea.setText("");
        dependenceTextArea.setText("");

        body.repaint();
        scrollPane.scrollRectToVisible(new Rectangle(0, 0));

        addControlsEvent();
    }

    @Override
    public JComponent getJComponent() {
        return scrollPane;
    }

}
