## Building documentation
For building documentation we use [orchid](https://orchid.run/)
Using standard approach of publishing to gh-pages branch.

To check documentation locally before publishing use:
```
./gradlew :docs:orchidRun

```

Before running orchid deploy configure your githubToken.

```
export githubToken=slfgkhlkhslkdfhlsd 
```

```
./gradlew :docs:orchidDeploy -PorchidEnvironment=prod
``` 

[Documentation site](https://cere-io.github.io/sdk-android/)

## Library publishing

See [dcendents](https://github.com/dcendents/android-maven-gradle-plugin)

On github project go to releases and create new release with same version as in cere_sdk/build.gradle
```
defaultConfig{
  versionName "1.0.0"
}
```