package me.junbin.misc.mvn.dwn.domain;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 22:00
 * @description :
 */
public class StorePath {

    private final Path localRepo;
    private final boolean replaceIfExists;

    public StorePath(String localRepo, boolean replaceIfExists) {
        this.localRepo = Paths.get(localRepo).normalize();
        this.replaceIfExists = replaceIfExists;
    }

    @Override
    public String toString() {
        return "StorePath{" +
                "localRepo=" + localRepo +
                ", replaceIfExists=" + replaceIfExists +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StorePath storePath = (StorePath) object;
        return replaceIfExists == storePath.replaceIfExists &&
                Objects.equals(localRepo, storePath.localRepo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localRepo, replaceIfExists);
    }

    public Path getLocalRepo() {
        return localRepo;
    }

    public boolean isReplaceIfExists() {
        return replaceIfExists;
    }

}
