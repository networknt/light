Light Core
=====

[![Join the chat at https://gitter.im/networknt/light](https://badges.gitter.im/networknt/light.svg)](https://gitter.im/networknt/light?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Light is an Omni-Channel Application Framework/REST API Server Platform leveraging [AngularJS](https://angularjs.org/), [Undertow](http://undertow.io/) 
and [OrientDB](http://www.orientechnologies.com/orientdb/). It's lightning fast, light weight and sheds light on how Web Component based front end applications, 
Microservices REST APIs can be built and deployed. On the front end, it is easy to add new components, views and applications and to deploy them individually all through to production. 
On the back end, it adhears to the REST API with Java POJO and deploys individual Java class file to production without dealing with EARs, WARs and JARs.


# Technical Stack

* AngularJS - Most popular Javascript MVW Framework
* ReactJS - Most popular UI Javascript library
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


# Get Started

## Installation On Linux


```
cd ~/networknt
git clone git@github.com:networknt/light.git
git checkout develop
cd light
cd networknt
npm install
```

After above steps you should have a build folder in light/networknt

Let's update the virtualhost.json in light/server/src/main/resources/config/dev

Here is the one works on my MacBookPro

```
{
  "www.edibleforestgarden.ca" : {
    "base" : "/Users/stevehu/networknt/light/edibleforestgarden/build",
    "transferMinSize" : "100",
    "supportDevices" : [ "Browser", "Android", "iOS" ],
    "id" : "www.edibleforestgarden.ca"
  },
  "www.networknt.com" : {
    "base" : "/Users/stevehu/networknt/light/networknt/build",
    "transferMinSize" : "100",
    "supportDevices" : [ "Browser", "Android", "iOS" ],
    "id" : "www.networknt.com"
  },
  "example" : {
    "base" : "/Users/stevehu/networknt/light/demo/build",
    "transferMinSize" : "100",
    "supportDevices" : [ "Browser", "Android", "iOS" ],
    "id" : "example"
  }
}
```

You need to update the base for networknt to your local absolute folder.

Also, you need to update /etc/hosts to add www.networknt.com for 127.0.0.1

```
127.0.0.1       localhost example www.networknt.com
```

Now, let's build and start the server

```
cd ~/networknt
cd light
mvn clean install
cd server
java -jar target/light-server.jar
```	

Now you can access the front end from browser.

http://www.networknt.com:8080

To login: stevehu/123456

## Installation On Windows

## Installation On Mac

##
# Live sites powered by Light Framework
www.networknt.com

www.edibleforestgarden.ca

username: test
password: 123456

