# A Maven plugin for the Java Card SDK

Supported Java Card SDKs:
* v2.2.0
* v2.2.1
* v3.0.3
* v3.0.4
* v3.0.5u1

#### Simple Use Cases

Usage for Java Card SDK 3.0.x:

    <plugin>
        <groupId>com.github.code2358</groupId>
        <artifactId>jcdk-maven-plugin</artifactId>
        <version>0.0.1</version>
        <configuration>
            <jcdkHome>/path/to/javacard-sdk3</jcdkHome>
            <jcdkVersion>3.0.x</jcdkVersion>
            
            <appletClass>my.package.MyApplet</appletClass>
            <appletId>01:02:03:04:05:06</appletId>
            <appletVersion>1.0</appletVersion>
            
            <!-- Optional: support for Int32 -->
            <!-- <supportInt32>true</supportInt32> -->
            
            <!-- Optional: exports of other applets -->
            <!-- exportPaths>
              <param>/some-path/export1</param>
              <param>/some-path/export2</param>
            </exportPaths -->
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>packageApplet</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    
Usage for Java Card SDK 2.2.x:

    <plugin>
        <groupId>com.github.code2358</groupId>
        <artifactId>jcdk-maven-plugin</artifactId>
        <version>0.0.1</version>
        <configuration>
            <jcdkHome>/path/to/javacard-sdk2</jcdkHome>
            <jcdkVersion>2.2.x</jcdkVersion>
            
            <appletClass>my.package.MyApplet</appletClass>
            <appletId>01:02:03:04:05:06</appletId>
            <appletVersion>1.0</appletVersion>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>packageApplet</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    
#### Advanced Use Cases

If a use case is not covered by the default parameters of this plugin it is possible to provide a configuration file instead:

    <plugin>
        <groupId>com.github.code2358</groupId>
        <artifactId>jcdk-maven-plugin</artifactId>
        <version>0.0.1</version>
        <configuration>
            <jcdkHome>/path/to/javacard-sdk3</jcdkHome>
            <jcdkVersion>3.0.x</jcdkVersion>
            
            <jcdkConfiguration>my-jcdk.config</jcdkConfiguration>
        </configuration>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>packageApplet</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
    
Example my-jcdk.config file:

    -classdir /code/my-applet/target/classes -exportpath /path/to/javacard-sdk3/api_export_files -applet 0x01:0x02:0x03:0x04:0x05:0x06 my.package.MyApplet -d /code/my-applet/target/generated-jcdk -out CAP EXP JCA -v my.package 0x01:0x02:0x03:0x04:0x05 1.0