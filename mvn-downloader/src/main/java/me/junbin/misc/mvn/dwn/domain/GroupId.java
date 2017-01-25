package me.junbin.misc.mvn.dwn.domain;

import me.junbin.commons.util.Args;
import me.junbin.misc.mvn.dwn.interfaces.Copyable;
import me.junbin.misc.mvn.dwn.interfaces.UrlReacher;

import java.util.Objects;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 18:25
 * @description :
 */
public class GroupId implements Copyable<GroupId>, UrlReacher {

    private String repoUrl;
    private String groupId;

    public GroupId(String repoUrl, String groupId) {
        this.repoUrl = Args.notEmpty(repoUrl);
        this.groupId = Args.notEmpty(groupId);
    }

    @Override
    public String toString() {
        return "GroupId{" +
                "repoUrl='" + repoUrl + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        GroupId groupId1 = (GroupId) object;
        return Objects.equals(repoUrl, groupId1.repoUrl) &&
                Objects.equals(groupId, groupId1.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repoUrl, groupId);
    }

    @Override
    public GroupId copy() {
        return new GroupId(this.repoUrl, this.groupId);
    }

    @Override
    public String url() {
        return repoUrl + groupId;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = Args.notEmpty(repoUrl);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = Args.notEmpty(groupId);
    }

}
