package com.networknt.light.rule.injector.main.feed;

import com.networknt.light.rule.Rule;
import com.networknt.light.server.DbService;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by husteve on 10/2/2014.
 */
public abstract class ClassFeedRule extends FeedRule implements Rule {
    public abstract boolean execute (Object ...objects) throws Exception;

    public String getLoanNumber() {
        long num = DbService.incrementCounter("injector.loanNumber");
        return "" + num;
    }
    /*
    public void send(IDataFeed dataFeed, Map<String, Object> fields) throws Exception {
        // the fields coming from UI has a lot more elements then need by inject. create a copy
        // for injector so that we don't need to worry about it if new element is added.
        Map<String, Object> toBeInj = new HashMap<String, Object>();
        Date date = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        String d = sdf.format(date);
        // TODO use datepicker to default current date.
        toBeInj.put("reportDate", d);
        toBeInj.put("processingDate", d);

        // put other dates here as injector code in ROP has a bug that expect them populated.
        toBeInj.put("approvedDate", d);
        toBeInj.put("creditScoreDate", d);
        toBeInj.put("dateOpened", d);
        toBeInj.put("disbursementDate", d);
        toBeInj.put("maturityDate", d);
        toBeInj.put("nextReviewDate", d);
        toBeInj.put("terminationDate", d);

        // generate sequence of requestId
        String requestId = getRequestId();
        toBeInj.put("requestId", requestId);
        // replace loadNumber only if it is empty
        String loanNumber = (String)fields.get("loanNumber");
        if(loanNumber == null) {
            loanNumber = getLoanNumber();
            toBeInj.put("loanNumber", loanNumber);
            fields.put("loanNumber", loanNumber);
        }

        toBeInj.put("messageNumber", fields.get("messageNumber"));
        toBeInj.put("pid", fields.get("pid"));
        toBeInj.put("hppInd", fields.get("hppInd"));
        toBeInj.put("loanCategroy", fields.get("loanCategroy"));
        toBeInj.put("debtConsolidation", fields.get("debtConsolidation"));
        toBeInj.put("incomeVerification", fields.get("incomeVerification"));
        toBeInj.put("liabilityType", fields.get("liabilityType"));
        toBeInj.put("plcPurposeCode", fields.get("plcPurposeCode"));
        toBeInj.put("fullLiabilityIndicator", fields.get("fullLiabilityIndicator"));
        toBeInj.put("solicitorName", fields.get("solicitorName"));
        toBeInj.put("propertyProvCode", fields.get("propertyProvCode"));
        toBeInj.put("centreCode", fields.get("centreCode"));
        toBeInj.put("customerName", fields.get("customerName"));
        toBeInj.put("postNumber", fields.get("postNumber"));
        toBeInj.put("transit", fields.get("transit"));
        toBeInj.put("cid", fields.get("cid"));
        toBeInj.put("fundsDisbursed", fields.get("fundsDisbursed"));
        toBeInj.put("relationship", fields.get("relationship"));
        toBeInj.put("marketingSourceCode", fields.get("marketingSourceCode"));
        toBeInj.put("provinceCode", fields.get("provinceCode"));
        toBeInj.put("newConstructionInd", fields.get("newConstructionInd"));

        // put dataFeedType into the input fields here as it is not coming from UI.
        fields.put("dataFeedType", dataFeed.getDataFeedType());
        fields.put("requestId", requestId);

        Class beanClass = dataFeed.getClass();
        Field field = null;

        Set entries = toBeInj.entrySet();
        for (Iterator it = entries.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value != null && !value.equals("")) {
                field = findInheritedField(beanClass, (String) key);
                //set the value to the correct data type
                value = getInputField(value.toString(), field.getType());
                field.setAccessible(true);
                field.set(dataFeed, value);
            }
        }

        // send the dataFeed here.
        sendToQueue((String)fields.get("environment"), dataFeed);
    }
    */

}
