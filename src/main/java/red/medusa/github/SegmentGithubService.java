package red.medusa.github;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import red.medusa.intellij.settings.AppSettingsComponent;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.service.service.SegmentEntityService;
import red.medusa.ui.NotifyUtils;
import red.medusa.utils.StringUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * @author huguanghui
 * @since 2020/11/28 周六
 */
@Slf4j
public class SegmentGithubService {
    private final static String HELP_URL = "https://github.com/Mr-medusa/MySegment";
    private final static String HELP_TITLE = "请先配置相应的配置项";
    private final AppSettingsState settings;
    private Git git;

    private SegmentGithubService() {
        settings = AppSettingsState.getInstance();
        openLocalRepo();
    }

    public void changeForSettings() {
        try {
            if (settings.useGithub) {
                if (settings.githubUrl.trim().length() == 0) {
                    NotifyUtils.notifyWarning("请添加一个Github地址");
                } else if (!hasLocalRepo() && settings.isCloneAvailable()) {
                    // clone一个
                    log.info("clone one...");
                    cloneRepo();
                    clear();
                }
            } else {
                if (!hasLocalRepo() && settings.isInitAvailable()) {
                    // 初始化一个
                    initRepo();
                    clear();
                }
            }

            /*
                切换分支
             */
            if (hasLocalRepo() && settings.isInitAvailable()) {
                String trimBranchName = StringUtils.canonicalPathName(settings.branchName);
                String savePosition = StringUtils.canonicalPathName(settings.localSavePosition);
                String branchFile = savePosition + "/.git/" + (trimBranchName.equals("") ? AppSettingsState.DEFAULT_BRANCH_NAME : trimBranchName);
                log.info("discover branch file location at: {}", branchFile);
                // 假设仓库是第一次初始化
                assumeEmptyRepoThenCreateOne();
                Ref checkout;
                // 是否需要检出分支
                if (!this.getCurrentBranch().equals(canonicalBranchName(trimBranchName))) {
                    // 释放数据库连接
                    SegmentEntityService.getInstance().finishService();
                    // 检出之前先提交
                    this.commitFile();
                    // 本地已经有了直接检出
                    if (new File(branchFile).exists()) {
                        checkout = checkout(trimBranchName, false);
                    } else {
                        checkout = checkout(trimBranchName, true);
                    }
                    log.info("checkout and current branch is: {}", checkout);
                }
            }
        } catch (InvalidRemoteException e1) {       // clone
            NotifyUtils.notifyWarning(HELP_TITLE, "没有添加的初始化仓库配置项", HELP_URL);
            e1.printStackTrace();
        } catch (RefAlreadyExistsException e2) {    // checkout
            e2.printStackTrace();
            NotifyUtils.notifyWarning(HELP_TITLE, "请检出本地有效的分支路径!", HELP_URL);
        } catch (Exception error) {
            NotifyUtils.notifyWarning(HELP_TITLE, error.getMessage(), HELP_URL);
            error.printStackTrace();
        }

    }

    public void openLocalRepo() {
        try {
            Repository repository = openLocalRepository();
            log.info("MySegment repository has already exists!");
            git = Git.wrap(repository);
        } catch (Exception e) {
            log.info("MySegment repository is not exists!");
            NotifyUtils.notifyWarning("打开本地仓库失败,可能仓库不存在或初始化仓库为非空目录,请配置后重试!", "help", HELP_URL);
        }
    }

