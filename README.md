Light Core
=====

Light is an Omni-Channel Application Framework/REST API Server Platform leveraging [AngularJS](https://angularjs.org/), [Undertow](http://undertow.io/) 
and [OrientDB](http://www.orientechnologies.com/orientdb/). It's lightning fast, light weight and sheds light on how Web Component based front end applications, 
Microservices REST APIs can be built and deployed. On the front end, it is easy to add new components, views and applications and to deploy them individually all through to production. 
On the back end, it adhears to the REST API with Java POJO and deploys individual Java class file to production without dealing with EARs, WARs and JARs.


# Technical Stack

* AngularJS - Most popular Javascript MVW Framework
* Undertow - One of the fastest Java Non-Blocking HTTP servers
* OrientDB - No-SQL Distributed Graph Database
* Light Rule Engine - A Java based Rule Engine

# Features
The Light Framework breaks a large application into smaller manageable pieces where each piece can be built by different teams or individuals. Front end Applications and Back end Services are separated by the API contract and can evolve in independently.

* [Event/Command Sourcing](http://martinfowler.com/eaaDev/EventSourcing.html) - When the client generates commands, the receiving API server converts it to event(s) which persists in event store. 
The entire system can be rebuilt from scratch by replaying all events in event store.
* [CQRS](http://martinfowler.com/bliki/CQRS.html) - Two sub systems on the server (Read and Write). Read System caches responses in JSON and subscribes events 
from Write System to update the cache if necessary.
* [Federated and Distributed](https://corporateblog.progress.com/2008/02/distributed-vs.html) - With the help of Light Gateway, the location of the services are
transparent to clients and multiple sites and work together to serve one to many clients together.
* [OAuth2 Authorization](http://oauth.net/2/) based on [JWT](https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-32) - This allows federated authorization on Gateway 
or both Gateway and Resource Server or Resource Server with public key.
* Modularity - Light modules contain Web Components and backend services packed together as a component, page or application. The framework provide templates to build them individually.
* Deployment - Each module (db object, Java rule, or UI component) can be deployed on dev server individually and exported to an event file. This file can be replayed on SIT, UAT, PAT and PROD for deployment
* Version and Dependency - Managed by a graph database which can be upgraded individually. User can choose which version to use to render UI and which web service version to serve the UI component.
* Form and Validation - Angular schema form is used to handle all forms and schema, form and actions are defined from the form builder and saved in the database. JSON Schema validation is done on both browser and server.

## Front End
* Versioning - All pages are loaded from database and versioned.
* Modularity - Components, Pages and Applications can be developed separately. A page contains many components and an application contains many pages. 
* Testability - Each piece can be tested individually with dependency on others. Templates are provided for components, pages, applications, and sites.
* Traceability - User actions, Angular runtime exceptions, and Global runtime exceptions are sent to the server as events and logged in event store. Replaying a series of events for one userId/IP can reproduce client errors.

## Back End
* Performance - 15 times faster than StrongLoop API Server based on Node.js and handles 50K requests per second on a standard desktop.
[Light Framework](https://www.youtube.com/watch?v=qpqdUWZCfH4) vs [StrongLoop](https://www.youtube.com/watch?v=fvkJTMSvTwA)
* Memory Footprint - Uses a similar amount of memory as Node.js even with the embedded Orient Database. Our [demo site](http://demo.networknt.com) is hosted on a free VPS.
* Productivity - Service endpoints map to one or more rules (Java POJOs) and can be unit tested and debugged with or without the embedded server running.
* Deployment Unit - A single Java rule source file can be a deployment unit and the newly deployed java source code will be compiled the first time the endpoint is accessed.
* Cache - Deep Etag based on the version of the current json string in cache and refresh based on events from Write Subsystem. The content won't be sent to client if Etag is not changed.
* Access Control - Each command will map to an API endpoint and access is controlled by clientId, role and user combination through JWT token.
* CORS - API endpoint can be configured to share the resource to all or certain sites so that components along with services can be integrated to other sites seamlessly.
* Transformation - For each request to the endpoint, developers can define a chain of rules to handle the request before response is returned to the client. (I.e. transformations can be applied).
* Router - Requests can be routed to different service providers or different rules based on JWT token, HTTP Header, etc... The framework supports multiple versions to be deployed at the same time.

# Challenges Addressed
Today’s software engineering approach has some challenges and the Light Framework is designed to address these.
## Productivity
Light Framework supports and encourages Agile Development. Agile software development is a software development methodology in which requirements and solutions evolve 
through collaboration between self-organizing, cross-functional teams. It promotes adaptive planning, evolutionary development, early and frequent delivery, continuous improvement 
and encourages rapid and flexible response to change. A group of people take responsibility for the entire life cycle of the software development and work with other 
teams for integration. This makes each team more productive as decisions happen locally without management overhead. Each team would have their own mission – to produce a reusable 
web component, view/page, application or API service. The component team would need to be aligned with view team and view team needs to be aligned with application team. In this 
sense they are loosely coupled but tightly aligned to the same mission.


The framework itself provides so many reusable common components, views, applications, and corresponding services which are ready to be used or customized. Most applications can be assembled from existing components found in the Market Place and only certain minor customization would be needed. Of course, you may need to build your domain specific 
modules but the existing ones can give you plenty of examples to follow. The framework also encourages brands and developers to publish their modules. The more people/brands using your experience, the more value you have. And the more developers using your modules, the more support and customization revenue you will have as developers.


By using the framework, large projects can be modularized to more manageable pieces and continuously integrated to allow components, views, and applications to grow 
gradually. This makes the development teams scalable and reduces risks for large projects.

## Quality Assurance
Different teams can manage components, views, and applications independently where the reusability of each is the main goal in the Light framework design. All pieces have unit tests and end-to-end tests 
in order to promote and give confidence to the end users. Each team can also have sample applications so end users can play with the module.


Front end AngularJS is known as a testable Javascript framework and backend does not have any container so rules can be tested as POJO. You don’t need to start a server 
to test your backend code and when it is time, you can user Apache HttpClient to test your API end to end with the embedded server. Debugging is easy as everything is POJO.


Agile development practices encourages QA and DEV teams to work together as one team reaching the common goal. The developers are writing the unit test cases and the testers are writing e2e test cases. If your organization won’t allow it, then a DIT exit report can be produced by the development team to assist the QA team during their testing process.

## Release Management

The Light framework is based on event sourcing and deployment by the means of generating events file from your development environment and replaying these events on DIT, SIT, UAT, PAT, and PROD.
Traditionally, releasing a new version of a product is so costly and risky such that some organizations might limit the number of releases to 3 or 4 per year. Each release will 
involve so many teams and last so long in testing since many changes will be packed into the same batch. An army consisting of DBAs, System Administrators, and Deployment Engineers 
will be working together during deployment time and must they follow the documentation step by step to get the job done. The impact to productivity by fixing defects and adding new 
features mid cycle slow the release to such an extent that meeting business needs in this dynamic world is rendered a much more difficult task.

In the Light framework we want the benefits of agile development and continuous integration all the way through to production. We encourage more deployments with high frequency and short 
cycles that lead to financial success. Whereas the traditional approach might read - fewer deployments with big thoroughly tested batch deployments that lead to financial success.
The above two approaches might have the same goal, but they seem to conflict with each other... How can the process be so different and lead to the same goal of financial success? To understand this, we need to understand how risk is calculated:

`ALE (Annual Loss Expectancy) = Single Loss Expectancy * Exposure Rate * Annualized Frequency`

In our software release world, we can understand it as:

`Loss = Single loss of error * Percentage of deployment error * Number of deployments`

For example, if one error occurs in 100 deployments, each error will cost $5000 and if there are 4 deployments per year, the ALE would be 0.0150004 = 200

Where the traditional approach would be to reduce the number of deployments to reduce the loss, our approach is to increase the number of deployments and reduce the single loss of 
error and percentage of deployment error. If this can be done, we can avoid financial losses due to downtime, bugs, noncompliance and loss of reputation. 
            
### Let’s look at the source of errors and try to lower the percentage of occurrence.

#### Defects in code
This can be addressed by unit test cases and e2e test cases. If we have enough coverage we can consistently change the code with confidence.

#### Errors in assembly or packaging
* Fast tests in continuous integration and delivery.
* Fail slow tests and violation of architecture and coding standards. 
* Clean build everything from Git repository Deploy the same way everywhere using events.
* Manage dependencies and versions with graph database.
* Manage Git branch and trunk through database to map to different release and environment. 
* Basically, automate everything.

#### Errors executing changes
Make deployment the same process everywhere by replaying serial events which include database updates, business rule updates, data updates, template updates, 
apps and experience updates etc. Basically, we don’t need an army for deployment, it is one click at the right time and place.


### Now, let’s look at the cost per error and see if we can reduce it:

* Zero down time deployment. 
* Database migrations and schema-less design(database change won’t break previous version of code) 
* Versioned identifiers for assets 
* Protocol versioning/Endpoint versioning 
* Decoupled architecture separating data and logic which can be deployed independently. 
* Configurable default version for every component letting the end-user choose if they want to use the updated version.
* Employees/Prod testers try out the new version before making it default version 
* User can downgrade version if they don’t like the new one. 
* Basically the user owns their experience.


In order to archive the above, we have the make our deployment unit as small as possible. Within the framework we have components, views, and apps where each of them can be 
versioned and deployed independently. Furthermore, they can be broken up to even smaller pieces to be deployed independently. For example, a component can have the following 
parts that can be deployed independently and versioned independently:

* AngularJS code (front end)
* Template (front end)
* Rules (back end)
* Rules Data (back end)
* Reference and configuration (back end)

For example, if the template gets a new version 1.0.2 deployed on the server, one site can user version 1.0.1 and another site can user 1.0.2 at the same time! This allows site
to customize the template for their channel as well. Even further, we can set template 1.0.1 as default so all the customers will have load the template when visiting, but we ask our
employees to try 1.0.2 version for a while before rolling it out as the default.

Although each piece can be deployed independently, they are loaded dynamically at the view level as part of angular routing. When Angular bootstraps, providers will be saved 
and they will be used to lazy load and register controllers, directives, filters, services, factories, providers, etc. When angular requires a page, the page id and page 
version will be passed to the server. (no version means default version will be used). The server will then check the dependencies of the page and assembly all the pieces together 
(java script code and templates) and send to the angular as the response. This would happen only for the first time the new version is loaded; the next time the same version is required
it can return the page from cache. The cached will only be updated once any piece of the page is changed through an event.

During the assembly phase the configuration data is combined with the logic and the final page is pre-processed. For example, the dynamic dropdown list will be generated 
at this phase for a form component.

Breaking up components to this level is not mandatory and it sometimes makes sense to have simple components packaged together and given only one version. You only need to break it up 
if your component is so complicated and has so many moving part as to make it configurable and customizable.


## Production Configuration
To make the application configurable on production, we need to separate the logic and data. The framework has three levels of configuration that can be performed on 
production and they each have a different level of risk associated with them.

The first level is reference data configuration like dropdowns, translations, etc. These will be saved into a set of schemas or tables 
and can be changed through the table maintenance application. The reference data is cached but will be refreshed once changed. This results in the lowest risk change on production as it will 
only impact the UI and can be rolled back if negative impact occurres. Of course, certain levels of validation have to be done as well as a having a submit/approve process in place.

The second level is the rules data configuration. All requests are handled by Light Rule Engine rules designed in two parts: Data, and logic. This level addresses 
the rule data changes and is low risk as it won’t impact rule logic, where the rule logic can be written to validate the data for the rules. For example, the system admin 
has the right to give promotions and discount products for a certain percentage, say 10 percent off. In this case, the 10 percent is the data, the rule might have validation
between 1 to 99 or 1 to 55 percent ranges. This piece of data (10%) is more important then the reference data as it impacts application logic but is isolated from the rules.
It can be modified easily without impacting the application.

The third level is rule logic configuration. The rules are just POJOs and can be updated and deployed through API services. This change is bigger but risk is still manageable 
as you only need to regression the components/views/apps that depends on this rule. Rules work independently of each other and failures would only impact one area of the app for which 
they can be easily be rolled back. To further reduce the risk on production, a new version of rule can be deployed and requests from users with a betaTester role can route to the new
version to test before switching the public to the new version.

## Security

API security or resource security is done by JWT token. When a user tries to access to protected resources, it will check if the access token is in the http request header. 
If it does not exist, it will redirect the user to a login page. The access token will be short lived up to 30 minutes and a 401 response along with token_expired will be sent 
back to client in order to refresh the now invalid token. If the user checks the remember me box when logging in, the life of the access token will be increased.

Access token contains clientId, roles, and userId so that the resource server can grant access based on either client-based, role-based, user-based authorization, or combinations of each.

Visibility control will be put into place based on the role of the users. For example, certain menus won’t be shown up unless you login with an admin role or certain web components 
could only show partial data based on the users role.

Light Gateway server provides another layer of security for the back-end legacy system as the Angular application would not talk to the back-end API directly. Also, this layer would 
do the validation before calling the back-end API so that a lot of invalid requests can premtively be filtered out.

## Performance

## Traceability

Traceability is more important with Angular applications as they are running on the end users’ browser. The server does not have the state of the user session since only the Angular 
application would know. In this case, event sourcing is utilized to log all the events happening on the client/browser side. Every user action will generate an event and it is sent to 
the server along with a JWT token that is the identifier for the user. The server is logging events into its event store.

* Un-caught runtime exceptions in Angular will be logged as an event and would be easily reproduced given a serial set of events leading to it for the same user in event store.
* Server error responses can be logged on server side as it is known who sent the request. For example, if a 404 error response is sent to the client, the monitoring support team 
would be preempted to the issue (be it a broken link, or otherwise) and would have the complete facility to reproduce it. Thus fixing the issue without the client needing to submit a ticket.
* Server side exceptions are logged with a stack trace and it can be reproduced along with events leading to it.
* Security violation will be logged when the system identifies that the request is not sent from our Angular applicatoin but some otherwise raw requests with missing data or wrong parameters.

System statistics can be viewed from the admin pages with information like current and historical user statistics, number of requests served over a period of time, and other usefull
metrics that would help you understand the trending health of your system.

Health check is an application that will check certain areas of the application based on the configuration data in order to make sure the overall system is fully functional. 
For example, it could check the connectivity with legacy systems etc. It is normally called upon once a new release is deployed and the system is behaving outside of expectations.

User behaviour analysis is an app that analyzes (a) users online behaviour and can be very valuable to drive sales/customer experience. If a customer goes to the bank branch to save a check and 
the sales person knows the customer was browsing life insurance products recently, it could benefit both the customer and the bank to bring up the idea.

Module update notifier will monitor if there are any security updates from the framework and notify system admins to take action.


# Get Started

## Installation On Linux

Requirements: maven, java8

1. Clone required projects:

  1. `git clone https://github.com/networknt/light.git`
  2. `git clone https://github.com/networknt/jsontoken.git`

2. Building jsontoken:

  1. `cd ./jsontoken`
  2. `mvn clean install`
    If this fails, it could be due Java8 being required, steps for ubuntu/mint linux:
      * `sudo add-apt-repository ppa:webupd8team/java`
      * `sudo apt-get update`
      * `sudo apt-get install oracle-java8-installer`
      * `sudo apt-get install oracle-java8-set-default`
  3. `cd ..`

3. Building the Light server:

  1. `cd ./light`
  2. `mvn clean install -DskipTests`

4. Light server configuration:

  Copy the virtualhost.json, and server.json file from light/server/src/main/resources to home/{user}/
  1. `cp server/src/main/resources/virtualhost.json ~`
  2. `cp server/src/main/resources/server.json ~`

5. Running the Light server:
    Run LightServer.java from server/src/main/java/com/networknt/light/server (In intellij, right click, and run)

## Installation On Windows

## Installation On Mac

##
# Live Demo
[demo.networknt.com](http://demo.networknt.com)

username: stevehu
password: 123456

