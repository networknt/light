package com.networknt.light.schema;

import graphql.schema.*;

import java.util.HashMap;
import java.util.Map;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLObjectType.newObject;

/**
 * Created by steve on 26/04/16.
 */
public class LightSchema {



    public static GraphQLObjectType userType = newObject()
            .name("User")
            .description("A logged in user of the framework.")
            .field(newFieldDefinition()
                    .name("userId")
                    .description("The unique userId picked during sign up.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("email")
                    .description("The email address that can be used to log in.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("firstName")
                    .description("User's first name or given name.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("lastName")
                    .description("User's last name or family name.")
                    .type(GraphQLString)
                    .build())
            .field(newFieldDefinition()
                    .name("accessToken")
                    .description("A JWT access token.")
                    .type(GraphQLBoolean)
                    .build())
            .field(newFieldDefinition()
                    .name("refreshToken")
                    .description("A refresh token.")
                    .type(GraphQLBoolean)
                    .build())
            .build();

    public static GraphQLInputObjectType inputUserSignInType = newInputObject()
            .name("inputUserSginInObjectType")
            .field(newInputObjectField()
                    .name("userIdEmail")
                    .description("Ether userId or email can be used for signIn.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("password")
                    .description("Password picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("rememberMe")
                    .description("Private computer and long lived token will be returned.")
                    .type(GraphQLBoolean)
                    .build())
            .build();

    public static GraphQLInputObjectType inputUserSignUpType = newInputObject()
            .name("inputUserSginInObjectType")
            .field(newInputObjectField()
                    .name("userId")
                    .description("Unique userId picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("email")
                    .description("Email address.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("password")
                    .description("Password picked during signUp.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("passwordConfirm")
                    .description("Password confirmation.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("firstName")
                    .description("First name or given name.")
                    .type(GraphQLString)
                    .build())
            .field(newInputObjectField()
                    .name("lastName")
                    .description("Last name or family name.")
                    .type(GraphQLString)
                    .build())
            .build();

    public static GraphQLObjectType queryType = newObject()
            .name("queryType")
            .field(newFieldDefinition()
                    .name("hello")
                    .type(GraphQLString)
                    .staticValue("world")
                    .build())
            .build();

    public static GraphQLObjectType mutationType = GraphQLObjectType.newObject()
            .name("mutationType")
            .description("All supported mutations.")
            .field(newFieldDefinition()
                    .name("signUpUser")
                    .type(userType)
                    .argument(newArgument()
                            .name("userId")
                            .type(GraphQLString)
                            .build())
                    .argument(newArgument()
                            .name("email")
                            .type(GraphQLString)
                            .build())
                    .argument(newArgument()
                            .name("password")
                            .type(GraphQLString)
                            .build())
                    .argument(newArgument()
                            .name("passwordConfirm")
                            .type(GraphQLString)
                            .build())
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment env) {


                            Root root = (Root) env.getSource();
                            return root.signUpUser((String)env.getArgument("userId"), (String)env.getArgument("email"), (String)env.getArgument("password"), (String)env.getArgument("passwordConfirm"));
                        }
                    })
                    .build())
            .field(newFieldDefinition()
                    .name("signInUser")
                    .type(userType)
                    .argument(newArgument()
                            .name("userId")
                            .type(GraphQLString)
                            .build())
                    .argument(newArgument()
                            .name("password")
                            .type(GraphQLString)
                            .build())
                    .dataFetcher(new DataFetcher() {
                        @Override
                        public Object get(DataFetchingEnvironment env) {
                            Root root = (Root) env.getSource();
                            return root.signInUser((String)env.getArgument("userId"), (String)env.getArgument("password"));
                        }
                    })
                    .build())
            .build();


    public static GraphQLSchema lightSchema = GraphQLSchema.newSchema()
            .query(queryType)
            .mutation(mutationType)
            .build();
}

