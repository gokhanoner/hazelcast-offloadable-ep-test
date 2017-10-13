# hazelcast-offloadable-ep-test
Hazelcast Offloadable Entry Processor Test with different in-memory data formats

Requires Java8

## Testing
Run a HzServer instance, with OBJECT or BINARY param, & then a HzClient using below commands.

HzClient first create 25K entry to a map, each around 5K. Then send Offloadable Readonly Enty Processor using `submitToKey` non-blocking API.

When using OBJECT in-memory format, around 30K, HzServer get OOM exception & heap dump created.

When using BINARY in-memory format, everything work as expected. Server print some health monitor logs but if you access the process with a tool like `jVisualVM` or `Eclipse Memory Analyzer`, then you can see that other than the entries, additional heap usage is very low.


### Running HzServer with OBJECT in-memory format
```
java -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -cp hazelcast-offloadable-ep-test-1.0-SNAPSHOT.jar com.oner.HzServer OBJECT
```

### Running HzServer with BINARY in-memory format
```
java -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -cp hazelcast-offloadable-ep-test-1.0-SNAPSHOT.jar com.oner.HzServer BINARY
```


### Running HzClient 
```
java -cp hazelcast-offloadable-ep-test-1.0-SNAPSHOT.jar com.oner.HzClient
```
