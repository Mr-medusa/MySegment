package red.medusa.utils;

/**
 * @author huguanghui
 * @since 2020/12/03 周四
 */
public class StringUtils {

   public static String canonicalPathName(String branchName) {
        if (branchName == null || branchName.trim().isEmpty())
            return branchName;
        return branchName.trim().replace('\\', '/');
    }
}
