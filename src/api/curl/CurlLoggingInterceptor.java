package api.curl;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import report.Reporting;

public class CurlLoggingInterceptor implements HttpRequestInterceptor {

  private final static Logger log = LoggerFactory.getLogger(CurlLoggingInterceptor.class);
  private final boolean logStacktrace;
  private final boolean printMultiliner;
  private final Reporting reporting;

  protected CurlLoggingInterceptor(CurlLoggingInterceptor.Builder b) {
    this.logStacktrace = b.logStacktrace;
    this.printMultiliner = b.printMultiliner;
    this.reporting = b.reporting;
  }

  public static CurlLoggingInterceptor.Builder defaultBuilder() {
    return new CurlLoggingInterceptor.Builder();
  }

  private static void printStacktrace(StringBuffer sb) {
    StackTraceElement[] trace = Thread.currentThread().getStackTrace();
    StackTraceElement[] var2 = trace;
    int var3 = trace.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      StackTraceElement traceElement = var2[var4];
      sb.append("\tat " + traceElement + System.lineSeparator());
    }

  }

  public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
    try {
      String curl = CurlBuilder.generateCurl(request, this.printMultiliner);
      StringBuffer message = new StringBuffer(curl);
      if (this.logStacktrace) {
        message.append(String.format("%n\tgenerated%n"));
        printStacktrace(message);
      }
      log.info(message.toString());
//      this.reporting
//          .log("Generating Curl Command for HTTP request", "Curl Command", message.toString(), "Done");
    } catch (Exception var5) {
//      this.reporting.log("Failed to generate CURL command for HTTP request", "Exception Message", var5.getMessage(), "Done");
    }
  }

  public static class Builder {

    private boolean logStacktrace = false;
    private boolean printMultiliner = false;
    private Reporting reporting;

    public Builder() {
    }

    public CurlLoggingInterceptor.Builder logStacktrace() {
      this.logStacktrace = true;
      return this;
    }

    public CurlLoggingInterceptor.Builder dontLogStacktrace() {
      this.logStacktrace = false;
      return this;
    }

    public CurlLoggingInterceptor.Builder printMultiliner() {
      this.printMultiliner = true;
      return this;
    }

    public CurlLoggingInterceptor.Builder printSingleliner() {
      this.printMultiliner = false;
      return this;
    }

    public CurlLoggingInterceptor.Builder setReporting(Reporting reporting) {
      this.reporting = reporting;
      return this;
    }

    public CurlLoggingInterceptor build() {
      return new CurlLoggingInterceptor(this);
    }
  }
}
