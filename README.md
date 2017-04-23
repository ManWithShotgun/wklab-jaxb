Exception: java.io.FileNotFoundException: file:/path/to/jarfile/bot.jar!/config/netclient.p (Exception! but file exist)
Answer: This is deliberate. The contents of the "file" may not be available as a file. Remember you are dealing with classes and resources that may be part of a JAR file or other kind of resource. The classloader does not have to provide a file handle to the resource, for example the jar file may not have been expanded into individual files in the file system.
        Anything you can do by getting a java.io.File could be done by copying the stream out into a temporary file and doing the same, if a java.io.File is absolutely necessary.


