# Setup

Add kotlin library and cere_sdk library dependencies to your /app/build.gradle file.

```
dependencies {
    api            "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72"
    implementation files('/Users/sahataba/Workspace/sdk_android/cere_sdk/build/outputs/aar/cere_sdk-debug.aar')
}
```

# Initialisation

Import cere_sdk inside your MainActivity class.

```java
import io.cere.cere_sdk.CereModule;
```

Call init method on CereModule with appId and externalUserId, inside MainActivity 

```java
public class MainActivity extends AppCompatActivity {

    private CereModule cereModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.cereModule = CereModule.init(getApplicationContext(), "242", "sdfsdfsdf3243rfsd");
    }   
}
```

# Send events

Anywhere from your app, call cereModule.sendEvent to trigger your event with custom payload.

For quick integration test, you can use "APP_LAUNCHED_TEST" event, which will trigger display of "Hello world!" text inside android modal dialog.

```java
  this.cereModule.sendEvent("APP_LAUNCHED_TEST", "{}");
```

