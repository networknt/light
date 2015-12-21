package com.networknt.light.rule.shipping;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by steve on 13/12/15.
 */
public abstract class AbstractAddressRule extends AbstractRule implements Rule {
    static final XLogger logger = XLoggerFactory.getXLogger(AbstractAddressRule.class);

    public abstract boolean execute (Object ...objects) throws Exception;

    protected void updAddress(Map<String, Object> data) throws Exception {
        logger.entry(data);
        OrientGraph graph = ServiceLocator.getInstance().getGraph();
        try {
            graph.begin();
            Vertex user =  graph.getVertexByKey("User.userId", data.get("userId"));
            if(user != null) {
                Map<String, Object> shippingAddress = (Map<String, Object>)data.get("shippingAddress");
                if(shippingAddress != null) {
                    user.setProperty("shippingAddress", shippingAddress);
                }
                Map<String, Object> paymentAddress = (Map<String, Object>)data.get("paymentAddress");
                if(paymentAddress != null) {
                    user.setProperty("paymentAddress", paymentAddress);
                }
                user.setProperty("updateDate", data.get("updateDate"));
            }
            graph.commit();
        } catch (Exception e) {
            logger.error("Exception:", e);
            graph.rollback();
            throw e;
        } finally {
            graph.shutdown();
        }
    }

    public static Map<String, Double> calculateTax(String province, double subTotal) {
        Map<String, Double> taxes = new HashMap<String, Double>();
        switch(province) {
            case "AB":
                taxes.put("GST(5%)", subTotal * 0.05);
                break;
            case "BC":
                taxes.put("GST(5%)", subTotal * 0.05);
                taxes.put("PST(7%)", subTotal * 0.07);
                break;
            case "MB":
                taxes.put("GST(5%)", subTotal * 0.05);
                taxes.put("PST(8%)", subTotal * 0.08);
                break;
            case "NB":
                taxes.put("HST(13%)", subTotal * 0.13);
                break;
            case "NF":
                taxes.put("HST(13%)", subTotal * 0.13);
                break;
            case "NS":
                taxes.put("HST(15%)", subTotal * 0.15);
                break;
            case "NT":
                taxes.put("GST(5%)", subTotal * 0.05);
                break;
            case "NU":
                taxes.put("GST(5%)", subTotal * 0.05);
                break;
            case "ON":
                taxes.put("HST(13%)", subTotal * 0.13);
                break;
            case "PE":
                taxes.put("HST(14%)", subTotal * 0.14);
                break;
            case "QC":
                taxes.put("GST(5%)", subTotal * 0.05);
                taxes.put("QST(9.975%)", subTotal * 0.09975);
                break;
            case "SK":
                taxes.put("GST(5%)", subTotal * 0.05);
                taxes.put("PST(5%)", subTotal * 0.05);
                break;
            case "YK":
                taxes.put("GST(5%)", subTotal * 0.05);
                break;
            default:
                logger.error("Unknown Province " + province);
                break;
        }
        return taxes;
    }

    public static double calculateShipping(String province, double subTotal) {
        return 30 + subTotal * 0.05;
    }

}
