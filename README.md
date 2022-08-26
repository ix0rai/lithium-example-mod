code todo:  
todo: add inventory optimisation  

tutorial todo:  
todo: mixin explanation  
todo: building lithium (optional, also link to github artifact page)  
todo: importing lithium (shadow)  
todo: gradle explanation (explaining why we have to use shadow, specify exactly which bit need to be used)  
todo: implementing lithium (inventory optimisations and pathfinding)

## tutorial

### importing lithium-api

The ideal way to use lithium api is to embed it in your mod jar. This is best for two reasons:
- it means the end user does not have to download any extra dependencies
- it gives your users the option of whether to use lithium, with no downsides of not using it

#### obtaining a lithium-api jar

The easiest way to get an up-to-date `lithium-api` jar is to download the latest artifact from the [github actions page](lithium_api_version=mc1.19.1-0.9.0-SNAPSHOT
). Simply download the artifacts package from any run, and extract the jar ending in `-api-dev.jar`.  
Alternatively, you can build lithium yourself by cloning the [lithium repository](https://github.com/CaffeineMC/lithium-fabric) and running the `apiJar` gradle task. Keep in mind that the `-api` jar is not remapped to yarn, and you must use the `-api-dev` jar.
  
Once obtained, place your api jar in the `libs` folder of your project. Create the folder in the root directory if it is absent.

#### gradle nonsense

First, we need to import the `shadow` gradle plugin. Shadow allows to you to embed (or "shadow") dependencies
in your mod jar. We place this in our `build.gradle` file, in the `plugins` section at the top of the file.
```groovy
    id "com.github.johnrengelman.shadow" version "7.1.2"
```

Next, just above your `dependecies` block, you need to add this code:
```groovy
configurations {
    shadow
    api.extendsFrom shadow
}
```
This will allow us to use a concise `shadow` statement to embed lithium-api in our mod jar.  
The final block of code we need to insert is this statement, which can go anywhere in your `build.gradle` file:
```groovy
shadowJar {
    dependsOn jar
    configurations = [project.configurations.shadow]
    destinationDirectory.set(file("${project.buildDir}/devlibs"))
    archiveClassifier.set("dev")
}
prepareRemapJar.dependsOn(shadowJar)
```
This will both configure shadow to embed `lithium-api` (as well as any other dependencies you choose to shadow) in your jar file, and also to move its created jar file
into a separate directory (`/devlibs`) to decrease clutter in your `/build/libs` directory.

Finally, we add a dependency on lithium-api to our `dependencies` block:
```groovy
// lithium api
shadow(files("libs/lithium-fabric-${project.lithium_api_version}-api-dev.jar"))
```

And a property to `gradle.properties`, so that we don't have to poke into `build.gradle` to update lithium api:
```properties
lithium_api_version=mc1.19.1-0.9.0-SNAPSHOT
```
  
  
Embedding `lithium-api` in your jar file will add (~5kb) to your jar file size, a negligible amount.
  
A complete buildscript example can be found here: https://github.com/ix0rai/lithium-example-mod/blob/master/build.gradle.

### using lithium-api
// todo
