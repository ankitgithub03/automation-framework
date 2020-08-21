package api;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import api.curl.CurlLoggingBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.module.jsv.JsonSchemaValidationException;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import report.custom.TestReporting;
import test.DriverFactory;
import ui.driverUtils.DriverActionsUtils;
import utils.CustomizeAssert;

public class API extends DriverActionsUtils {

  public ApiRequestDto apiRequestDto = null;
  protected static Logger log = LoggerFactory.getLogger(API.class);

  public API(TestReporting reporting, CustomizeAssert customizeAssert) {
    super(reporting, customizeAssert);
    this.apiRequestDto = new ApiRequestDto();
  }

  public Response execute() {
    RequestSpecification requestSpecification = getRequestSpecification(apiRequestDto);
    Response response = requestSpecification.request(apiRequestDto.getMethod());
    addRequestResponseInReport(response);
    DriverFactory.setResponse(response);
    DriverFactory.setResponse(apiRequestDto.getApiName(),response);
    validateResponseSchema(response);
    apiRequestDto = new ApiRequestDto();
    return response;
  }

  private void addRequestResponseInReport(Response response) {
    String url = apiRequestDto.getHostName() + apiRequestDto.getUrl();
    String rawResponse = "", rawRequest = "";
    rawRequest += getRequestHeaders(apiRequestDto.getHeaders(), rawRequest);
    rawResponse += getResponseHeaders(response.getHeaders(), rawResponse) + "\n";
    url = url + "?" + getQueryParam(apiRequestDto.getQueryParams());
    rawRequest += "Url : " + url + "\n";
    rawRequest += "Request method : " + apiRequestDto.getMethod().toUpperCase() + "\n";
    if (apiRequestDto.getBody() != null) {
      rawRequest += "Request body : \n";
      rawRequest += apiRequestDto.getBody().startsWith("{") ? new JSONObject(apiRequestDto.getBody()).toString(2)
              : apiRequestDto.getBody().startsWith("[") ? new JSONArray(apiRequestDto.getBody())
                  .toString(2) : apiRequestDto.getBody();
    }
    int responseCode = response.getStatusCode();
    String responseMessage = response.getStatusLine();
    String str_response = response.getBody().asString();
    rawResponse += str_response.startsWith("{") ? new JSONObject(str_response).toString(2)
        : str_response.startsWith("[") ? new JSONArray(str_response).toString(2) : str_response;
      sTest.put("API_NAME", apiRequestDto.getApiName());
      sTest.put("RAW_REQUEST", rawRequest.replaceAll("(\r\n|\n)", "<br />"));
      sTest.put("RAW_RESPONSE",
          "Server response code : " + responseCode + " " + responseMessage + "<br /><br />"
              + rawResponse.replaceAll("(\r\n|\n)", "<br />"));
    if (responseCode < 500) {
      if (reporting != null) {
        reporting.log("Request url - " + url, "response is generated successfully - " + responseCode, "Pass");
      }
    } else {
      if (reporting != null) {
        reporting.log("Request url - " + url, "response is not generated - " + responseCode, "Fail");
      }
    }
    log.info(url + " -- " + responseCode);
  }


  /**
   * hit the passed url
   *
   * @param url
   * @return response
   */
  public Response checkGetRequest(String url) {
    Response response = given().get(url).then().extract().response();
    reporting.log(url, response.statusCode()+"->"+response.getBody().asString(), "Done");
    return response;
  }


