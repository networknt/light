Light Core
=====

Light is an Omni-Channel Application Framework/REST API Server Platform leverages [AngularJS](https://angularjs.org/), [Undertow](http://undertow.io/) 
and [OrientDB](http://www.orientechnologies.com/orientdb/). It is lightning fast, light weighted and shed light on how Web Component based front end applications, 
Microservices REST APIs can be built and deployed. On front end, it is easy to add new components, views and applications and to deploy them individually to production. 
On back end, it implements REST API with Java POJO and deploys individual Java class file to production without dealing with EARs, WARs and JARs.


# Technical Stack

* AngularJS - Most popular Javascript MVW Framework
* Undertow - One of the fastest Java Non-Blocking HTTP servers
* OrientDB - No-SQL Distributed Graph Database
* Light Rule Engine - A Java based Rule Engine

# Features
Light Framework breaks large application into smaller manageable pieces and each piece can be built by different teams or individuals. Front end Applications and Back end Services
are separated by the API contract and can evolve in independently.

* [Event/Command Sourcing](http://martinfowler.com/eaaDev/EventSourcing.html) - Client generates command and API server converts it to event(s) then persists in event store. 
The entire system can be rebuilt from scratch by replaying all events in event store.
* [CQRS](http://martinfowler.com/bliki/CQRS.html) - Two sub systems on the server (Read and Write). Read System caches responses in JSON and subscribes events 
from Write System to update the cache if necessary.
* [Federated and Distributed](https://corporateblog.progress.com/2008/02/distributed-vs.html) - With the help of Light Gateway, the location of the services are
transparent to clients and multiple sites and work together to serve one to many clients together.
* [OAuth2 Authorization](http://oauth.net/2/) based on [JWT](https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32) - This allows federated authorization on Gateway 
or both Gateway and Resource Server or Resource Server with public key.
* Modularity - Light module contains Web Component and backend service and they packed together as component, page or application. The framework provide templates to build them individually.
* Deployment - Each module (db object, Java rules and UI components) can be deployed on dev server individually and exported to an event file. This file can be replayed on SIT, UAT, PAT and PROD for deployment.
* Version and Dependency - Managed by graph database and can be upgraded individually. User can choose which version to use to render UI and which web service version to serve the UI component.
* Form and Validation - Angular schema form is used to handle all forms and schema, form and actions are defined from form builder and saved in database. JSON Schema validation is done on both browser and server.

## Front End
* Versioning - All pages are loaded from database and versioned
* Modularity - Components, Pages and Applications can be developed separately. A page contains many components and A application contains may pages. 
* Testability - Each piece can be tested individually with dependency on others. Templates are provided for component, page, application and site.
* Traceability - User actions, Angular runtime exceptions and Global runtime exceptions are sent to the server as events and logged in event store. Replay a serial of events for one userId/IP can reproduce client errors.

## Back End
* Performance - 15 times faster than StrongLoop API Server based on Node.js and handles 50K requests per second on my desktop.
[Light Framework](https://www.youtube.com/watch?v=qpqdUWZCfH4) vs [StrongLoop](https://www.youtube.com/watch?v=fvkJTMSvTwA)
* Memory Footprint - Use similar amount of memory like Node.js even with embedded Orient Database. Our [demo site](http://demo.networknt.com) is host on a free VPS.
* Productivity - Service endpoint map to one or many rules that are Java POJOs and can be unit tested and debugged without or without embedded server running.
* Deploy Unit - Single Java rule source file can be a deployment unit and newly deployed java source code will be compiled the first time the endpoint is accessed.
* Cache - Deep Etag based on the version of json string in cache and refresh based on events from Write Subsystem. The content won't be sent to client if Etag is not changed.
* Access Control - Each command will map to an API endpoint and access is controlled by clientId, role and user combination through JWT token.
* CORS - API endpoint can be configured to share the resource to all or certain sites so that component along with service can be integrated to other sites seamlessly.
* Transformation - For each request to the endpoint, you can define a chain of rules to handle the request and before response is returned to the client, transformations can be applied.
* Router - Request can be routed to different service providers or different rules based on JWT token, HTTP Header etc. The framework supports multiple versions to be deployed at the same time.

# Challenges Addressed
Today’s software engineering approach has some challenges and Light Framework is designed to address these.
## Productivity
Light Framework supports and encourages Agile Development. Agile software development is a software development methods in which requirements and solutions evolve 
through collaboration between self-organizing, cross-functional teams. It promotes adaptive planning, evolutionary development, early delivery, continuous improvement 
and encourages rapid and flexible response to change. A group of people take responsibility for the entire life cycle of the software development and work with other 
teams for integration. This makes each team more productive as decisions happen locally without management overhead. Teams have their own mission – to produce reusable 
web component, view/page, application or API service. Component team needs to be aligned with view team and view team needs to be aligned with application team. In this 
sense they are loosely coupled but tightly aligned to the same mission.


The framework itself provides so many reusable common components, views,  applications and corresponding services and they are ready to be used or customized. So most of 
the applications can be assembled from existing pieces from Market Place and only certain customizations are needed. Of cause, you may need to build your domain specific 
modules but the existing ones can give you examples to follow. The framework also encourages brands and developer to publish their modules. The more brands using your 
brand’s experience, the more brand value you have. The more developers are using your modules the more support and customization revenue you will have as developers.


By using the framework, large projects can be break down to more manageable pieces and integration happens continuously to allow components, views and applications grow 
gradually. This makes the development teams scalable and reduces the risks for large projects.

## Quality Assurance
Different teams manage components, views and applications independently and reusability is the main goal in design. All pieces have unit tests and end-to-end tests 
in order to promote and give confidence for the end users. Also, each team have a sample application so end users can play with the module.


Front end AngularJS is known as testable Javascript framework and backend does not have any container so rules can be tested as POJO. You don’t need to start a server 
to test your backend code and when it is time, you can user Apache HttpClient to test your API end to end with the embedded server. Debugging is easy as everything is POJO.


Agile encourages QA and DEV teams are working together in one team. The developers are writing the unit test cases and the testers are writing e2e test cases. If your 
organization won’t allow it, then a DIT exist report will be produced by the development team to assist QA team for testing.

## Release Management

The Light framework is based on event sourcing and deployment just means to generate events file from development environment and replay the events on DIT, SIT, UAT, PAT and PROD.
Traditionally, release a new version of product is very costly and risky so some organizations might limit the number of releases to 3 or 4 times per year. Each release will 
involve so many teams and last so long for testing and many changes will be packed into the same batch. An army consists of DBAs, System Administrators and Deployment Engineers 
will be work together during deployment time and they follow the document step by step to get the job done. This impacts the productivity and makes fixing defects, adding new 
features so slow and could not meet business need in this dynamically changing world.

In Light framework, we want the benefits of agile development and continuous integration all the way to production. We encourage more deployments with high velocity and short 
cycles that lead to financial success. This conflicts with the traditional approach - fewer deployments with big thoroughly test batch deployment that lead to financial success.
Above two approaches have the same goal but it seems conflicting each other. How come they can lead to the same goal for financial success? To understand that, we need to 
understand how risk is calculated.

`ALE (Annual Loss Expectancy) = Single Loss Expectancy * Exposure Rate * Annualized Frequency`

In our software release world, we can understand it as

`Loss = Single lost of error * Percentage of deployment error * Number of deployments`

For example, if one error occurs in 100 deployments, each error will cost $5000 and there are 4 deployments per year, then the ALE would be 0.0150004 = 200

The traditional approach is to reduce the number of deployments to reduce the lost. And our approach is to increase the number of deployments and reduce the single lost of 
error and percentage of deployment error. If this can be done, we can avoid financial losses due to downtime, bugs, noncompliance and loss of reputation. 
            
### Let’s look at the source of errors and try to lower the percentage of occurrence.

#### Defects in code
This can be addressed by unit test cases and e2e test cases. If we have enough coverage, then we can change the code with confidence.

#### Errors in assembly or packaging
* Fast tests in continuous integration and delivery
* Fail slow tests and violation of architecture and coding standards. 
* Clean build everything from Git repository Deploy the same way everywhere using events 
* Manage dependencies and versions with graph database 
* Manage Git branch and trunk through database to map to different release and environment. 
* Basically, make everything automatic.

#### Errors executing changes
Make deployment the same process everywhere by just replay serial events which include database updates, business rules updates, rule data updates, template updates, 
apps and experience updates etc. Basically, we don’t need a army for deployment, it is one click at the right time and place.


### Now, let’s look at the cost of error and see if we can reduce it.

* Zero down time deployment. 
* Database migrations and schema-less (database change won’t break previous version of code) 
* Versioned identifiers for assets 
* Protocol versioning Endpoint versioning 
* Decoupled architecture Separate data and logic and they can be deployed independently. 
* Configurable default version for every component Let end-user to choose if they want to use the updated version 
* Employees try out the new version before making it default version 
* User can downgrade version if they don’t like the new one. 
* Basically user owns experience.


In order to archive the above, we have the make our deployment unit the smallest possible. Within the framework, we have component, view and app and each of them can be 
versioned and deployed independently. Further, they can be break up to even smaller piece to be deployed independently. For example: a component can have the following 
part that can be deployed independently and versioned independently.

* AngularJS code (front end)
* Template (front end)
* Rules (back end)
* Rules Data (back end)
* Reference and configuration (back end)

For example, only template get a new version 1.0.2 deployed on the server and other pieces are still in version 1.0.1 and we have an component version 1.0.2. One site 
can user version 1.0.1 and another site can user 1.0.2 and this allows site to customize the template for their channel as well. Even further, we can set the template 
1.0.1 as default so all the customer will have the default template but we ask our employee to try 1.0.2 version for a while before make it as default.

Although each piece can be deployed independently, they are loaded dynamically at the view level as part of angular routing. When Angular bootstraps, providers will be saved 
and they will be used to lazy load and register controllers, directives, filters, services, factories and providers etc. When angular requires a page, an page id and page 
version will be passed to the server. (no version means default version will be used) The server will check the dependencies of the page and assembly all piece together 
(java script code and templates) and send to the angular as response. This is for the first time, the next time the same version is required, it just response back the page 
cached. The cached will only be updated once any piece of the page is changed through event.

During the assembly phase, the configuration data and be combined with logic and the final page is pre-processed. For example, the dynamic dropdown list will be generated 
at this phase for a form component.

Breaking up the component to this level is no mandatory and it makes sense to have simple component packaged together and give it only one version. You only need to break it up 
if you component is so complicated and have too many moving part that is configurable and customizable.


## Production Configuration
To make the application configurable on production, we need to separate the logic and data. The framework has three levels of configurations that can be performed on 
production and they have different level of risk associated with them.

The first level is reference data configuration. Most applications have reference data like dropdowns, translations etc. These will be saved into a set of schemas or tables 
and can be changed through table maintenance app. The reference data is cached but will be refreshed once changed. This is the lowest risk change on production as it will 
only impact the UI look and feel most of the time and can be rolled back if negative impact occurred. Of cause, certain level of validation has to be done and approve process 
must be in place.

The second level is rules data configuration. All requests are handled by Light Rule Engine rules and rules are designed to be two part, Data and logic. This level is address 
the rule data change and it is at low risk as it won’t impact rule logic and the rule logic can be written to validate the data for the rules. For example, the system admin 
has the right to give promotion to discount one product for 10 percent off. The 10 percent is the data. And the rule might have validation between 1 to 99 or 1 to 55. This 
piece of data is more important then reference data as it is impact application logic but it is isolated from the rules. It can be changed easily without breaking the application.

The third level is rule logic configuration. The rules are just POJOs and can be updated and deployed though API service. This change is bigger and risk is still manageable 
as you only need to regression to all the component/view/app that depends on the rule. Rules are working independently and it fails it only impact one area of the app and 
it can be easily rolled back. To further reduce the risk on production, a new version of rule can be deployed and requests from betaTester role can route to the new version to test
it for a while before switching public to the new version.

## Security

API security or resource security is done by JWT token. When user is trying to access to protected resources, it will check if the access token is in the http request header. 
If it does not exist, it will redirect the user to login page. The access token will be short lived up to 30 minutes and a 401 response along with token_expired will be sent 
back to client for refresh token if the user checked remember me when logging in or login page will be shown up.

Access token contains clientId, roles and userId so that the resource serve can grant access based on client-based, role-based or user-based authorization or combinations above.

Visibility control will be put into place based on the role of the users. For example, certain menu won’t be shown up unless you login as an admin role or certain web component 
shows only partial of data the user role is just anonymous.

Light Gateway server provides another layer of security for the back-end legacy system for Angular application is not talking to back-end API directly. Also, this layer will 
do the validation before calling to back-end API so that a lot of invalid requests will be filtered out.

## Performance

## Traceability

Traceability is more important with Angular application as it is running on the end users’ browser. The server does not have the state of the user session and only Angular 
application knows. In this case, event sourcing is utilized to log all the events happening on the browser side. Every user action will generate an event and it is sent to 
the server along with JWT token that is identifier for the user. The server is logging events into event store.

* Un-caught runtime exception in Angular will logged as an event and it will be easily reproduced given a serial events leading to it for the same user in event store.
* Server error response will be logged on server side as it is known who sent the request. For example, 404 error response is sent to the client and support team need 
to reproduce it.
* Server side exception is logged with stack trace and it can be reproduced along with events leading to it.
* Security violation will be logged when system identify that the request is not sent from our Angular app but some raw request with missing data or wrong parameters.

System statistic can be viewed from admin page with information like how many users are online, how many requests are served within a period of time etc.

Health check is an application that will check certain area of the application based on the configuration data in order to make sure the over all system is healthy. 
For example, it will check the connectivity with legacy system etc. It is normally called once new release is deployed and when system is behaved strangely.

User behaviour analysis is an app that analyzes user online behaviour and it can be very valuable to drive sales. If customer goes to a bank branch to save a check and 
the sale person knows the customer was browsing life insurance product yesterday with his mobile phone.

Module update notification will monitor if there are any security updates from the framework and notify system admin to take action.


# Get Started

## Installation On Linux

## Installation On Windows

## Installation On Mac

##
# Live Demo
[demo.networknt.com](http://demo.networknt.com)

username: stevehu
password: 123456

