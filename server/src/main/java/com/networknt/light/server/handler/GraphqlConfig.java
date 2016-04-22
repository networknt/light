package com.networknt.light.server.handler;

/**
 * Created by hus5 on 4/22/2016.
 */
public class GraphqlConfig {
    boolean graphiql;   // if graphiql will be served to browser
    boolean pretty;
    boolean debug;

    public GraphqlConfig() {
    }

    public boolean isGraphiql() {
        return graphiql;
    }

    public boolean isPretty() {
        return pretty;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setPretty(boolean pretty) {
        this.pretty = pretty;
    }

    public void setGraphiql(boolean graphiql) {
        this.graphiql = graphiql;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphqlConfig that = (GraphqlConfig) o;

        if (graphiql != that.graphiql) return false;
        if (pretty != that.pretty) return false;
        return debug == that.debug;

    }

    @Override
    public int hashCode() {
        int result = (graphiql ? 1 : 0);
        result = 31 * result + (pretty ? 1 : 0);
        result = 31 * result + (debug ? 1 : 0);
        return result;
    }
}