  private RequestSpecification getRequestSpecification(ApiRequestDto apiRequestDto) {
    RequestSpecification requestSpecification = null;
    RestAssured.defaultParser = Parser.JSON;
    RestAssuredConfig config = (new CurlLoggingBuilder(reporting)).build();
    try {
      apiRequestDto.addHeader("Accept", "application/json");
      apiRequestDto.addHeader("Content-Type", "application/json");
      requestSpecification = RestAssured.given()
          .headers(apiRequestDto.getHeaders())
          .queryParams(apiRequestDto.getQueryParams())
          .baseUri(apiRequestDto.getHostName() + apiRequestDto.getUrl())
          .config(config);
      //		headerRequest.put("Accept-Encoding","gzip");
      //		headerRequest.put("Accept-Encoding","application/json");
      //		RestAssured.config = RestAssured.config().
      //				 httpClient(HttpClientConfig.httpClientConfig().
      //                 setParam( "CONNECTION_MANAGER_TIMEOUT", 70000).
      //                 setParam( "SO_TIMEOUT", 70000));
      //		headerRequest.put("Accept-Encoding","gzip");
      if (apiRequestDto.getBody() != null) {
        requestSpecification.body(apiRequestDto.getBody());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return requestSpecification;
  }

  private String getRequestHeaders(Map<String, ?> requestHeaders, String rawRequest) {
    Set<String> keys = requestHeaders.keySet();
    Iterator<String> iterator = keys.iterator();
    while(iterator.hasNext()){
      String keyName = iterator.next();
      if(keyName == null || keyName.trim().equalsIgnoreCase("null"))
        continue;
      rawRequest += keyName + " : ";
      List<String> values;
      if(requestHeaders.get(keyName) instanceof String)
        values = Arrays.asList((String)requestHeaders.get(keyName));
      else
        values = (List<String>) requestHeaders.get(keyName);
      int i = 0;
      rawRequest += values.get(i);
      for(i = 1 ; i < values.size(); i++){
        rawRequest += ", " + values.get(i);
      }
      rawRequest += "\n";
    }
      sTest.put("REQUEST_HEADERS", rawRequest);
    return rawRequest;
  }

  private String getResponseHeaders(Headers headers, String rawResponse) {
    Iterator<Header> iter = headers.iterator();
    String cookies = "";
    while (iter.hasNext()) {
      Header header = iter.next();
      String keyName = header.getName();
      if ("Set-Cookie".equalsIgnoreCase(keyName)) {
        List<String> headerFieldValue = headers.getValues(keyName);
        for (String headerValue : headerFieldValue) {
          String[] fields = headerValue.split(";\\s*");
          String cookieValue = fields[0];
          cookies += cookieValue + ";";
        }
      }
      if (keyName == null || keyName.trim().equalsIgnoreCase("null")) {
        continue;
      }
      rawResponse += keyName + " : ";
      List<String> values = headers.getValues(keyName);
      int i = 0;
      rawResponse += values.get(i);
      for (i = 1; i < values.size(); i++) {
        rawResponse += ", " + values.get(i);
      }
      rawResponse += "\n";
    }
    sTest.put("RESPONSE_HEADERS", rawResponse);
    sTest.put("RESPONSE_COOKIES", cookies);
    return rawResponse;
  }

  private String getQueryParam(Map<String, ?> queryParams){
    String mapAsString = queryParams.keySet().stream().map(key -> key + "=" + queryParams.get(key))
        .collect(Collectors.joining("&"));
    return mapAsString;
  }

  public void validateResponseSchema(Response response) {
    if (apiRequestDto.getJsonSchemaFilePath() != null) {
      String schema = "";
      try {
        schema = FileUtils.readFileToString(new File(apiRequestDto.getJsonSchemaFilePath()), StandardCharsets.UTF_8);response.then().assertThat().body(matchesJsonSchema(new File(apiRequestDto.getJsonSchemaFilePath())));
        reporting.log("Excepted Response Schema :" + "\n" + schema, "Obtained Response :" + "\n" + response.getBody().asString(), "PASS");
      } catch (JsonSchemaValidationException nsee) {
        reporting.log("Excepted Response Schema :" + "\n" + schema, "Obtained Response :" + "\n" + response.getBody().asString() + "\n Exception Message :" + nsee.getMessage() + "\n Exception Cause :" + nsee.getCause(), "FAIL");
      } catch (IOException io) {
        reporting.log("Unable to read jsonSchema file", apiRequestDto.getJsonSchemaFilePath(), "FAIL");
      }
    }
  }


  /**
   *
   * @param response
   * @param url : $.authToken, $.abc.name
   * @return
   */
  public String getAnyValueFromResponseBody(Response response, String url) {
    String value = "";
    try {
      String json_String = response.getBody().asString();
      Object document = Configuration.defaultConfiguration().jsonProvider().parse(json_String);
      value = (String) JsonPath.read(document, url, new Predicate[0]);
    } catch (Exception var6) {
      reporting.log("get value from json for "+ url, value, "FAIL");
    }
    return value;
  }
}
