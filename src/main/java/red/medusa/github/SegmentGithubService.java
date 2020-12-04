package red.medusa.github;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import red.medusa.intellij.settings.AppSettingsComponent;
import red.medusa.intellij.settings.AppSettingsState;
import red.medusa.ui.NotifyUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
                } else {
                    NotifyUtils.notifyWarning(HELP_TITLE, "没有添加克隆配置项", HELP_URL);
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
                String trimBranchName = settings.branchName.trim();
                String savePosition = settings.localSavePosition.trim();
                String branchFile = savePosition + "/.git/" + (trimBranchName.equals("") ? AppSettingsState.DEFAULT_BRANCH_NAME : trimBranchName);
                log.info("discover branch file location at: {}", branchFile);
                Ref checkout;
                if (new File(branchFile).exists()) {    // 本地已经有了直接检出
                    checkout = checkout(trimBranchName, false);
                    log.info("checkout and current branch is: {}", checkout);
                } else {
                    if (isEmptyRepo())
                        git.commit().setMessage("Initial commit by segment plugin...").call();
                }
            }
        } catch (InvalidRemoteException e1) {       // clone
            NotifyUtils.notifyWarning(HELP_TITLE, "没有添加的初始化仓库配置项", HELP_URL);
            e1.printStackTrace();
        } catch (RefAlreadyExistsException e2) {    // checkout
            // e2.printStackTrace();
            // NotifyUtils.notifyWarning(HELP_TITLE, "分支已存在", HELP_URL);
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

    public List<String> getRemoteBranch() throws Exception {
        return runInSpecialClassLoader(
                () -> {
                    ListBranchCommand branchCommand = git.branchList();
                    if (!hasLocalRepo())
                        branchCommand.setListMode(ListBranchCommand.ListMode.REMOTE);

                    List<String> branchNames = branchCommand.call()
                            .stream()
                            .map(Ref::getName)
                            .collect(Collectors.toList());

                    log.info("all branch name has : {}", branchNames.toString());

                    return branchNames;
                });
    }

    public List<String> getAllBranch() throws Exception {
        return runInSpecialClassLoader(
                () -> {
                    if (hasLocalRepo()) {
                        Git git = Git.wrap(openLocalRepository());
                        ListBranchCommand branchCommand = git.branchList();
                        List<String> branchNames = branchCommand
                                .setListMode(ListBranchCommand.ListMode.ALL)
                                .call()
                                .stream()
                                .map(Ref::getName)
                                .collect(Collectors.toList());

                        log.info("all local branch name has : {}", branchNames.toString());

                        return branchNames;
                    } else
                        return Collections.singletonList(AppSettingsState.DEFAULT_BRANCH_NAME);
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
                git.commit().setMessage("Committed by segment plugin").call();

            // 正确的提交了内容才可以push
            return mayBeNeedPush && git.status().call().getConflicting().isEmpty();
        });
    }

    public void push() throws Exception {
        this.runInSpecialClassLoader(() -> {
            Iterable<PushResult> pushResults = git.push().call();
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
            Set<String> urls = findRemoteUrlOfLocalRepo();
            // 本地已经有了直接返回
            if (urls != null && urls.contains(githubUrl)) {
                return new AppSettingsComponent.TestGithubUrlResult(true, this.getRemoteBranch());
            }
            // 测试 clone
            return testFetch(githubUrl);
        } catch (Exception e) {
            return new AppSettingsComponent.TestGithubUrlResult(false, Collections.EMPTY_LIST);
        }
    }

    private Set<String> findRemoteUrlOfLocalRepo() throws Exception {
        return this.runInSpecialClassLoader(() -> {
            Set<String> remoteUrls = new HashSet<>();
            if (this.hasLocalRepo()) {
                Config storedConfig = git.getRepository().getConfig();
                Set<String> remotes = storedConfig.getSubsections("remote");
                for (String remoteName : remotes) {
                    remoteUrls.add(storedConfig.getString("remote", remoteName, "url"));
                }
            }
            return remoteUrls;
        });
    }

    public List<String> findRemoteBranches(String githubUrl) throws GitAPIException {
        LsRemoteCommand lsRemoteCommand = Git.lsRemoteRepository();
        lsRemoteCommand.setRemote(githubUrl);
        final Map<String, Ref> map = lsRemoteCommand.setHeads(true).callAsMap();
        List<String> branchNames = new ArrayList<>();
        for (Map.Entry<String, Ref> entry : map.entrySet()) {
            branchNames.add(entry.getKey());
        }
        return branchNames;
    }

    private AppSettingsComponent.TestGithubUrlResult testFetch(String githubUrl) throws Exception {
        return this.runInSpecialClassLoader(() -> {
            try {
                LsRemoteCommand lsRemoteCommand = Git.lsRemoteRepository();
                lsRemoteCommand.setRemote(githubUrl);
                final Map<String, Ref> map = lsRemoteCommand.setHeads(true).callAsMap();
                List<String> branchNames = new ArrayList<>();
                for (Map.Entry<String, Ref> entry : map.entrySet()) {
                    branchNames.add(entry.getKey());
                }
                log.info("测试连接远程仓库并获得分支: {}", branchNames);
                return new AppSettingsComponent.TestGithubUrlResult(true, branchNames);
            } catch (Exception e) {
                return new AppSettingsComponent.TestGithubUrlResult(true, Collections.EMPTY_LIST);
            }
        });
    }

    public boolean isEmptyRepo() {
        String gitDir = settings.localSavePosition + "/.git/refs/heads";
        return new File(gitDir).list().length == 0;
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
}


















