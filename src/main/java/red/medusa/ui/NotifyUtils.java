package red.medusa.ui;

import com.intellij.notification.*;
import red.medusa.intellij.utils.SegmentAppUtils;


public class NotifyUtils {

    private static final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("MySegment Notification Group", NotificationDisplayType.BALLOON, true);

    public static void notifyWarning(String content) {
        Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.WARNING);
        notification.notify(SegmentAppUtils.getProject());
    }

    public static void notifyInfo(String content) {
        Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION);
        notification.notify(SegmentAppUtils.getProject());
    }

    public static void notifyWarning(String content, String helpText, String helpUrl) {
        content = content != null ? content : "";
        helpText = helpText != null ? helpText : "";
        helpUrl = helpUrl != null ? helpUrl : "";
        Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR);
        notification.addAction(new BrowseNotificationAction(helpText, helpUrl));
        notification.notify(SegmentAppUtils.getProject());
    }

    public static void notifyError(String content) {
        content = content != null ? content : "notifyError";
        Notification notification = NOTIFICATION_GROUP.createNotification(content, NotificationType.ERROR);
        Notifications.Bus.notifyAndHide(notification, SegmentAppUtils.getProject());
    }


}