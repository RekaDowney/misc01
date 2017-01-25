package me.junbin.misc.mvn.dwn.launch;

import me.junbin.misc.mvn.dwn.domain.ArtifactId;
import me.junbin.misc.mvn.dwn.domain.StorePath;
import me.junbin.misc.mvn.dwn.util.Downloader;

import java.util.List;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 23:12
 * @description :
 */
public class Launcher {

    // 远程仓库地址，必须指定
    public static String REMOTE_REPO = "https://oss.sonatype.org/content/repositories/releases";

    // 依赖组ID，必须指定，支持 io.protostuff 或者 io/protostuff
    public static String GROUP_ID = "io.protostuff";

    // 本地仓库配置，必须指定
    public static StorePath STORE_PATH = new StorePath("M:/Software/Env/Apache/Maven/repository", false);

    public static void downloadLatest(String remoteRepo, String groupId, StorePath storePath) {
        List<ArtifactId> artifactIdList = Downloader.artifactIds(Downloader.validationGroupId(remoteRepo, groupId));
        for (ArtifactId artifactId : artifactIdList) {
            Downloader.latestVersion(artifactId).ifPresent(
                    v -> Downloader.download(v, storePath)
            );
        }
    }

    public static void downloadSelected(String remoteRepo, String groupId, StorePath storePath, String version) {
        List<ArtifactId> artifactIdList = Downloader.artifactIds(Downloader.validationGroupId(remoteRepo, groupId));
        for (ArtifactId artifactId : artifactIdList) {
            Downloader.selectedVersion(artifactId, version).ifPresent(
                    v -> Downloader.download(v, storePath)
            );
        }
    }

}
