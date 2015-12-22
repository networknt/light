package com.networknt.light.rule.shipping;

import com.networknt.light.rule.AbstractRule;
import com.networknt.light.rule.Rule;
import com.networknt.light.util.ServiceLocator;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static Map<String, BigDecimal> calculateTax(String province, BigDecimal subTotal) {
        Map<String, BigDecimal> taxes = new HashMap<String, BigDecimal>();
        BigDecimal gst = subTotal.multiply(new BigDecimal(0.05));
        gst = gst.setScale(2, RoundingMode.HALF_UP);
        switch(province) {
            case "AB":
                taxes.put("GST", gst);
                break;
            case "BC":
                taxes.put("GST", gst);
                BigDecimal bc = subTotal.multiply(new BigDecimal(0.07));
                bc = bc.setScale(2, RoundingMode.HALF_UP);
                taxes.put("PST", bc);
                break;
            case "MB":
                taxes.put("GST", gst);
                BigDecimal mb = subTotal.multiply(new BigDecimal(0.08));
                mb = mb.setScale(2, RoundingMode.HALF_UP);
                taxes.put("PST", mb);
                break;
            case "NB":
                BigDecimal nb = subTotal.multiply(new BigDecimal(0.13));
                nb = nb.setScale(2, RoundingMode.HALF_UP);
                taxes.put("HST", nb);
                break;
            case "NF":
                BigDecimal nf = subTotal.multiply(new BigDecimal(0.13));
                nf = nf.setScale(2, RoundingMode.HALF_UP);
                taxes.put("HST", nf);
                break;
            case "NS":
                BigDecimal ns = subTotal.multiply(new BigDecimal(0.15));
                ns = ns.setScale(2, RoundingMode.HALF_UP);
                taxes.put("HST", ns);
                break;
            case "NT":
                taxes.put("GST", gst);
                break;
            case "NU":
                taxes.put("GST", gst);
                break;
            case "ON":
                BigDecimal on = subTotal.multiply(new BigDecimal(0.13));
                on = on.setScale(2, RoundingMode.HALF_UP);
                taxes.put("HST", on);
                break;
            case "PE":
                BigDecimal pe = subTotal.multiply(new BigDecimal(0.14));
                pe = pe.setScale(2, RoundingMode.HALF_UP);
                taxes.put("HST", pe);
                break;
            case "QC":
                taxes.put("GST", gst);
                BigDecimal qc = subTotal.multiply(new BigDecimal(0.09975));
                qc = qc.setScale(2, RoundingMode.HALF_UP);
                taxes.put("QST", qc);
                break;
            case "SK":
                taxes.put("GST", gst);
                BigDecimal sk = subTotal.multiply(new BigDecimal(0.05));
                sk = sk.setScale(2, RoundingMode.HALF_UP);
                taxes.put("PST", sk);
                break;
            case "YK":
                taxes.put("GST", gst);
                break;
            default:
                logger.error("Unknown Province " + province);
                break;
        }
        return taxes;
    }

    public static BigDecimal calculateShipping(String province, BigDecimal subTotal) {
        BigDecimal b = subTotal.multiply(new BigDecimal(0.05));
        b = b.setScale(2, RoundingMode.HALF_UP);
        return b.add(new BigDecimal(30.00));
    }

}
