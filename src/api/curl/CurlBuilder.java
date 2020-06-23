package api.curl;

import io.restassured.internal.multipart.RestAssuredMultiPartEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurlBuilder {
    private static final Logger log = LoggerFactory.getLogger(CurlBuilder.class);
    private static final List<String> nonBinaryContentTypes = Arrays.asList("application/x-www-form-urlencoded", "application/json");

    public CurlBuilder() {
    }

    public static String generateCurl(HttpRequest request) throws Exception {
        return generateCurl(request, false);
    }

    public static String generateCurl(HttpRequest request, boolean printMultiliner) throws Exception {
        List<List<String>> command = new ArrayList();
        Set<String> ignoredHeaders = new HashSet();
        List<Header> headers = Arrays.asList(request.getAllHeaders());
        String inferredUri = request.getRequestLine().getUri();
        String inferredMethod;
        if (!isValidUrl(inferredUri)) {
            inferredMethod = getHost(request);
            String inferredScheme = "http";
            if (inferredMethod.endsWith(":443")) {
                inferredScheme = "https";
            } else if (request instanceof RequestWrapper && getOriginalRequestUri(request).startsWith("https")) {
                inferredScheme = "https";
            }

            if ("CONNECT".equals(request.getRequestLine().getMethod())) {
                inferredUri = String.format("%s://%s", inferredScheme, inferredMethod);
            } else {
                inferredUri = String.format("%s://%s/%s", inferredScheme, inferredMethod, inferredUri).replaceAll("(?<!http(s)?:)//", "/");
            }
        }

        command.add(line("curl", escapeString(inferredUri).replaceAll("[[{}\\\\]]", "\\$&")));
        inferredMethod = "GET";
        List<String> data = new ArrayList();
        Optional<String> requestContentType = tryGetHeaderValue(headers, "Content-Type");
        Optional<String> formData = Optional.empty();
        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest requestWithEntity = (HttpEntityEnclosingRequest)request;

            try {
                HttpEntity entity = requestWithEntity.getEntity();
                if (entity != null) {
                    if (((String)requestContentType.get()).startsWith("multipart/form")) {
                        ignoredHeaders.add("Content-Type");
                        ignoredHeaders.add("Content-Length");
                        handleMultipartEntity(entity, command);
                    } else if (((String)requestContentType.get()).startsWith("multipart/mixed")) {
                        headers = (List)headers.stream().filter((h) -> {
                            return !h.getName().equals("Content-Type");
                        }).collect(Collectors.toList());
                        headers.add(new BasicHeader("Content-Type", "multipart/mixed"));
                        ignoredHeaders.add("Content-Length");
                        handleMultipartEntity(entity, command);
                    } else {
                        formData = Optional.of(EntityUtils.toString(entity));
                    }
                }
            } catch (IOException var12) {
                log.error("Failed to consume form data (entity) from HTTP request", var12);
                throw var12;
            }
        }

        if (requestContentType.isPresent() && nonBinaryContentTypes.contains(requestContentType.get()) && formData.isPresent()) {
            data.add("--data");
            data.add(escapeString((String)formData.get()));
            ignoredHeaders.add("Content-Length");
            inferredMethod = "POST";
        } else if (formData.isPresent()) {
            data.add("--data-binary");
            data.add(escapeString((String)formData.get()));
            ignoredHeaders.add("Content-Length");
            inferredMethod = "POST";
        }

        if (!request.getRequestLine().getMethod().equals(inferredMethod)) {
            command.add(line("-X", request.getRequestLine().getMethod()));
        }

        headers = handleAuthenticationHeader(headers, command);
        headers = handleCookieHeaders(command, headers);
        handleNotIgnoredHeaders(headers, ignoredHeaders, command);
        if (!data.isEmpty()) {
            command.add(data);
        }

        command.add(line("--compressed"));
        command.add(line("--insecure"));
        command.add(line("--verbose"));
        return (String)command.stream().map((line) -> {
            return (String)line.stream().collect(Collectors.joining(" "));
        }).collect(Collectors.joining(chooseJoiningString(printMultiliner)));
    }

    private static CharSequence chooseJoiningString(boolean printMultiliner) {
        return printMultiliner ? String.format(" %s%n  ", commandLineSeparator()) : " ";
    }

    private static String commandLineSeparator() {
        return isOsWindows() ? "^" : "\\";
    }

    private static List<String> line(String... arguments) {
        return Arrays.asList(arguments);
    }

    private static List<Header> handleCookieHeaders(List<List<String>> command, List<Header> headers) {
        List<Header> cookiesHeaders = (List)headers.stream().filter((h) -> {
            return h.getName().equals("Cookie");
        }).collect(Collectors.toList());
        cookiesHeaders.forEach((h) -> {
            handleCookiesHeader(h, command);
        });
        headers = (List)headers.stream().filter((h) -> {
            return !h.getName().equals("Cookie");
        }).collect(Collectors.toList());
        return headers;
    }

    private static void handleCookiesHeader(Header header, List<List<String>> command) {
        List<String> cookies = Arrays.asList(header.getValue().split("; "));
        cookies.forEach((c) -> {
            handleCookie(c.trim(), command);
        });
    }

    private static void handleCookie(String cookie, List<List<String>> command) {
        String[] nameAndValue = cookie.split("=", 2);
        command.add(line("-b", escapeString(String.format("%s=%s", nameAndValue[0], nameAndValue[1]))));
    }

    private static void handleMultipartEntity(HttpEntity entity, List<List<String>> command) throws NoSuchFieldException, IllegalAccessException, IOException {
        HttpEntity wrappedEntity = (HttpEntity)getFieldValue(entity, "wrappedEntity");
        RestAssuredMultiPartEntity multiPartEntity = (RestAssuredMultiPartEntity)wrappedEntity;
        MultipartEntityBuilder multipartEntityBuilder = (MultipartEntityBuilder)getFieldValue(multiPartEntity, "builder");
        List<FormBodyPart> bodyParts = (List)getFieldValue(multipartEntityBuilder, "bodyParts");
        bodyParts.forEach((p) -> {
            handlePart(p, command);
        });
    }

    private static void handlePart(FormBodyPart bodyPart, List<List<String>> command) {
        String contentDisposition = ((MinimalField)bodyPart.getHeader().getFields().stream().filter((f) -> {
            return f.getName().equals("Content-Disposition");
        }).findFirst().orElseThrow(() -> {
            return new RuntimeException("Multipart missing Content-Disposition header");
        })).getBody();
        List<String> elements = Arrays.asList(contentDisposition.split(";"));
        Map<String, String> map = (Map)elements.stream().map((s) -> {
            return s.trim().split("=");
        }).collect(Collectors.toMap((a) -> {
            return a[0];
        }, (a) -> {
            return a.length == 2 ? a[1] : "";
        }));
        if (map.containsKey("form-data")) {
            StringBuffer part = new StringBuffer();
            part.append(removeQuotes((String)map.get("name"))).append("=");
            if (map.get("filename") != null) {
                part.append("@").append(removeQuotes((String)map.get("filename")));
            } else {
                try {
                    part.append(getContent(bodyPart));
                } catch (IOException var7) {
                    throw new RuntimeException("Could not read content of the part", var7);
                }
            }

            part.append(";type=" + bodyPart.getHeader().getField("Content-Type").getBody());
            command.add(line("-F", escapeString(part.toString())));
        } else {
            throw new RuntimeException("Unsupported type " + map.entrySet().stream().findFirst().get());
        }
    }

    private static String getContent(FormBodyPart bodyPart) throws IOException {
        ContentBody content = bodyPart.getBody();
        ByteArrayOutputStream out = new ByteArrayOutputStream((int)content.getContentLength());
        content.writeTo(out);
        return out.toString();
    }

    private static String removeQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    private static String getBoundary(String contentType) {
        String boundaryPart = contentType.split(";")[1];
        return boundaryPart.split("=")[1];
    }

    private static void handleNotIgnoredHeaders(List<Header> headers, Set<String> ignoredHeaders, List<List<String>> command) {
        headers.stream().filter((h) -> {
            return !ignoredHeaders.contains(h.getName());
        }).forEach((h) -> {
            command.add(line("-H", escapeString(h.getName() + ": " + h.getValue())));
        });
    }

    private static List<Header> handleAuthenticationHeader(List<Header> headers, List<List<String>> command) {
        headers.stream().filter((h) -> {
            return isBasicAuthentication(h);
        }).forEach((h) -> {
            command.add(line("--user", escapeString(getBasicAuthCredentials(h.getValue()))));
        });
        headers = (List)headers.stream().filter((h) -> {
            return !isBasicAuthentication(h);
        }).collect(Collectors.toList());
        return headers;
    }

    private static boolean isBasicAuthentication(Header h) {
        return h.getName().equals("Authorization") && h.getValue().startsWith("Basic");
    }

    private static String getBasicAuthCredentials(String basicAuth) {
        String credentials = basicAuth.replaceAll("Basic ", "");
        return new String(Base64.getDecoder().decode(credentials));
    }

    private static String getOriginalRequestUri(HttpRequest request) {
        if (request instanceof HttpRequestWrapper) {
            return ((HttpRequestWrapper)request).getOriginal().getRequestLine().getUri();
        } else if (request instanceof RequestWrapper) {
            return ((RequestWrapper)request).getOriginal().getRequestLine().getUri();
        } else {
            throw new IllegalArgumentException("Unsupported request class type: " + request.getClass());
        }
    }

    private static String getHost(HttpRequest request) {
        return (String)tryGetHeaderValue(Arrays.asList(request.getAllHeaders()), "Host").orElseGet(() -> {
            return URI.create(getOriginalRequestUri(request)).getHost();
        });
    }

    private static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException var2) {
            return false;
        }
    }

    private static Optional<String> tryGetHeaderValue(List<Header> headers, String headerName) {
        return headers.stream().filter((h) -> {
            return h.getName().equals(headerName);
        }).map(NameValuePair::getValue).findFirst();
    }

    private static boolean isOsWindows() {
        return System.getProperty("os.name") != null && System.getProperty("os.name").startsWith("Windows");
    }

    private static String escapeString(String s) {
        return isOsWindows() ? escapeStringWin(s) : escapeStringPosix(s);
    }

    private static String escapeStringWin(String s) {
        return "\"" + s.replaceAll("\"", "\"\"").replaceAll("%", "\"%\"").replaceAll("\\\\", "\\\\").replaceAll("[\r\n]+", "\"^$&\"") + "\"";
    }

    private static String escapeStringPosix(String s) {
        if (s.matches("^.*([^\\x20-\\x7E]|').*$")) {
            String escaped = s.replaceAll("\\\\", "\\\\").replaceAll("'", "\\'").replaceAll("\n", "\\n").replaceAll("\r", "\\r");
            escaped = (String)escaped.chars().mapToObj((c) -> {
                return escapeCharacter((char)c);
            }).collect(Collectors.joining());
            return "$'" + escaped + "'";
        } else {
            return "'" + s + "'";
        }
    }

    private static String escapeCharacter(char c) {
        String codeAsHex = Integer.toHexString(c);
        if (c < 256) {
            return c < 16 ? "\\x0" + codeAsHex : "\\x" + codeAsHex;
        } else {
            return "\\u" + ("" + codeAsHex).substring(codeAsHex.length(), 4);
        }
    }

    private static <T> Object getFieldValue(T obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field f = getField(obj.getClass(), fieldName);
        f.setAccessible(true);
        return f.get(obj);
    }

    private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException var4) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw var4;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }
}
