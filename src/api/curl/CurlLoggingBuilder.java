package api.curl;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.HttpClientConfig.HttpClientFactory;
import io.restassured.config.RestAssuredConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import report.Reporting;

public class CurlLoggingBuilder {
  
    private final CurlLoggingInterceptor.Builder interceptorBuilder;
    private final RestAssuredConfig config;

    public CurlLoggingBuilder(RestAssuredConfig config, Reporting reporting) {
        this.config = config;
        this.interceptorBuilder = CurlLoggingInterceptor.defaultBuilder().setReporting(reporting);
    }

    public CurlLoggingBuilder(Reporting reporting) {
        this(RestAssured.config(),reporting);
    }

    public CurlLoggingBuilder logStacktrace() {
        this.interceptorBuilder.logStacktrace();
        return this;
    }

    public CurlLoggingBuilder dontLogStacktrace() {
        this.interceptorBuilder.dontLogStacktrace();
        return this;
    }

    public CurlLoggingBuilder printMultiliner() {
        this.interceptorBuilder.printMultiliner();
        return this;
    }

    public CurlLoggingBuilder printSingleliner() {
        this.interceptorBuilder.printSingleliner();
        return this;
    }

    public RestAssuredConfig build() {
        return this.config.httpClient(HttpClientConfig.httpClientConfig().reuseHttpClientInstance().httpClientFactory(new CurlLoggingBuilder.MyHttpClientFactory(this.interceptorBuilder.build())));
    }

    private static class MyHttpClientFactory implements HttpClientFactory {
        private final CurlLoggingInterceptor curlLoggingInterceptor;

        public MyHttpClientFactory(CurlLoggingInterceptor curlLoggingInterceptor) {
            this.curlLoggingInterceptor = curlLoggingInterceptor;
        }

        public HttpClient createHttpClient() {
            AbstractHttpClient client = new DefaultHttpClient();
            client.addRequestInterceptor(this.curlLoggingInterceptor);
            return client;
        }
    }
}
