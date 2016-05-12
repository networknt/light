package com.networknt.light.schema;

import org.junit.Test;

/**
 * Created by steve on 26/04/16.
 */
public class LightSchemaTest {
     Root root = new Root();

    public void testExecute1() throws Exception {
        String query = "{hello}";

        ExecutionResult executionResult = new GraphQL(LightSchema.lightSchema).execute(query, new MutationSchema.Root(6));
        Object object = executionResult.getData();
        System.out.println(object);
    }

    @Test
    public void testExecute2() throws Exception {
        String query = "mutation signUpUser{user: signUpUser(userId:\"stevehu\", email:\"stevehu@gmail.com\", password:\"123456\", passwordConfirm:\"123456\") {userId, email}}";
        ExecutionResult executionResult = new GraphQL(LightSchema.lightSchema).execute(query, root);
        Object object = executionResult.getData();
        System.out.println(object);

        query = "mutation signUpUser{user: signUpUser(userId:\"test\", email:\"test@gmail.com\", password:\"123456\", passwordConfirm:\"123456\") {userId, email}}";
        executionResult = new GraphQL(LightSchema.lightSchema).execute(query, root);
        object = executionResult.getData();
        System.out.println(object);

        query = "mutation signInUser{user: signInUser(userId:\"stevehu\", password:\"123456\") {userId, email}}";
        executionResult = new GraphQL(LightSchema.lightSchema).execute(query, root);
        object = executionResult.getData();
        System.out.println(object);

    }


}
