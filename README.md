# Simple Data Flow with Strimzi

Simple POC for running different programs on a Apache Kafka dataflow, through Strimzi.

## Install Strimzi

Install Strimzi into your Openshift cluster. Could be done with [Strimzi-APB](https://github.com/matzew/strimzi-apb)...

## Install the apps

We have three apps that simulate a flow of data through a cluster of Apache Kafka. Location of the Kafka cluster as well as the different names for the Kafka topics are injected into the application using Environment Variables.

On the shell, go to your Openshift project, containing the Strimzi installation, like:

```
oc project myproject
```
  
### The Producer (JAX-RS Application with WF-Swarm)
The producer is a simple JAX-RS / CDI app, using kafka-cdi, and produces messages to a injected Kafka topic, based on some random data, after a RESTful endpoint is accessed. This could be an API gateway, for instance...

Get it up and running:

```
mvn clean fabric8:deploy
```

This kicks in the deployment to the current Openshift project, by using a S2i build.

### A vertx Consumer

The above producer writes to two different topic. The verx consumers reads from one of these topics. Install the app like:

```
mvn clean fabric8:deploy -Popenshift
```

### A FatJAR Consumer 

A normal FatJAR is a different consumer, reading Records from a different topic. Run the app like:

```
 mvn clean fabric8:deploy -Popenshift
 ```

## See it in Action 

Do `oc get pods`, this might give you something like:

```
NAME                                          READY     STATUS      RESTARTS   AGE
my-cluster-kafka-0                            1/1       Running     0          24m
my-cluster-topic-controller-527886806-2lh6w   1/1       Running     0          23m
my-cluster-zookeeper-0                        1/1       Running     0          27m
producer-1-wd7s2                              1/1       Running     0          21m
producer-s2i-1-build                          0/1       Completed   0          21m
raw-app-1-hmjmh                               1/1       Running     0          19m
raw-app-s2i-1-build                           0/1       Completed   0          19m
strimzi-cluster-controller-3119394011-tpvcx   1/1       Running     0          27m
vertx-kafka-consumer-1-b7f8x                  1/1       Running     0          17m
vertx-kafka-consumer-s2i-1-build              0/1       Completed   0          18m
```

### show the vertx logs:

Run `oc logs -f vertx-kafka-consumer-1-b7f8x -c vertx`

### Show the FatJAR logs:

Run `oc logs -f raw-app-1-hmjmh -c java-exec`

### Send some data using cURL

Do `oc get route` to see the route of the `producer`. Output is like:

```
NAME                   HOST/PORT                                        PATH      SERVICES               PORT      TERMINATION   WILDCARD
producer               producer-myproject.192.168.37.1.nip.io                     producer               8080                    None
raw-app                raw-app-myproject.192.168.37.1.nip.io                      raw-app                8080                    None
vertx-kafka-consumer   vertx-kafka-consumer-myproject.192.168.37.1.nip.io         vertx-kafka-consumer   8080                    None
```

Now run something like the below to genrate some traffic and see it flow through the system:

```
watch -n 0.25 curl http://producer-myproject.192.168.37.1.nip.io/hello
```



