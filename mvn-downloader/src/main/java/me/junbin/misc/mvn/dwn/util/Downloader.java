package me.junbin.misc.mvn.dwn.util;

import me.junbin.commons.util.Args;
import me.junbin.commons.util.CollectionUtils;
import me.junbin.commons.util.http.HttpBuilder;
import me.junbin.commons.util.http.HttpProxy;
import me.junbin.commons.util.http.HttpUtil;
import me.junbin.misc.mvn.dwn.domain.ArtifactId;
import me.junbin.misc.mvn.dwn.domain.GroupId;
import me.junbin.misc.mvn.dwn.domain.StorePath;
import me.junbin.misc.mvn.dwn.domain.Version;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.junbin.commons.util.ansi.ColorfulPrinter.red;
import static me.junbin.commons.util.ansi.ColorfulPrinter.yellow;

/**
 * @author : Zhong Junbin
 * @email : <a href="mailto:rekadowney@163.com">发送邮件</a>
 * @createDate : 2017/1/25 19:00
 * @description :
 */
public class Downloader {

    private static final HttpProxy SS_PROXY = HttpProxy.custom()
                                                       .host("127.0.0.1")
                                                       .port(1080)
                                                       .scheme("http")
                                                       .build();
/*
    private static final int TIMEOUT = (int) TimeUnit.MINUTES.toMillis(10);
    private static final RequestConfig TIMEOUT_CONFIG = RequestConfig.custom()
                                                                     .setSocketTimeout(TIMEOUT)
                                                                     .setConnectTimeout(TIMEOUT)
                                                                     .setConnectionRequestTimeout(TIMEOUT)
                                                                     .build();
    private static final CloseableHttpClient PROXY_CLIENT = HttpClients.custom()
                                                                       .setDefaultRequestConfig(TIMEOUT_CONFIG)
                                                                       .setRoutePlanner(SS_PROXY.getProxy())
                                                                       .build();
*/

    private static final CloseableHttpClient PROXY_CLIENT = HttpBuilder.custom()
                                                                       .proxy(SS_PROXY)
                                                                       .timeout(10, TimeUnit.MINUTES)
                                                                       .build();

    private static final Function<Element, String> hrefMapper = element -> element.attr("href");
    private static final Predicate<String> validationFilter = url -> !(url.endsWith(".xml") ||
            url.endsWith(".sha1") || url.endsWith(".md5") || url.equals("../") ||
            url.endsWith(".asc"));
    private static final Pattern startWithLetter = Pattern.compile("[a-zA-Z]+.*");
    private static final Pattern startWithNumber = Pattern.compile("[0-9]+.*");
    private static final Predicate<String> startWithLetterFilter = url -> startWithLetter.matcher(url).matches();
    private static final Predicate<String> startWithNumberFilter = url -> startWithNumber.matcher(url).matches();
    private static final Predicate<String> jarOrPomFilter = url -> url.endsWith(".jar") || url.endsWith(".pom");
    private static final char SLASH_CHAR = '/';
    private static final String SLASH = "/";
    private static final String HTTP_SCHEME = "http";

    private static String normalizeGroupId(final String groupId) {
        return normalize(groupId.replaceAll("\\.", SLASH));
    }

    private static String normalize(final String url) {
        if (url.startsWith(HTTP_SCHEME)) {
            if (!url.endsWith(SLASH)) {
                return url + SLASH_CHAR;
            }
            return url;
        } else {
            String uri = url;
            if (uri.startsWith(SLASH)) {
                uri = url.substring(1);
            }
            if (!uri.endsWith(SLASH)) {
                uri = url + SLASH_CHAR;
            }
            return uri;
        }
    }

    private static String uri(final String url) {
        String uri = url;
        if (uri.startsWith(HTTP_SCHEME)) {
            if (uri.endsWith(SLASH)) {
                uri = uri.substring(0, url.length() - 1);
            }
            return uri.substring(uri.lastIndexOf(SLASH) + 1) + SLASH_CHAR;
        } else {
            if (uri.startsWith(SLASH)) {
                uri = url.substring(1);
            }
            if (!uri.endsWith(SLASH)) {
                uri = url + SLASH_CHAR;
            }
            return uri;
        }
    }

