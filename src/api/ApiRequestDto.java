package api;

import java.util.Hashtable;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class ApiRequestDto {

  private String api_name;
  private String method;
  private String url;
  private String hostName;
  private String apiPath;
  private String mobileNumber;
  private String body;
  private String jsonFilePath;
  private String jsonSchemaFilePath;
  @Setter(value= AccessLevel.NONE)
  private Map<String, String> headers = new Hashtable<>();
  @Setter(value= AccessLevel.NONE)
  private Map<String, String> queryParams = new Hashtable<>();

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  public void addQueryParams(String key, String value) {
    queryParams.put(key, value);
  }

  public void setQueryParams(Map<String, String> params){
    queryParams.putAll(params);
  }

  public void setHeaders(Map<String, String> header){
    headers.putAll(header);
  }
}
