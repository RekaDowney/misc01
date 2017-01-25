package me.junbin.misc.mvn.dwn.domain;

import me.junbin.commons.util.Args;
import me.junbin.misc.mvn.dwn.interfaces.Copyable;
import me.junbin.misc.mvn.dwn.interfaces.UrlReacher;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 18:29
 * @description :
 */
public class Version implements Copyable<Version>, UrlReacher {

    private ArtifactId artifactId;
    private String version;

    public Version(ArtifactId artifactId, String version) {
        this.artifactId = Args.notNull(artifactId);
        this.version = Args.notEmpty(version);
    }

    @Override
    public String toString() {
        return "Version{" +
                "artifactId=" + artifactId +
                ", version='" + version + '\'' +
                '}';
    }

    public static final Comparator<Version> LATEST_COMPARATOR = (version1, version2) -> {
        String v1 = version1.getVersion();
        String v2 = version2.getVersion();

        String[] part1 = v1.split("\\.|-|/");
        String[] part2 = v2.split("\\.|-|/");

        String s1;
        String s2;
        Integer i1;
        Integer i2;

        int len1 = part1.length;
        int len2 = part2.length;

        int len;
        int diff;

        int diffLen = Integer.compare(len1, len2);

        len = len1 > len2 ? len2 : len1;

        for (int i = 0; i < len; i++) {
            s1 = part1[i];
            s2 = part2[i];
            try {
                i1 = Integer.parseInt(s1);
                i2 = Integer.parseInt(s2);
                diff = Integer.compare(i1, i2);
                if (diff == 0) {
                    continue;
                }
                return diff;
            } catch (NumberFormatException ignore) {
                diff = s1.compareTo(s2);
                if (diff == 0) {
                    continue;
                }
                return diff;
            }
        }
        return diffLen;
    };

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Version version1 = (Version) object;
        return Objects.equals(artifactId, version1.artifactId) &&
                Objects.equals(version, version1.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, version);
    }

    @Override
    public Version copy() {
        return new Version(this.artifactId.copy(), this.version);
    }

    @Override
    public String url() {
        return this.artifactId.url() + this.version;
    }

    public ArtifactId getArtifactId() {
        return artifactId.copy();
    }

    public void setArtifactId(ArtifactId artifactId) {
        this.artifactId = Args.notNull(artifactId);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = Args.notEmpty(version);
    }

}
