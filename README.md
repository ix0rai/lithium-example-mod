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

Because lithium-api is not published as a mod (that is, containing a `fabric|quilt.mod.json`), we cannot rely on loom to jar-in-jar it for us, and have to use other tools.  
First, we need to import the `shadow` gradle plugin. Shadow allows to you to embed (or "shadow") dependencies
in your mod jar. We place this in our `build.gradle` file, in the `plugins` section at the top of the file.
```groovy
plugins {
    // loom: required for fabric mod development
    id "fabric-loom" version "0.13-SNAPSHOT"
    // shadow: allows us to embed dependencies in our jar
    id "com.github.johnrengelman.shadow" version "7.1.2"
}

// Next, just above your `dependencies` block, you need to add this code, which will
// allow us to use a concise `shadow` statement to embed lithium-api in our mod jar.
configurations {
    shadow
    api.extendsFrom shadow
}

//The final block of code we need to insert is this statement, which can go anywhere in your `build.gradle` file:
// This will both configure shadow to embed `lithium-api` (as well as any other dependencies you choose to shadow)
// in your jar file, and also to move its created jar file
// into a separate directory (`/devlibs`) to decrease clutter in your `/build/libs` directory.
shadowJar {
    dependsOn jar
    configurations = [project.configurations.shadow]
    destinationDirectory.set(file("${project.buildDir}/devlibs"))
    archiveClassifier.set("dev")
}
prepareRemapJar.dependsOn(shadowJar)

//Finally, we add a dependency on lithium-api to our `dependencies` block:
dependencies {
    // lithium api
    shadow(files("libs/lithium-fabric-${project.lithium_api_version}-api-dev.jar"))
    
    // versions are defined in gradle.properties
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"

    // fabric api
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
}
```

And a property to `gradle.properties`, so that we don't have to poke into `build.gradle` to update lithium api:
```properties
lithium_api_version=mc1.19.1-0.9.0-SNAPSHOT
```
  
  
Embedding `lithium-api` in your jar file will add (~5kb) to your jar file size, a negligible amount.
  
A complete buildscript example can be found here: https://github.com/ix0rai/lithium-example-mod/blob/master/build.gradle.

### using lithium-api

#### pathfinding

The most important usage of the lithium api is to fix pathfinding when both your mod and lithium are installed.  
In a mod, the easiest way to make a mob avoid stepping on your block is to inject into the `getCommonNodeType` method of minecraft's `PathNodeMaker`,
making it return something that lets the mob know the block is unsafe, like `PathNodeType.DAMAGE_OTHER`, or `PathNodeType.DANGER_FIRE`.
However, lithium's pathfinding optimisations bypass this method entirely, making the mixin completely useless. So how do we solve this?
  
Luckily, `lithium-api` provides a solution. Its interface `BlockPathingBehavior` allows you to tell lithium what type of node your block provides, therefore
helping *you* to teach mobs that they should avoid your block. In order to use it, you first have to implement the interface in your block like this:

```java
// Here we extend `Block`, allowing you to simply call `new DamagingBlock()` instead of `new Block()` when creating the block object.
public class DamagingBlock extends Block implements BlockPathingBehavior {
    // Constructor for the block
    public DamagingBlock(Settings settings) {
        super(settings);
    }

    // Then, you need to implement lithium's two methods:
    // Note that we're providing summaries here, and there is extensive javadoc at https://github.com/CaffeineMC/lithium-fabric/blob/1.19.x/dev/src/api/java/me/jellysquid/mods/lithium/api/pathing/BlockPathingBehavior.java
    @Override
    public PathNodeType getPathNodeType(BlockState state) {
        // Determines the type of node your block provides.
        return PathNodeType.DAMAGE_OTHER;
    }

    @Override
    public PathNodeType getPathNodeTypeAsNeighbor(BlockState state) {
        // If your block is very dangerous, and its danger extends to the neighboring blocks, you want to return a dangerous type here.
        return PathNodeType.WALKABLE;
    }

    // Actually damage the entity, giving our block a reason to be dangerous.
    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity && !(world.isClient)) {
            entity.damage(DamageSource.MAGIC, 1.0f);
        }
    }
}
```