    public Repository openLocalRepository() throws Exception {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        return repositoryBuilder
                .setGitDir(new File(settings.localSavePosition + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();
    }

    public void cloneRepo() throws Exception {
        this.git = this.runInSpecialClassLoader(
                () -> Git.cloneRepository()
                        .setURI(settings.githubUrl)
                        .setDirectory(new File(settings.localSavePosition))
                        .call());
    }

    public Ref checkout(String branchName, boolean isNeedCreate) throws Exception {
        log.info("checkout to -> {}", branchName);
        return runInSpecialClassLoader(() ->
                git.checkout()
                        .setName(canonicalBranchName(branchName))
                        .setCreateBranch(isNeedCreate)
                        .call());
    }

    public Set<String> getAllBranchForTest(String githubUrl) throws Exception {
        return runInSpecialClassLoader(
                () -> {
                    Set<String> branchNameResult = findLocalBranchNames();

                    branchNameResult.addAll(findRemoteBranchNames(githubUrl));

                    if (branchNameResult.isEmpty())
                        branchNameResult.add(AppSettingsState.DEFAULT_BRANCH_NAME);

                    log.info("all local branch name has : {}", branchNameResult.toString());

                    return branchNameResult;
                });
    }

    public String getCurrentBranch() throws Exception {
        if (hasLocalRepo()) {
            return this.runInSpecialClassLoader(() -> git.getRepository().getFullBranch());
        }
        return "";
    }

    public DirCache add(String file) throws Exception {
        return git.add().addFilepattern(file).call();
    }

    public DirCache rm(String file) throws Exception {
        return git.rm().addFilepattern(file).call();
    }

    // 表示是否可以push,没有改变就不用push
    public boolean commitFile() throws Exception {
        return this.runInSpecialClassLoader(() -> {
            Map<String, Set<String>> info = new HashMap<>();
            Set<String> addOrUpdate = new HashSet<>();
            Set<String> removed = new HashSet<>();
            info.put("addOrUpdate", addOrUpdate);
            info.put("removed", removed);
            Status status = git.status().call();
            // 忽略的文件
            Set<String> notNeedCommitFiles = new HashSet<>(status.getIgnoredNotInIndex());
            log.info("not need commit files = {}", notNeedCommitFiles);

            boolean mayBeNeedPush = false;
            // 新增的
            for (String adding : status.getUntracked()) {
                if (!notNeedCommitFiles.contains(adding)) {
                    this.add(adding);
                    addOrUpdate.add(adding);
                    mayBeNeedPush = true;
                }
            }
            // 修改或删除的
            if (status.hasUncommittedChanges()) {
                for (String modified : status.getModified()) {
                    this.add(modified);
                    addOrUpdate.add(modified);
                }
                for (String modified : status.getChanged()) {
                    this.add(modified);
                    addOrUpdate.add(modified);
                }
                for (String missing : status.getMissing()) {
                    this.rm(missing);
                    removed.add(missing);
                }
                for (String missing : status.getRemoved()) {
                    this.rm(missing);
                    removed.add(missing);
                }
                mayBeNeedPush = true;
            }

            log.info("need commit files : {}", info);

            if (!info.isEmpty())
                git.commit()
                        .setAmend(true) // 避免太多历史记录
                        .setMessage("Committed by segment plugin")
                        .call();

            // 正确的提交了内容才可以push
            return mayBeNeedPush && git.status().call().getConflicting().isEmpty();
        });
    }

    public void push() throws Exception {
        this.runInSpecialClassLoader(() -> {
            Iterable<PushResult> pushResults = git
                    .push()
                    .setPushAll()
                    .setForce(true) // 之前使用了 amend 提交,因此这里使用强制推送
                    .call();
            for (PushResult pushResult : pushResults) {
                log.info("push result = {}", pushResult.getMessages());
                Collection<RemoteRefUpdate> remoteUpdates = pushResult.getRemoteUpdates();
                for (RemoteRefUpdate remoteUpdate : remoteUpdates) {
                    log.info("remote name = {},remote update = {}", remoteUpdate.getRemoteName(), remoteUpdate.getStatus());
                }
            }
            return pushResults;
        });
    }

    public boolean hasLocalRepo() {
        return new File(settings.localSavePosition + "/.git").exists();
    }

    public void initRepo() throws Exception {
        this.git = runInSpecialClassLoader(() -> Git
                .init()
                .setDirectory(new File(settings.localSavePosition))
                .call());
    }

    public void clear() throws Exception {
        runInSpecialClassLoader(() -> {
            if (this.git != null)
                this.git.close();
            return null;
        });

    }

    /*
        test
     */
    public AppSettingsComponent.TestGithubUrlResult testRemoteUrlAvailability(String githubUrl) {
        try {
            // 测试后将值还原回来
            AppSettingsComponent.TestGithubUrlResult testFetch = testFetch(githubUrl);
            return testFetch;
        } catch (Exception e) {
            return new AppSettingsComponent.TestGithubUrlResult(false, Collections.EMPTY_SET);
        }
    }

    private AppSettingsComponent.TestGithubUrlResult testFetch(String githubUrl) throws Exception {
        return this.runInSpecialClassLoader(() -> {
            try {
                Set<String> branchNames = getAllBranchForTest(githubUrl);
                log.info("测试连接远程仓库并获得分支: {}", branchNames);
                return new AppSettingsComponent.TestGithubUrlResult(true, branchNames);
            } catch (Exception e) {
                return new AppSettingsComponent.TestGithubUrlResult(true, Collections.EMPTY_SET);
            }
        });
    }

    public void assumeEmptyRepoThenCreateOne() {
        String gitDir = settings.localSavePosition + "/.git/refs/heads";
        if (new File(gitDir).list().length == 0) {
            try {
                git.commit().setMessage("Initial commit by segment plugin...").call();
            } catch (GitAPIException e) {
                NotifyUtils.notifyWarning("初始化仓库失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static String canonicalBranchName(String branchName) {
        if (branchName == null || branchName.trim().isEmpty())
            return branchName;
        branchName = branchName.trim().replace('\\', '/');
        int i = branchName.lastIndexOf("/");
        if (i == -1)
            return "master";
        return branchName.substring(i + 1);
    }

    /*
        singleton implements
     */
    private static class SingletonClassInstance {
        private static final SegmentGithubService instance = new SegmentGithubService();
    }

    public static SegmentGithubService getInstance() {
        return SingletonClassInstance.instance;
    }

    /*
        class loader
     */

    public <T> T runInSpecialClassLoader(Callable<T> callable) throws Exception {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        Object result = callable.call();
        Thread.currentThread().setContextClassLoader(current);
        return (T) result;
    }


    //------------------------------------------------------------------------------------
    public Set<String> findRemoteBranchNames(String githubUrl) throws Exception {
        return this.runInSpecialClassLoader(() -> {
            LsRemoteCommand lsRemoteCommand = Git.lsRemoteRepository();
            lsRemoteCommand.setRemote(githubUrl);
            final Map<String, Ref> map = lsRemoteCommand.setHeads(true).callAsMap();
            Set<String> branchNames = new LinkedHashSet<>();
            for (Map.Entry<String, Ref> entry : map.entrySet()) {
                branchNames.add(entry.getKey());
            }
            return branchNames;
        });
    }

    public Set<String> findLocalBranchNames() throws Exception {
        if (settings.localSavePosition != null && !settings.localSavePosition.trim().isEmpty()) {
            return this.runInSpecialClassLoader(() -> {
                String prefix = "refs/heads/";
                Set<String> localhostBranchNames = new LinkedHashSet<>();
                File file = new File(settings.localSavePosition + "/.git/refs/heads");
                if (file.exists()) {
                    for (String fileName : Objects.requireNonNull(file.list())) {
                        localhostBranchNames.add(prefix + fileName);
                    }
                }
                return localhostBranchNames;
            });
        } else {
            return new LinkedHashSet<>(Collections.singleton(AppSettingsState.DEFAULT_BRANCH_NAME));
        }
    }
}


















