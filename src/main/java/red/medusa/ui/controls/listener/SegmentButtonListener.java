package red.medusa.ui.controls.listener;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBList;
import com.twelvemonkeys.util.LinkedSet;
import lombok.extern.slf4j.Slf4j;
import red.medusa.service.entity.Img;
import red.medusa.service.entity.Segment;
import red.medusa.service.entity.Url;
import red.medusa.ui.NotifyUtils;
import red.medusa.ui.SegmentAddOrEdit;
import red.medusa.ui.context.SegmentContextHolder;
import red.medusa.ui.controls.SegmentLabel2;
import red.medusa.ui.controls.SegmentTextField;
import red.medusa.ui.renderer.SegmentImgListCellRenderer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author huguanghui
 * @since 2020/12/01 周二
 */
@Slf4j
public class SegmentButtonListener implements ActionListener, DebounceWorkAction {
    public static final String ADD = "添加";
    public static final String DEL = "删除";
    public static final String ADD_URL_COMMAND = "ADD_URL_COMMAND";
    public static final String DEL_URL_COMMAND = "DEL_URL_COMMAND";
    public static final String ADD_IMG_COMMAND = "ADD_IMG_COMMAND";
    public static final String DEL_IMG_COMMAND = "DEL_IMG_COMMAND";
    private final DefaultListModel<Url> urlListModel;
    private final DefaultListModel<Img> imgListModel;
    private final List<VirtualFile> tempImgListModel;
    private final SegmentTextField urlTextField;
    private final SegmentLabel2 imgLabelField;
    private final JBList<Url> urlListForAdd;
    private final JBList<Img> imgListForAdd;

    private final SegmentImgListCellRenderer cellRenderer;

    public SegmentButtonListener(DefaultListModel<Url> urlListModel,
                                 DefaultListModel<Img> imgListModel,
                                 List<VirtualFile> tempImgListModel,
                                 SegmentTextField urlTextField,
                                 SegmentLabel2 imgLabelField,
                                 JBList<Url> urlListForAdd,
                                 JBList<Img> imgListForAdd,
                                 SegmentImgListCellRenderer cellRenderer) {
        this.urlListModel = urlListModel;
        this.imgListModel = imgListModel;
        this.tempImgListModel = tempImgListModel;
        this.urlTextField = urlTextField;
        this.imgLabelField = imgLabelField;
        this.urlListForAdd = urlListForAdd;
        this.imgListForAdd = imgListForAdd;
        this.cellRenderer = cellRenderer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case ADD_URL_COMMAND:
                String text = urlTextField.getText();
                if (text != null && text.trim().length() > 0) {
                    Url url = new Url().setUrl(text);
                    urlListModel.add(urlListModel.getSize(), url);

                    this.addUrl(url);
                }
                urlTextField.setText("");
                break;
            case ADD_IMG_COMMAND:
                Set<Img> list = new LinkedSet<>();
                for (VirtualFile file : tempImgListModel) {
                    try {
                        InputStream inputStream = file.getInputStream();
                        byte[] imgBytes = new byte[inputStream.available()];
                        inputStream.read(imgBytes, 0, imgBytes.length);
                        Img img = new Img().setImage(imgBytes);
                        imgListModel.addElement(img);

                        //  写入数据库
                        list.add(img);
                    } catch (IOException ioException) {
                        NotifyUtils.notifyWarning("加载图片异常: " + ioException.getMessage());
                    }
                }
                imgLabelField.setText(SegmentAddOrEdit.IMG_LABEL);

                /*
                    写入数据库
                 */
                addImg(list);
                break;
            case DEL_URL_COMMAND:
                int urlIndex = urlListForAdd.getSelectedIndex();
                if (urlIndex != -1) {
                    Url url = urlListForAdd.getSelectedValue();
                    if (this.removeUrl(url, urlIndex) == 1)
                        urlListModel.remove(urlIndex);
                }
                break;
            case DEL_IMG_COMMAND:
                int imgIndex = imgListForAdd.getSelectedIndex();
                if (imgIndex != -1) {
                    Img img = imgListForAdd.getSelectedValue();
                    if (this.removeImg(img,imgIndex) == 1) {
                        cellRenderer.removeByIndex(imgIndex);
                        imgListModel.remove(imgIndex);
                    }
                }
                break;
        }
    }

    public void addUrl(Url url) {
        Segment segment = SegmentContextHolder.getSegment();
        Set<Url> urls = segment.getUrls();
        if (urls == null) {
            segment.setUrls(new LinkedSet<>()).getUrls().add(url);
        } else {
            segment.getUrls().add(url);
        }
        this.work();
    }

    public void addImg(Set<Img> imgList) {
        Segment segment = SegmentContextHolder.getSegment();
        Set<Img> imgs = segment.getImgs();
        if (imgs == null) {
            segment.setImgs(imgList);
        } else {
            segment.getImgs().addAll(imgList);
        }
        this.work();
    }

    public int removeUrl(Url url, int index) {
        Segment segment = SegmentContextHolder.getSegment();
        Set<Url> urls = segment.getUrls();
        if (url.getId() == null) {
            NotifyUtils.notifyInfo("链接还未保存成功,请刷新后重试!");
            return -1;
        }
        if (urls != null) {
            Iterator<Url> iterator = urls.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                iterator.next();
                if (i++ == index) {
                    iterator.remove();
                    break;
                }
            }
            return segmentEntityService.delete(url);
        }
        return -1;
    }

    public int removeImg(Img img,int index) {
        Segment segment = SegmentContextHolder.getSegment();
        Set<Img> imgs = segment.getImgs();
        if (img.getId() == null) {
            NotifyUtils.notifyInfo("图片还未保存成功,请稍后重试!");
            return -1;
        }

        if (imgs != null) {
            Iterator<Img> iterator = imgs.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                iterator.next();
                if (i++ == index) {
                    iterator.remove();
                    break;
                }
            }
            return segmentEntityService.delete(img);
        }
        return -1;
    }

}
















