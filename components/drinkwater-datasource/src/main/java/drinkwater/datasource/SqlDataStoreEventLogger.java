package drinkwater.datasource;

import com.fasterxml.jackson.databind.SerializationFeature;
import drinkwater.helper.json.CustomJacksonObjectMapper;
import drinkwater.trace.AbstractEventLogger;
import drinkwater.trace.BaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * this class inject events in the following schema
 *
 *   CREATE TABLE trace (
 *           id serial primary key,
 *           correlationId character varying(256) NOT NULL,
 *           instant timestamp without time zone NOT NULL,
 *           name character varying(256) NOT NULL,
 *           description character varying(512),
 *           application character varying(512),
 *           service character varying(512),
 *           operation text,
 *           body text,
 *           headers text
 *   );
 */
public class SqlDataStoreEventLogger extends AbstractEventLogger {

    private Logger logger = LoggerFactory.getLogger(SqlDataStoreEventLogger.class);

    public JndiSqlDataStore store;

    public String tableName;

    @Override
    public void logEvent(BaseEvent baseEvent) {

        try {
            if (handleEvent(baseEvent.getClass())) {

                Map<String, Object> properties = fillBaseproperties(baseEvent);

                Map<String, Object> additionalHeaders = extractHeaders(baseEvent);

                if (additionalHeaders != null) {
                    properties.putAll(additionalHeaders);
                }

                if (saveBodyOf(baseEvent)) {
                    properties.put("body", serializeObject(baseEvent.getPayload().getBody()));
                }
                if (saveHeadersOf(baseEvent)) {
                    CustomJacksonObjectMapper mapper = new CustomJacksonObjectMapper();
                    mapper.disable(SerializationFeature.INDENT_OUTPUT);
                    Map<String, String> newHeaders = removeCamelHeaders(baseEvent.getPayload().getHeaders());
                    String headersAsJson = mapper.writeValueAsString(newHeaders);
                    properties.put("headers", headersAsJson);
                }
                if (saveMethodOf(baseEvent)) {
                    properties.put("operation", serializeObject(baseEvent.getPayload().getOperation()));
                }

                store.executeInsert(getTraceTableName(), properties);
            }
        } catch (Exception ex) {
            onException(ex, baseEvent);
        }

    }

    public static Map<String, String> removeCamelHeaders(final Map<String, Object> allHeaders){
        Map<String, String> headers = new LinkedHashMap<>();
        if(allHeaders != null) {

            allHeaders.forEach((k, v) -> {
                        if (k != null) {
                            if (!k.toLowerCase().startsWith("camel")) {
                                if(v != null) {
                                    headers.put(k, v.toString());
                                }
                            }
                        }
                    }
            );

        }
        return headers;

    }

    public String getTraceTableName() {
        if(tableName == null){
            tableName = "trace";
        }
        return tableName;
    }

    protected Map<String, Object> fillBaseproperties(BaseEvent baseEvent) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("correlationid", baseEvent.getCorrelationId());
        properties.put("instant", baseEvent.getTime());
        properties.put("name", baseEvent.getName());
        properties.put("description", baseEvent.getDescription());
        properties.put("application", baseEvent.getApplicationName());
        properties.put("service", baseEvent.getServiceName());

        return properties;

    }

    protected Map<String, Object> extractHeaders(BaseEvent baseEvent){
        return null;
    }

    protected  boolean saveBodyOf(BaseEvent baseEvent){
        return true;
    }

    protected boolean saveMethodOf(BaseEvent baseEvent){
        return true;
    }

    protected boolean saveHeadersOf(BaseEvent baseEvent){
        return true;
    }

    public void onException(Exception ex, BaseEvent event){
        try {
            logger.error("could not log event due to  ->" + serializeObject(ex));
            logger.error("event was                   ->" + serializeEvent(event));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean handleEvent(Class eventType){
        return true;
    }
}