    public static GroupId validationGroupId(final String _repoUrl, final String _groupId) {
        String repoUrl = normalize(_repoUrl);
        String groupId = normalizeGroupId(_groupId);
        String url = repoUrl + groupId;
        try {
            HttpUtil.execute(PROXY_CLIENT, new HttpGet(url));
        } catch (IOException e) {
            throw new RuntimeException(String.format("非法的 groupId (%s)", _groupId), e);
        }
        return new GroupId(repoUrl, groupId);
    }

    public static List<ArtifactId> artifactIds(final GroupId groupId) {
        String url = groupId.url();
        try {
            String html = HttpUtil.execute(PROXY_CLIENT, new HttpGet(url));
            Document document = Jsoup.parse(html);
            List<String> artifactUrls = document.getElementsByTag("a")
                                                .stream()
                                                .parallel()
                                                .map(hrefMapper)
                                                .filter(validationFilter)
                                                .map(Downloader::uri)
                                                .filter(startWithLetterFilter)
                                                .collect(Collectors.toList());

            return artifactUrls.stream()
                               .map(artifactUrl -> new ArtifactId(groupId, artifactUrl))
                               .collect(Collectors.toList());

        } catch (IOException e) {
            red("groupId --> " + groupId + "；异常信息 --> " + e);
            return Collections.emptyList();
        }
    }

    public static List<Version> versions(final ArtifactId artifactId) {
        String url = artifactId.url();
        try {
            String html = HttpUtil.execute(PROXY_CLIENT, new HttpGet(url));

            List<String> versionUrls = Jsoup.parse(html)
                                            .getElementsByTag("a")
                                            .stream()
                                            .parallel()
                                            .map(hrefMapper)
                                            .filter(validationFilter)
                                            .map(Downloader::uri)
                                            .filter(startWithNumberFilter)
                                            .collect(Collectors.toList());

            return versionUrls.stream()
                              .map(versionUrl -> new Version(artifactId, versionUrl))
                              .collect(Collectors.toList());

        } catch (IOException e) {
            red("artifactId --> " + artifactId + "；异常信息 --> " + e);
            return Collections.emptyList();
        }
    }

    public static Optional<Version> max(final List<Version> versionList) {
        if (CollectionUtils.isEmpty(versionList)) {
            return Optional.empty();
        }
        return Optional.of(Collections.max(versionList, Version.LATEST_COMPARATOR));
    }

    public static Optional<Version> selected(final List<Version> versionList, final String version) {
        String uri = uri(version.trim());
        for (Version v : versionList) {
            if (uri.equals(v.getVersion())) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    public static Optional<Version> latestVersion(final ArtifactId artifactId) {
        return max(versions(artifactId));
    }

    public static Optional<Version> selectedVersion(final ArtifactId artifactId, final String version) {
        return selected(versions(artifactId), version);
    }

    public static String file(final String url) {
        Args.check(jarOrPomFilter.test(url), "url 必须以 .jar 或者 .pom 结尾");
        int index = url.lastIndexOf(SLASH);
        if (index == -1) {
            return url;
        } else {
            return url.substring(index + 1);
        }
    }

    public static void download(final Version version, final StorePath storePath) {
        String url = version.url();
        try {
            String html = HttpUtil.execute(PROXY_CLIENT, new HttpGet(url));
            List<String> jarOrPom = Jsoup.parse(html)
                                         .getElementsByTag("a")
                                         .stream()
                                         .parallel()
                                         .map(hrefMapper)
                                         .filter(jarOrPomFilter)
                                         .map(Downloader::file)
                                         .collect(Collectors.toList());

            Path localRepo = storePath.getLocalRepo();
            String baseUrl = version.getArtifactId().getGroupId().getRepoUrl();
            int len = baseUrl.length();
            String basePath = url.substring(len);
            Path dir = localRepo.resolve(basePath).normalize();
            boolean replaceIfExists = storePath.isReplaceIfExists();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path filePath;

            for (String file : jarOrPom) {
                filePath = dir.resolve(file);
                if (!Files.exists(filePath) || replaceIfExists) {
                    try (CloseableHttpResponse response = PROXY_CLIENT.execute(new HttpGet(url + file))) {
                        HttpEntity entity = response.getEntity();
                        long bytes = Files.copy(entity.getContent(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        yellow(file + " --> " + bytes);
                        EntityUtils.consume(entity);
                    }
                }
            }

        } catch (IOException e) {
            red("version --> " + version + "；异常信息 --> " + e);
        }
    }

    public static void download(final List<Version> versionList, final StorePath storePath) {
        for (Version version : versionList) {
            download(version, storePath);
        }
    }

}
