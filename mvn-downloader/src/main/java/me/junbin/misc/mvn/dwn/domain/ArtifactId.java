package me.junbin.misc.mvn.dwn.domain;

import me.junbin.commons.util.Args;
import me.junbin.misc.mvn.dwn.interfaces.Copyable;
import me.junbin.misc.mvn.dwn.interfaces.UrlReacher;

import java.util.Objects;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 18:26
 * @description :
 */
public class ArtifactId implements Copyable<ArtifactId>, UrlReacher {

    private GroupId groupId;
    private String artifactId;

    public ArtifactId(GroupId groupId, String artifactId) {
        this.groupId = Args.notNull(groupId);
        this.artifactId = Args.notEmpty(artifactId);
    }

    @Override
    public String toString() {
        return "ArtifactId{" +
                "groupId=" + groupId +
                ", artifactId='" + artifactId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ArtifactId that = (ArtifactId) object;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(artifactId, that.artifactId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId);
    }

    @Override
    public ArtifactId copy() {
        return new ArtifactId(this.groupId.copy(), this.artifactId);
    }

    @Override
    public String url() {
        return this.groupId.url() + this.artifactId;
    }

    public GroupId getGroupId() {
        return groupId.copy();
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = Args.notNull(groupId);
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = Args.notEmpty(artifactId);
    }

}
