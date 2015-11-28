package com.networknt.light.rule.com.travelcoup.api;

import com.networknt.light.client.RestClient;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.HashUtil;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by steve on 25/11/15.
 *
 * It contains all the functions shared by all the APIs for Travel Coup
 */
public abstract class AbstractApiRule implements Rule {

    static final XLogger logger = XLoggerFactory.getXLogger(AbstractApiRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected String requestList(Map<String, Object> data) {

        String json = null;

        /*
        * destinationString
        * numberOfAdults
        * numberOfChildren
        * arrivalDate
        * departureDate
        */
        try {
            String url = "http://api.ean.com/ean-services/rs/hotel/v3/list?apiKey=6djofb5dc9s97rd4oh7v8fg14h&cid=494672&_type=json";
            long timestamp = System.currentTimeMillis()/1000L;
            String sig = HashUtil.md5("6djofb5dc9s97rd4oh7v8fg14h" + "1b45lkimnrdun" + timestamp);
            url = url + "&sig=" + sig;

            String destinationString = (String)data.get("destinationString");
            if(destinationString != null) {
                url = url + "&destinationString=" + destinationString;
            }
            String arrivalDate = (String)data.get("arrivalDate");
            if(arrivalDate != null) {
                url = url + "&arrivalDate=" + arrivalDate;
            }
            String departureDate = (String)data.get("departureDate");
            if(departureDate != null) {
                url = url + "&departureDate=" + departureDate;
            }
            Integer numberOfAdults = (Integer)data.get("numberOfAdults");
            if(numberOfAdults != null) {
                url = url + "&room1=" + numberOfAdults;
            }
            Integer numberOfChildren = (Integer)data.get("numberOfChildren");
            if(numberOfChildren != null) {
                switch(numberOfChildren) {
                    case 1:
                        url = url + ",5";
                        break;
                    case 2:
                        url = url + ",5,5";
                        break;
                    case 3:
                        url = url + ",5,5,5";
                        break;
                    default:
                        break;
                }
            }
            System.out.println(url);
            ResponseEntity<String> entity = RestClient.getInstance().restTemplate().getForEntity(url, String.class);
            if(entity.getStatusCode().equals(HttpStatus.OK)) {
                json = entity.getBody();
            }

        } catch (Exception e) {
            logger.error("Exception:", e);
        }
        return json;
    }

}

